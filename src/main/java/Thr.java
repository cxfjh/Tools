
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Thr {

    // Logger 用于记录日志
    private static final Logger logger = Logger.getLogger(Thr.class.getName());


    /**
     * 创建一个固定大小的线程池
     * 
     * @param size 线程池大小
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService create(final int size) { return Executors.newFixedThreadPool(size);};


    /**
     * 执行用户传入的Runnable任务
     * 
     * @param executor 执行任务的线程池
     * @param task     需要执行的Runnable任务
     * @return boolean 执行是否成功，成功返回true，失败返回false
     */
    public static boolean run(final ExecutorService executor, final Runnable task) { return runTaskInternal(executor, task); };


    /**
     * 执行用户传入的Callable任务
     * 
     * @param executor 执行任务的线程池
     * @param task     需要执行的Callable任务
     * @return boolean 执行是否成功，成功返回true，失败返回false
     */
    public static boolean run(final ExecutorService executor, final Callable<Void> task) { return runTaskInternal(executor, task); };


    /**
     * 内部方法，用于执行Runnable任务并返回是否成功
     * 
     * @param executor 执行任务的线程池
     * @param task     需要执行的Runnable任务
     * @return boolean 执行是否成功，成功返回true，失败返回false
     */
    private static boolean runTaskInternal(final ExecutorService executor, final Runnable task) {
        try { executor.submit(task).get();  return true; } 
        catch (InterruptedException | ExecutionException e) { handleException(e);  return false; }
    };


    /**
     * 内部方法，用于执行Callable任务并返回是否成功
     * 
     * @param executor 执行任务的线程池
     * @param task     需要执行的Callable任务
     * @return boolean 执行是否成功，成功返回true，失败返回false
     */
    private static boolean runTaskInternal(final ExecutorService executor, final Callable<Void> task) {
        try { executor.submit(task).get(); return true; } 
        catch (InterruptedException | ExecutionException e) { handleException(e); return false; }
    };


    /**
     * 提交多个任务并返回所有任务的执行结果
     * 
     * @param executor 执行任务的线程池
     * @param tasks    多个Callable任务
     * @return boolean[] 每个任务执行的结果数组，成功为true，失败为false
     */
    public static boolean[] runAll(final ExecutorService executor, final Callable<Void>[] tasks) {
        final boolean[] results = new boolean[tasks.length];
        final List<Callable<Void>> taskList = new ArrayList<>();
        for (final Callable<Void> task : tasks) { taskList.add(task); };

        try {
            final List<Future<Void>> futures = executor.invokeAll(taskList);
            for (int i = 0; i < futures.size(); i++) {
                try { futures.get(i).get();  results[i] = true; } 
                catch (InterruptedException | ExecutionException e) { handleException(e); results[i] = false; }
            };
        } catch (final InterruptedException e) {
            handleException(e);
            for (int i = 0; i < results.length; i++) { results[i] = false; };
        };
        return results;
    };


    /**
     * 并发执行多个Runnable任务
     * 
     * @param executor 执行任务的线程池
     * @param tasks    多个Runnable任务
     */
    public static void runs(final ExecutorService executor, final Runnable[] tasks) {
        final List<Future<?>> futures = new ArrayList<>();
        for (final Runnable task : tasks) { futures.add(executor.submit(task)); }

        for (final Future<?> future : futures) {
            try { future.get(); } 
            catch (final InterruptedException | ExecutionException e) { handleException(e); }
        };
    };


    /**
     * 关闭线程池
     * 
     * @param executor 需要关闭的线程池
     */
    public static void close(final ExecutorService executor) { if (executor != null && !executor.isShutdown()) { executor.shutdown(); }; };


    /**
     * 统一的异常处理方法，记录日志并恢复线程中断状态
     * 
     * @param e 发生的异常
     */
    private static void handleException(final Exception e) {
        Thread.currentThread().interrupt();
        logger.log(Level.SEVERE, "Task execution failed", e);
    };
};
