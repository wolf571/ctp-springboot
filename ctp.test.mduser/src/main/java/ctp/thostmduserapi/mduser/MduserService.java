package ctp.thostmduserapi.mduser;

import ctp.thostmduserapi.CThostFtdcDepthMarketDataField;
import ctp.thostmduserapi.CThostFtdcMdApi;
import ctp.thostmduserapi.CThostFtdcReqUserLoginField;
import ctp.thostmduserapi.CThostFtdcUserLogoutField;
import ctp.thostmduserapi.utils.JsonHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MduserService {

    /**
     * 是否在线
     */
    @Setter
    private boolean online = false;

    private CThostFtdcMdApi mdApi;
    /**
     * 初始订阅合约列表
     * <p>
     * 包含主力，次主力合约
     */
    List<String> subscriptionList = null;
    /**
     * 订阅合约列表
     */
    private String[] instruments;

    public void setContractsMap(Map<String, List<String>> contractsMap) {
        this.contractsMap = contractsMap;
    }

    /**
     * 产品合约列表
     */
    private Map<String, List<String>> contractsMap = new HashMap<>();
    /**
     * 合约行情列表
     * 用于日盘收盘前计算主力合约
     */
    private Map<String, Double> depthMdMap = new HashMap<>();

    @Autowired
    MduserConfig mdConfig;

    @Autowired
    MduserSpi mdSpi;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        if (mdApi != null) {
            mdApi.Release();
            mdApi = null;
        }
        mdApi = CThostFtdcMdApi.CreateFtdcMdApi();
        mdApi.RegisterSpi(mdSpi);
        String[] address = mdConfig.getAddress().split(",");
        for (String addr : address) {
            mdApi.RegisterFront(addr);
        }
        mdApi.Init();
    }

    /**
     * 登录
     */
    public void login() {
        CThostFtdcReqUserLoginField field = new CThostFtdcReqUserLoginField();
        field.setBrokerID(mdConfig.getBrokerId());
        field.setUserID(mdConfig.getUserId());
        field.setPassword(mdConfig.getPassword());

        mdApi.ReqUserLogin(field, 0);
    }

    /**
     * 订阅合约
     */
    public void subscribe() {
        String[] array = new String[]{
                "rb2001", "rb2002", "rb2003", "rb2004", "rb2005", "rb2006", "rb2007", "rb2008", "rb2009", "rb2010", "rb2011", "rb2012",
                "pp2001", "pp2002", "pp2003", "pp2004", "pp2005", "pp2006", "pp2007", "pp2008", "pp2009", "pp2010", "pp2011", "pp2012",
                "eg1912", "eg2001", "eg2002", "eg2003", "eg2004", "eg2005", "eg2006", "eg2007", "eg2008", "eg2009", "eg2010", "eg2011",
                "m2001", "m2003", "m2005", "m2007", "m2008", "m2009", "m2011", "m2012"
        };
        mdApi.SubscribeMarketData(array, array.length);
    }

    public void subscribe(String instrumentId) {
        mdApi.SubscribeMarketData(new String[]{instrumentId}, 1);
    }

    /**
     * 订阅合约列表
     */
    public void subscribeProducts() {
        log.info("开始订阅产品合约数据..., {}", contractsMap);
        contractsMap.forEach((k, v) -> {
//            String[] ins = new String[v.size()];
//            v.toArray(ins);
            log.info("v...{}", v);
            for (String instrument : v) {
                log.info("{}...", instrument);
                subscribe(instrument);
                try {
                    Thread.currentThread().sleep(10000);
                } catch (InterruptedException e) {
                    log.info("error..., {}", e.getMessage());
                }
            }
        });
    }

    /**
     * 退订合约
     */
    public void unSubscribe(String contracts) {
        mdApi.UnSubscribeMarketData(new String[]{contracts}, 1);
    }

    /**
     * 发布行情数据到kafka
     *
     * @param field
     */
    @Async
    public void publish(CThostFtdcDepthMarketDataField field) {
        String contract = field.getInstrumentID();
        if (!contract.equals("rb2001")) {
            this.unSubscribe(contract);
        }
        String content = JsonHelper.toJsonString(field);
        log.info("{},{}", contract, content);
    }

    /**
     * 订阅合约信息
     *
     * @param str
     */
    public void consumeSubscribe(String str) {
        Map map = JsonHelper.toMap(str);
        //产品代码
        String product = map.get("productID").toString();
        //合约代码
        String contract = map.get("instrumentID").toString();
        //多头保证金率
        double longMarginRatio = Double.parseDouble(map.get("longMarginRatio").toString());
        //空头保证金率
        double shortMarginRatio = Double.parseDouble(map.get("shortMarginRatio").toString());

        List<String> ins = contractsMap.get(product);
        if (ins == null) {
            ins = new ArrayList<>();
            contractsMap.put(product, ins);
        }
        if (!ins.contains(contract)) {
            ins.add(contract);
        }
    }

    /**
     * 计算主力/次主力合约
     */
    public void calcMainContract() {
        log.info("开始计算主力合约...");
        Map<String, String> dominantMap = new HashMap<>();
        Map<String, String> secondaryMap = new HashMap<>();
        log.info("depthMdMap: {}", depthMdMap);
        depthMdMap.forEach((k, v) -> {
            //product id
            String k0 = k.replaceAll("\\d+", "");
            String d = dominantMap.get(k0);
            if (StringUtils.isEmpty(d)) {
                dominantMap.put(k0, k);
                return;
            }
            if (v > depthMdMap.get(d)) {
                dominantMap.put(k0, k);
                secondaryMap.put(k0, d);
                return;
            }
            String s = secondaryMap.get(k0);
            if (StringUtils.isEmpty(s) || v > depthMdMap.get(s)) {
                secondaryMap.put(k0, k);
            }
        });
    }

    /**
     * 登出/释放
     */
    public void release() {
        if (!online) {
            return;
        }
        CThostFtdcUserLogoutField field = new CThostFtdcUserLogoutField();
        field.setBrokerID(mdConfig.getBrokerId());
        field.setUserID(mdConfig.getUserId());
        mdApi.ReqUserLogout(field, 0);

        mdApi.Release();
        mdApi = null;

        this.setOnline(false);
    }
}
