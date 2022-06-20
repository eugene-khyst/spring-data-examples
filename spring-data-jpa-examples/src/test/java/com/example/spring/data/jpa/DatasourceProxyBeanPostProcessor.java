package com.example.spring.data.jpa;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (bean instanceof DataSource && !(bean instanceof ProxyDataSource)) {
      final ProxyFactory factory = new ProxyFactory(bean);
      factory.setProxyTargetClass(true);
      factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
      return factory.getProxy();
    }
    return bean;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    return bean;
  }

  private static class ProxyDataSourceInterceptor implements MethodInterceptor {

    private final DataSource dataSource;

    public ProxyDataSourceInterceptor(final DataSource dataSource) {
      this.dataSource =
          ProxyDataSourceBuilder.create(dataSource)
              .name("ProxyDataSource")
              .multiline()
              .logQueryBySlf4j(SLF4JLogLevel.INFO)
              .build();
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
      final Method proxyMethod =
          ReflectionUtils.findMethod(this.dataSource.getClass(), invocation.getMethod().getName());
      if (proxyMethod != null) {
        return proxyMethod.invoke(this.dataSource, invocation.getArguments());
      }
      return invocation.proceed();
    }
  }
}
