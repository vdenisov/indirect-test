package org.plukh.indirecttest.tests;

import org.plukh.indirecttest.TestException;
import org.plukh.indirecttest.work.Work;

public interface IndirectCalls {
    void init(Work work) throws TestException;
    void run(long workRepetitions) throws TestException;
    void dispose() throws TestException;
}
