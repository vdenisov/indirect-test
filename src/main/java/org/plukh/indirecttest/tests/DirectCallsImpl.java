package org.plukh.indirecttest.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plukh.indirecttest.TestException;
import org.plukh.indirecttest.work.Work;

public class DirectCallsImpl implements IndirectCalls {
    private static final Logger log = LogManager.getLogger(DirectCallsImpl.class);

    private Work work;

    public DirectCallsImpl() {
    }

    @Override
    public void init(Work work) throws TestException {
        this.work = work;
    }

    @Override
    public void run(long workRepetitions) throws TestException {
        //if (log.isDebugEnabled()) log.debug("Running work: " + work.getClass().getSimpleName());

        for (long i = 0; i < workRepetitions; ++i) {
            work.work();
        }
    }

    @Override
    public void dispose() throws TestException {

    }
}
