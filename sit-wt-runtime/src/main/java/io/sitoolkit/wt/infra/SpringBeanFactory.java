package io.sitoolkit.wt.infra;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import io.sitoolkit.util.tabledata.BeanFactory;

public class SpringBeanFactory implements BeanFactory {

    @Resource
    private ApplicationContext appCtx;

    @Override
    public <T> T getBean(String arg0, Class<T> arg1) {
        return appCtx.getBean(arg0, arg1);
    }

}
