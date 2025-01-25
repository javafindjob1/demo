package com.abc;

import java.io.IOException;

public class ExecuteExeWithArgs {
    public static void main(String[] args) {
        try {
            // 创建 ProcessBuilder 对象并设置要执行的命令及其参数
            String sdf = "C:\\Users\\76769\\Downloads\\偶久改图一条龙\\tools\\BLPConv.exe";
            String arg1 = "C:\\Users\\76769\\Desktop\\disbtnitem_chihuangtianyi.blp";
            ProcessBuilder processBuilder = new ProcessBuilder(sdf, arg1);

            // 启动进程
            Process process = processBuilder.start();

            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}