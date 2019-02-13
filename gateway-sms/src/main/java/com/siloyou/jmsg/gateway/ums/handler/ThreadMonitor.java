package com.siloyou.jmsg.gateway.ums.handler;

public class ThreadMonitor {

    private boolean isStop  = false;

    private long    timeout = 8000;

    private Thread  parentThread;

    private Monitor m       = null;

    public ThreadMonitor() {

    }

    public ThreadMonitor(Thread parentThread) {
        this.parentThread = parentThread;
    }

    public void interruptParent() {
        if (this.parentThread != null) {
            try {
                this.parentThread.interrupt();
            } catch (Exception e) {

            }
        }
    }

    public ThreadMonitor(long timeout) {
        this.timeout = timeout;
    }

    public void start() {
        m = new Monitor();
        m.setDaemon(true);
        m.start();
    }

    public void stop() {
        this.isStop = true;
        if (m != null && m.isAlive()) {
            m.interrupt();
        }
    }

    class Monitor extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(timeout);
                if (!isStop) {
                    interruptParent();
                    throw new MethodTimeoutException("method timeout!");
                }
            } catch (InterruptedException e) {

            }

        }
    }

}
