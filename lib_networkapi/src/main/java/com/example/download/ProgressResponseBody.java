package com.example.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by ZYB on 2017-03-10.
 */

public class ProgressResponseBody extends ResponseBody {
    private ResponseBody mOriginalResponseBody;
    private ProgressListener mProgressListener = null;
    private BufferedSource mBufferedSource = null;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        mOriginalResponseBody = responseBody;
        mProgressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return mOriginalResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mOriginalResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mOriginalResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long totalByteReaded = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long byteReaded = super.read(sink, byteCount);
                totalByteReaded += byteReaded != -1 ? byteReaded : 0;
                if (mProgressListener != null)
                    mProgressListener.onProgress(totalByteReaded, mOriginalResponseBody.contentLength(), byteReaded == -1);
                return byteReaded;
            }
        };
    }
}
