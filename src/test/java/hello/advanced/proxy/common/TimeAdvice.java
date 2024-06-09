package hello.advanced.proxy.common;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class TimeAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("Time Proxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = invocation.proceed(); // MethodInvocation에서 Target정보를 다 가지고 있음

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 ResultTime={}", resultTime);

        return result;
    }
}
