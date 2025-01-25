package com.chess.busi;

import lombok.Data;

@Data
public class User {
  /** 用户id */
  private String id;
  /** 当前登录的channelId */
  private String wsChannelId;

  /** 昵称 */
  private String name;

}
