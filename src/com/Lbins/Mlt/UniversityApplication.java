package com.Lbins.Mlt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.Lbins.Mlt.base.InternetURL;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.PlatformConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.yixia.camera.VCamera;
//import com.yixia.camera.demo.service.AssertService;
//import com.yixia.camera.util.DeviceUtils;

/**
 * author: ${zhanghailong}
 * Date: 2015/1/29
 * Time: 17:04
 * 类的功能、说明写在此处.
 */
public class UniversityApplication extends Application {
    // 运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    private ExecutorService lxThread;
    private Gson gson;
    private RequestQueue requestQueue;
    private SharedPreferences sp;

    private static UniversityApplication application;

    public static Context applicationContext;
    // login user name
    public final String PREF_USERNAME = "username";


//    public LocationClient mLocationClient;
//    public MyLocationListener mMyLocationListener;

//    public TextView mLocationResult,logMsg;
//    public TextView trigger,exit;
//    public Vibrator mVibrator;

//    public static  Double lat;
//    public static  Double lng;

    public static String lat;
    public static String lng;
    public static String area;
    public static String address;

    public static String fontSize;
    public static String fontColor;

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler crashHandler = CrashHandler.getInstance() ;
//        crashHandler.init(this) ;
        applicationContext = this;
        instance = this;
        application = this;
        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();
        lxThread = Executors.newFixedThreadPool(20);
        sp = getSharedPreferences("university_manage", Context.MODE_PRIVATE);
        imageLoader = new com.android.volley.toolbox.ImageLoader(requestQueue, new BitmapCache());
        initImageLoader(this);
        PlatformConfig.setWeixin(InternetURL.WEIXIN_APPID, InternetURL.WEIXIN_SECRET);
        //微信 appid appsecret
        PlatformConfig.setQQZone("1105177727", "BQ8MVq3t9yOFl1SP");
        // QQ和Qzone appid appkey

        // 讯飞
        SpeechUtility.createUtility(getApplicationContext(), "appid=" + InternetURL.XUNFEI_APPID);
    }


    public static Context getContext() {
        return application;
    }


    /**
     * 获取自定义线程池
     *
     * @return
     */
    public ExecutorService getLxThread() {
        if (lxThread == null) {
            lxThread = Executors.newFixedThreadPool(20);
        }
        return lxThread;
    }

    /**
     * 获取Gson
     *
     * @return
     */
    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * 获取Volley请求队列
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        return requestQueue;
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    public SharedPreferences getSp() {
        if (sp == null) {
            sp = getSharedPreferences("university_manage", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static DisplayImageOptions options;
    public static DisplayImageOptions adOptions;
    public static DisplayImageOptions txOptions;//头像图片

    public UniversityApplication() {

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_none)
                .showImageForEmptyUri(R.drawable.pic_none)    // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.pic_none)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                           // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                             // 设置下载的图片是否缓存在内存卡中
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)          //图片的解码类型
//                .displayer(new RoundedBitmapDisplayer(5))
                .build();                                       // 创建配置过得DisplayImageOption对象

        txOptions = new DisplayImageOptions.Builder()//头像
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)    // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.logo)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                           // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                             // 设置下载的图片是否缓存在内存卡中
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)          //图片的解码类型头像
                .build();

        adOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ad_mp)
                .showImageForEmptyUri(R.drawable.ad_mp)    // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ad_mp)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                           // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                             // 设置下载的图片是否缓存在内存卡中
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)          //图片的解码类型
//                .displayer(new RoundedBitmapDisplayer(5))
                .build();
    }

    /**
     * 初始化图片加载组件ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private com.android.volley.toolbox.ImageLoader imageLoader;

    private class BitmapCache implements com.android.volley.toolbox.ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }

    public com.android.volley.toolbox.ImageLoader getImageLoader() {
        return imageLoader;
    }


    private static UniversityApplication instance;

    // 构造方法
    // 实例化一次
    public synchronized static UniversityApplication getInstance() {
        if (null == instance) {
            instance = new UniversityApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // 关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

}

