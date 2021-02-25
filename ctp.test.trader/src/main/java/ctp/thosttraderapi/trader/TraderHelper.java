package ctp.thosttraderapi.trader;

import ctp.thosttraderapi.*;
import ctp.thosttraderapi.enums.*;
import ctp.thosttraderapi.utils.IdGenerator;
import ctp.thosttraderapi.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * 交易帮助类
 */
@Slf4j
@Component
public class TraderHelper {

    @Autowired
    TraderService traderService;

    /**
     * 获取orderRef
     *
     * @return
     */
    public String getOrderRef() {
        return "T" + LocalTime.now().toSecondOfDay();
    }

    /**
     * 查询产品合约信息
     */
    public void qryProduct() {
        traderService.qryProduct("DCE", "m");
    }

    /**
     * 处理查询合约返回信息
     * 将合约编号发送到消息
     *
     * @param field
     */
    public void dealQryInstrument(CThostFtdcInstrumentField field) {
        //合约代码
        String contract = field.getInstrumentID();
        //多头保证金率
        double longMarginRatio = field.getLongMarginRatio();
        //空头保证金率
        double shortMarginRatio = field.getShortMarginRatio();


        String content = JsonHelper.toJsonString(field);
        log.info("产品： {}", content);
    }

    public void insert(ExchangeEnum exchangeEnum, String contract, OrderTypeEnum orderTypeEnum, DirectionEnum directionEnum, OrderOffsetEnum orderOffsetEnum, double price, double stopPrice, int count) {
        log.info("order insert：contract {}, type {}, direction {}, offset {}, price {}, stopPrice {}, count {}", contract, orderTypeEnum, directionEnum, orderOffsetEnum, price, stopPrice, count);

        long id = IdGenerator.generate();
        String orderRef = getOrderRef();
//        int requestId = RandomUtils.nextInt();

        Order order = new Order();
        order.setId(id);
        order.setExchange(exchangeEnum);
        order.setContract(contract);
        order.setOrderType(orderTypeEnum);
        order.setDirection(directionEnum);
        order.setPrice(price);
        order.setStopPrice(stopPrice);
        order.setCount(count);
        order.setTradingDay("20191203");
        order.setTransTime(System.currentTimeMillis());
        order.setStatus(OrderStatusEnum.INITIAL);

        traderService.reqOrderInsert(order, orderRef, OrderOffsetEnum.OPEN);
    }


    /**
     * 处理资金账户返回信息
     * <p>
     * ///期货结算准备金
     * TThostFtdcMoneyType	Balance;
     * ///可用资金
     * TThostFtdcMoneyType	Available;
     * ///可取资金
     * TThostFtdcMoneyType	WithdrawQuota;
     * ///信用额度
     * TThostFtdcMoneyType	Credit;
     *
     * @param field
     */
    public void dealTradingAccount(CThostFtdcTradingAccountField field) {
        double balance = field.getBalance();
//        double available = field.getAvailable();
        double getWithdrawQuota = field.getBalance();
        double credit = field.getCredit();
    }

    /**
     * 平仓
     *
     * @param orderList
     */
    private void close(List<Order> orderList) {
        orderList.forEach(order -> {
            this.close(order);
        });
    }

    /**
     * 平仓
     *
     * @param order
     */
    public void close(Order order) {
        this.close(order, 0);
    }

    /**
     * 市价平仓
     *
     * @param order
     * @param price
     */
    private void close(Order order, double price) {
        traderService.reqOrderInsert(order, getOrderRef(), OrderOffsetEnum.CLOSE);
    }

    /**
     * 撤单
     *
     * @param order
     */
    private void cancel(Order order) {
        String orderRef = this.getOrderRef();

        traderService.reqOrderAction(order.getExchange(), orderRef, order.getContract(), 1);
    }

    /**
     * 处理持仓响应返回的数据
     *
     * @param field
     */
    public void dealInvestorPosition(CThostFtdcInvestorPositionField field) {
        log.info(JsonHelper.toJsonString(field));
    }

    /**
     * 报单回调更新交易单/订单状态
     *
     * @param field
     */
    public void updateOrder(CThostFtdcOrderField field) {
//        int frontId = field.getFrontID();
//        long sessionId = field.getSessionID();
        String orderSysId = field.getOrderSysID();
        String orderRef = field.getOrderRef();
        char orderSubmitStatus = field.getOrderSubmitStatus();
        char orderStatus = field.getOrderStatus();
        //今成交数量
        int count = field.getVolumeTraded();
        //剩余数量
        int left = field.getVolumeTotal();
        String remark = field.getStatusMsg();
        log.info("{}", JsonHelper.toJsonString(field));
    }

    /**
     * 报单错误
     *
     * @param orderRef
     * @param message
     */
    public void updateOrderErr(String orderRef, String message) {
//        trading.setRemark(message);
//        tradingService.upsert(trading);
//
//        orderService.update(trading.getOrderId(), OrderStatusEnum.FAILED);
        log.info("{}, {}", orderRef, message);
    }

    /**
     * 成交回调更新订单状态
     *
     * @param field
     */
    public void updateTrade(CThostFtdcTradeField field) {
        String orderSysId = field.getOrderSysID();
        String orderRef = field.getOrderRef();
        double price = field.getPrice();
        int count = field.getVolume();

        log.info("orderRef: {}, tradeDate: {}, tradeTime: {}", orderRef, field.getTradeDate(), field.getTradeTime());
    }
}
