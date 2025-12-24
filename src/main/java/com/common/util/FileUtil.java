package com.common.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class FileUtil {
  public static void copyWithTransferTo(String sourcePath, String destPath) throws IOException {
    Path source = Paths.get(sourcePath);
    Path dest = Paths.get(destPath);
    copyWithTransferTo(source, dest);
  }

  public static void copyWithTransferTo(Path source, Path dest) throws IOException {
    try (FileInputStream fis = new FileInputStream(source.toFile());
        FileOutputStream fos = new FileOutputStream(dest.toFile());
        FileChannel sourceChannel = fis.getChannel();
        FileChannel destChannel = fos.getChannel()) {

      sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
    }
  }
}
