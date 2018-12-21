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
    private static final String PUSH_URL = "ws://smarthome.zhunilink.com:5009/websocket/userId";
    private WebSocket webSocket;
    private OkHttpClient client;
    private int userId = -1;
    private boolean isSendUserId;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture scheduledFuture;

    private static final String TAG = "webSocket";

    public WebSocketHelper(OkHttpClient client) {
        this.client = client;
        initWebSocket(false);
    }

    private void initWebSocket(boolean isSendUserId) {

        this.isSendUserId = isSendUserId;

        Request request = new Request.Builder()
                .url(PUSH_URL)
                .build();

        if (client != null) {
            webSocket = client.newWebSocket(request, webSocketListener);
        }
    }

    public void sendUserId(int userId) {

        Log.i(TAG, "send userId:" + userId);

        if (userId == -1) {
            return;
        }

        this.userId = userId;
        webSocket.send("userId:" + userId);
    }

    public void sendUserId() {
        sendUserId(userId);
    }

    WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(final WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            if (isSendUserId) {
                //每隔一秒发送一次userId,发送5次, 防止过早发送而没有收到推送，太晚发送又丢失某些推送
                for (int delay=0, i=0; i<5; i++, delay+=1000) {
                    executor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            sendUserId(userId);

                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            if (dataListener != null) {
                dataListener.onReceive(text);
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
//            Log.i(TAG, "websocket onClosed:" + reason);

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
//            Log.i(TAG, "websocket onFailure:" + t.getMessage());

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

    private DataListener dataListener;

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public interface DataListener {
        void onReceive(String data);
    }
}
