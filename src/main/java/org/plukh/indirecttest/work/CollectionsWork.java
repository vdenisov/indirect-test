package org.plukh.indirecttest.work;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionsWork implements Work {
    private List<Integer> intList;
    private List<Integer> copyList;

    private static final int MAX = 25;

    @Override
    public void init() {
        intList = new LinkedList<>();
        copyList = new ArrayList<>();
    }

    @Override
    public void work() {
        for (int i = 0; i < MAX; ++i) {
            intList.add(i);
        }
        copyList.addAll(intList);

        intList.clear();
        copyList.clear();
    }

    @Override
    public void dispose() {

    }
}
