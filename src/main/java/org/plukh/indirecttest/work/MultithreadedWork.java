package org.plukh.indirecttest.work;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultithreadedWork implements Work {
    private static final Logger log = LogManager.getLogger(MultithreadedWork.class);

    private final static int COUNT = 1;

    private BlockingQueue<Integer> workQueue;
    private ConsumerThread consumerThread;
    private BlockingQueue<Boolean> completionQueue;

    private PipedInputStream workIn;
    private PipedOutputStream workOut;
    private PipedInputStream completionIn;
    private PipedOutputStream completionOut;

    private class ConsumerThread extends Thread {
        private volatile boolean shouldStop;

        private BlockingQueue<Integer> workQueue;
        private BlockingQueue<Boolean> completionQueue;

        private PipedInputStream in;
        private PipedOutputStream out;

        private ConsumerThread(PipedInputStream in, PipedOutputStream out, BlockingQueue<Integer> workQueue, BlockingQueue<Boolean> completionQueue) {
            super("ConsumerThread");
            this.in = in;
            this.out = out;
            this.workQueue = workQueue;
            this.completionQueue = completionQueue;
        }

        @Override
        public void run() {
            while (!shouldStop) {
                try {
                    //workQueue.take();
                    //completionQueue.put(true);
                    in.read();
                    out.write(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void terminate() {
            shouldStop = true;
            this.interrupt();
        }
    }

    @Override
    public void init() {
        workQueue = new LinkedBlockingQueue<>();
        completionQueue = new LinkedBlockingQueue<>();

        try {
            workIn = new PipedInputStream();
            workOut = new PipedOutputStream(workIn);

            completionIn = new PipedInputStream();
            completionOut = new PipedOutputStream(completionIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        consumerThread = new ConsumerThread(workIn, completionOut, workQueue, completionQueue);
        consumerThread.start();
    }

    @Override
    public void work() {
        try {
/*
            workQueue.put(1);
            workQueue.take();
            completionQueue.put(true);
            completionQueue.take();
*/
            workOut.write(2);
            completionIn.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        consumerThread.terminate();
    }
}
