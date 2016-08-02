package com.Lbins.Mlt.upload;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.util.Map;

/**
 * Created by liuzwei on 2015/3/31.
 */
public class CommonUtil {

    private volatile static RequestQueue requestQueue;
    private volatile static RequestQueue uploadRequestQueue;
    private volatile static Gson gson;

    /**
     * ����RequestQueue���� *
     */
    public static RequestQueue getQueue(Context context) {
        if (requestQueue == null) {
            synchronized (CommonUtil.class) {
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                }
            }
        }
        return requestQueue;
    }

    public static RequestQueue getUploadRequestQueue(Context context) {
        if (uploadRequestQueue == null) {
            synchronized (CommonUtil.class) {
                if (uploadRequestQueue == null) {
                    uploadRequestQueue = Volley.newRequestQueue(context, new MultiPartStack());
                }
            }
        }
        return uploadRequestQueue;
    }

    public static Gson getGson() {
        if (gson == null) {
            synchronized (CommonUtil.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    public static void addPutUploadFileRequest(Context context, final String url,
                                               final Map<String, File> files, final Map<String, String> params,
                                               final Response.Listener<String> responseListener, final Response.ErrorListener errorListener,
                                               final Object tag) {
        if (null == url || null == responseListener) {
            return;
        }

        MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
                Request.Method.POST, url, responseListener, errorListener) {

            @Override
            public Map<String, File> getFileUploads() {
                return files;
            }

            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }

        };

        getUploadRequestQueue(context).add(multiPartRequest);
    }
}
