package com.ut.unilink.jobluetooth;

import java.nio.ByteBuffer;


public class DataAssemble {

    public static final byte[] BYTE_HEAD = {(byte) 0xA5, 0x5A};
    private ByteBuffer buffer = ByteBuffer.allocate(255);

    private static final int STATE_HEAD1 = 0;    //正在解析头1
    private static final int STATE_HEAD2 = 1;    //正在解析头2
    private static final int STATE_LENGTH = 2;//正在解析长度
    private static final int STATE_BODY = 3;    //正在解析报文内容（除了头外的其余部分）

    private int state;
    private int needMsgLength;      //还需读取的消息长度

    private ReceiveCallback mReceiveCallback;

    public interface ReceiveCallback {
        void onReceiveSuccess(byte[] data);
    }

    public void setReceiveCallback(ReceiveCallback mReceiveCallback) {
        this.mReceiveCallback = mReceiveCallback;
    }

    public static DataAssemble get() {
        return Holder.instance;
    }


    public void reset() {
        state = STATE_HEAD1;
        buffer.clear();
    }


    public void receiveData(final byte[] data) {

        int pos = 0;

        switch (state) {
            case STATE_HEAD1:
                if (data.length < 1) {
                    return;
                }

                if (data[0] == BYTE_HEAD[0]) {
                    buffer.put((byte) 0xA5);
                    state = STATE_HEAD2;
                }
                pos ++;
                break;

            case STATE_HEAD2:
                if (data.length < 1) {
                    return;
                }

                if (data[0] == BYTE_HEAD[1]) {
                    buffer.put((byte) 0x5A);
                    state = STATE_LENGTH;
                } else {
                    reset();
                }
                pos ++;
                break;

            case STATE_LENGTH:
                if (data.length < 1) {
                    return;
                }

                int dataLength = data[0] & 0xFF;   //获取正文长度
                buffer.put((byte) dataLength);
                needMsgLength = 4 + dataLength;
                state = STATE_BODY;
                pos ++;
                break;

            case STATE_BODY:
                int currentReadLength = Math.min(needMsgLength, data.length);
                buffer.put(data, 0, currentReadLength);
                needMsgLength -= currentReadLength;
                pos += currentReadLength;

                if (needMsgLength == 0) {
                    if (mReceiveCallback != null) {
                        byte[] b = new byte[buffer.position()];
                        buffer.flip();
                        buffer.get(b);
                        mReceiveCallback.onReceiveSuccess(b);
                        reset();
                    }
                }
                break;

            default:
        }

        if (data.length - pos == 0) {
            return;
        }

        byte[] a = new byte[data.length - pos];
        System.arraycopy(data, pos, a, 0, a.length);
        receiveData(a);
    }

    private static class Holder {
        private static final DataAssemble instance = new DataAssemble();
    }
}
