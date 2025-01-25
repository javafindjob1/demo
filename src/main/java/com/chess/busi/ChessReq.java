package com.chess.busi;

import lombok.Data;

@Data
public class ChessReq {
  private String type;
  /** 桌子id */
  private String deskId;

  /** 占座 -1 黑色 1红色 */
  private Integer my;


  /** 走的最后一步棋的信息， 表示第index手棋，红色【黑色】走了6633这步棋 */
  private Integer index;
  private String player;
  private String pace;

  /** 公屏消息 */
  private String messageType;
  private String message;
}
