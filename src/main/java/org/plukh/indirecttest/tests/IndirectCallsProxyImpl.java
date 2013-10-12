package org.plukh.indirecttest.tests;

import org.plukh.indirecttest.TestException;
import org.plukh.indirecttest.work.Work;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class IndirectCallsProxyImpl implements IndirectCalls {
    private Work work;

    private class WorkProxy implements InvocationHandler {
        private Work proxiedWork;

        private WorkProxy(Work work) {
            proxiedWork = work;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(proxiedWork, args);
        }
    }

    @Override
    public void init(Work work) throws TestException {
        this.work = (Work) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Work.class}, new WorkProxy(work));
    }

    @Override
    public void run(long workRepetitions) throws TestException {
        for (long i = 0; i < workRepetitions; ++i) {
            work.work();
        }
    }

    @Override
    public void dispose() throws TestException {

    }
}
