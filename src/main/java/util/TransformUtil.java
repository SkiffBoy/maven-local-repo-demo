package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 项目转化工具类
 *
 * @author pengxinkui
 */
public class TransformUtil {

    /**
     * 将反编译之后的目录转化为 maven 项目结构
     *
     * @param dir       反编译之后的项目目录的上级目录
     * @param targetDir maven 需要输出为maven项目的上级目录
     * @throws IOException
     */
    public static void transformToMavenStruct(String dir, String targetDir) throws IOException {
        File dirFile = new File(dir);
        File projectPath = new File(targetDir, FilenameUtils.getName(dir));

        /**
         * 创建 maven 项目目录结构
         */
        FileUtils.forceMkdir(projectPath);
        File src = new File(projectPath, "src");
        FileUtils.forceMkdir(src);
        File main = new File(src, "main");
        FileUtils.forceMkdir(main);
        File java = new File(main, "java");
        FileUtils.forceMkdir(java);
        File resources = new File(main, "resources");
        FileUtils.forceMkdir(resources);

        // 排除 隐藏文件, .DS_Store 文件, __MACOSX 文件夹
        FileFilter fileFilter = f -> !f.isHidden()
                && !(FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("DS_Store"))
                && !(FilenameUtils.getBaseName(f.getName()).equalsIgnoreCase("__MACOSX"));
        // 移动文件
        for (File file : dirFile.listFiles(fileFilter)) {
            // 判断是否为目录，目录下是否有 java 文件
            if (file.isDirectory()) {
                // 查找 pom.xml 文件并移动到 项目根目录
                Collection<File> pomFiles = FileUtils.listFiles(file, FileFilterUtils.nameFileFilter("pom.xml"), DirectoryFileFilter.INSTANCE);
                File pomFile = pomFiles.stream().findFirst().orElse(null);
                if (pomFile != null && pomFile.isFile() && pomFile.exists()) {
                    FileUtils.moveToDirectory(pomFile, projectPath, true);
                }

                // 查找 spring boot 启动依赖 springframework.boot.loader 并删除
                FileUtils.listFilesAndDirs(file, FileFilterUtils.nameFileFilter("loader"), DirectoryFileFilter.DIRECTORY)
                        .stream().filter(d -> d.getName().equals("loader")
                        && d.getParentFile().getName().equals("boot")
                        && d.getParentFile().getParentFile().getName().equals("springframework")
                )
                        .forEach(f -> FileUtils.deleteQuietly(f));

                // 清理空文件夹
                FileUtil.deleteEmptyDir(file);
                if (!file.exists()) {
                    continue;
                }

                final String fileName = FilenameUtils.getName(file.getAbsolutePath());
                // META-INF 属于 resource 目录
                if ("META-INF".equals(fileName)) {
                    // 删除 META-INF 下的 maven 目录
                    Arrays.stream(file.listFiles(f ->
                            f.isDirectory() && "maven".equals(f.getName())))
                            .forEach(FileUtils::deleteQuietly);
                    if (file.exists()) {
                        // 其他文件/目录，移动到 resources 目录下
                        FileUtils.moveToDirectory(file, resources, true);
                    }
                    continue;
                } else if ("BOOT-INF".equals(fileName)) {
                    for (File f : file.listFiles()) {
                        if ("classes".equals(FilenameUtils.getName(f.getAbsolutePath()))) {
                            final File[] classesFiles = f.listFiles();
                            for (File classesFile : classesFiles) {
                                int javaFileCount = 0;
                                if (classesFile.isDirectory()) {
                                    javaFileCount = FileUtils.listFiles(classesFile, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE).size();
                                }
                                // 有 java 文件的目录，属于 java 源码目录
                                if (javaFileCount > 0 && classesFile.exists()) {
                                    FileUtils.moveToDirectory(classesFile, java, true);
                                } else if (classesFile.exists()) {
                                    // classes 目录下的其他文件，一律归类为 resources 下的文件
                                    FileUtils.moveToDirectory(classesFile, resources, true);
                                }
                            }
                            try {
                                f.deleteOnExit();
                            } catch (Exception e) {
                            }
                        } else if ("lib".equals(f.getName()) && f.isDirectory()) {
                            InputStream is = TransformUtil.class.getResourceAsStream("/jar.ignore");
                            final String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                            // 需要排除的非项目 jar 名称列表（以文件开头）
                            final Set<String> excludeJars = Arrays.stream(content.split("\n"))
                                    .filter(s -> s.trim().length() > 0 && !s.startsWith("#"))
                                    .map(String::trim)
                                    .collect(Collectors.toSet());

                            // 将需要排除的非项目 jar 删除
                            Arrays.stream(f.listFiles(jar -> jar.getName().toLowerCase().endsWith(".jar")))
                                    .filter(jar -> excludeJars.stream().anyMatch(jar.getName()::startsWith))
                                    .forEach(FileUtils::deleteQuietly);
                        }
                        if (f.exists()) {
                            FileUtils.moveToDirectory(f, resources, true);
                        }
                    }
                    continue;
                } else {
                    int javaFileCount = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter("java"), DirectoryFileFilter.INSTANCE).size();
                    // 有 java 文件的目录，属于 java 源码目录
                    if (javaFileCount > 0 && file.exists()) {
                        FileUtils.moveToDirectory(file, java, true);
                        continue;
                    }
                }
            }
            // 目录下，没有java 文件的，均认为属于 resource 目录
            if (file.exists()) {
                FileUtils.moveToDirectory(file, resources, true);
            }
        }
        // 清理空文件夹
        if (dirFile.exists()) {
            FileUtil.deleteEmptyDir(dirFile);
        }
    }

}
