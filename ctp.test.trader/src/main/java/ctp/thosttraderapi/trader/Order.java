package ctp.thosttraderapi.trader;

import ctp.thosttraderapi.enums.DirectionEnum;
import ctp.thosttraderapi.enums.ExchangeEnum;
import ctp.thosttraderapi.enums.OrderStatusEnum;
import ctp.thosttraderapi.enums.OrderTypeEnum;
import lombok.Data;

/**
 * 交易订单
 */
@Data
public class Order {
    /**
     * 交易编号
     */
    private long id;
    /**
     * 交易所
     */
    private ExchangeEnum exchange;
    /**
     * 合约
     */
    private String contract;
    /**
     * 订单类型
     */
    private OrderTypeEnum orderType;
    /**
     * 方向，0买1卖
     * <p>
     * DirectionEnum
     */
    private DirectionEnum direction;
    /**
     * 委托价格
     */
    private double price;
    /**
     * 条件标记价，止损止盈价
     */
    private double stopPrice;
    /**
     * 手数，下单时填写的数量
     */
    private int count;
    /**
     * 成交数
     */
    private int turnover;
    /**
     * 撤单数
     */
    private int revoke;
    /**
     * 平仓数
     */
    private int closeout;
    /**
     * 状态
     */
    private OrderStatusEnum status;
    /**
     * 交易日
     */
    private String tradingDay;
    /**
     * 下单时间
     */
    private long transTime;
    /**
     * 最后操作时间
     */
    private long updateTime;
    /**
     * 备注
     */
    private String remark;
}
