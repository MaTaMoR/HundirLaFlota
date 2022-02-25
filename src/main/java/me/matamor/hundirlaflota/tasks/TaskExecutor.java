package me.matamor.hundirlaflota.tasks;

import me.matamor.hundirlaflota.util.Constantes;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TaskExecutor extends Thread {

    public static int tasksIds = 0;

    private final AtomicBoolean active;

    private final List<TaskHolder> toAdd;
    private final List<TaskHolder> tasks;

    public TaskExecutor() {
        this.active = new AtomicBoolean(true);

        this.toAdd = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public boolean isActive() {
        return this.active.get();
    }

    public void stopExecutor() {
        this.active.set(false);
    }

    public synchronized TaskHolder runTask(Runnable runnable, int ticks) {
        return runTask(runnable, ticks, false);
    }

    public synchronized TaskHolder runTask(Runnable runnable, int ticks, boolean repeat) {
        if (ticks < 1) {
            throw new IllegalArgumentException("Ticks can't be less than 1!");
        }

        TaskHolder taskHolder = new TaskHolder(tasksIds++, runnable, ticks, repeat);
        this.toAdd.add(taskHolder);

        if (runnable instanceof CancellableRunnable) {
            ((CancellableRunnable) runnable).setTaskHolder(taskHolder);
        }

        return taskHolder;
    }

    public synchronized boolean cancelTask(int id) {
        //Buscamos las tasks por la ID
        List<TaskHolder> toCancel = this.tasks.stream().filter(e -> e.getId() == id).collect(Collectors.toList());
        toCancel.forEach(TaskHolder::cancel);

        return toCancel.size() > 0;
    }

    public synchronized boolean cancelTask(Runnable runnable) {
        //Buscamos las tasks por la runnable
        List<TaskHolder> toCancel = this.tasks.stream().filter(e -> e.getRunnable() == runnable).collect(Collectors.toList());
        toCancel.forEach(TaskHolder::cancel);

        return toCancel.size() > 0;
    }

    public synchronized void cancelAll() {
        this.tasks.clear();
    }

    private void callTasks() {
        synchronized (this.tasks) {
            synchronized (this.toAdd) {
                for (TaskHolder taskHolder : this.toAdd) {
                    if (taskHolder.getId() != -1) {
                        this.tasks.add(taskHolder);
                    }
                }

                this.toAdd.clear();
            }

            ListIterator<TaskHolder> iterator = this.tasks.listIterator();

            while (iterator.hasNext()) {
                TaskHolder taskHolder = iterator.next();

                //Si el ID es -1 significa que la task ha sido cancelada, es importante hacerlo así para no crear un ConcurrentModificationException
                if (taskHolder.getId() == -1) {
                    iterator.remove();
                } else {
                    //Reducimos los ticks restantes antes de la siguiente ejecución
                    int ticksLeft = taskHolder.getLeftTicks() - 1;

                    //Si no quedan ticks restantes ejecutamos el task
                    if (ticksLeft == 0) {
                        try {
                            taskHolder.getRunnable().run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Si la task es repetible reiniciamos los ticks
                        if (taskHolder.isRepeat()) {
                            taskHolder.setLeftTicks(taskHolder.getTicks());
                        } else {
                            //Si la task no es repetible cancelamos el task
                            iterator.remove();
                        }
                    } else {
                        //Actualizamos los ticks restantes
                        taskHolder.setLeftTicks(ticksLeft);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        long millisecond = TimeUnit.SECONDS.toMillis(1);
        double millisPerTick = millisecond / (double) Constantes.MAX_TICKS;

        //El tiempo de la última ejecución, de forma predeterminada debe ser el tiempo de inicio
        long lastExecution = System.currentTimeMillis();

        //Se ejecutará mientras el juego este activo
        while (this.active.get()) {
            //El tiempo actual
            long now = System.currentTimeMillis();

            //Calculamos las diferencia de tiempo entre la última ejecución
            long difference = now - lastExecution;
            if (difference >= millisPerTick) {
                lastExecution = now;
            }

            //Ejecutamos el juego la cantidad de ticks que caben en la diferencia de tiempo
            while (difference >= millisPerTick) {
                callTasks();

                difference -= millisecond;
            }
        }
    }

    public static class TaskHolder {

        private int id;

        private final Runnable runnable;
        private final int ticks;

        private final boolean repeat;

        private int leftTicks;

        public TaskHolder(int id, Runnable runnable, int ticks, boolean repeat) {
            this.id = id;
            this.runnable = runnable;
            this.ticks = ticks;
            this.repeat = repeat;
            this.leftTicks = ticks;
        }

        public int getId() {
            return this.id;
        }

        public Runnable getRunnable() {
            return this.runnable;
        }

        public int getTicks() {
            return this.ticks;
        }

        public int getLeftTicks() {
            return this.leftTicks;
        }

        public void setLeftTicks(int leftTicks) {
            this.leftTicks = leftTicks;
        }

        public boolean isRepeat() {
            return this.repeat;
        }

        public void cancel() {
            this.id = -1;
        }
    }
}
