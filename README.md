# demo

### 打包

####  使用jar命令打包

jar cvf myapblp.jar -C bin/ .

#### vscode设置jdk版本

进入 【JAVA PROJECTS】

修改项目jdk版本

查看编译后的类文件版本
javap -verbose Test.class | grep "major version"

### webp
```
<!-- webp-imageio 依赖 -->
<dependency>
    <groupId>org.sejda.imageio</groupId>
    <artifactId>webp-imageio</artifactId>
    <version>0.1.6</version>
</dependency>
```

```
String inputFilePath = "chess.png";
String outputFilePath = inputFilePath.replace(".png", ".webp");
// 获取 JPG 图像写入器
Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
if (!writers.hasNext()) {
    System.out.println("没有可用的 JPG 图像写入器");
    return;
}
ImageWriter writer = writers.next();
System.out.println(writer);
BufferedImage img = ImageIO.read(new File(inputFilePath));
ImageIO.write(img, "webp", new File(outputFilePath));

```

### arm64环境

#### jdk-arm64

```
sudo apt-get update
sudo apt-get install openjdk-8-jdk
```

```
/usr/lib/jvm/java-8-openjdk-arm64/bin
-rwxr-xr-x. 1 aidlux aidlux 6168 Oct 30 21:54 javac*
```



#### 脚本

```
export JAVA_HOME=/path/to/your/java/home
export PATH=$JAVA_HOME/bin:$PATH

nohup java -cp target/demo-1.0.jar com.chess.ChatServer &
```

