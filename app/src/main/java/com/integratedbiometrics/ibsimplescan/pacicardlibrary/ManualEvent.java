package com.integratedbiometrics.ibsimplescan.pacicardlibrary;


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class ManualEvent {
    private static final int MAX_WAIT = 1000;
    private static final String TAG = "ManualEvent";
    private final Semaphore semaphore = new Semaphore(1000, false);
    private volatile boolean signaled = false;
    private volatile int count = 0;

    ManualEvent(boolean var1) {
        this.signaled = var1;
        if (!var1) {
            this.semaphore.drainPermits();
        }

    }

    public boolean WaitOne() {
        return this.WaitOne(9223372036854775807L);
    }

    public boolean WaitOne(long var1) {
        boolean var3 = true;
        if (this.signaled) {
            return true;
        } else {
            try {
                ++this.count;
                if (this.count > 1000) {
                }

                var3 = this.semaphore.tryAcquire(var1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException var8) {
                var3 = false;
            } finally {
                --this.count;
            }

            return var3;
        }
    }

    public void Set() {
        this.signaled = true;
        this.semaphore.release(1000);
    }

    public void Reset() {
        this.signaled = false;
        int var1 = this.semaphore.drainPermits();
    }
}
