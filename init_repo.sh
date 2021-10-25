# 本脚本用于将 lib 目录下的 jar 包建立为私有仓库
# 如果私有依赖没有变更（包括更新），只需要执行一次(windows 下需要使用 git 的 bash 模式执行)
# 指定本地仓库相对路径
export repo_dir="./repo/"
# shellcheck disable=SC2142,SC2154
alias add_to_repo='function __add_to_repo() { file_path=${1}; groupId=${2}; artifactId=${3}; version=${4};  repoId=local-maven-repo;  mvn deploy:deploy-file    -DgroupId=${groupId}    -DartifactId=${artifactId}    -Dversion=${version}    -Durl=file:${repo_dir}    -DrepositoryId=${repoId}    -DupdateReleaseInfo=true    -Dfile=${file_path};    echo "${file_path} has add to ${repoId}";   unset -f __add_to_repo; }; __add_to_repo'
mkdir -p $repo_dir

# 发布到三方私有类库或者开源修改版
add_to_repo ./lib/java-decompiler-2019.3.5.jar org.jetbrains java-decompiler 2019.3.5

mvn -X -U clean package


# 自定义别名 add_to_repo 命令的内容:
# 从左到右一共 4 个参数:
#	file_path=${1};
#	groupId=${2};
#	artifactId=${3};
#	version=${4};
# 最核心的其实就一个命令，简化了而已
# mvn deploy:deploy-file  
# -DgroupId=${groupId}  
# -DartifactId=${artifactId}  
# -Dversion=${version}  
# -Durl=file:${repo_dir}  
# -DrepositoryId=local-maven-repo
# -DupdateReleaseInfo=true  
# -Dfile=${file_path};  
#
# echo "${file_path} has add to local-maven-repo";
