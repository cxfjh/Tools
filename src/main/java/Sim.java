
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;


public class Sim {
    // Robot 对象是一个模拟鼠标和键盘操作的工具，使用静态代码块进行初始化
    private static final Robot robot;


    // 静态代码块初始化 Robot 对象，确保程序启动时 Robot 被成功创建
    static {
        try { robot = new Robot(); } 
        catch (AWTException e) { throw new RuntimeException("no robot available", e); }
    };





    // -------------------------------------------------- 组合键方法 --------------------------------------------------

    /**
     * 执行组合键的按下和释放操作
     * @param keyCodes 按键的 keyCode 数组，可以传递多个键
     */
    public static void key(final int... keyCodes) {
        for (final int keyCode : keyCodes) { robot.keyPress(keyCode); };
        for (final int keyCode : keyCodes) { robot.keyRelease(keyCode); };
    };





    //-------------------------------------------------- 获取键码 --------------------------------------------------

    /**
     * 输出所有键盘按键的键码
     * 通过反射获取 KeyEvent 类中的所有常量字段，输出其名称和对应的键码
     */
    public static void getKey() {
        for (final Field field : KeyEvent.class.getFields()) {
            if (field.getName().startsWith("VK_")) {
                try {
                    final int keyCode = field.getInt(null);
                    System.out.printf("%-25s: %d%n", field.getName(), keyCode); 
                } catch (IllegalAccessException e) { e.printStackTrace(); }
            };
        };
    };





    // -------------------------------------------------- 鼠标移动方法 --------------------------------------------------

    /**
     * 移动鼠标到指定的 (x, y) 坐标位置
     * @param x 目标位置的 x 坐标
     * @param y 目标位置的 y 坐标
     */
    public static void move(final int x, final int y) { robot.mouseMove(x, y); };


    /**
     * 移动鼠标位置，支持相对移动
     * @param xOffset 偏移量的 x 坐标
     * @param yOffset 偏移量的 y 坐标
     * @param relative 是否相对移动
     */
    public static void move(final int xOffset, final int yOffset, final boolean relative) {
        if (relative) { move(xOffset, yOffset); } 
        else {
            final int[] mousePosition = getMouse();
            move(mousePosition[0] + xOffset, mousePosition[1] + yOffset);
        };
    };





    // -------------------------------------------------- 鼠标点击方法 --------------------------------------------------

    /**
     * 点击指定的鼠标按钮，可以传入多个按钮进行按下和释放操作
     * @param buttons 鼠标按钮的标识（0 为左键，1 为右键，2 为中键）
     */
    public static void click(final int... buttons) {
        int buttonMask = 0;
        for (final int button : buttons) {
            switch (button) {
                case 0: buttonMask |= InputEvent.BUTTON1_DOWN_MASK; break;
                case 1: buttonMask |= InputEvent.BUTTON3_DOWN_MASK; break;
                case 2: buttonMask |= InputEvent.BUTTON2_DOWN_MASK; break;
                default: throw new IllegalArgumentException("Invalid button parameters: " + button); 
            };
        };

        robot.mousePress(buttonMask);
        robot.mouseRelease(buttonMask);
    };





    // -------------------------------------------------- 鼠标拖动方法 --------------------------------------------------

    /**
     * 模拟鼠标从一个点拖动到另一个点
     * @param startX 起始点的 x 坐标
     * @param startY 起始点的 y 坐标
     * @param endX 目标点的 x 坐标
     * @param endY 目标点的 y 坐标
     */
    public static void drag(final int startX, final int startY, final int endX, final int endY) {
        move(startX, startY); 
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);  
        move(endX, endY); 
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); 
    };





    // -------------------------------------------------- 鼠标滚轮方法 --------------------------------------------------

    /**
     * 模拟鼠标滚轮操作，正数向下滚动，负数向上滚动
     * @param notches 滚动的次数（正数向下，负数向上）
     */
    public static void scroll(final int notches) { robot.mouseWheel(notches); };





    // -------------------------------------------------- 键盘输入方法 --------------------------------------------------

    /**
     * 获取当前鼠标的位置
     * @return 一个包含鼠标当前位置的数组，索引 0 为 x 坐标，索引 1 为 y 坐标
     */
    public static int[] getMouse() {
        final PointerInfo pointer = MouseInfo.getPointerInfo();
        final Point point = pointer.getLocation();
        return new int[] { point.x, point.y }; 
    };





    // -------------------------------------------------- 鼠标多次点击方法 --------------------------------------------------

    /**
     * 执行鼠标的多次点击操作，每次点击之间有间隔时间
     * @param count 点击次数
     * @param button 鼠标按钮（0 为左键，1 为右键，2 为中键）
     * @param interval 每次点击之间的间隔时间（毫秒）
     */
    public static void clicks(final int count, final int button, final long interval) {
        for (int i = 0; i < count; i++) {
            click(button);
            try { Thread.sleep(interval); } 
            catch (InterruptedException e) { e.printStackTrace(); }
        };
    };
};
