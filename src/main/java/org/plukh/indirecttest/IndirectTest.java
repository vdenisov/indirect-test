package org.plukh.indirecttest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IndirectTest {
    private static final Logger log = LogManager.getLogger(IndirectTest.class);

    private static final IndirectTestConfig config = new IndirectTestConfig();
    private static final String CONFIG_FILE_NAME = "indirecttest.properties";

    private static final TestRunner runner = new TestRunner();

    public static void main(String[] args) {
        log.info("IndirectTest starting up...");

        showVersion();

        loadConfig(args.length > 0 ? args[0] : null);

        configureRunner();

        runTests();
    }



    private static void showVersion() {
        Properties mavenProps = new Properties();
        try {
            final InputStream in =
                    IndirectTest.class.getResourceAsStream("/META-INF/maven/org.plukh/indirect-test/pom.properties");
            if (in != null) {
                mavenProps.load(in);
                log.info("Version " + mavenProps.getProperty("version"));
            } else log.warn("Unable to determine version - pom.properties missing or not accessible");
        } catch (IOException e) {
            log.error("Error determining version", e);
        }
    }

    private static void loadConfig(String fileName) {
        if (fileName == null) fileName = CONFIG_FILE_NAME;
        try {
            config.load(fileName);
        } catch (IOException e) {
            log.error("Error loading configuration file " + fileName, e);
            System.exit(1);
        }
        log.info("Configuration loaded from file " + fileName);
    }

    private static void configureRunner() {
        configureTestClasses();
        configureWorkClasses();
        configureRepetitions();
    }

    private static void configureTestClasses() {
        final String testClassesStr = config.getProperty(IndirectTestConfig.TEST_CLASSES);
        if (StringUtils.isEmpty(testClassesStr)) {
            log.error("Test classes not specified in config file, aborting...");
            System.exit(1);
        }

        final String arr[] = testClassesStr.split(",");
        for (String className : arr) {
            try {
                runner.addTestClass(className.trim());
            } catch (ClassNotFoundException e) {
                log.warn("Test class " + className.trim() + " not found, will be skipped during this test run");
            }
        }
    }

    private static void configureWorkClasses() {
        final String workClassesStr = config.getProperty(IndirectTestConfig.WORK_CLASSES);
        if (StringUtils.isEmpty(workClassesStr)) {
            log.error("Work classes not specified in config file, aborting...");
            System.exit(1);
        }

        final String arr[] = workClassesStr.split(",");
        for (String className : arr) {
            try {
                runner.addWorkClass(className.trim());
            } catch (ClassNotFoundException e) {
                log.warn("Work class " + className.trim() + " not found, will be skipped during this test run");
            }
        }
    }

    private static void configureRepetitions() {
        runner.setTestRepetitions(Long.parseLong(config.getProperty(IndirectTestConfig.TEST_REPETITIONS)));
        runner.setWorkRepetitions(Long.parseLong(config.getProperty(IndirectTestConfig.WORK_REPETITIONS)));
        runner.setAverageOver(Integer.parseInt(config.getProperty(IndirectTestConfig.AVERAGE_OVER)));
    }

    private static void runTests() {
        try {
            runner.runTests();
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("Error running tests", e);
        }
    }

}
