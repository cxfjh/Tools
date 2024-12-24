
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Iterator;


public class Http {
    // -------------------------------------------------- HTTP请求方法 --------------------------------------------------

    /**
     * 通用的请求方法，支持GET和POST
     *
     * @param urlStr 请求的URL
     * @param method 请求方法，"GET" 或 "POST"
     * @param urlParameters 请求参数
     * @param headers 请求头
     * @return 服务器响应
     * @throws IOException IO异常
     */
    private static String sendRequest(final String urlStr, final String method, final String urlParameters, final Map<String, String> headers) throws IOException {
        final StringBuilder response = new StringBuilder();
        final URL url = new URL(urlStr);
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);

        // 设置请求头
        if (headers != null) { for (Map.Entry<String, String> entry : headers.entrySet()) {con.setRequestProperty(entry.getKey(), entry.getValue()); }; };

        // 对于POST请求，允许输出并写入参数
        if ("POST".equalsIgnoreCase(method) && urlParameters != null) {
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            };
        };

        // 读取响应内容
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) { response.append(inputLine); };
        };

        return response.toString();
    };





    // -------------------------------------------------- Get方法 --------------------------------------------------

    /**
     * GET请求方法
     *
     * @param urlStr 请求的URL
     * @return 服务器响应
     */
    public static String get(final String urlStr) {
        try { return sendRequest(urlStr, "GET", null, null); } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    };





    // -------------------------------------------------- Post方法 --------------------------------------------------

    /**
     * POST请求方法
     *
     * @param urlStr 请求的URL
     * @param urlParameters 请求参数
     * @return 服务器响应
     */
    public static String post(final String urlStr, final String urlParameters) {
        try { return sendRequest(urlStr, "POST", urlParameters, null); } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    };







    // -------------------------------------------------- 监控服务器 --------------------------------------------------

    public static class Father {
        private final int port;
        private DatagramSocket socket;
        private JFrame frame;
        private JLabel label;
        private String title;
        private int width;
        private int height;
        private BufferedImage currentImage;
        private final Object lock = new Object();
        private byte[] imageBuffer = new byte[0];
        private long lastUpdateTime = 0;
        private static final long UPDATE_INTERVAL = 100; // 更新间隔时间，单位毫秒
        private VolatileImage backBuffer; // 后备缓冲区
    

        public Father(final int port, String title, int width, int height) {
            this.port = port;
            this.title = title;
            this.width = width;
            this.height = height;
        };
    


        // 启动监控服务器
        public void start() {
            try {
                System.out.println(InetAddress.getLocalHost().getHostAddress() + ":" + port);
                socket = new DatagramSocket(port);  // 创建UDP套接字
    
                // 创建图像显示窗口
                frame = new JFrame(title);
                label = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (currentImage != null) {
                            final int labelWidth = getWidth();
                            final int labelHeight = getHeight();
                            final int imageWidth = currentImage.getWidth();
                            final int imageHeight = currentImage.getHeight();
                            final double scaleX = (double) labelWidth / imageWidth;
                            final double scaleY = (double) labelHeight / imageHeight;
                            final double scale = Math.min(scaleX, scaleY);
    
                            final int x = (int) ((labelWidth - imageWidth * scale) / 2);
                            final int y = (int) ((labelHeight - imageHeight * scale) / 2);
                            final int scaledWidth = (int) (imageWidth * scale);
                            final int scaledHeight = (int) (imageHeight * scale);
    
                            g.drawImage(currentImage, x, y, scaledWidth, scaledHeight, null);
                        }
                    }
                };
    
                // 图像显示组件
                frame.getContentPane().add(label, BorderLayout.CENTER);  // 将组件添加到窗口中
                frame.setSize(width, height);  // 设置窗口大小
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置关闭窗口时的行为
                frame.setVisible(true);  // 显示窗口
                frame.setLocationRelativeTo(null);  // 窗口居中显示
    
                // 监听窗口大小变化
                frame.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) { label.repaint(); };
                });
    
                // 初始化后备缓冲区
                backBuffer = frame.createVolatileImage(width, height);
    
                // 使用一个单独的线程来接收数据包并处理图像
                final Thread receiverThread = new Thread(this::receiveData);
                receiverThread.start();
            } catch (IOException e) { e.printStackTrace(); };
        };
    


        // 接收数据包并处理图像
        private void receiveData() {
            try {
                final byte[] buffer = new byte[65535];  // 接收数据缓冲区
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    
                while (true) {
                    socket.receive(packet);  // 接收数据包
                    final byte[] imageData = Arrays.copyOf(packet.getData(), packet.getLength());  // 获取数据包中的图像数据
                    if (imageData == null || imageData.length == 0) continue; // 如果收到的数据为空，跳过此次处理
    
                    // 拼接数据包
                    synchronized (lock) {
                        if (isImageStart(imageData)) { imageBuffer = Arrays.copyOf(imageData, imageData.length); } 
                        else {
                            imageBuffer = Arrays.copyOf(imageBuffer, imageBuffer.length + imageData.length);
                            System.arraycopy(imageData, 0, imageBuffer, imageBuffer.length - imageData.length, imageData.length);
                        };
                    };
    
                    // 尝试解析完整的图像数据
                    if (isImageComplete()) {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBuffer);
                        try {
                            BufferedImage image = ImageIO.read(byteArrayInputStream);
                            if (image != null) {
                                synchronized (lock) { currentImage = image; imageBuffer = new byte[0]; };
                                updateImage();
                            };
                        } catch (IIOException e) {
                            // 处理JPEG文件结构错误
                            System.err.println("JPEG文件结构错误: " + e.getMessage());
                            synchronized (lock) { imageBuffer = new byte[0]; } // 清空缓冲区
                        }
                    };
                }
            } catch (IOException e) { e.printStackTrace(); };
        };


    
        // 判断图像数据是否开始
        private boolean isImageStart(byte[] imageData) {
            final byte[] jpgSignature = {(byte) 0xFF, (byte) 0xD8};  // JPEG文件头标志
            return imageData[0] == jpgSignature[0] && imageData[1] == jpgSignature[1];
        };
    


        // 判断图像数据是否完整
        private boolean isImageComplete() {
            if (imageBuffer.length > 2) {
                final byte[] jpgEnd = {(byte) 0xFF, (byte) 0xD9};  // JPEG文件尾标志
                return imageBuffer[imageBuffer.length - 2] == jpgEnd[0] && imageBuffer[imageBuffer.length - 1] == jpgEnd[1];
            };
            return false;
        };



        // 更新图像
        private void updateImage() {
            final long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime < UPDATE_INTERVAL) return;
            lastUpdateTime = currentTime;
    
            SwingUtilities.invokeLater(() -> {
                if (currentImage != null) {
                    do {
                        int valid = backBuffer.validate(frame.getGraphicsConfiguration());
                        if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
                            backBuffer = frame.createVolatileImage(frame.getWidth(), frame.getHeight());
                            if (backBuffer == null) return;
                        };
                        final Graphics2D g2d = backBuffer.createGraphics();
                        final int labelWidth = frame.getWidth();
                        final int labelHeight = frame.getHeight();
                        final int imageWidth = currentImage.getWidth();
                        final int imageHeight = currentImage.getHeight();
                        final double scaleX = (double) labelWidth / imageWidth;
                        final double scaleY = (double) labelHeight / imageHeight;
                        final double scale = Math.min(scaleX, scaleY);
    
                        final int x = (int) ((labelWidth - imageWidth * scale) / 2);
                        final int y = (int) ((labelHeight - imageHeight * scale) / 2);
                        final int scaledWidth = (int) (imageWidth * scale);
                        final int scaledHeight = (int) (imageHeight * scale);
    
                        g2d.drawImage(currentImage, x, y, scaledWidth, scaledHeight, null);
                        g2d.dispose();
                    } while (backBuffer.contentsLost());
                    label.repaint();
                }
            });
        };
    };
    

    
    




    // -------------------------------------------------- 屏幕捕获客户端 --------------------------------------------------

    public static class Child {
        private int port = 100;  // 服务器端口（可修改）
        private int frameRate = 10;  // 每秒发送的帧数
        private float compressionQuality = 0.1f;  // 压缩质量（0.0 - 1.0）
        private final int MAX_PACKET_SIZE = 60 * 1024;  // 最大数据包大小，64KB
        private DatagramSocket socket;
        private InetAddress serverAddr;
        private Robot robot;
        private Rectangle screenRect;
        private ImageWriter imageWriter;
        private ImageWriteParam writeParam;
        private ByteArrayOutputStream byteArrayOutputStream;
        private ExecutorService captureExecutor = Executors.newSingleThreadExecutor();
        private ExecutorService sendExecutor = Executors.newCachedThreadPool();


        /**
         * 构造方法
         *
         * @param serverAddress         服务器地址
         * @param port                  服务器端口
         * @param dpiScalingFactor      屏幕分辨率缩放因子
         */
        public Child(final String serverAddress, final int port, final double dpiScalingFactor) {
            this.port = port;

            try {
                socket = new DatagramSocket();  // 创建UDP套接字
                serverAddr = InetAddress.getByName(serverAddress);  // 获取服务端IP地址
                robot = new Robot();  // 使用Robot类进行屏幕截图

                // 调整屏幕分辨率
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = (int) (screenSize.width * dpiScalingFactor);
                final int height = (int) (screenSize.height * dpiScalingFactor);
                screenRect = new Rectangle(0, 0, width, height); // 创建截图区域

                initializeImageWriter();
            } catch (Exception e) { e.printStackTrace(); }
        };



        /**
         * 构造方法
         *
         * @param serverAddress         服务器地址
         * @param port                  服务器端口
         * @param dpiScalingFactor      屏幕分辨率缩放因子
         * @param frameRate             每秒发送的帧数
         * @param compressionQuality    压缩质量
         */
        public Child(final String serverAddress, final int port, final double dpiScalingFactor, final int frameRate, final float compressionQuality) {
            this.port = port;
            this.frameRate = frameRate;
            this.compressionQuality = compressionQuality;

            try {
                socket = new DatagramSocket();  // 创建UDP套接字
                serverAddr = InetAddress.getByName(serverAddress);  // 获取服务端IP地址
                robot = new Robot();  // 使用Robot类进行屏幕截图

                // 调整屏幕分辨率
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = (int) (screenSize.width * dpiScalingFactor);
                final int height = (int) (screenSize.height * dpiScalingFactor);
                screenRect = new Rectangle(0, 0, width, height); // 创建截图区域

                initializeImageWriter();
            } catch (Exception e) { e.printStackTrace(); }
        };



        /**
         * 初始化JPEG编码器和压缩参数
         */
        private void initializeImageWriter() {
            try {
                // 使用单例的ImageWriter和ImageWriteParam，避免每次都重复获取
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
                if (!writers.hasNext()) { throw new IOException("没有找到JPEG编码器"); };
                imageWriter = writers.next();
                writeParam = imageWriter.getDefaultWriteParam();
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(compressionQuality);
                byteArrayOutputStream = new ByteArrayOutputStream();
            } catch (IOException e) { e.printStackTrace(); };
        };



        /**
         * 启动客户端
         */
        public void start() { captureExecutor.submit(this::captureAndSend); };



        /**
         * 屏幕捕获并发送数据
         */
        private void captureAndSend() {
            try {
                final long frameInterval = 1000 / frameRate;  // 每帧的时间间隔
                
                while (true) {
                    final long startTime = System.nanoTime();  // 获取当前时间戳，微秒级
                    final BufferedImage screenCapture = robot.createScreenCapture(screenRect);  // 截取屏幕
                    final byte[] imageData = convertImageToByteArray(screenCapture);  // 将图像转换为压缩后的字节数组

                    // 如果数据超过64KB，则分包处理
                    if (imageData.length > MAX_PACKET_SIZE) { sendDataInChunks(imageData); } 
                    else { sendData(imageData); };

                    // 控制帧率，确保固定的发送频率
                    final long elapsedTime = System.nanoTime() - startTime;
                    final long sleepTime = Math.max(0, frameInterval - TimeUnit.NANOSECONDS.toMillis(elapsedTime));  // 计算需要休眠的时间
                    Thread.sleep(sleepTime);  // 控制帧率
                }
            } catch (IOException | InterruptedException e) { e.printStackTrace(); }
        };



        /**
         * 将图像转换为压缩后的字节数组
         *
         * @param image 图像
         * @return 压缩后的字节数组
         * @throws IOException IO异常
         */
        private byte[] convertImageToByteArray(BufferedImage image) throws IOException {
            // // 1. 对图像进行跳采样（缩小图像分辨率）
            // final BufferedImage downsampledImage = downsampleImage(image, 0.9);
        
            // 2. 清空字节输出流
            byteArrayOutputStream.reset();
        
            // 3. 创建ImageOutputStream，将压缩后的图像写入字节流
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                imageWriter.setOutput(ios);
                imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), writeParam);
            };
        
            return byteArrayOutputStream.toByteArray();
        };


        
        /**
         * 对图像进行跳采样（缩小图像分辨率）
         *
         * @param originalImage 原始图像
         * @param scaleFactor 缩放因子
         */
        @SuppressWarnings("unused")
        private BufferedImage downsampleImage(final BufferedImage originalImage, final double scaleFactor) {
            final int width = (int) (originalImage.getWidth() * scaleFactor);
            final int height = (int) (originalImage.getHeight() * scaleFactor);
            
            // 创建缩小后的图片
            BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
            Graphics2D g2d = resizedImage.createGraphics();
            
            // 使用高质量的缩放算法
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();
            
            return resizedImage;
        };
        

        /**
         * 发送数据包
         *
         * @param data 数据
         */
        private void sendData(byte[] data) {
            sendExecutor.submit(() -> {
                try {
                    final DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, port);
                    socket.send(packet);  // 发送数据包
                } catch (IOException e) { e.printStackTrace(); };
            });
        };



        /**
         * 发送数据包（分包处理）
         *
         * @param data 数据
         */
        private void sendDataInChunks(final byte[] data) {
            sendExecutor.submit(() -> {
                try {
                    // 分割数据为多个包，每个包不超过64KB
                    final int totalLength = data.length;
                    int offset = 0;
                    while (offset < totalLength) {
                        int length = Math.min(MAX_PACKET_SIZE, totalLength - offset);
                        byte[] chunk = new byte[length];
                        System.arraycopy(data, offset, chunk, 0, length);
                        DatagramPacket packet = new DatagramPacket(chunk, chunk.length, serverAddr, port);
                        socket.send(packet);  // 发送数据包
                        offset += length;
                    }
                } catch (IOException e) { e.printStackTrace(); }
            });
        };



        /**
         * 关闭资源
         */
        public void close() {
            if (socket != null && !socket.isClosed()) { socket.close(); };
            if (imageWriter != null) { imageWriter.dispose(); };
            captureExecutor.shutdown();
            sendExecutor.shutdown();
        };
    };
};
