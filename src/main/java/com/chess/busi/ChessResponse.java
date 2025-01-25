package com.chess.busi;

import lombok.Data;

@Data
public class ChessResponse {
  private String type;
  /** 桌子id */
  private String deskId;
  /** 触发事件的玩家 */
  private String player;
  
  private String retCode;
  private String retMsg;

  private Object data;
}
