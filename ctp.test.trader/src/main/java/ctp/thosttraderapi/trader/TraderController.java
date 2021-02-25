package ctp.thosttraderapi.trader;

import ctp.thosttraderapi.enums.DirectionEnum;
import ctp.thosttraderapi.enums.ExchangeEnum;
import ctp.thosttraderapi.enums.OrderOffsetEnum;
import ctp.thosttraderapi.enums.OrderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TraderController {

    @Autowired
    TraderService traderService;

    @Autowired
    TraderHelper traderHelper;

    @GetMapping("/javalib")
    public String hello() {
        return System.getProperty("java.library.path");
    }

    @GetMapping("/buy")
    public void buy() {
//        traderHelper.insert(ExchangeEnum.DCE, "m2005", OrderTypeEnum.LIMIT, DirectionEnum.BUY,
//                OrderOffsetEnum.OPEN, 2788, 2700, 1);
    }

    @GetMapping("/query")
    public void query() {
//        traderHelper.qryProduct();
//        traderService.qryOrder("m2001");
//        traderService.qryTrade();
        traderService.qryInvestorPosition(null);
//        traderService.qryInvestorPositionCombineDetail();
    }

    @GetMapping("/close")
    public void close() {
        Order order = new Order();
        order.setExchange(ExchangeEnum.DCE);
        order.setContract("m2005");
        order.setDirection(DirectionEnum.BUY);
        order.setTradingDay("20200211");
        order.setCount(33);
        order.setPrice(2651);
        order.setOrderType(OrderTypeEnum.LIMIT);

        String orderRef = "100000000001";
        log.info("orderRef: {}", orderRef);

        traderService.reqOrderInsert(order, orderRef, OrderOffsetEnum.CLOSE);
    }

    @GetMapping("/cancel")
    public void cancel() {
//        String orderRef = traderHelper.getOrderRef();

        traderService.reqOrderAction(ExchangeEnum.DCE, "T53955", "m2005", 1);
    }
}
