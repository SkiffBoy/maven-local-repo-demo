# 项目要求：jdk 8

* windows 环境，打开资源管理器，需要在项目目录下右键 git_bash ，执行以下命令 
* Mac 环境，需要在项目目录下，直接使用终端执行

```bash
chmod +x *.sh
sh init_repo.sh
```


# 项目说明

* `java-decompiler-2019.3.5.jar` 来源

    * `ideaIU-2019.3.5.win/plugins/java-decompiler/lib/java-decompiler.jar`

* java-decompiler 其实是 JetBrains 的一个开源插件

    * https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine

* 命令行使用 IDEA 自带的 java-decompiler.jar 进行反编译

```shell
# mac
java -cp "/Applications/IntelliJ IDEA.app/Contents/plugins/java-decompiler/lib/java-decompiler.jar" org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler  -dgs=true  "./lib/java-decompiler-2019.3.5.jar" ~/Desktop/

# windows
java -cp "C:\Program Files\JetBrains\IntelliJ IDEA 2020.3.4\plugins\java-decompiler\lib\java-decompiler.jar" org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler  -dgs=true  ".\lib\java-decompiler-2019.3.5.jar" ~/Desktop/
```