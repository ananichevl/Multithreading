package org.ananichev;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leonid on 11/29/16.
 */
public class ExecutionManagerImpl implements ExecutionManager {
    private PoolWorker[] threads;
    private LinkedList<Runnable> queue = new LinkedList<>();
    private int failed;
    private int completed;
    private int removed;
    private int countTasks;

    public ExecutionManagerImpl(int poolSize) {
        threads = new PoolWorker[poolSize];
        for (int i = 0; i < poolSize; i++){
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        for (Runnable task : tasks) {
            synchronized (queue){
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            task.run();
                        }catch (Exception e){
                            failed++;
                        }
                        completed++;
                    }
                };
                queue.add(r);
                countTasks++;
                queue.notify();
            }
        }
        return new ContextImpl(this);
    }

    public int getFailed() {
        return failed;
    }

    public void interrupt(){
        synchronized (queue){
            while (!queue.isEmpty()){
                queue.removeLast();
                removed++;
            }
        }
    }

    public int getCompleted() {
        return completed;
    }

    public int getRemoved() {
        return removed;
    }

    public boolean isFinished(){
        return countTasks == completed + removed;
    }

    private class PoolWorker extends Thread{
        @Override
        public void run() {
            Runnable r;
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                r = queue.removeFirst();
            }
            r.run();
            r = null;
            synchronized (queue){
                if (!queue.isEmpty()){
                    r = queue.removeFirst();
                }
            }
            if (r != null){
                r.run();
            }

        }
    }
}
