package org.ananichev;

/**
 * Created by leonid on 12/19/16.
 */
public class ContextImpl implements Context {

    private final ExecutionManagerImpl manager;

    public ContextImpl(ExecutionManagerImpl manager) {
        this.manager = manager;
    }


    @Override
    public int getCompletedTaskCount() {
        return manager.getCompleted();
    }

    @Override
    public int getFailedTaskCount() {
        return manager.getFailed();
    }

    @Override
    public int getInterruptedTaskCount() {
        return manager.getRemoved();
    }

    @Override
    public void interrupt() {
        manager.interrupt();
    }

    @Override
    public boolean isFinished() {
        return manager.isFinished();
    }
}
