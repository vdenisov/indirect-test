package org.plukh.indirecttest.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plukh.indirecttest.TestException;
import org.plukh.indirecttest.work.Work;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IndirectCallsReflectionImpl implements IndirectCalls {
    private static final Logger log = LogManager.getLogger(IndirectCallsReflectionImpl.class);

    private Method workMethod;
    private Work work;

    @Override
    public void init(Work work) throws TestException {
        this.work = work;
        try {
            workMethod = work.getClass().getMethod("work");
        } catch (NoSuchMethodException e) {
            throw new TestException("Error finding work method via reflection", e);
        }
    }

    @Override
    public void run(long workRepetitions) throws TestException {
        for (long i = 0; i < workRepetitions; ++i) {
            try {
                workMethod.invoke(work);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new TestException("Error executing work method via reflection", e);
            }
        }
    }

    @Override
    public void dispose() throws TestException {

    }
}
