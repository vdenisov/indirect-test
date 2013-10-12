package org.plukh.indirecttest.work;

public class NullWork implements Work {
    private int i;

    @Override
    public void init() {

    }

    @Override
    public void work() {
        ++i;
    }

    @Override
    public void dispose() {

    }
}
