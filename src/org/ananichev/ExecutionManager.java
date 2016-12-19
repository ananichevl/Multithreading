package org.ananichev;

/**
 * Created by leonid on 11/29/16.
 */
public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}
