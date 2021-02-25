package ctp.thosttraderapi.enums;

/**
 * 开平标志
 * <p>
 * 上期所区分昨仓和今仓，平昨仓时，开平标志类型设置为平仓 THOST_FTDC_OF_Close
 * <p>
 * 平今仓时，开平标志类型设置为平今仓 THOST_FTDC_OF_CloseToday
 * <p>
 * 其他交易所不区分昨仓和今仓，开平标志类型统一设置为平仓 THOST_FTDC_OF_Close
 */
public enum OrderOffsetEnum {
    OPEN(0, "开仓"),
    CLOSE(1, "平仓"),
    CLOSE_TODAY(10, "平今"),
    DELETE(1, "撤单"),

    ;

    private int code;
    private String text;

    OrderOffsetEnum(int code, String text) {
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
