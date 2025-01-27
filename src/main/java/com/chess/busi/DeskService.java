package com.chess.busi;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

public interface DeskService {

  public Map<String,Desk> getDeskMap();

  public void addDesk(ChessReq req, ChannelHandlerContext ctx);

  public void getDeskList(ChessReq req, ChannelHandlerContext ctx);

  /**
   * 行棋
   * 
   * @param req
   * @param ctx
   */
  public void pace(ChessReq req, ChannelHandlerContext ctx);

  /**
   * 占座
   * 
   * @param req
   * @param ctx
   */
  public void sitDown(ChessReq req, ChannelHandlerContext ctx);

  /**
   * 接受game
   * 
   * @param req
   * @param ctx
   */
  public void play(ChessReq req, ChannelHandlerContext ctx);

  /**
   * 重新开始
   * 
   * @param req
   * @param ctx
   */
  public void restart(ChessReq req, ChannelHandlerContext ctx);


  /**
   * 公屏消息
   */
  public void message(ChessReq req, ChannelHandlerContext ctx);

  /**
   * 悔棋
   */
  public void regret(ChessReq req, ChannelHandlerContext ctx);

}
