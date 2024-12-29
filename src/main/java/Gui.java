
import java.util.function.IntConsumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.io.IOException;

public class Gui {
    // -------------------------------------------------- 普通弹窗方法 --------------------------------------------------

    /**
     * 显示普通弹窗
     * 
     * @param type 弹窗类型：0 -> 普通框, 1 -> 警告框, 2 -> 错误框
     * @param title 弹窗标题
     * @param message 弹窗内容
     */
    public static void alert(final int type, final String title, final String message) {
        switch (type) {
            case 0: JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE); break;
            case 1: JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE); break;
            case 2: JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE); break;
        };
    };





    // -------------------------------------------------- 输入弹窗方法 --------------------------------------------------

    /**
     * 显示输入弹窗
     * 
     * @param type 弹窗类型：0 -> 确认框, 1 -> 输入框, 2 -> 选项框
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @return 用户输入或选择的结果
     */
    public static String prompt(final int type, final String title, final String message) {
        String result = "";
        switch (type) {
            case 0: {
                int option = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                result = option == JOptionPane.YES_OPTION ? "是" : "否";
                break;
            }
            case 1: result = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE); break;
            case 2: {
                String[] options = {"选项1", "选项2", "选项3"};
                int option = JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (option >= 0 && option < options.length) { result = options[option]; }
                break;
            }
        };
        return result;
    };


    /**
     * 显示确认框
     * 
     * @param type 弹窗类型：必须为 0 (确认框)
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @param succeed 用户选择“是”时的返回值
     * @param fail 用户选择“否”时的返回值
     * @return 用户选择的结果
     */
    public static String prompt(final int type, final String title, final String message, final String succeed, final String fail) {
        if (type != 0) return "";
        final int option = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION ? succeed : fail;
    };


    /**
     * 显示选项框
     * 
     * @param type 弹窗类型：必须为 2 (选项框)
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @param options 选项数组
     * @return 用户选择的结果
     */
    public static String prompt(final int type, final String title, final String message, final String[] options) {
        if (type != 2) return "";
        final int option = JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (option >= 0 && option < options.length) { return options[option]; };
        return "";
    };





    // -------------------------------------------------- 窗口方法 --------------------------------------------------

    /**
     * 创建并显示一个窗口
     * 
     * @param title 窗口标题
     * @param width 窗口宽度
     * @param height 窗口高度
     * @param resizable 窗口是否可缩放
     * @return 创建的 JFrame 对象
     */
    public static JFrame win(final String title, final int width, final int height, final boolean resizable) {
        final JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(resizable);
        frame.setVisible(true);
        frame.setLayout(null);
        return frame;
    };


    /**
     * 创建并显示一个窗口，带更多选项
     * 
     * @param title 窗口标题
     * @param width 窗口宽度
     * @param height 窗口高度
     * @param resizable 窗口是否可缩放
     * @param alwaysOnTop 窗口是否置顶
     * @param fullScreen 窗口是否全屏
     * @param uncrossable 窗口是否不可关闭
     * @return 创建的 JFrame 对象
     */
    public static JFrame win(final String title, final int width, final int height, final boolean resizable, final boolean alwaysOnTop, final boolean fullScreen, final boolean uncrossable) {
        final JFrame frame = new JFrame(title);

        if (fullScreen) {
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        };
        
        frame.setAlwaysOnTop(alwaysOnTop);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(resizable);
        frame.setVisible(true);
        frame.setLayout(null);

        if (uncrossable) { frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); } 
        else { frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); };

        frame.setVisible(true);
        return frame;
    };





    // -------------------------------------------------- 标签方法 --------------------------------------------------

    /**
     * 创建并添加一个标签到窗口
     * 
     * @param text 标签文本内容
     * @param x 标签位置 x 坐标
     * @param y 标签位置 y 坐标
     * @param width 标签宽度
     * @param height 标签高度
     * @param frame 父窗口
     * @return 创建的 JLabel 对象
     */
    public static JLabel label(final String text, final int x, final int y, final int width, final int height, final JFrame frame) {
        final JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setFont(new java.awt.Font("楷体", java.awt.Font.PLAIN, 22));
        frame.add(label);
        frame.repaint();
        return label;
    };


    /**
     * 创建并添加一个标签到窗口，带自定义字体
     * 
     * @param text 标签文本内容
     * @param x 标签位置 x 坐标
     * @param y 标签位置 y 坐标
     * @param width 标签宽度
     * @param height 标签高度
     * @param frame 父窗口
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @return 创建的 JLabel 对象
     */
    public static JLabel label(final String text, final int x, final int y, final int width, final int height, final JFrame frame, final int fontSize, final String fontName) {
        final JLabel label = label(text, x, y, width, height, frame);
        label.setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize));
        frame.repaint();
        return label;
    };





    // -------------------------------------------------- 输入框方法 --------------------------------------------------

    /**
     * 创建并添加一个输入框到窗口
     * 
     * @param x 输入框位置 x 坐标
     * @param y 输入框位置 y 坐标
     * @param width 输入框宽度
     * @param height 输入框高度
     * @param frame 父窗口
     * @return 创建的 JTextField 对象
     */
    public static JTextField txtInput(final int x, final int y, final int width, final int height, final JFrame frame) {
        final JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new java.awt.Font("楷体", java.awt.Font.PLAIN, 20));
        frame.add(textField);
        frame.repaint();
        return textField;
    };


    /**
     * 创建并添加一个输入框到窗口，带自定义字体
     * 
     * @param x 输入框位置 x 坐标
     * @param y 输入框位置 y 坐标
     * @param width 输入框宽度
     * @param height 输入框高度
     * @param frame 父窗口
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @return 创建的 JTextField 对象
     */
    public static JTextField txtInput(final int x, final int y, final int width, final int height, final JFrame frame, final int fontSize, final String fontName) {
        final JTextField textField = txtInput(x, y, width, height, frame);
        textField.setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize));
        frame.repaint();
        return textField;
    };





    // -------------------------------------------------- 按钮方法 --------------------------------------------------

    /**
     * 创建并添加一个按钮到窗口
     * 
     * @param text 按钮文本内容
     * @param x 按钮位置 x 坐标
     * @param y 按钮位置 y 坐标
     * @param width 按钮宽度
     * @param height 按钮高度
     * @param frame 父窗口
     * @return 创建的 JButton 对象
     */
    public static JButton button(final String text, final int x, final int y, final int width, final int height, final JFrame frame) {
        final JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFont(new java.awt.Font("楷体", java.awt.Font.PLAIN, 18));
        frame.add(button);
        frame.repaint();
        return button;
    };


    /**
     * 创建并添加一个按钮到窗口，带自定义字体
     * 
     * @param text 按钮文本内容
     * @param x 按钮位置 x 坐标
     * @param y 按钮位置 y 坐标
     * @param width 按钮宽度
     * @param height 按钮高度
     * @param frame 父窗口
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @return 创建的 JButton 对象
     */
    public static JButton button(final String text, final int x, final int y, final int width, final int height, final JFrame frame, final int fontSize, final String fontName) {
        final JButton button = button(text, x, y, width, height, frame);
        button.setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize));
        frame.repaint();
        return button;
    };


    /**
     * 为按钮添加点击事件
     * 
     * @param button 要添加点击事件的按钮
     * @param fun 点击事件处理函数
     */
    public static void button(final JButton button, final IntConsumer fun) {
        button.addActionListener(e -> {
            fun.accept(0);
        });
    };





    // -------------------------------------------------- 下拉框方法 --------------------------------------------------

    /**
     * 创建并添加一个下拉框到窗口
     * 
     * @param x 下拉框位置 x 坐标
     * @param y 下拉框位置 y 坐标
     * @param width 下拉框宽度
     * @param height 下拉框高度
     * @param frame 父窗口
     * @param options 选项数组
     * @return 创建的 JComboBox 对象
     */
    public static JComboBox<String> comboBox(final int x, final int y, final int width, final int height, final JFrame frame, final String[] options) {
        final JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setBounds(x, y, width, height);
        comboBox.setFont(new java.awt.Font("楷体", java.awt.Font.PLAIN, 18));
        frame.add(comboBox);
        frame.repaint();
        return comboBox;
    };


    /**
     * 创建并添加一个下拉框到窗口，带自定义字体
     * 
     * @param x 下拉框位置 x 坐标
     * @param y 下拉框位置 y 坐标
     * @param width 下拉框宽度
     * @param height 下拉框高度
     * @param frame 父窗口
     * @param options 选项数组
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @return 创建的 JComboBox 对象
     */
    public static JComboBox<String> comboBox(final int x, final int y, final int width, final int height, final JFrame frame, final String[] options, final int fontSize, final String fontName) {
        final JComboBox<String> comboBox = comboBox(x, y, width, height, frame, options);
        comboBox.setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize));
        frame.repaint();
        return comboBox;
    };





    // -------------------------------------------------- 文本域方法 --------------------------------------------------

    /**
     * 创建并添加一个文本域到窗口
     * 
     * @param x 文本域位置 x 坐标
     * @param y 文本域位置 y 坐标
     * @param width 文本域宽度
     * @param height 文本域高度
     * @param frame 父窗口
     * @return 创建的 JTextArea 对象
     */
    public static JTextArea textArea(final int x, final int y, final int width, final int height, final JFrame frame) {
        final JTextArea textArea = new JTextArea();
        textArea.setBounds(x, y, width, height);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new java.awt.Font("楷体", java.awt.Font.PLAIN, 18));
        frame.add(textArea);
        frame.repaint();
        return textArea;
    };


    /**
     * 创建并添加一个文本域到窗口，带自定义字体
     * 
     * @param x 文本域位置 x 坐标
     * @param y 文本域位置 y 坐标
     * @param width 文本域宽度
     * @param height 文本域高度
     * @param frame 父窗口
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @return 创建的 JTextArea 对象
     */
    public static JTextArea textArea(final int x, final int y, final int width, final int height, final JFrame frame, final int fontSize, final String fontName) {
        final JTextArea textArea = textArea(x, y, width, height, frame);
        textArea.setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize));
        frame.repaint();
        return textArea;
    };





    // -------------------------------------------------- 进度条方法 --------------------------------------------------

    /**
     * 创建并添加一个进度条到窗口
     * 
     * @param x 进度条位置 x 坐标
     * @param y 进度条位置 y 坐标
     * @param width 进度条宽度
     * @param height 进度条高度
     * @param frame 父窗口
     * @return 创建的 JProgressBar 对象
     */
    public static JProgressBar progressBar(final int x, final int y, final int width, final int height, final JFrame frame) {
        final JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(x, y, width, height);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new java.awt.Color(255, 153, 0));
        progressBar.setFont(new java.awt.Font("楷体", java.awt.Font.PLAIN, 18));
        frame.add(progressBar);
        frame.repaint();
        return progressBar;
    };


    /**
     * 创建并添加一个进度条到窗口，带自定义字体
     * 
     * @param x 进度条位置 x 坐标
     * @param y 进度条位置 y 坐标
     * @param width 进度条宽度
     * @param height 进度条高度
     * @param frame 父窗口
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @return 创建的 JProgressBar 对象
     */
    public static JProgressBar progressBar(final int x, final int y, final int width, final int height, final JFrame frame, final int fontSize, final String fontName) {
        final JProgressBar progressBar = progressBar(x, y, width, height, frame);
        progressBar.setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize));
        frame.repaint();
        return progressBar;
    };


    /**
     * 创建并添加一个进度条到窗口，带自定义背景颜色
     * 
     * @param x 进度条位置 x 坐标
     * @param y 进度条位置 y 坐标
     * @param width 进度条宽度
     * @param height 进度条高度
     * @param frame 父窗口
     * @param fontSize 字体大小
     * @param fontName 字体名称
     * @param backgroundColor 背景颜色数组
     * @return 创建的 JProgressBar 对象
     */
    public static JProgressBar progressBar(final int x, final int y, final int width, final int height, final JFrame frame, final int fontSize, final String fontName, final int[] backgroundColor) {
        final JProgressBar progressBar = progressBar(x, y, width, height, frame, fontSize, fontName);
        progressBar.setBackground(new java.awt.Color(backgroundColor[0], backgroundColor[1], backgroundColor[2]));
        frame.repaint();
        return progressBar;
    };


    /**
     * 更新进度条的进度值
     * 
     * @param progressBar 要更新的进度条
     * @param progressValue 新的进度值 (0-100)
     */
    public static void progressBar(final JProgressBar progressBar, final int progressValue) {
        if (progressValue < 0 || progressValue > 100) return;
        progressBar.setValue(progressValue);
        progressBar.setString(progressValue + "%");
    };





    // -------------------------------------------------- 窗口显示网页方法 --------------------------------------------------

    /**
     * 在窗口中显示一个网页
     * 
     * @param title 窗口标题
     * @param width 窗口宽度
     * @param height 窗口高度
     * @param url 要显示的网页地址
     * @param resizable 窗口是否可缩放
     */
    private static void showWeb(final String title, final int width, final int height, final String url, final boolean resizable) {
        SwingUtilities.invokeLater(() -> {
            // 创建Swing窗口
            final JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);

            // 创建JEditorPane并设置页面
            final JEditorPane editorPane = new JEditorPane();
            editorPane.setContentType("text/html");
            try {
                editorPane.setPage(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            editorPane.setEditable(false);

            // 将JEditorPane添加到窗口
            frame.add(new JScrollPane(editorPane), BorderLayout.CENTER);

            frame.setVisible(true);
            frame.setResizable(resizable);
        });
    };


    /**
     * 在窗口中显示一个网页
     * 
     * @param title 窗口标题
     * @param width 窗口宽度
     * @param height 窗口高度
     * @param url 要显示的网页地址
     * @param resizable 窗口是否可缩放
     */
    public static void web(final String title, final int width, final int height, final String url, final boolean resizable) { SwingUtilities.invokeLater(() -> { showWeb(title, width, height, url, resizable); }); };
};

