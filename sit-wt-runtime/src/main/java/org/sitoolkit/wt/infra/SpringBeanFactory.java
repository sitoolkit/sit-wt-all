package org.sitoolkit.wt.infra;

import javax.annotation.Resource;

import org.sitoolkit.util.tabledata.BeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringBeanFactory implements BeanFactory {

    @Resource
    private ApplicationContext appCtx;

    @Override
    public <T> T getBean(String arg0, Class<T> arg1) {
        return appCtx.getBean(arg0, arg1);
    }

}
