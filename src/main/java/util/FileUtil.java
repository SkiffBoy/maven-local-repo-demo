package util;

import java.io.File;

/**
 * 文件夹删除工具类
 *
 * @author pengxinkui
 */
public class FileUtil {
    /**
     * 递归删除空文件夹
     *
     * @param dir
     */
    public static void deleteEmptyDir(File dir) {
        for (File file : dir.listFiles()) {
            if (".DS_Store".equals(file.getName())) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteEmptyDir(file);
                //一直递归到最后的目录
                if (file.exists() && file.listFiles().length == 0) {
                    //如果是文件夹里面没有文件证明是空文件，进行删除
                    file.delete();
                }
            }
        }
        if (dir.exists() && dir.isDirectory() && dir.listFiles().length == 0) {
            dir.delete();
            return;
        }
    }
}
