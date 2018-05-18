package com.lei;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//这个类启动后不是单例，每增加一个websocket连接就会有新的实例
@ServerEndpoint("/WebsocketByAnnotation")
public class WebsocketByAnnotation {
 public WebsocketByAnnotation() {
     System.out.println("he");
 }

 @OnOpen
 public void open(Session session){
     System.out.println("open============sessionID:"+ session.getId());
 }

 @OnClose
 public void close(Session session){
     System.out.println("close============");
 }

 @OnMessage
 public void sendText(Session session ,String msg, boolean type){
//这里接收浏览器发过来的信息
     System.out.println("收到信息：" + msg);
     try {
         if (session.isOpen()) {
             session.getBasicRemote().sendText("server回复"+msg, type);
         }
     } catch (IOException e) {
    	 System.out.println("sendText方法的错误:"+e.toString());
         try {
             session.close();
         } catch (IOException e1) {
             // Ignore
         }
     }
 }
 @OnError
 public void onError(Throwable t) throws Throwable {
	 System.out.println("onError:"+t.toString());
	 t.printStackTrace();
 }
 
}