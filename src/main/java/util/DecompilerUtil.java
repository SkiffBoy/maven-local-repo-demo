package util;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 反编译工具类
 *
 * @author pengxinkui
 */
public class DecompilerUtil {

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
     * 反编译源目录到目标目录
     *
     * @param jarPath     需要被反编译的 jar 包
     * @param unzipPath   临时解压获得的源码目录
     * @param projectPath 输出 maven 项目的源码目录
     */
    public static void decompileJarToMaven(String jarPath, String unzipPath, String projectPath) {
        // jar 文件名，含后缀
        String jarName = FilenameUtils.getName(jarPath);
        // jar 文件名，不含后缀
        String artifactName = FilenameUtils.getBaseName(jarPath);
        // 反编译 jarPath 的 jar 文件到 unzipPath
        DecompilerUtil.doDecompiler(jarPath, unzipPath, System.out);
        // 如果是 jar 包，反编译输出为 jar 包，需要解压
        String decompiledJar = new File(unzipPath, jarName).getAbsolutePath();
        // 反编译的 jar 解压后目录
        String decompiledDir = new File(unzipPath, artifactName).getAbsolutePath();
        ZipUtil.unzipJar(decompiledJar, decompiledDir);
        try {
            TransformUtil.transformToMavenStruct(decompiledDir, projectPath);
        } catch (IOException e) {
            e.printStackTrace();
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
