
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.IntConsumer;

public class Use {
    private static final Scanner scanner = new Scanner(System.in);

    // -------------------------------------------------- 用户输入方法 --------------------------------------------------

    /**
     * 该方法用于录入一个整数。
     * 
     * @param message 提示信息
     * @param mistake 是否允许错误输入，如果为false，则输入错误时返回-1，否则继续提示用户输入
     * @return 输入的整数
     */
    public static int input(final String message, final boolean mistake) {
        while (true) {
            try {
                System.out.print(message);
                final int value = scanner.nextInt();
                return value;
            } catch (Exception e) {
                System.out.println("输入的【 " + scanner.nextLine() + " 】不是整数！");
                if (!mistake) return -1; 
            };
        }
    };


    /**
     * 该方法用于录入一个字符串。
     * 
     * @param message 提示信息
     * @return 输入的字符串
     */
    public static String input(final String message) {
        System.out.print(message);
        final String value = scanner.next();
        return value;
    };





    // -------------------------------------------------- 输出方法 --------------------------------------------------


    /**
     * 输出方法，根据是否换行来决定是否换行。
     * 
     * @param value 输出的内容
     * @param lineBreaks 是否换行
     */
    public static<T> void print(final T value, final boolean lineBreaks) {
        if (lineBreaks) { System.out.println(value); } 
        else { System.out.print(value); }
    };


    /**
     * 输出方法，直接输出内容，并换行。
     * 
     * @param value 输出的内容
     */
    public static<T> void print(final T value) { 
        System.out.println(value); 
    };


    /**
     * 输出方法，输出数组的内容，每个元素之间使用指定的分隔符。
     * 
     * @param value 数组内容
     * @param split 分隔符
     */
    public static<T> void print(final T[] value, final String split) {
        System.out.println(String.join(split, Arrays.stream(value).map(String::valueOf).toArray(String[]::new)));
    };


    /**
     * 输出方法，输出数组的内容，并在输出前后加上指定的前后缀。
     * 
     * @param value 数组内容
     * @param split 分隔符
     * @param former 前缀
     * @param latter 后缀
     */
    public static<T> void print(final T[] value, final String split, final String former, final String latter) {
        System.out.print(former);
        for (int i = 0; i < value.length; i++) {
            if (i == value.length - 1) { System.out.print(value[i]); } 
            else { System.out.print(value[i] + split); }
        };
        System.out.print(latter); 
        System.out.println();
    };


    /**
     * 输出方法，输出数组的内容，每个元素占一行。
     * 
     * @param value 数组内容
     */
    public static<T> void print(final T[] value) {
        for (final T item : value) {
            System.out.println(item);
        };
    };





    // -------------------------------------------------- 循环方法 --------------------------------------------------

    /**
     * 循环方法，指定次数输出相同内容。
     * 
     * @param index 循环次数
     * @param value 要输出的内容
     */
    public static void loop(final int index, final String value) {
        for (int i = 0; i < index; i++) {
            System.out.println(value); 
        };
    };


    /**
     * 循环方法，指定次数执行给定的循环体函数。
     * 
     * @param index 循环次数
     * @param fun 循环体函数，执行某段代码
     */
    public static void loop(final int index, final IntConsumer fun) {
        for (int i = 0; i < index; i++) {
            fun.accept(i);
        };
    };





    // -------------------------------------------------- 随机方法 --------------------------------------------------

    /**
     * 生成指定范围内的随机整数。
     * 
     * @param min 随机数的最小值
     * @param max 随机数的最大值
     * @return 生成的随机整数
     */
    public static int random(final int min, final int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    };


    /**
     * 从数组中随机选择一个元素。
     * 
     * @param array 数组
     * @return 随机选择的元素
     */
    public static<T> T random(final T[] array) {
        return array[random(0, array.length - 1)]; 
    };


    /**
     * 从数组中随机选择若干个元素。
     * 
     * @param array 数组
     * @param count 选择的元素数量
     * @return 随机选择的若干个元素
     */
    private static<T> T[] random(final T[] array, final int count) {
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), count);
        for (int i = 0; i < count; i++) { result[i] = array[random(0, array.length - 1)]; };
        return result;
    };


    /**
     * 从数组中随机选择若干个不重复的元素。
     * 
     * @param array 数组
     * @param count 选择的元素数量
     * @param distinct 是否要求元素不重复
     * @return 随机选择的不重复元素
     */
    public static<T> T[] random(final T[] array, final int count, final boolean distinct) {
        if (distinct) { return random(array, count); };
        final T[] result = random(array, count);
        final java.util.Set<T> set = new java.util.HashSet<>();
        for (int i = 0; i < count; i++) { set.add(result[i]); };
        return set.toArray(java.util.Arrays.copyOf(result, set.size()));
    };
    




    // -------------------------------------------------- 数组排序方法 --------------------------------------------------

    /**
     * 数组排序方法，根据数组元素的大小进行排序。
     * 
     * @param array 要排序的数组
     * @param ascending 是否升序排序
     * @return 排序后的数组
     */
    public static Integer[] sort(final Integer[] array, final boolean ascending) {
        if (ascending) { Arrays.sort(array); }
        else { Arrays.sort(array, (a, b) -> b - a); }
        return array;
    };


    /**
     * 数组排序方法，根据数组元素的大小进行排序。
     * 
     * @param array 要排序的数组
     * @param ascending 是否升序排序
     * @return 排序后的数组
     */
    public static String[] sort(final String[] array, final boolean ascending) {
        if (ascending) { Arrays.sort(array); }
        else { Arrays.sort(array, (a, b) -> b.compareTo(a)); }
        return array;
    };


    /**
     * 数组排序方法，根据数组元素的大小进行排序。
     * 
     * @param array 要排序的数组
     * @param ascending 是否升序排序
     * @return 排序后的数组
     */
    public static Double[] sort(final Double[] array, final boolean ascending) {
        if (ascending) { Arrays.sort(array); }
        else { Arrays.sort(array, (a, b) -> b.compareTo(a)); }
        return array;
    };





    // -------------------------------------------------- 清空控制台 --------------------------------------------------

    /**
     * 该方法用于清空控制台屏幕。
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    };
};
