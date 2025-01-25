# demo

## 没有使用任何构建工具的情况下 使用jar命令打包
jar cvf myapblp.jar -C bin/ .

## JAVA PROJECTS
修改项目jdk版本

查看编译后的类文件版本
javap -verbose Test.class | grep "major version"