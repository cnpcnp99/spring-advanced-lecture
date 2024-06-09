package hello.advanced.proxy.advisor;

import hello.advanced.proxy.common.service.ServiceImpl;
import hello.advanced.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

@Slf4j
public class MultiAdvisorTest {

    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        // 원하는 로직: client -> proxy2(advisor2) -> proxy1(advisor1) -> target

        // Proxy 1
        ServiceInterface target = new ServiceImpl();
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();

        // Proxy 2
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        // 실행
        proxy2.save();

        // 문제점: 어드바이저가 10개라면 10개의 프록시 팩토리를 만들어야 함...!
    }

    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");
            return invocation.proceed();
        }
    }


    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");
            return invocation.proceed();
        }
    }

    @Test
    @DisplayName("하나의 프록시, 여러 어드바이저")
    void multiAdvisorTest2() {
        // 원하는 로직: client -> proxy -> advisor2 -> advisor1 -> target

        // Proxy
        ServiceInterface target = new ServiceImpl();
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        ProxyFactory proxyFactory1 = new ProxyFactory(target);

        // 호출하는 순서대로 add
        proxyFactory1.addAdvisor(advisor2);
        proxyFactory1.addAdvisor(advisor1);

        // 실행
        ServiceInterface proxy = (ServiceInterface) proxyFactory1.getProxy();
        proxy.save();
    }
}
