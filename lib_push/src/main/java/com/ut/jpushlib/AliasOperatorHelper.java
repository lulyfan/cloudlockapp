package com.ut.jpushlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;

/**
 * 处理tagalias相关的逻辑
 */
public class AliasOperatorHelper {
    public static int sequence = 1;
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;

    public static final int ACTION_CHECK = 6;

    public static final int DELAY_SEND_ACTION = 1;

    public static final int DELAY_SET_MOBILE_NUMBER_ACTION = 2;

    private Context context;

    private static AliasOperatorHelper mInstance;

    private AliasOperatorHelper() {
    }

    public static AliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (AliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new AliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void setAlias(Context context, String alias) {
        delaySendHandler.removeMessages(DELAY_SEND_ACTION);
        setActionCache.clear();
        handleAction(context, sequence, new TagAliasBean(ACTION_SET, alias));
    }

    public void deleteAlias(Context context) {
        delaySendHandler.removeMessages(DELAY_SEND_ACTION);
        handleAction(context, sequence, new TagAliasBean(ACTION_DELETE, null));
    }


    private void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private SparseArray<Object> setActionCache = new SparseArray<Object>();

    public Object get(int sequence) {
        return setActionCache.get(sequence);
    }

    public Object remove(int sequence) {
        return setActionCache.get(sequence);
    }

    public void put(int sequence, Object tagAliasBean) {
        setActionCache.put(sequence, tagAliasBean);
    }


    @SuppressLint("HandlerLeak")
    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasBean) {
                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        setActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            handleAction(context, sequence, tagAliasBean);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 处理设置tag
     */
    public void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            return;
        }
        put(sequence, tagAliasBean);
        switch (tagAliasBean.action) {
            case ACTION_GET:
                JPushInterface.getAlias(context, sequence);
                break;
            case ACTION_DELETE:
                JPushInterface.deleteAlias(context, sequence);
                break;
            case ACTION_SET:
                JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                break;
            default:
                return;
        }
    }

    private boolean RetryActionIfNeeded(int errorCode, TagAliasBean tagAliasBean) {
        if (!Utils.isConnected(context)) {
            return false;
        }
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            if (tagAliasBean != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasBean;
                delaySendHandler.sendMessageDelayed(message, 1000 * 30);
                return true;
            }
        }
        return false;
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            setActionCache.remove(sequence);
        } else {
            RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean);
        }
    }

    public static class TagAliasBean {
        int action;
        String alias;

        public TagAliasBean(int action, String alias) {
            this.action = action;
            this.alias = alias;
        }

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", alias='" + alias + '\'' +
                    '}';
        }
    }


}
