package ctp.thostmduserapi.mduser;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "md")
@PropertySource(value = {"classpath:config.properties"})
public class MduserConfig {
    /**
     * 行情订阅地址
     */
    private String address;

    /**
     * 经纪公司代码
     */
    private String brokerId;

    /**
     * 操作员代码
     */
    private String userId;
    /**
     * 口令
     */
    private String password;
    /**
     * 投资者代码
     */
    private String investorId;

    private String accountId;
    /**
     * CNY
     */
    private String currencyId;
}
