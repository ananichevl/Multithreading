package org.ananichev;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leonid on 11/29/16.
 */
public class ExecutionManagerImpl implements ExecutionManager {
    private List<PoolWorker> threads;
    private LinkedList<Runnable> queue = new LinkedList<>();
    private int failed;
    private int completed;
    private int removed;
    private int countTasks;

    public ExecutionManagerImpl(int poolSize) {
        threads = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++){
            threads.add(new PoolWorker());
            threads.get(i).start();
        }
    }

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(threads.size() > tasks.length) {
                    int extra = threads.size() - tasks.length;
                    for(int i = 0; i < extra; i++){
                        threads.get(i).stop();
                    }
                }
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
                for (Thread t : threads){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                callback.run();
            }
        });
        t.start();
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
            while (true){
                r = null;
                synchronized (queue) {
                    while(queue.isEmpty()){
                        stop();
                    }
                    r = queue.removeFirst();
                }
                r.run();
            }
        }
    }
}
