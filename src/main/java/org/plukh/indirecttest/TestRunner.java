package org.plukh.indirecttest;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plukh.indirecttest.tests.IndirectCalls;
import org.plukh.indirecttest.work.Work;
import org.plukh.util.TimePrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestRunner {
    private static final Logger log = LogManager.getLogger(TestRunner.class);

    private static final SimpleDateFormat FILENAME_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");

    private final List<Class<?>> testClasses;
    private final List<Class<?>> workClasses;
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

                        test.run(workRepetitions);

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

        log.info("Writing test results to the file");

        //Write test results to CSV file
        try (CSVWriter writer = new CSVWriter(getResultsWriter())) {
            final String[] str = new String[] {"Test class", "Work class", "Average execution time"};
            writer.writeNext(str);
            for (TestResult result : results.values()) {
                str[0] = result.getTestClass().getSimpleName();
                for (Class<?> workClass : workClasses) {
                    str[1] = workClass.getSimpleName();
                    str[2] = TimePrinter.printTime(result.getAverage(workClass));
                    writer.writeNext(str);
                }
            }
        } catch (IOException e) {
            log.error("Error writing test results to the file", e);
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

    public void setWorkRepetitions(long workRepetitions) {
        this.workRepetitions = workRepetitions;
        log.debug("Set work repetitions to " + workRepetitions);
    }

    public void setAverageOver(int averageOver) {
        this.averageOver = averageOver;
        log.debug("Results will be averaged over " + averageOver + " runs");
    }

    private Writer getResultsWriter() throws IOException {
        final String jvmVersion = System.getProperty("java.specification.version");
        final String bits = System.getProperty("sun.arch.data.model");
        final String arch = System.getProperty("os.arch");
        final String fileName = "indirect-test-" +
                arch + "-Java" + jvmVersion + "-" + bits + "-" +
                FILENAME_TIMESTAMP_FORMAT.format(new Date()) + ".csv";

        return new BufferedWriter(new FileWriter(fileName));
    }
}
