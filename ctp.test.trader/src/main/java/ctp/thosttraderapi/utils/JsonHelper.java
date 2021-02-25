package ctp.thosttraderapi.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duxl
 * @remark Json工具类
 */
public final class JsonHelper {

    private static Logger log = LoggerFactory.getLogger(JsonHelper.class);

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 将bean转换成json
     *
     * @param obj bean对象
     * @return json
     */
    public static String toJsonString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("转换json失败：{}", e.getMessage());
        }

        return null;
    }

    /**
     * 先把bean转换为json字符串，再把json字符串进行base64编码
     *
     * @param obj bean
     * @return
     */
    public static String toBase64String(Object obj) {
        String json = toJsonString(obj);

        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    /**
     * 把json字符串转换为相应的JavaBean对象
     *
     * @param json json数据
     * @param type bean 类型
     * @param <T>  泛型
     * @return bean
     */
    public static <T> T toBean(String json, Class<T> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            log.error("无法转换字符串{}为{}", json, type);
        }

        return null;
    }

    /**
     * 将json转换成指定类型的集合
     *
     * @param json
     * @param elementClasses
     * @param <T>
     * @return List
     */
    public static <T> T toList(String json, Class<?>... elementClasses) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructParametricType(List.class, elementClasses));
        } catch (Exception e) {
            log.error("无法转换字符串{}为List。", json);

            return null;
        }
    }

    /**
     * 将json字符串转换为HashMap
     *
     * @param json json
     * @return hashmap
     */
    public static Map toMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return mapper.readValue(json, HashMap.class);
        } catch (Exception e) {
            log.error("无法转换字符串{}为Map。", json);

            return null;
        }
    }

    /**
     * 获取json中的某个字段值
     *
     * @param json json字符串
     * @return 字段值
     */
    public static String getValue(String json, String name) {
        try {
            Map<String, String> map = mapper.readValue(json, HashMap.class);

            return map.get(name);
        } catch (Exception e) {
            log.error("从{}中获取字段{}失败：{}", json, name, e.getMessage());
        }

        return null;
    }

    /**
     * 获取泛型Collection JavaType
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getJavaType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
