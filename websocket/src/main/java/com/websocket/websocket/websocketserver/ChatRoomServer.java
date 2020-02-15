package com.websocket.websocket.websocketserver;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket")
public class ChatRoomServer {
    private Session session;                        //当前websocket的session
    private Map<String, Object> userInfo;           //当前session的用户信息
    private static Map<String, Object> serverInfo;  //服务器信息

    private static CopyOnWriteArraySet<ChatRoomServer> webSocketSet = new CopyOnWriteArraySet<ChatRoomServer>();

    /**
     * websocket连接的时候调用此方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);

        String str = session.getQueryString();  //取请求参数 url?key=value&key2=value2
        System.out.println(str);
        String[] params = str.split("&");

        userInfo = new HashMap<>();

        for (int i = 0; i < params.length; i++) {
            System.out.println(params[i]);
            String key = params[i].split("=")[0];
            String value = params[i].split("=")[1];
            userInfo.put(key, value);
        }

        System.out.println("open，当前在线人数"+webSocketSet.size());

        if(serverInfo == null){                     //初始化服务器参数
            serverInfo = new HashMap<>();
            serverInfo.put("username", "服务器");
        }

        serverInfo.put("member", webSocketSet.size());

//        addDate();

        try {
            sendObject(serverInfo, "欢迎光临！！");                             //欢迎上线用户
            groupTextMessaging(userInfo.get("username")+"上线了", false);   //服务器向其他用户发送上线提醒
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收客户端信息
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        try {
//            addDate();
            sendToSelf(message);
            groupTextMessaging(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发消息，默认发送用户的消息
     * @param message
     * @throws IOException
     */
    private void groupTextMessaging(String message) throws IOException {
        groupTextMessaging(message, true);
    }

    /**
     * 群发消息
     * @param message
     * @param isUser
     * @throws IOException
     */
    private void groupTextMessaging(String message, boolean isUser) throws IOException {

        //群发消息
        for(ChatRoomServer item: webSocketSet){
            if(item != this) {
                try {
                    item.sendObject(isUser? userInfo: serverInfo, message);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private void sendToSelf(String message) throws IOException {
        sendObject(userInfo, message);
    }

    /**
     * 断开websocket连接
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        this.session = null;

        System.out.println("一人断开连接，当前在线人数"+webSocketSet.size());
        serverInfo.put("member", webSocketSet.size());

        try {
            groupTextMessaging(userInfo.get("username")+"下线了", false);//服务器向其他用户发送下线提醒
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 传纯字符串
     * @param message
     * @throws IOException
     */
    private void sendMessage(String message) throws IOException {
        message = encodeResponseMsg(message);

        System.out.printf("userInfo %s  serverInfo %s", userInfo, serverInfo);

        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 包装传输对象
     * @param o
     * @param msg
     * @throws IOException
     */
    private void sendObject(Object o, String msg) throws IOException {
        addDate();

        JSONObject object = (JSONObject) JSONObject.toJSON(o);
        object.put("responseMsg", msg);

        sendMessage(object.toJSONString());
    }

    private String encodeResponseMsg(String responseMsg){
        responseMsg = responseMsg.replaceAll("&", "&amp;");
        responseMsg = responseMsg.replaceAll("<", "&lt;");
        responseMsg = responseMsg.replaceAll(">", "&gt;");
        responseMsg = responseMsg.trim();

        return responseMsg;
    }

    private void addDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        userInfo.put("date", date);
        serverInfo.put("date", date);
    }
}
