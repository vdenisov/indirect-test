package org.plukh.indirecttest.work;

import org.plukh.indirecttest.TestException;

public class StringWork implements Work {
    @Override
    public void init() throws TestException {

    }

    @Override
    public void work() {
        final String s = "The quick brown fox";
        final String[] split = s.split(" ");
        final boolean[] lowercase = new boolean[split.length];

        int i = 0;
        for (String word : split) {
            lowercase[i++] = word.toLowerCase().equals(word);
        }
    }

    @Override
    public void dispose() {

    }
}
