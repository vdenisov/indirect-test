package org.plukh.indirecttest;

import java.util.HashMap;
import java.util.Map;

public class TestResult {
    private Class<?> testClass;
    private Map<Class<?>, WorkResult> workResults = new HashMap<>();

    private class WorkResult {
        private long time;
        private int count;

        private long getTime() {
            return time;
        }

        private void setTime(long time) {
            this.time = time;
        }

        private int getCount() {
            return count;
        }

        private void setCount(int count) {
            this.count = count;
        }
    }

    public TestResult(Class<?> testClass) {
        this.testClass = testClass;
    }

    public void addRunResult(Class<?> clazz, long runTime) {
        WorkResult result = workResults.get(clazz);

        if (result == null) {
            result = new WorkResult();
            workResults.put(clazz, result);
        }

        result.setTime(result.getTime() + runTime);
        result.setCount(result.getCount() + 1);
    }

    public long getAverage(Class<?> clazz) {
        final WorkResult result = workResults.get(clazz);
        return result.getTime() / result.getCount();
    }

    public Class<?> getTestClass() {
        return testClass;
    }
}
