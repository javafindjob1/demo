package com.chess.busi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class DeskServiceImpl implements DeskService {
  private Map<String, Desk> deskMap = new LinkedHashMap<>();
  private int DESK_NO = 2;
  private final ChannelGroup group;
  private final Map<String, User> userMap;

  public DeskServiceImpl(ChannelGroup group, Map<String, User> userMap) {
    this.group = group;
    this.userMap = userMap;
    Desk desk = new Desk();
    desk.setDeskId("board" + DESK_NO);
    deskMap.put(desk.getDeskId(), desk);
    DESK_NO++;
  }

  @Override
  public void addDesk(ChessReq desk2, ChannelHandlerContext ctx) {
    Desk desk = new Desk();
    desk.setDeskId("#board" + DESK_NO);
    deskMap.put(desk.getDeskId(), desk);
    DESK_NO++;
  }

  @Override
  public void getDeskList(ChessReq req, ChannelHandlerContext ctx) {
    System.out.println("getDeskList:" + ctx.channel().id().toString());
    ChessResponse result = new ChessResponse();
    result.setType("init");

    String cid = ctx.channel().id().toString();
    User user = userMap.get(cid);
    if (user != null) {
      System.out.println("用户标识："+user.getId());
      result.setPlayer(user.getId());
    }
    List<Desk> list = new ArrayList<>();
    list.addAll(deskMap.values());
    result.setData(list);
    result.setRetCode("0000");
    result.setRetMsg("ws连接成功");

    ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(result)));
  }

  @Override
  public void pace(ChessReq req, ChannelHandlerContext ctx) {
    System.out.println("pace:" + JSON.toJSONString(req));
    String lastPaceStr = req.getPace();
    Pace lastPace = new Pace(lastPaceStr);
    String deskId = req.getDeskId();
    int lastIndex = req.getIndex();

    ChessResponse result = new ChessResponse();
    result.setType("pace");
    result.setDeskId(deskId);

    String cid = ctx.channel().id().toString();
    User user = userMap.get(cid);
    if (user == null) {
      sendNotLogin(ctx, result);
      return;
    }

    String id = user.getId();

    Desk desk = deskMap.get(deskId);

    if (!desk.play()) {
      result.setRetCode("0003");
      result.setRetMsg("游戏尚未开始");
    } else if (!desk.getPlayers().containsKey(id)) {
      result.setRetCode("0004");
      result.setRetMsg("非本桌玩家");
    } else {
      if (lastIndex == desk.getIndex() + 1
          && desk.getSeat()[(lastIndex) % 2].getId().equals(id)) {
        Man key = desk.getManByPos(lastPace.getPos());
        boolean canMove = key.indexOfPs(lastPace.getNewPos());
        if (canMove) {
          desk.setIndex(lastIndex);
          desk.getPace().add(lastPaceStr);
          desk.setLastPace(lastPaceStr);
          desk.move(lastPace);

          result.setRetCode("0000");
          result.setData(desk);
          // 创建新的 TextWebSocketFrame 对象
          String resultStr = JSON.toJSONString(result);
          TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
          System.out.println("全局推送这步棋");
          group.writeAndFlush(modifiedFrame.retain());
          return;
        } else {
          result.setData(desk);
          result.setRetCode("0001");
          result.setRetMsg("不可以走这步棋");
        }
      } else {
        result.setData(desk);
        result.setRetCode("0002");
        result.setRetMsg("步数不正确");
      }
    }

    // 创建新的 TextWebSocketFrame 对象
    String resultStr = JSON.toJSONString(result);
    TextWebSocketFrame resultFrame = new TextWebSocketFrame(resultStr);
    ctx.pipeline().writeAndFlush(resultFrame.retain());
  }

  @Override
  public void sitDown(ChessReq req, ChannelHandlerContext ctx) {
    System.out.println("sitDown:" + JSON.toJSONString(req));
    String deskId = req.getDeskId();
    Desk desk = deskMap.get(deskId);

    ChessResponse result = new ChessResponse();
    result.setType("sitDown");
    result.setDeskId(deskId);

    String cid = ctx.channel().id().toString();
    User user = userMap.get(cid);
    if (user == null) {
      sendNotLogin(ctx, result);
      return;
    }

    String id = user.getId();

    result.setPlayer(id);

    boolean canSit = desk.sitDown(req.getMy(), user);
    if (canSit) {
      result.setRetCode("0000");
      result.setRetMsg("占座成功");
      result.setData(desk);
      // 创建新的 TextWebSocketFrame 对象
      String resultStr = JSON.toJSONString(result);
      TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
      group.writeAndFlush(modifiedFrame.retain());
    } else {
      result.setRetCode("0001");
      result.setRetMsg("已经有人了");
      result.setData(desk);
      // 创建新的 TextWebSocketFrame 对象
      String resultStr = JSON.toJSONString(result);
      TextWebSocketFrame resultFrame = new TextWebSocketFrame(resultStr);
      ctx.pipeline().writeAndFlush(resultFrame.retain());
    }

  }

  @Override
  public void play(ChessReq req, ChannelHandlerContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'play'");
  }

  public void restart(ChessReq req, ChannelHandlerContext ctx) {
    System.out.println("restart:" + JSON.toJSONString(req));
    String deskId = req.getDeskId();
    Desk desk = deskMap.get(deskId);

    ChessResponse result = new ChessResponse();
    result.setType("restart");
    result.setDeskId(deskId);

    String cid = ctx.channel().id().toString();
    User user = userMap.get(cid);
    if (user == null) {
      sendNotLogin(ctx, result);
      return;
    }

    String id = user.getId();

    if (!desk.getPlayers().containsKey(id)) {
      // 非本桌玩家

      result.setRetCode("0001");
      result.setRetMsg("非本桌玩家");
      result.setData(desk);
      // 创建新的 TextWebSocketFrame 对象
      String resultStr = JSON.toJSONString(result);
      TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
      ctx.writeAndFlush(modifiedFrame.retain());

    } else {
      desk.restart();

      result.setRetCode("0000");
      result.setData(desk);
      // 创建新的 TextWebSocketFrame 对象
      String resultStr = JSON.toJSONString(result);
      TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
      group.writeAndFlush(modifiedFrame.retain());
    }

  }

  @Override
  public void message(ChessReq req, ChannelHandlerContext ctx) {
    System.out.println("message:" + req.getMessage().length()
        + JSON.toJSONString(req).substring(0, Math.min(100, req.getMessage().length())));
    String deskId = req.getDeskId();

    ChessResponse result = new ChessResponse();
    result.setType("message");
    result.setDeskId(deskId);

    String cid = ctx.channel().id().toString();
    User user = userMap.get(cid);
    if (user != null) {
      result.setPlayer(user.getId());
    }

    result.setRetCode("0000");
    result.setRetMsg("消息");

    Message message = new Message();
    message.setMy(req.getMy());
    message.setType(req.getMessageType());
    result.setData(message);
    // 创建新的 TextWebSocketFrame 对象
    String resultStr;

    if (req.getMessageType().equals("image")) {
      resultStr = JSON.toJSONString(result);
      resultStr += req.getMessage();
    } else {
      message.setValue(req.getMessage());
      resultStr = JSON.toJSONString(result);
    }

    TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
    group.writeAndFlush(modifiedFrame.retain());
  }

  @Override
  public void regret(ChessReq req, ChannelHandlerContext ctx) {
    System.out.println("regret:" + JSON.toJSONString(req));
    String deskId = req.getDeskId();
    Desk desk = deskMap.get(deskId);

    ChessResponse result = new ChessResponse();
    result.setType("regret");
    result.setDeskId(deskId);

    String cid = ctx.channel().id().toString();
    User user = userMap.get(cid);
    if (user == null) {
      sendNotLogin(ctx, result);
      return;
    }

    String id = user.getId();
    result.setPlayer(id);

    List<String> pace = desk.getPace();
    Integer my = desk.getPlayers().get(id);
    if (my == null) {
      // 非对弈玩家不能悔棋
      System.out.println("非对弈玩家不能悔棋");
      result.setRetCode("0001");
      result.setRetMsg("非对弈玩家不能悔棋");
    } else if (desk.getPace().size() < 2) {
      // 游戏刚开始不能悔棋
      System.out.println("游戏刚开始不能悔棋");
      result.setRetCode("0002");
      result.setRetMsg("游戏刚开始不能悔棋");
    } else if (my == 1 && pace.size() % 2 != 0 || my == -1 && pace.size() % 2 != 1) {
      System.out.println("不在黑（红）棋的回合内不可以悔棋");
      result.setRetCode("0003");
      result.setRetMsg("不在黑（红）棋的回合内不可以悔棋");
    } else {
      desk.regret();
      result.setRetCode("0000");
      result.setRetMsg("悔棋");
    }
    result.setData(desk);
    // 创建新的 TextWebSocketFrame 对象
    String resultStr = JSON.toJSONString(result);
    TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
    group.writeAndFlush(modifiedFrame.retain());
  }

  public void sendNotLogin(ChannelHandlerContext ctx, ChessResponse result) {
    System.out.println("未登录");
    result.setRetCode("NOT-LOGIN");
    result.setRetMsg("未登录");
    String resultStr = JSON.toJSONString(result);
    TextWebSocketFrame modifiedFrame = new TextWebSocketFrame(resultStr);
    ctx.pipeline().channel().writeAndFlush(modifiedFrame.retain());
  }

}
