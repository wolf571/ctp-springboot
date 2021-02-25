package ctp.thosttraderapi.enums;

/**
 * 交易trading状态
 */
public enum TradeStatusEnum {
    INITIAL(0, "已提交"),
    SUCCESS(1, "成功"),
    SUCCESS_PART(2, "部分成功"),
    FAILED(3, "失败"),

    ;

    private int code;
    private String text;

    TradeStatusEnum(int code, String text) {
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
