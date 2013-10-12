package org.plukh.indirecttest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plukh.indirecttest.tests.IndirectCalls;
import org.plukh.indirecttest.work.Work;
import org.plukh.util.TimePrinter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestRunner {
    private static final Logger log = LogManager.getLogger(TestRunner.class);

    private final List<Class<?>> testClasses;
    private final List<Class<?>> workClasses;
    private long testRepetitions;
    private long workRepetitions;
    private int averageOver;

    private Map<Class<?>, TestResult> results;

    public TestRunner() {
        testClasses = new LinkedList<>();
        workClasses = new LinkedList<>();
        results = new LinkedHashMap<>();
    }

    public void runTests() throws IllegalAccessException, InstantiationException {

        try {
            //Repeat all tests for a set amount of time
            for (int run = 0; run < averageOver; ++run) {
                //Iterate over test classes
                for (Class<?> testClass : testClasses) {
                    final IndirectCalls test = (IndirectCalls) testClass.newInstance();

                    if (log.isDebugEnabled()) log.debug("Instantiated test class: " + test.getClass().getSimpleName());

                    for (Class<?> workClass : workClasses) {
                        final Work work = (Work) workClass.newInstance();

                        if (log.isDebugEnabled()) log.debug("Instantiated work class: " + work.getClass().getSimpleName());

                        work.init();
                        test.init(work);

                        final long start = System.currentTimeMillis();

                        for (long i = 0; i < testRepetitions; ++i) {
                            test.run(workRepetitions);
                        }

                        final long end = System.currentTimeMillis();

                        final long runTime = end - start;
                        log.info("Test class " + testClass.getSimpleName() + ", work class " + workClass.getSimpleName() +
                                ", run took " + TimePrinter.printTime(runTime));

                        //Store results
                        final TestResult result = results.get(testClass);
                        result.addRunResult(workClass, runTime);

                        work.dispose();
                        test.dispose();
                    }
                }
            }
        } catch (TestException e) {
            log.error("Error running tests", e);
            System.exit(1);
        }

        //Log results
        for (TestResult result : results.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Averages for class ").append(result.getTestClass().getSimpleName());
            for (Class<?> workClass : workClasses) {
                sb.append("\n").append(workClass.getSimpleName()).append(": ")
                        .append(TimePrinter.printTime(result.getAverage(workClass)));
            }
            log.info(sb.toString());
        }
    }

    public void addTestClass(String className) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("org.plukh.indirecttest.tests." + className);
        if (IndirectCalls.class.isAssignableFrom(clazz)) {
            testClasses.add(clazz);
        } else {
            throw new IllegalArgumentException("Test classes must implement IndirectCalls interface, but "
                    + clazz.getName() + " doesn't");
        }

        results.put(clazz, new TestResult(clazz));

        log.debug("Added test class: " + clazz.getName());
    }

    public void addWorkClass(String className) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("org.plukh.indirecttest.work." + className);
        if (Work.class.isAssignableFrom(clazz)) {
            workClasses.add(clazz);
        } else {
            throw new IllegalArgumentException("Work classes must implement Work interface, but "
                    + clazz.getName() + " doesn't");
        }

        log.debug("Added work class: " + clazz.getName());
    }

    public void setTestRepetitions(long testRepetitions) {
        this.testRepetitions = testRepetitions;
        log.debug("Set test repetitions to " + testRepetitions);
    }

    public void setWorkRepetitions(long workRepetitions) {
        this.workRepetitions = workRepetitions;
        log.debug("Set work repetitions to " + workRepetitions);
    }

    public void setAverageOver(int averageOver) {
        this.averageOver = averageOver;
        log.debug("Results will be averaged over " + averageOver + " runs");
    }
}
