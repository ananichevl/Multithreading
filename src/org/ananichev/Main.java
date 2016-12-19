package org.ananichev;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonid on 12/19/16.
 */
public class Main {
    public static void main(String[] args) {
        ExecutionManager manager = new ExecutionManagerImpl(4);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println("finished");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable[] tasks = new Runnable[8];
        for(int i = 0; i < 8; i++){
            tasks[i] = runnable;
        }
        Context c = manager.execute(runnable, tasks);
        System.out.println("Completed = " + c.getCompletedTaskCount());
        try {
            Thread.sleep(1000);
            c.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed = " + c.getCompletedTaskCount());
        System.out.println("Interrupted = " + c.getInterruptedTaskCount());
        System.out.println("Failed = " + c.getFailedTaskCount());
        try {
            Thread.sleep(2000);
            System.out.println("Completed = " + c.getCompletedTaskCount());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
