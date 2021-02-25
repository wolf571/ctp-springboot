package ctp.thosttraderapi.enums;

/**
 * 订单order状态
 * <p>
 * 只要有部分成交就算成交
 */
public enum OrderStatusEnum {
    INITIAL(0, "初始状态"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成交"),
    FAILED(4, "失败"),
    CANCELED(5, "已撤单"),
    CLOSED(6, "已平仓"),

    ;

    private int code;
    private String text;

    OrderStatusEnum(int code, String text) {
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
