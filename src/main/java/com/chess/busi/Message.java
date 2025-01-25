package com.chess.busi;

import lombok.Data;

@Data
public class Message {
  // 文本或者图片
  private String type = "text";
  private String value;
  private int my;
}
