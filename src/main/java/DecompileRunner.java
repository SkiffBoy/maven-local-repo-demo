import util.DecompilerUtil;

import java.io.File;

/**
 * @author pengxinkui
 */
public class DecompileRunner {
    public static void main(String[] args) {
        String projectDir = System.getProperty("user.dir");
        String libDir = new File(projectDir, "lib").getAbsolutePath();
        String targetDir = new File(projectDir, "target").getAbsolutePath();

        String decompileOutDir = targetDir + File.separator + "decompiled" + File.separator;
        String logPath = targetDir + File.separator + "decompile.log";
        System.out.println("准备反编译...");
        DecompilerUtil.doDecompiler(libDir + File.separator + "java-decompiler-2019.3.5.jar", decompileOutDir, logPath);
        System.out.println("反编译结束，反编译输出目录的jar包中为源码文件.");
        System.out.println("--------------------------------------");

        String sourceMavenJar = libDir + File.separator + "enjoy-4.9.16.jar";
        String tempUnzipDir = targetDir + File.separator + "unzip" + File.separator;
        String mavenProjectDir = targetDir + File.separator + "maven" + File.separator;
        DecompilerUtil.decompileJarToMaven(sourceMavenJar, tempUnzipDir, mavenProjectDir);
        System.out.println("反编译结束，maven 项目已生成至: " + mavenProjectDir);
    }

}
