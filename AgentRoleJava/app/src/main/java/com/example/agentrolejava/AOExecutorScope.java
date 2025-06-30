package com.example.agentrolejava;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AOExecutorScope {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final List<Future<?>> activeTasks = new CopyOnWriteArrayList<>();

    public interface TaskCallback<T> {
        void onResult(T result);
        void onError(Exception e);
    }

    public interface SimpleCallback {
        void onComplete();
        void onError(Exception e);
    }

    /**
     * 执行异步任务
     */
    public static Future<?> execute(Runnable task) {
        Future<?> future = executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        activeTasks.add(future);
        return future;
    }

    /**
     * 执行异步任务并在主线程回调
     */
    public static void executeWithCallback(Runnable task, SimpleCallback callback) {
        execute(() -> {
            try {
                task.run();
                if (callback != null) {
                    mainHandler.post(() -> callback.onComplete());
                }
            } catch (Exception e) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e));
                }
            }
        });
    }

    /**
     * 延迟执行任务
     */
    public static void executeDelayed(Runnable task, long delayMillis) {
        mainHandler.postDelayed(() -> execute(task), delayMillis);
    }

    /**
     * 在主线程执行任务
     */
    public static void executeOnMain(Runnable task) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task.run();
        } else {
            mainHandler.post(task);
        }
    }

    /**
     * 取消所有任务
     */
    public static void cancelAll() {
        for (Future<?> task : activeTasks) {
            task.cancel(true);
        }
        activeTasks.clear();
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        cancelAll();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 