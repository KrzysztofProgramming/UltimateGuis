package ad.guis.ultimateguis.multithreading;

import ad.guis.ultimateguis.UltimateGuis;
import org.bukkit.Bukkit;

public class Operation<T> {
    private final ReturnFunction<T> returnFunction;
    private final boolean async;
    private SubscribeFunction<T> asyncSubscribeFunction;
    private SubscribeFunction<T> syncSubscribeFunction;
    private boolean finished = false;

    public Operation(ReturnFunction<T> returnFunction) {
        this(returnFunction, true);
    }

    public Operation(ReturnFunction<T> returnFunction, boolean isAsync) {
        this.returnFunction = returnFunction;
        this.async = isAsync;
    }

    public boolean isAsync() {
        return async;
    }

    public synchronized Operation<T> asyncSubscribe(SubscribeFunction<T> function) {
        asyncSubscribeFunction = function;
        return this;
    }

    public synchronized Operation<T> syncSubscribe(SubscribeFunction<T> function) {
        syncSubscribeFunction = function;
        return this;
    }

    public boolean isFinished() {
        return finished;
    }

    public void run() {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(UltimateGuis.getInstance(), this::onRun);
        else Bukkit.getScheduler().runTask(UltimateGuis.getInstance(), this::onRun);
    }

    private void onRun() {
        T value = this.returnFunction.run();
        finished = true;
        if (async) {
            if (syncSubscribeFunction != null) Bukkit.getScheduler().runTask(UltimateGuis.getInstance(),
                    () -> syncSubscribeFunction.subscribe(value));
            if (asyncSubscribeFunction != null) asyncSubscribeFunction.subscribe(value);
        } else {
            if (asyncSubscribeFunction != null) Bukkit.getScheduler().runTaskAsynchronously(UltimateGuis.getInstance(),
                    () -> asyncSubscribeFunction.subscribe(value));
            if (syncSubscribeFunction != null) syncSubscribeFunction.subscribe(value);
        }
    }
}
