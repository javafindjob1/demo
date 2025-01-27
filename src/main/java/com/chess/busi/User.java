package com.chess.busi;

import java.util.Date;

import lombok.Data;

@Data
public class User {
  /** 用户id */
  private String id;
  /** 当前登录的channelId */
  private String wsChannelId;

  /** 昵称 */
  private String name;

  /** 正在下棋的桌子 */
  private String deskId;

  /** 活跃时间（走棋、重新开始、悔棋） */
  private Long updateTime;

  /** 下线时间 */
  private Long offlineTime;

  public void offline(){
    this.wsChannelId = null;
    this.offlineTime = new Date().getTime();
  }

  public void active(){
    this.updateTime = new Date().getTime();
  }

  public static void main(String[] args) {
    
    System.out.println(new Date().getTime());

  }



}
