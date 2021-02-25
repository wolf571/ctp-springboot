package ctp.thosttraderapi.enums;

/**
 * 期货交易所
 */
public enum ExchangeEnum {
    SHFE("SHFE", "中国上海期货交易所"),
    DCE("DCE", "大连商品交易所"),
    CZCE("CZCE", "郑州商品交易所"),
    SGE("SGE", "上海黄金交易所"),
    SSE("SSE", "上海证券交易所"),
    SZSE("SZSE", "深圳证券交易所"),
    CFFEX("CFFEX", "中国金融期货交易所"),
    UNKNOWN("UNKNOWN", "未知"),

    ;

    private String exchangeID;
    private String exchangeName;

    ExchangeEnum(String id, String name) {
        this.exchangeID = id;
        this.exchangeName = name;
    }

    public String getExchangeID() {
        return exchangeID;
    }

    public void setExchangeID(String exchangeID) {
        this.exchangeID = exchangeID;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
}
