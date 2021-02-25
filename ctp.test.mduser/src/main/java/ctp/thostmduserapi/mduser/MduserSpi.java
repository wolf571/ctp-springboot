package ctp.thostmduserapi.mduser;

import ctp.thostmduserapi.CThostFtdcDepthMarketDataField;
import ctp.thostmduserapi.CThostFtdcMdSpi;
import ctp.thostmduserapi.CThostFtdcRspInfoField;
import ctp.thostmduserapi.CThostFtdcRspUserLoginField;
import ctp.thostmduserapi.enums.RespCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 订阅合约号修改这里，如果运行成功没收到行情，参见如下解决
 * https://blog.csdn.net/pjjing/article/details/100532276
 */
@Slf4j
@Component
public class MduserSpi extends CThostFtdcMdSpi {

    @Lazy
    @Autowired
    MduserService mduserService;

    public void OnFrontConnected() {
        log.info("OnFrontConnected ...");

        mduserService.login();
    }

    public void OnRspUserLogin(CThostFtdcRspUserLoginField pRspUserLogin, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("OnRspUserLogin ...");

        if (pRspInfo != null && pRspInfo.getErrorID() != RespCodeEnum.SUCCESS.getCode()) {
            log.error("Error: ", pRspInfo.getErrorMsg());
            return;
        }

        mduserService.setOnline(true);

        mduserService.subscribe();
    }

    public void OnRtnDepthMarketData(CThostFtdcDepthMarketDataField pDepthMarketData) {
//        log.info("OnRtnDepthMarketData ...");

        if (pDepthMarketData == null) {
            log.error("null md received.");
            return;
        }

        mduserService.publish(pDepthMarketData);
    }

    public void OnRspError(CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("OnRspError ...");
        log.error("{}，{}，requestId: {}", pRspInfo.getErrorID(), pRspInfo.getErrorMsg(), nRequestID);
    }

    public void OnFrontDisconnected(int nReason) {
        log.warn("OnFrontDisconnected ... {}", nReason);
    }
}
