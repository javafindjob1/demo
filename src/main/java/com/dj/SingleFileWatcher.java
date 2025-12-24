package com.dj;

import java.util.*;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SingleFileWatcher {
    private final Path filePath;
    private final Path directory;
    private final String fileName;
    private volatile boolean running = false;
    private Thread watchThread;

    public SingleFileWatcher(String filePath) {
        this.filePath = Paths.get(filePath);
        this.directory = this.filePath.getParent();
        this.fileName = this.filePath.getFileName().toString();
    }

    public interface FileChangeListener {
        void onFileModified();

        void onFileDeleted();

        void onError(Exception e);
    }

    private static void copyWithTransferTo(String sourcePath, String destPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(destPath);
        copyWithTransferTo(source, dest);
    }

    private static void copyWithTransferTo(Path source, Path dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source.toFile());
                FileOutputStream fos = new FileOutputStream(dest.toFile());
                FileChannel sourceChannel = fis.getChannel();
                FileChannel destChannel = fos.getChannel()) {

            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }

    public void startWatching(FileChangeListener listener) {
        if (running) {
            return;
        }

        running = true;
        watchThread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                directory.register(watchService, ENTRY_MODIFY, ENTRY_DELETE);

                System.out.println("开始监听文件: " + filePath);

                while (running) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        break;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path changedFile = (Path) event.context();

                        // 只处理目标文件的变化
                        if (changedFile.toString().equals(fileName)) {
                            if (kind == ENTRY_MODIFY) {
                                // 小延迟避免文件正在被写入
                                listener.onFileModified();
                            } else if (kind == ENTRY_DELETE) {
                                listener.onFileDeleted();
                            }
                        }
                    }

                    if (!key.reset()) {
                        break;
                    }
                }
            } catch (Exception e) {
                listener.onError(e);
            }
        });

        watchThread.setDaemon(true);
        watchThread.start();
    }

    public void stopWatching() {
        running = false;
        if (watchThread != null) {
            watchThread.interrupt();
        }
    }

    // 使用示例
    public static void main(String[] args) throws InterruptedException {
        String sourceFile = "D:\\war5-jass\\jass_plugin\\w3x2lni_zhCN_v2.5.2\\w3x2lni_zhCN_v2.5.2\\0x4\\E2AF578809778F1821BF50DB4ECC3BAD.w3x";
        String targetFile = "D:\\war5\\Maps\\dz\\E2AF578809778F1821BF50DB4ECC3BAD.w3x";

        SingleFileWatcher watcher = new SingleFileWatcher(sourceFile);

        FileChangeListener listener = new FileChangeListener() {
            Timer t = new Timer();
            TimerTask timerTask = null;

            @Override
            public void onFileModified() {
                System.out.println("文件已被修改: " + sourceFile);
                // 这里可以重新加载文件内容
                // reloadFileContent();
                if (timerTask != null) {
                    timerTask.cancel();
                    t.purge();
                }
                timerTask = new TimerTask() {
                    public void run() {
                        try {
                            System.out.println("开始复制..");
                            copyWithTransferTo(sourceFile, targetFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                t.schedule(timerTask, 1000);
            }

            @Override
            public void onFileDeleted() {
                System.out.println("文件已被删除: " + sourceFile);
            }

            @Override
            public void onError(Exception e) {
                System.err.println("监控出错: " + e.getMessage());
            }
        };

        watcher.startWatching(listener);

        // 保持程序运行
        // Thread.sleep(300000); // 5分钟
        new Scanner(System.in).nextLine();
        watcher.stopWatching();
    }
}