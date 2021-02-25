package ctp.thosttraderapi.trader;

import ctp.thosttraderapi.*;
import ctp.thosttraderapi.enums.DirectionEnum;
import ctp.thosttraderapi.enums.ExchangeEnum;
import ctp.thosttraderapi.enums.OrderOffsetEnum;
import ctp.thosttraderapi.enums.OrderTypeEnum;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static ctp.thosttraderapi.THOST_TE_RESUME_TYPE.THOST_TERT_RESTART;

@Slf4j
@Service
public class TraderService {
    /**
     * 是否在线
     */
    private boolean online = false;

    public boolean isOnline() {
        return online;
    }

    /**
     * 会话标识
     */
    private int frontId;
    private int sessionId;
    private String tradingDay;

    private CThostFtdcTraderApi traderApi;

    @Autowired
    TraderConfig traderConfig;

    @Autowired
    TraderSpi traderSpi;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        if (traderApi != null) {
            traderApi.Release();
            traderApi = null;
        }
        traderApi = CThostFtdcTraderApi.CreateFtdcTraderApi();
        traderApi.RegisterSpi(traderSpi);
        // 订阅私有流
        traderApi.SubscribePrivateTopic(THOST_TERT_RESTART);
        // 订阅公有流
        traderApi.SubscribePublicTopic(THOST_TERT_RESTART);
        String[] address = traderConfig.getAddress().split(",");
        for (String addr : address) {
            traderApi.RegisterFront(addr);
        }
        traderApi.Init();
    }

    /**
     * 设置会话信息
     *
     * @param on
     * @param fid
     * @param sid
     * @param day
     */
    public void setFs(boolean on, int fid, int sid, String day) {
        this.online = on;
        this.frontId = fid;
        this.sessionId = sid;
        this.tradingDay = day;
    }

    public int getFrontId() {
        return frontId;
    }

    public int getSessionId() {
        return sessionId;
    }

    /**
     * 获取tradingDay
     *
     * @return
     */
    public String getTradingDay() {
        return this.tradingDay;
    }

    /**
     * 认证
     */
    public void authenticate() {
        CThostFtdcReqAuthenticateField field = new CThostFtdcReqAuthenticateField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setUserID(traderConfig.getUserId());
        field.setAppID(traderConfig.getAppId());
        field.setAuthCode(traderConfig.getAuthCode());

        traderApi.ReqAuthenticate(field, 0);
    }

    /**
     * 登录
     */
    public void login() {
        CThostFtdcReqUserLoginField field = new CThostFtdcReqUserLoginField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setUserID(traderConfig.getUserId());
        field.setPassword(traderConfig.getPassword());
        field.setUserProductInfo(traderConfig.getProductInfo());

        traderApi.ReqUserLogin(field, 0);
    }

    /**
     * 查询结算信息确认
     * <p>
     * 响应：OnRspQrySettlementInfoConfirm
     */
    public void qrySettlementInfoConfirm() {
        CThostFtdcQrySettlementInfoConfirmField field = new CThostFtdcQrySettlementInfoConfirmField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        field.setAccountID(traderConfig.getAccountId());
        field.setCurrencyID(traderConfig.getCurrency());

        traderApi.ReqQrySettlementInfoConfirm(field, 0);
    }

    /**
     * 查询投资者结算结果
     * <p>
     * 响应：OnRspQrySettlementInfo
     */
    public void qrySettlementInfo() {
        CThostFtdcQrySettlementInfoField field = new CThostFtdcQrySettlementInfoField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
//        field.setTradingDay(m_TradingDay);
        field.setAccountID(traderConfig.getAccountId());
        field.setCurrencyID(traderConfig.getCurrency());

        traderApi.ReqQrySettlementInfo(field, 0);
    }

    /**
     * 确认结算单
     * <p>
     * 响应：OnRspSettlementInfoConfirm
     */
    public void settlementInfoConfirm() {
        CThostFtdcSettlementInfoConfirmField field = new CThostFtdcSettlementInfoConfirmField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        //field.setConfirmDate("");

        traderApi.ReqSettlementInfoConfirm(field, 0);
    }

    /**
     * 查询资金账户
     * <p>
     * 响应: OnRspQryTradingAccount
     */
    public void qryTradingAccount() {
        CThostFtdcQryTradingAccountField field = new CThostFtdcQryTradingAccountField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        field.setCurrencyID(traderConfig.getCurrency());

        this.assureRate();
        traderApi.ReqQryTradingAccount(field, 0);
    }

    /**
     * 查询合约
     * <p>
     * 响应:OnRspQryInstrument
     *
     * @param exchange
     * @param contract
     */
    public void qryInstrument(String exchange, String contract) {
        CThostFtdcQryInstrumentField field = new CThostFtdcQryInstrumentField();
        field.setExchangeID(exchange);
        field.setInstrumentID(contract);

        this.assureRate();
        traderApi.ReqQryInstrument(field, 0);
    }

    /**
     * 查询合约
     * <p>
     * 响应:OnRspQryInstrument
     *
     * @param exchange
     * @param product  如m, TA
     */
    public void qryProduct(String exchange, String product) {
        CThostFtdcQryInstrumentField field = new CThostFtdcQryInstrumentField();
        field.setExchangeID(exchange);
        field.setProductID(product);

        this.assureRate();
        traderApi.ReqQryInstrument(field, 0);
    }

    /**
     * 报单录入
     * <p>
     * 录入错误时对应响应OnRspOrderInsert、OnErrRtnOrderInsert
     * <p>
     * 正确时对应回报OnRtnOrder、OnRtnTrade成交回报
     * <p>
     * 可以录入限价单、市价单、条件单等交易所支持的指令，撤单时使用ReqOrderAction
     * <p>
     * 不支持预埋单录入，预埋单请使用ReqParkedOrderInsert
     * <p>
     * OrderRef 要填满 TThostFtdcOrderRefType 的全部空间，xxxxxxxxxxxx
     *
     * @param order  订单
     * @param offset 开平标识
     */
    public void reqOrderInsert(Order order, String orderRef, OrderOffsetEnum offset) {
        CThostFtdcInputOrderField field = new CThostFtdcInputOrderField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        field.setUserID(traderConfig.getUserId());

        ExchangeEnum exchangeEnum = order.getExchange();
        field.setExchangeID(exchangeEnum.getExchangeID());
        field.setInstrumentID(order.getContract());
        //报单引用，递增
        field.setOrderRef(orderRef);
        //组合开平标志
        char off = '\0';
        boolean ifbuy = order.getDirection() == DirectionEnum.BUY;
        switch (offset) {
            case OPEN:
                off = thosttradeapiConstants.THOST_FTDC_OF_Open;
                //买卖方向
                field.setDirection(ifbuy ? thosttradeapiConstants.THOST_FTDC_D_Buy : thosttradeapiConstants.THOST_FTDC_D_Sell);
                break;
            case CLOSE:
                //上交所区分平昨和平今
                boolean isShfe = exchangeEnum == ExchangeEnum.SHFE && order.getTradingDay().equals(this.getTradingDay());
                off = isShfe ? thosttradeapiConstants.THOST_FTDC_OF_CloseToday : thosttradeapiConstants.THOST_FTDC_OF_Close;
                field.setDirection(ifbuy ? thosttradeapiConstants.THOST_FTDC_D_Sell : thosttradeapiConstants.THOST_FTDC_D_Buy);
                break;
        }
        field.setCombOffsetFlag(String.valueOf(off));
        //组合投机套保标志，投机THOST_FTDC_ECIDT_Speculation
        field.setCombHedgeFlag(String.valueOf(thosttradeapiConstants.THOST_FTDC_BHF_Speculation));
        //数量
        field.setVolumeTotalOriginal(order.getCount());
        //成交量类型，全部数量THOST_FTDC_VC_CV
        //field.setVolumeCondition(thosttradeapiConstants.THOST_FTDC_VC_CV);
        field.setVolumeCondition(thosttradeapiConstants.THOST_FTDC_VC_AV);
        //最小成交量
        field.setMinVolume(1);
        //触发条件
        field.setContingentCondition(thosttradeapiConstants.THOST_FTDC_CC_Immediately);
        //强平原因
        field.setForceCloseReason(thosttradeapiConstants.THOST_FTDC_FCC_NotForceClose);
        //自动挂起标志
        field.setIsAutoSuspend(0);
        //用户强评标志：否
        field.setUserForceClose(0);

        //根据订单类型设置参数
        switch (order.getOrderType()) {
            case LIMIT:
                //报单价格条件，限价
                field.setOrderPriceType(thosttradeapiConstants.THOST_FTDC_OPT_LimitPrice);
                //价格
                field.setLimitPrice(order.getPrice());
                //当日有效
                field.setTimeCondition(thosttradeapiConstants.THOST_FTDC_TC_GFD);
                //撤销前有效
//                field.setTimeCondition(thosttradeapiConstants.THOST_FTDC_TC_GTC);
                break;
            case ANY:
                //市价
                field.setOrderPriceType(thosttradeapiConstants.THOST_FTDC_OPT_AnyPrice);
                //价格
                field.setLimitPrice(0);
                //立即完成，否则撤销
                //郑商所的组合合约下单，TimeCondition应该用THOST_FTDC_TC_IOC（1）
                field.setTimeCondition(thosttradeapiConstants.THOST_FTDC_TC_IOC);
                break;
            case CONDITION:
                field.setOrderPriceType(thosttradeapiConstants.THOST_FTDC_OPT_LimitPrice);
                //价格
                field.setLimitPrice(order.getPrice());
                //条件标记价，止损止盈价
                field.setStopPrice(order.getStopPrice());
                field.setTimeCondition(thosttradeapiConstants.THOST_FTDC_TC_GFD);
                break;
            case BEST:
                //市价
                field.setOrderPriceType(thosttradeapiConstants.THOST_FTDC_OPT_BestPrice);
                //价格
                field.setLimitPrice(0);
                field.setTimeCondition(thosttradeapiConstants.THOST_FTDC_TC_IOC);
                break;
        }

        this.assureRate();

//        log.info(JsonHelper.toJsonString(field));
        traderApi.ReqOrderInsert(field, 0);
    }

    /**
     * 报单操作
     * <p>
     * 错误响应: OnRspOrderAction，OnErrRtnOrderAction
     * <p>
     * 正确响应：OnRtnOrder委托回报
     *
     * @param exchange
     * @param instrumentId
     */
    public void reqOrderAction(ExchangeEnum exchange, String orderRef, String instrumentId, int orderActionRef) {
        CThostFtdcInputOrderActionField field = new CThostFtdcInputOrderActionField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        field.setUserID(traderConfig.getUserId());
        //前置编号
        field.setFrontID(frontId);
        //会话编号
        field.setSessionID(sessionId);
        //报单引用
        field.setOrderRef(orderRef);
        field.setOrderActionRef(orderActionRef);
        field.setExchangeID(exchange.getExchangeID());
        field.setInstrumentID(instrumentId);
        //只支持删除（撤销），不支持修改
        field.setActionFlag(thosttradeapiConstants.THOST_FTDC_AF_Delete);

        this.assureRate();

//        log.info(JsonHelper.toJsonString(field));
        traderApi.ReqOrderAction(field, 0);
    }

    /**
     * 查询报单(委托)
     * <p>
     * 响应: OnRspQryOrder
     *
     * @param instrumentId
     */
    public void qryOrder(String instrumentId) {
        CThostFtdcQryOrderField field = new CThostFtdcQryOrderField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        if (StringUtils.isNotEmpty(instrumentId)) {
            field.setInstrumentID(instrumentId);
        }

        this.assureRate();
        traderApi.ReqQryOrder(field, 0);
    }

    /**
     * 查询成交
     * <p>
     * 响应: OnRspQryTrade
     */
    public void qryTrade() {
        CThostFtdcQryTradeField field = new CThostFtdcQryTradeField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());

        this.assureRate();
        traderApi.ReqQryTrade(field, 0);
    }

    /**
     * 预埋单录入请求
     * <p>
     * 响应: OnRspParkedOrderInsert
     *
     * @param exchange
     * @param instrumentId
     * @param orderRef
     * @param direction
     * @param orderType
     * @param orderOffsetEnum
     * @param price
     * @param stopPrice
     */
    public void reqParkedOrderInsert(ExchangeEnum exchange, String instrumentId, String orderRef, DirectionEnum direction, OrderTypeEnum orderType, OrderOffsetEnum orderOffsetEnum, double price, double stopPrice) {

        this.assureRate();

    }

    /**
     * 预埋撤单录入请求
     * <p>
     * 响应: OnRspQryParkedOrderAction
     *
     * @param exchange
     * @param frontId
     * @param sessionId
     * @param orderRef
     * @param instrumentId
     */
    public void reqParkedOrderAction(ExchangeEnum exchange, int frontId, int sessionId, String orderRef, String instrumentId) {


        this.assureRate();
    }

    /**
     * 请求查询预埋单
     * <p>
     * 响应: OnRspQryParkedOrder
     *
     * @param instrumentId
     */
    public void reqQryParkedOrder(String instrumentId) {

        this.assureRate();
    }

    /**
     * 请求查询预埋撤单
     * <p>
     * 响应: OnRspQryParkedOrderAction
     */
    public void reqQryParkedOrderAction() {

        this.assureRate();
    }

    /**
     * 查询投资者持仓
     * <p>
     * 响应：OnRspQryInvestorPosition
     * <p>
     * CTP系统将持仓明细记录按合约，持仓方向，开仓日期（仅针对上期所，区分昨仓、今仓）进行汇总。
     *
     * @param instrumentId
     */
    public void qryInvestorPosition(String instrumentId) {
        CThostFtdcQryInvestorPositionField field = new CThostFtdcQryInvestorPositionField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());
        if (StringUtils.isNotEmpty(instrumentId)) {
            field.setInstrumentID(instrumentId);
        }

        this.assureRate();
        traderApi.ReqQryInvestorPosition(field, 0);
    }

    /**
     * 查询投资者持仓明细
     * <p>
     * 响应: OnRspQryInvestorPositionCombineDetail
     */
    public void qryInvestorPositionCombineDetail() {
        CThostFtdcQryInvestorPositionCombineDetailField field = new CThostFtdcQryInvestorPositionCombineDetailField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setInvestorID(traderConfig.getInvestorId());

        this.assureRate();
        traderApi.ReqQryInvestorPositionCombineDetail(field, 0);
    }

    /**
     * 处理持仓明细响应返回的数据
     *
     * @param field
     */
    public void dealInvestorPositionCombineDetail(CThostFtdcInvestorPositionCombineDetailField field) {

    }

    /**
     * 登出/释放
     */
    public void release() {
        if (!online) {
            return;
        }
        CThostFtdcUserLogoutField field = new CThostFtdcUserLogoutField();
        field.setBrokerID(traderConfig.getBrokerId());
        field.setUserID(traderConfig.getUserId());
        traderApi.ReqUserLogout(field, 0);
        traderApi.Release();
        traderApi = null;

        this.setFs(false, 0, 0, null);
    }

    /**
     * 上次请求的时间戳
     */
    private long millis = 0;

    /**
     * 保证请求频率不大于1s一次
     */
    @Synchronized
    private void assureRate() {
        while (System.currentTimeMillis() - millis < 1000) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                log.error("assure error: ", e);
            }
        }

        millis = System.currentTimeMillis();
    }
}
