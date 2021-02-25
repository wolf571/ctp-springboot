package ctp.thosttraderapi.trader;

import ctp.thosttraderapi.enums.RespCodeEnum;
import ctp.thosttraderapi.utils.JsonHelper;
import ctp.thosttraderapi.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TraderSpi extends CThostFtdcTraderSpi {

    @Lazy
    @Autowired
    TraderService traderService;

    @Lazy
    @Autowired
    TraderHelper traderHelper;

    @Override
    public void OnFrontConnected() {
        log.info("连接建立：Front Connected ...");

        traderService.authenticate();
    }

    @Override
    public void OnRspAuthenticate(CThostFtdcRspAuthenticateField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("认证响应：OnRspAuthenticate ...");
        if (!asserts(pRspInfo)) {
            return;
        }

        log.info("success.");

        traderService.login();
    }

    @Override
    public void OnRspUserLogin(CThostFtdcRspUserLoginField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("登录响应：OnRspUserLogin ...");
        if (!asserts(pRspInfo)) {
            return;
        }

        log.info("success.");
        log.info("tradingDay...{}", field.getTradingDay());
        //可以从field 中获得上次登入用过的最大 OrderRef, MaxOrderRef
        //记下当前会话的 FrontID、SessionID
        traderService.setFs(true, field.getFrontID(), field.getSessionID(), field.getTradingDay());
        traderService.qrySettlementInfoConfirm();
    }

    @Override
    public void OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("查询结算信息确认响应：OnRspQrySettlementInfoConfirm ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        //若没有确认过结算单，则进行查询并确认
        if (bIsLast) {
            traderService.qrySettlementInfo();
        }
    }

    @Override
    public void OnRspQrySettlementInfo(CThostFtdcSettlementInfoField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
//        log.info("查询投资者结算结果响应：OnRspQrySettlementInfo ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        if (field != null && StringUtils.isEmpty(field.getContent())) {
            System.out.print(field.getContent());
        }
        if (bIsLast) {
            traderService.settlementInfoConfirm();
        }
    }

    @Override
    public void OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("投资者结算结果确认：OnRspSettlementInfoConfirm ...");
    }

    @Override
    public void OnRspQryTradingAccount(CThostFtdcTradingAccountField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("查询资金账户响应：OnRspQryTradingAccount ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        if (field == null) {
            return;
        }

        traderHelper.dealTradingAccount(field);
    }

    @Override
    public void OnRspQryInstrument(CThostFtdcInstrumentField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
//        log.info("查询合约信息响应：OnRspQryInstrument ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        if (field == null) {
            return;
        }

        traderHelper.dealQryInstrument(field);
    }

    @Override
    public void OnRtnOrder(CThostFtdcOrderField field) {
        log.info("委托响应：OnRtnOrder ...");
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));

        traderHelper.updateOrder(field);
    }

    @Override
    public void OnRtnTrade(CThostFtdcTradeField field) {
        log.info("成交响应：OnRtnTrade ...");
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));

        traderHelper.updateTrade(field);
    }

    @Override
    public void OnRspOrderInsert(CThostFtdcInputOrderField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("报单录入请求响应：OnRspOrderInsert ...");
        asserts(pRspInfo);
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));

        traderHelper.updateOrderErr(field.getOrderRef(), pRspInfo.getErrorMsg());
    }

    @Override
    public void OnErrRtnOrderInsert(CThostFtdcInputOrderField field, CThostFtdcRspInfoField pRspInfo) {
        log.info("报单录入请求响应：OnErrRtnOrderInsert ...");
        asserts(pRspInfo);
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));

        traderHelper.updateOrderErr(field.getOrderRef(), pRspInfo.getErrorMsg());
    }

    @Override
    public void OnRspOrderAction(CThostFtdcInputOrderActionField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("报单操作请求响应：OnRspOrderAction ...");
        asserts(pRspInfo);
        if (field == null) {
            return;
        }
//        log.info(JsonHelper.toJsonString(field));

        traderHelper.updateOrderErr(field.getOrderRef(), pRspInfo.getErrorMsg());
    }

    @Override
    public void OnErrRtnOrderAction(CThostFtdcOrderActionField field, CThostFtdcRspInfoField pRspInfo) {
        log.info("报单操作错误回报：OnErrRtnOrderAction ...");
        asserts(pRspInfo);
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));

        traderHelper.updateOrderErr(field.getOrderRef(), pRspInfo.getErrorMsg());
    }

    @Override
    public void OnRspQryOrder(CThostFtdcOrderField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("查询报单响应：OnRspQryOrder ...");
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));
    }

    @Override
    public void OnRspQryTrade(CThostFtdcTradeField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("查询成交响应：OnRspQryTrade ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        if (field == null) {
            return;
        }
        log.info(JsonHelper.toJsonString(field));
    }

    @Override
    public void OnRspQryInvestorPosition(CThostFtdcInvestorPositionField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("查询投资者持仓响应：OnRspQryInvestorPosition ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        if (field == null) {
            return;
        }

        traderHelper.dealInvestorPosition(field);
    }

    @Override
    public void OnRspQryInvestorPositionCombineDetail(CThostFtdcInvestorPositionCombineDetailField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("查询投资者持仓明细响应：OnRspQryInvestorPositionCombineDetail ...");
        if (!asserts(pRspInfo)) {
            return;
        }
        if (field == null) {
            return;
        }

        log.info(JsonHelper.toJsonString(field));
        traderService.dealInvestorPositionCombineDetail(field);
    }

    @Override
    public void OnHeartBeatWarning(int nTimeLapse) {
        log.info("心跳警告：OnHeartBeatWarning ...");
        log.warn("{}", nTimeLapse);
    }

    @Override
    public void OnRspError(CThostFtdcRspInfoField field, int nRequestID, boolean bIsLast) {
        asserts(field);
    }

    @Override
    public void OnRspUserLogout(CThostFtdcUserLogoutField field, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        log.info("登出响应：OnRspUserLogout ...");
        if (!asserts(pRspInfo)) {
            return;
        }

        log.info("down.");
    }

    @Override
    public void OnFrontDisconnected(int nReason) {
        log.info("断开连接：OnFrontDisconnected ... {} ", nReason);
    }

    /**
     * 判断返回值
     * <p>
     * 若有错误，输出错误信息与返回false
     *
     * @param pRspInfo
     * @return
     */
    private boolean asserts(CThostFtdcRspInfoField pRspInfo) {
        if (pRspInfo == null || pRspInfo.getErrorID() == RespCodeEnum.SUCCESS.getCode()) {
            return true;
        }

        log.error("Error: {}, {}", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());

        return false;
    }
}