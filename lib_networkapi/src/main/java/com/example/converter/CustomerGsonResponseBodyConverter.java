/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.converter;

import android.util.DebugUtils;
import android.util.Log;

import com.example.entity.base.Result;
import com.example.i.INoLoginListener;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class CustomerGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    private INoLoginListener mNoLoginListener = null;

    public CustomerGsonResponseBodyConverter<T> setNoLoginListener(INoLoginListener iNoLoginListener) {
        this.mNoLoginListener = iNoLoginListener;
        return this;
    }

    CustomerGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String originBody = value.string();
            T result = null;
            try {
                result = adapter.fromJson(originBody);
            } catch (Exception e) {
                result = (T) originBody;
            }
            if (mNoLoginListener != null && (result instanceof Result<?>)) {
                if (((Result) result).code == 401 || "还未登录".equals(((Result) result).msg)) {
//                    Log.i(CustomerGsonResponseBodyConverter.class.getSimpleName(), "未登录错误！");
                    mNoLoginListener.onNoLogin();
                    ((Result) result).msg = "";
                }
            }
            return result;
//      return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
