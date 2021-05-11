package com.th.workbase.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class InitBeanConfig implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        InitBeanConfig.applicationContext = applicationContext;

    }

    /**
     * 根据class取得组件实例
     *
     * @param clazz 组件类型
     * @return Spring组件实例
     * @author reachauto haojr
     */
    public static <T> T getComponent(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据class取得组件实例
     *
     * @param beanName 组件名称
     * @return Spring组件实例
     * @author reachauto haojr
     */
    public static Object getComponent(String beanName) {
        return applicationContext.getBean(beanName);
    }

}
