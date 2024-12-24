
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.Desktop;

public class Sys {
    // -------------------------------------------------- time 方法 --------------------------------------------------

    /**
     * 定时执行任务方法
     * 
     * @param task 要执行的任务
     * @param intervalMillis 时间间隔（毫秒）
     * @param durationMillis 持续时间（毫秒）
     */
    public static void time(final Runnable task, final long intervalMillis, final long durationMillis) {
        if (intervalMillis < 0 || durationMillis < 0) {  throw new IllegalArgumentException("time cannot be negative"); };
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        final Runnable scheduledTask = new Runnable() {
            @Override
            public void run() {
                final long currentTime = System.currentTimeMillis();
                final long endTime = currentTime + durationMillis;
                
                if (currentTime >= endTime) {  scheduler.shutdown(); } 
                else {
                    try {  task.run();  } 
                    catch (Exception e) {  System.err.println("任务执行异常: " + e.getMessage());  }
                };
            };
        };

        scheduler.scheduleAtFixedRate(scheduledTask, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
        scheduler.schedule(() -> { if (!scheduler.isShutdown()) { scheduler.shutdown(); } }, durationMillis, TimeUnit.MILLISECONDS);
    };


    /**
     * 定时执行任务方法（带停止标记）
     * 
     * @param task 要执行的任务
     * @param intervalMillis 时间间隔（毫秒）
     * @return 用于停止定时器的标记
     */
    public static AtomicBoolean time(final Runnable task, final long intervalMillis) {
        if (intervalMillis < 0) { throw new IllegalArgumentException("time cannot be negative"); };
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final AtomicBoolean isRunning = new AtomicBoolean(true);

        final Runnable scheduledTask = new Runnable() {
            @Override
            public void run() {
                if (isRunning.get()) {
                    try { task.run(); } 
                    catch (Exception e) { System.err.println("任务执行异常: " + e.getMessage()); }
                } else { scheduler.shutdown(); };
            };
        };

        scheduler.scheduleAtFixedRate(scheduledTask, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
        return isRunning;
    };


    /**
     * 获取当前时间戳方法
     * 
     * @return 当前时间戳（毫秒）
     */
    public static long time() {  return System.currentTimeMillis(); }

    
    /**
     * 计算时间差方法
     * 
     * @param millis 起始时间戳（毫秒）
     * @return 时间差（毫秒）
     */
    public static long time(final long millis) { 
        final long currentTime = System.currentTimeMillis();
        return currentTime - millis;
    };





    // -------------------------------------------------- 时间暂停方法 --------------------------------------------------
    
    /**
     * 时间暂停方法
     * 
     * @param millis 暂停时间（毫秒）
     */
    public static void sleep(final long millis) {
        try {  Thread.sleep(millis); } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("线程被中断: " + e.getMessage());
        };
    };





    // -------------------------------------------------- 获取当前线程 --------------------------------------------------

    /**
     * 获取当前线程信息方法
     */
    public static void getThread() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("tasklist").getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {  System.out.println(line); }
        } catch (IOException e) {
            System.err.println("获取当前线程信息异常: " + e.getMessage());
            e.printStackTrace();
        }
    };





    // -------------------------------------------------- 结束线程 --------------------------------------------------

    /**
     * 结束线程方法
     * 
     * @param name 线程名称
     */
    public static void delThread(final String name) {
        try {  Runtime.getRuntime().exec("taskkill /F /IM " + name); } 
        catch (IOException e) {
            System.err.println("结束线程异常: " + e.getMessage());
            e.printStackTrace();
        };
    };





    // -------------------------------------------------- 打开网页 --------------------------------------------------
    
    /**
     * 打开网页方法
     * 
     * @param url 要打开的网址
     */
    public static void openUrl(final String url) {
        try {
            final URI uri = new URI(url);
            if (Desktop.isDesktopSupported()) {
                final Desktop desktop = Desktop.getDesktop();
                desktop.browse(uri);
            } else {  System.out.println("桌面操作不被支持"); }
        } catch (Exception e) {  e.printStackTrace(); }
    };





    // -------------------------------------------------- 关机 --------------------------------------------------

    /**
     * 关机方法
     * 
     * @param millis 延迟关机时间（秒）
     */
    public static void stopWin(final long millis) {
        if (millis < 0) {  throw new IllegalArgumentException("time cannot be negative");  }

        try {  Runtime.getRuntime().exec("shutdown -s -t " + millis); } 
        catch (IOException e) {
            System.err.println("关机异常: " + e.getMessage());
            e.printStackTrace();
        };
    };





    // -------------------------------------------------- 结束桌面程序 --------------------------------------------------

    /**
     * 结束或启动桌面程序方法
     * 
     * @param state true 启动桌面程序, false 结束桌面程序
     */
    public static void desktop(final boolean state) {
        try { 
            if (state) {  Runtime.getRuntime().exec("explorer.exe"); return; };
            Runtime.getRuntime().exec("taskkill /F /IM explorer.exe");
        } catch (IOException e) {
            System.err.println("结束桌面程序异常: " + e.getMessage());
            e.printStackTrace();
        };
    };


    /**
     * 结束或启动桌面程序方法（带时间延迟）
     * 
     * @param state true 启动桌面程序, false 结束桌面程序
     * @param time 延迟时间（毫秒）
     */
    public static void desktop(final boolean state, final int time) {
        try {
            desktop(state);
            Thread.sleep(time);
            desktop(!state);
        } catch (InterruptedException e) { e.printStackTrace(); };
    };





    // -------------------------------------------------- 获取系统信息 --------------------------------------------------

    /**
     * 获取系统信息方法
     */
    public static void get() {
        try {
            final String osName = System.getProperty("os.name");
            final String osArch = System.getProperty("os.arch");
            final String osVersion = System.getProperty("os.version");
            final String javaVersion = System.getProperty("java.version");
            final String userName = System.getProperty("user.name");
            final String userHome = System.getProperty("user.home");
            final String userDir = System.getProperty("user.dir");
            System.out.println("操作系统名称: " + osName);
            System.out.println("操作系统架构: " + osArch);
            System.out.println("操作系统版本: " + osVersion);
            System.out.println("Java版本: " + javaVersion);
            System.out.println("用户名: " + userName);
            System.out.println("用户主目录: " + userHome);
            System.out.println("用户工作目录: " + userDir);
        } catch (Exception e) {  e.printStackTrace();  }
    };





    // -------------------------------------------------- 禁用任务管理器 --------------------------------------------------

    /**
     * 禁用或启用任务管理器方法
     * 
     * @param state true 启用任务管理器, false 禁用任务管理器
     */
    public static void task(final boolean state) {
        final String registryPath = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System";
        final String registryKey = "DisableTaskMgr";
        final String registryValue = state ? "0" : "1"; // 0启用, 1禁用
        final String command = "reg add \"" + registryPath + "\" /v " + registryKey + " /t REG_DWORD /d " + registryValue + " /f";

        try {
            final Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e)  { e.printStackTrace(); };
    };
};
