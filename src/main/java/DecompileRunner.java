import org.apache.commons.io.FilenameUtils;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author pengxinkui
 */
public class DecompileRunner {
    public static void main(String[] args) {
        String projectDir = System.getProperty("user.dir");
        String sourceDir = projectDir + File.separator + "lib" + File.separator + "java-decompiler-2019.3.5.jar";
        String targetDir = projectDir + File.separator + "decompiled" + File.separator;
        String logPath = projectDir + File.separator + "decompile.log";
        System.out.println("准备反编译...");
        doDecompiler(sourceDir, targetDir, logPath);
        System.out.println("反编译结束，反编译输出目录的jar包中为源码文件.");
    }

    /**
     * 反编译源目录到目标目录
     *
     * @param sourceDir 源目录
     * @param targetDir 输出目录
     * @param logPath   日志文件路径
     */
    public static void doDecompiler(String sourceDir, String targetDir, String logPath) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(logPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        doDecompiler(sourceDir, targetDir, printStream);
    }

    /**
     * 反编译源目录到目标目录
     *
     * @param sourceDir   源目录
     * @param targetDir   输出目录
     * @param printStream 打印输出流
     */
    public static void doDecompiler(String sourceDir, String targetDir, PrintStream printStream) {
        File sourceDirectory = new File(sourceDir);
        File destination = new File(targetDir);
        doDecompiler(sourceDirectory, destination, printStream);
    }

    /**
     * 反编译源目录到目标目录
     *
     * @param sourceDirectory 源目录/jar文件
     * @param destination     输出目录
     * @param printStream     打印输出流
     */
    public static void doDecompiler(File sourceDirectory, File destination, PrintStream printStream) {
        Map<String, Object> mapOptions = new HashMap();
        // 重命名不明确的（resp. obfuscated）类和类元素
        mapOptions.put("ren", "1");
        List<File> sources = new ArrayList();
        if (!destination.exists()) {
            destination.mkdirs();
        }
        if (FilenameUtils.getExtension(sourceDirectory.getName()).equalsIgnoreCase("jar")
                || sourceDirectory.isDirectory()) {
            sources.add(sourceDirectory);
        }
        List<File> libraries = new ArrayList();
        if (sources.isEmpty()) {
            System.err.println("error: no sources given");
        } else {
            if (destination.isDirectory() && !destination.exists()) {
                destination.mkdirs();
            }
            if (!destination.isDirectory()) {
                System.err.println("error: destination '" + destination + "' is not a directory");
            } else {
                PrintStreamLogger logger = new PrintStreamLogger(printStream == null ? System.out : printStream);
                org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler decompiler = new ConsoleDecompiler(destination, mapOptions, logger);
                Iterator iterator = libraries.iterator();

                File source;
                while (iterator.hasNext()) {
                    source = (File) iterator.next();
                    decompiler.addLibrary(source);
                }

                iterator = sources.iterator();

                while (iterator.hasNext()) {
                    source = (File) iterator.next();
                    decompiler.addSource(source);
                }

                decompiler.decompileContext();
            }
        }
    }

    /**
     * 为了构造 jar 包中的 ConsoleDecompiler，继承 jar 包中的类
     */
    static class ConsoleDecompiler extends org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler {
        public ConsoleDecompiler(File destination, Map<String, Object> options, IFernflowerLogger logger) {
            super(destination, options, logger);
        }
    }
}
