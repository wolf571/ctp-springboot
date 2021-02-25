package ctp.thosttraderapi.enums;

/**
 * 响应代码
 */
public enum RespCodeEnum {
    SUCCESS(0, "成功"),;

    private int code;
    private String text;

    RespCodeEnum(int code, String text) {
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
