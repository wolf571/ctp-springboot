package ctp.thosttraderapi.enums;

/**
 * 交易方向
 */
public enum DirectionEnum {
    BUY(0, "买入"),
    SELL(1, "卖出");

    private int code;
    private String text;

    DirectionEnum(int code, String text) {
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

    public DirectionEnum reverse() {
        return this == BUY ? SELL : BUY;
    }
}
