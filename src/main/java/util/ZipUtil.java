package util;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * jar 文件提取工具类
 *
 * @author pengxinkui
 */
public class ZipUtil {

    /**
     * 解压 jar 文件
     *
     * @param jarPath   jar文件路径
     * @param unzipPath 保存路径
     */
    public static void unzipJar(String jarPath, String unzipPath) {
        if (!FilenameUtils.getExtension(jarPath).equalsIgnoreCase("jar")) {
            throw new IllegalArgumentException("不支持非 jar 文件 {}" + jarPath);
        }
        File jarFile = new File(jarPath);
        try {
            //获得输出流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(jarFile));
            ArchiveInputStream in = new ArchiveStreamFactory()
                    .createArchiveInputStream(ArchiveStreamFactory.JAR,
                            bufferedInputStream);
            JarArchiveEntry entry = null;
            //循环遍历解压
            while ((entry = (JarArchiveEntry) in.getNextEntry()) != null) {
                if (entry.getName().indexOf("__MACOSX") != -1) {
                    continue;
                }
                if (entry.isDirectory()) {
                    new File(unzipPath, entry.getName()).mkdir();
                } else {
                    OutputStream out = FileUtils.openOutputStream(new File(
                            unzipPath, entry.getName()));
                    IOUtils.copy(in, out);
                    out.close();
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.err.println("未找到 jar 文件");
        } catch (ArchiveException e) {
            System.err.println("不支持的压缩格式");
        } catch (IOException e) {
            System.err.println("文件写入发生错误");
        }
    }
}
