package me.matamor.hundirlaflota.tasks;

public class CountdownTask extends CancellableRunnable {

    private final int maxTicks;
    private final Runnable callback;

    private int ticks;

    public CountdownTask(int maxTicks, Runnable callback) {
        this.maxTicks = maxTicks;
        this.callback = callback;

        this.ticks = 0;
    }

    @Override
    public void run() {
        this.ticks = this.ticks + 1;

        if (this.ticks == this.maxTicks) {
            cancel();

            this.callback.run();
        }
    }
}
