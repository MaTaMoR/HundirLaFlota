package me.matamor.hundirlaflota.tasks;

public abstract class CancellableRunnable implements Runnable {

    private TaskExecutor.TaskHolder taskHolder;

    protected void setTaskHolder(TaskExecutor.TaskHolder taskHolder) {
        this.taskHolder = taskHolder;
    }

    public void cancel() {
        if (this.taskHolder != null) {
            this.taskHolder.cancel();
        }
    }
}
