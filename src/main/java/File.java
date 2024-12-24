import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;


public class File {
    // -------------------------------------------------- 追加内容到指定文件 --------------------------------------------------

    /**
     * 追加内容到指定文件。
     *
     * @param filePath 文件路径，指定要追加内容的文件。
     * @param content  要追加的内容数组，每项代表文件中的一行。
     * @return 如果追加成功返回 true，否则返回 false。
     */
    public static boolean add(final String filePath, final String[] content) {
        try {
            final BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            };
            writer.close();
        } catch (IOException e) { System.err.println("追加内容失败: " + e.getMessage()); return false; };
        return true;
    };





    // -------------------------------------------------- 删除文件 --------------------------------------------------
    
    /**
     * 删除文件。
     * 
     * @param filePath 文件路径，指定要删除的文件。
     * @return 如果删除成功返回 true，否则返回 false。
     */
    public static boolean del(final String filePath) {
        try {
            final Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) { System.err.println("删除文件失败: " + e.getMessage()); return false; }
    };

    



    // -------------------------------------------------- 覆盖文件内容 --------------------------------------------------

    /**
     * 覆盖文件内容。
     *
     * @param filePath 文件路径，指定要覆盖内容的文件。
     * @param content  要覆盖的内容数组，每项代表文件中的一行。
     * @return 如果覆盖成功返回 true，否则返回 false。
     */
    public static boolean set(final String filePath, final String[] content) {
        try {
            final BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            for (String line : content) { writer.write(line); writer.newLine(); };
            writer.close();
        } catch (IOException e) { System.err.println("覆盖文件失败: " + e.getMessage()); return false; }
        return true; 
    };





    // -------------------------------------------------- 获取文件内容 --------------------------------------------------

    /**
     * 获取文件内容。
     *
     * @param filePath 文件路径，指定要获取内容的文件。
     * @return 文件内容数组，每项代表文件中的一行。
     */
    public static String[] get(final String filePath) {
        try {
            final List<String> content = Files.readAllLines(Paths.get(filePath));
            return content.toArray(new String[0]);
        } catch (IOException e) { System.err.println("读取文件失败: " + e.getMessage()); return new String[0]; }
    };





    // -------------------------------------------------- 创建文件 --------------------------------------------------

    /**
     * 创建文件。
     * 
     * @param filePath 文件路径，指定要创建的文件。
     * @return 如果创建成功返回 true，否则返回 false。
     */
    public static boolean create(final String filePath) {
        try {
            final Path path = Paths.get(filePath);
            if (Files.notExists(path)) { Files.createFile(path); };
        } catch (IOException e) { System.err.println("创建文件失败: " + e.getMessage()); return false; };
        return true;
    };





    // -------------------------------------------------- 复制文件 --------------------------------------------------

    /**
     * 复制文件。
     * 
     * @param sourceFilePath 源文件路径，指定要复制的文件。
     * @param targetFilePath 目标文件路径，指定复制后的文件。
     * @return 如果复制成功返回 true，否则返回 false。
     */
    public static boolean copy(final String sourceFilePath, final String targetFilePath) {
        try {
            final Path sourcePath = Paths.get(sourceFilePath);
            final Path targetPath = Paths.get(targetFilePath);
            if (Files.notExists(targetPath)) { Files.copy(sourcePath, targetPath); };
        } catch (IOException e) { System.err.println("复制文件失败: " + e.getMessage()); return false; };
        return true;
    };





    // -------------------------------------------------- 复制文件夹 --------------------------------------------------

    /**
     * 复制文件夹。
     *  
     * @param sourceDirPath 源文件夹路径，指定要复制的文件夹。
     * @param targetDirPath 目标文件夹路径，指定复制后的文件夹。
     * @return 如果复制成功返回 true，否则返回 false。
     */
    public static boolean copyDir(final String sourceDirPath, final String targetDirPath) {
        try {
            final Path sourcePath = Paths.get(sourceDirPath);
            final Path targetPath = Paths.get(targetDirPath);
            if (Files.notExists(targetPath)) { Files.createDirectory(targetPath); };
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    final Path targetFile = targetPath.resolve(sourcePath.relativize(file));
                    if (Files.notExists(targetFile)) { Files.copy(file, targetFile); };
                    return FileVisitResult.CONTINUE;
                };
            });
        } catch (IOException e) { System.err.println("复制文件夹失败: " + e.getMessage()); return false; }
        return true;
    };





    // -------------------------------------------------- 创建文件夹 --------------------------------------------------
    
    /**
     * 创建文件夹。
     * 
     * @param dirPath 文件夹路径，指定要创建的文件夹。
     * @return 如果创建成功返回 true，否则返回 false。
     */
    public static boolean mkdir(final String dirPath) {
        try {
            final Path path = Paths.get(dirPath);
            if (Files.notExists(path)) { Files.createDirectory(path); };
        } catch (IOException e) { System.err.println("创建文件夹失败: " + e.getMessage()); return false; }
        return true;
    };



    

    // -------------------------------------------------- 删除文件夹 --------------------------------------------------

    /**
     * 删除文件夹。
     * 
     * @param dirPath 文件夹路径，指定要删除的文件夹。
     * @return 如果删除成功返回 true，否则返回 false。
     */
    public static boolean rmdir(final String dirPath) {
        final Path directoryPath = Paths.get(dirPath); 
        try {
            // 使用walkFileTree递归删除文件夹及其内容
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // 删除文件
                    return FileVisitResult.CONTINUE;
                };

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // 删除空目录
                    return FileVisitResult.CONTINUE;
                };
            });
            return true;
        } catch (IOException e) { System.err.println("删除文件夹时发生错误: " + e.getMessage()); return false; }
    };
};