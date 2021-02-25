package ctp.thostmduserapi.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author duxl
 * @remark
 */
@Component
public class ApplicationContextHandler implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * spring bean 工厂获取指定名字的bean
     *
     * @param name
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name) {
        Object bean = context.getBean(name);
        if (bean == null) {
            return null;
        }

        return (T) bean;
    }

    /**
     * 根据类型获取所有bean
     *
     * @param clazz
     * @return
     */
    public static Map getBeansByType(Class clazz) {
        Map beans = context.getBeansOfType(clazz);

        return beans;
    }

}
