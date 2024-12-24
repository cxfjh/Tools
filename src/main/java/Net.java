
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;


@SuppressWarnings("restriction")
public class Net {
    private HttpServer server;


    /**
     * 构造函数，默认端口为 100
     */
    public Net() { this(100); }


    /**
     * 构造函数，指定端口号
     * 
     * @param port 指定的端口号
     */
    public Net(final int port) {
        if (port < 1 || port > 65535) {  throw new IllegalArgumentException("The port number must be between 1 and 65535"); };
        try {  this.server = HttpServer.create(new InetSocketAddress(port), 0); } 
        catch (IOException e) {  System.out.println("服务器创建失败: " + e.getMessage()); };
    };


    /**
     * 启动服务器
     */
    public void start() {
        try {
            final InetAddress inetAddress = InetAddress.getLocalHost();
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            System.out.println("服务器地址: http://" + inetAddress.getHostAddress() + ":" + server.getAddress().getPort() + "/");
        } catch (IOException e) {  System.out.println("服务器启动失败: " + e.getMessage()); };
    };


    /**
     * 停止服务器
     * 
     * @param delay 停止延迟时间（秒）
     */
    public void stop(final int delay) {
        server.stop(delay);
        System.out.println("服务器已停止");
    };


    /**
     * 处理 GET 请求
     * 
     * @param routes 路由数组，每个元素包含路径和响应内容
     */
    public void get(final String[][] routes) {
        for (String[] route : routes) {
            final String path = route[0];
            final String response = route[1];
            server.createContext(path, new GetHandler(response));
        };
    };


    /**
     * 处理 POST 请求
     * 
     * @param routes 路由数组，每个元素包含路径和响应内容
     */
    public void post(final String[][] routes) {
        for (String[] route : routes) {
            final String path = route[0];
            final String response = route[1];
            server.createContext(path, new PostHandler(response));
        };
    };


    /**
     * 处理 GET 请求的自定义逻辑
     * 
     * @param path 路径
     * @param processor 请求处理器
     */
    public void get(final String path, final RequestProcessor processor) { server.createContext(path, new CustomGetHandler(processor)); };


    /**
     * 处理 POST 请求的自定义逻辑
     * 
     * @param path 路径
     * @param processor 请求处理器
     */
    public void post(final String path, final RequestProcessor processor) { server.createContext(path, new CustomPostHandler(processor)); };


    /**
     * 托管静态文件夹
     * 
     * @param path URL 路径
     * @param directory 文件夹目录
     */
    public void web(final String path, final String directory) { server.createContext(path, new StaticFileHandler(directory)); };


    /**
     * 托管静态文件夹，默认托管到根目录
     * 
     * @param directory 文件夹目录
     */
    public void web(final String directory) {  web("/", directory); };



    // GET 请求处理类
    private static class GetHandler implements HttpHandler {
        private final String response;
        public GetHandler(final String response) { this.response = response; };

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    final URI requestURI = exchange.getRequestURI();
                    final String requestContent = requestURI.getQuery();
                    System.out.println("收到 GET 请求，内容: " + requestContent);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    final byte[] responseBytes = response.getBytes("UTF-8");
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try (final OutputStream os = exchange.getResponseBody()) { os.write(responseBytes); };
                } catch (Exception e) {
                    exchange.sendResponseHeaders(400, -1); 
                    System.out.println("处理 GET 请求失败: " + e.getMessage());
                };
            } else { exchange.sendResponseHeaders(405, -1); };
        };
    };



    // POST 请求处理类
    private static class PostHandler implements HttpHandler {
        private final String response;
        public PostHandler(final String response) { this.response = response; };

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    final InputStream inputStream = exchange.getRequestBody();
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) { baos.write(buffer, 0, length); };
                    final byte[] requestBody = baos.toByteArray();
                    final String requestContent = new String(requestBody, "UTF-8");
                    System.out.println("收到 POST 请求，内容: " + requestContent);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    final byte[] responseBytes = response.getBytes("UTF-8");
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) { os.write(responseBytes); };
                } catch (Exception e) {
                    exchange.sendResponseHeaders(400, -1); 
                    System.out.println("处理 POST 请求失败: " + e.getMessage());
                };
            } else { exchange.sendResponseHeaders(405, -1); };
        };
    };



    // 自定义 GET 请求处理类
    private static class CustomGetHandler implements HttpHandler {
        private final RequestProcessor processor;
        public CustomGetHandler(final RequestProcessor processor) { this.processor = processor; };

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                final String requestContent = exchange.getRequestURI().getQuery();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                final String response = processor.processRequest(exchange, requestContent);
                final byte[] responseBytes = response.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(responseBytes); };
            } else { exchange.sendResponseHeaders(405, -1); };
        };
    };



    // 自定义 POST 请求处理类
    private static class CustomPostHandler implements HttpHandler {
        private final RequestProcessor processor;
        public CustomPostHandler(final RequestProcessor processor) { this.processor = processor; };

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    final InputStream inputStream = exchange.getRequestBody();
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) { baos.write(buffer, 0, length); };
                    final byte[] requestBody = baos.toByteArray();
                    final String requestContent = new String(requestBody, "UTF-8");
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    final String response = processor.processRequest(exchange, requestContent);
                    final byte[] responseBytes = response.getBytes("UTF-8");
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) { os.write(responseBytes); };
                } catch (Exception e) {
                    exchange.sendResponseHeaders(400, -1);
                    System.out.println("处理 POST 请求失败: " + e.getMessage());
                };
            } else { exchange.sendResponseHeaders(405, -1); };
        };
    };



    // 静态文件处理类
    private static class StaticFileHandler implements HttpHandler {
        private final String directory;
        public StaticFileHandler(String directory) { this.directory = directory; };

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            final URI requestURI = exchange.getRequestURI();
            final String path = requestURI.getPath();
            final Path filePath = Paths.get(directory, path);

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                exchange.getResponseHeaders().set("Content-Type", Files.probeContentType(filePath));
                final byte[] fileContent = Files.readAllBytes(filePath);
                exchange.sendResponseHeaders(200, fileContent.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(fileContent); };
            } else {
                final String notFoundResponse = "404 Not Found";
                exchange.sendResponseHeaders(404, notFoundResponse.length());
                try (OutputStream os = exchange.getResponseBody()) { os.write(notFoundResponse.getBytes()); };
            };
        };
    };

    

    // 定义一个接口，用户可以实现该接口来自定义如何处理请求
    public interface RequestProcessor { String processRequest(HttpExchange exchange, String requestContent) throws IOException; };
};
