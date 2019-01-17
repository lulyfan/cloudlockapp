package com.example.operation;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketHelper {
//    private static final String PUSH_URL = "ws://smarthome.zhunilink.com:5009/websocket/userId";
    private static final String PUSH_URL = "ws://192.168.104.48:8201/websocket/userId";
    private WebSocket webSocket;
    private OkHttpClient client;
    private int userId = -1;
    private String appId;
    private boolean isSendUserId;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture scheduledFuture;
    private ScheduledFuture heartTimeoutScheduledFuture;   //用于心跳超时
    private ScheduledFuture sendHeartScheduledFuture;   //用于发送心跳

    private static final String TAG = "webSocket";
    private static final int CODE_CLOSE_NORMAL = 1000;  //正常关闭
    private static final int CODE_CONNECT_INTERRUPT = 1001; //与服务器的连接断开

    public WebSocketHelper(OkHttpClient client) {
        this.client = client;
    }

    public void initWebSocket(boolean isSendUserId) {

        if (webSocket != null) {
            return;
        }

        this.isSendUserId = isSendUserId;

        Request request = new Request.Builder()
                .url(PUSH_URL)
                .build();

        if (client != null) {
            webSocket = client.newWebSocket(request, webSocketListener);
        }
    }

    public void setUserId(int userId, String appId) {
        this.userId = userId;
        this.appId = appId;
    }

    public void sendUserId(int userId, String appId) {

        Log.i(TAG, "send userId:" + userId + " appId:" + appId);

        if (userId == -1) {
            return;
        }

        this.userId = userId;
        this.appId = appId;

        if (webSocket != null) {
            webSocket.send("userId:" + userId + ",appid:" + appId);
        }
    }

    public void sendUserId() {
        sendUserId(userId, appId);
    }

    WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(final WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            Log.i(TAG, "open");

            if (isSendUserId) {
                //每隔一秒发送一次userId,发送5次, 防止过早发送而没有收到推送，太晚发送又丢失某些推送
                for (int delay=0, i=0; i<5; i++, delay+=1000) {
                    executor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            sendUserId();

                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }

                sendHeartScheduledFuture = executor.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        sendUserId();
                        heartTimeoutScheduledFuture = executor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("websocket","与服务器的连接断开");
                                webSocket.close(1001, "与服务器连接断开");
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }, 30, 30, TimeUnit.SECONDS);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            if ("ok".equals(text)) {
                handleHeartData();
                return;
            }

            if (webSocketDataListener != null) {
                webSocketDataListener.onReceive(text);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.i(TAG, "websocket onClosed:" + reason);

            WebSocketHelper.this.webSocket = null;
            if (sendHeartScheduledFuture != null) {
                sendHeartScheduledFuture.cancel(true);
                sendHeartScheduledFuture = null;
            }

            if (code == CODE_CLOSE_NORMAL) {
                return;
            }

            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    initWebSocket(true);
                }
            }, 2000, TimeUnit.MILLISECONDS);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Log.i(TAG, "websocket onFailure:" + t.getMessage());

            WebSocketHelper.this.webSocket = null;
            if (sendHeartScheduledFuture != null) {
                sendHeartScheduledFuture.cancel(true);
                sendHeartScheduledFuture = null;
            }

            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }

            scheduledFuture = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    initWebSocket(true);
                }
            }, 2000, TimeUnit.MILLISECONDS);

        }
    };

    //处理心跳数据
    private void handleHeartData() {
        Log.i("websocket", "收到websocket心跳包");
        if (heartTimeoutScheduledFuture != null) {
            heartTimeoutScheduledFuture.cancel(true);
            heartTimeoutScheduledFuture = null;
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(CODE_CLOSE_NORMAL, "主动关闭");
            webSocket = null;
        }
    }

    private WebSocketDataListener webSocketDataListener;


    public void setWebSocketDataListener(WebSocketDataListener webSocketDataListener) {
        this.webSocketDataListener = webSocketDataListener;
    }

    public interface WebSocketDataListener {
        void onReceive(String data);
    }
}
