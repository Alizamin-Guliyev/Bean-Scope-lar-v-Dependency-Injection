package az.iktex.coreplatform.simasignapi.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    private static final String BASE_PACKAGE = "az.iktex";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isOwnBean(bean)) {
            System.out.println("[BPP] before init: " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (isOwnBean(bean)) {
            System.out.println("[BPP] after init: " + beanName);
        }
        return bean;
    }

    private boolean isOwnBean(Object bean) {
        return bean.getClass().getName().startsWith(BASE_PACKAGE);
    }
}
