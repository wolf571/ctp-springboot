package ctp.thosttraderapi.enums;

/**
 * 开单类型
 * 限价单THOST_FTDC_OPT_LimitPrice：条件单+FAK（Fill and Kill）+FOK（Fill or Kill）
 * 市价单THOST_FTDC_OPT_AnyPrice：没有价格，按现在价格和方向成交
 * 条件单：当合约价格达到StopPrice价格时，按照LimitPrice的价格，限价买入
 */
public enum OrderTypeEnum {
    LIMIT(0, "限价单"),
    ANY(1, "市价单"),
    CONDITION(2, "条件单"),
    BEST(3, "最优价"),
    ;

    private int code;
    private String text;

    OrderTypeEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
