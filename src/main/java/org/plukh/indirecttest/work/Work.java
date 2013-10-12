package org.plukh.indirecttest.work;

import org.plukh.indirecttest.TestException;

public interface Work {
    void init() throws TestException;
    void work();
    void dispose();
}
