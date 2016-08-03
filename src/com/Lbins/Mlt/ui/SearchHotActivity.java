package com.Lbins.Mlt.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemSearchHotAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.data.HotWordObjData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.HotWordObj;
import com.Lbins.Mlt.util.IatSettings;
import com.Lbins.Mlt.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.Lbins.Mlt.util.JsonParser;
import com.iflytek.cloud.*;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by zhl on 2016/8/2.
 */
public class SearchHotActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private EditText keyword;
    private List<Map<String, String>> mylist = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> splitList = new ArrayList<Map<String, String>>();
    PullToRefreshListView lstv;
    ItemSearchHotAdapter adapter;
    List<HotWordObj> listHotWord = new ArrayList<HotWordObj>();
    List<HotWordObj> hotWordObjs = new ArrayList<HotWordObj>();

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private static String TAG = SearchHotActivity.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private Toast mToast;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_hot_activity);

        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.btn_search).setOnClickListener(this);
        keyword = (EditText) this.findViewById(R.id.keyword);
        keyword.addTextChangedListener(watcher);
        hotWordObjs = DBHelper.getInstance(SearchHotActivity.this).getWords();
        lstv = (PullToRefreshListView) findViewById(R.id.lstv);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        // 配置适配器
        adapter = new ItemSearchHotAdapter(this, mylist, splitList); // 布局里的控件id
        adapter.setOnClickContentItemListener(this);
        lstv.setAdapter(adapter);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                lstv.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                lstv.onRefreshComplete();
            }
        });

        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                lists.get(position - 1).setIs_read("1");
//                adapter.notifyDataSetChanged();
//                recordVO = lists.get(position - 1);
//                DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
            }
        });
        progressDialog = new ProgressDialog(SearchHotActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        setData(); //设置数据

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(SearchHotActivity.this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        findViewById(R.id.iat_recognize).setOnClickListener(SearchHotActivity.this);

        mIatDialog = new RecognizerDialog(SearchHotActivity.this, mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
            }
        }
    };

    private void setData() {
        // 组织数据源
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("itemTitle", "历史搜索");
        mylist.add(mp);
        splitList.add(mp);
        for (int i = 0; i < hotWordObjs.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("itemTitle", hotWordObjs.get(i).getMm_hot_word_title());
            mylist.add(map);
        }
        initData();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //todo
        }
    };

    int ret = 0; // 函数调用返回值

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_search:
            {
                //搜索
                if(!StringUtil.isNullOrEmpty(keyword.getText().toString())){
                    HotWordObj hotWordObj = new HotWordObj();
                    hotWordObj.setMm_hot_word_title(keyword.getText().toString());
                    DBHelper.getInstance(SearchHotActivity.this).saveWord(hotWordObj);

                    Intent searchIntent = new Intent(SearchHotActivity.this, SearchRecordActivity.class);
                    searchIntent.putExtra("content" , keyword.getText().toString());
                    startActivity(searchIntent);
                    finish();
                }
            }
                break;
            case R.id.iat_recognize:
            {
                //输入
                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(SearchHotActivity.this, "iat_recognize");

                mIatResults.clear();
                // 设置参数
                setParam();
                boolean isShowDialog = mSharedPreferences.getBoolean(
                        getString(R.string.pref_key_iat_show), true);
                if (isShowDialog) {
                    // 显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    mIatDialog.show();
                    showMsg(SearchHotActivity.this, getString(R.string.text_begin));
                } else {
                    // 不显示听写对话框
                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
//                        showMsg(SearchHotActivity.this, "听写失败,错误码：" + ret);
                    } else {
                        showMsg(SearchHotActivity.this, getString(R.string.text_begin));
                    }
                }
            }
                break;
        }
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showMsg(SearchHotActivity.this, "开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showMsg(SearchHotActivity.this, error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showMsg(SearchHotActivity.this, "结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showMsg(SearchHotActivity.this, "当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        if(!StringUtil.isNullOrEmpty(resultBuffer.toString()) && resultBuffer.toString().length() > 0){
            keyword.setText(resultBuffer.toString().substring(0, resultBuffer.toString().length()-1));
        }else {
            if(!StringUtil.isNullOrEmpty(resultBuffer.toString())){
                keyword.setText(resultBuffer.toString());
            }
        }
//        mResultText.setSelection(mResultText.length());
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }
        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
//            showMsg(SearchHotActivity.this, error.getPlainDescription(true));
        }

    };

    void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_HOT_WORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    HotWordObjData data = getGson().fromJson(s, HotWordObjData.class);
                                    listHotWord = data.getData();
                                    Map<String, String> mp = new HashMap<String, String>();
                                    mp.put("itemTitle", "热门搜索");
                                    mylist.add(mp);
                                    splitList.add(mp);
                                    for (int i = 0; i < listHotWord.size(); i++) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("itemTitle", listHotWord.get(i).getMm_hot_word_title());
                                        mylist.add(map);
                                    }
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SearchHotActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }


    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag){
            case 1:
                String str = (String) object;
                if(!StringUtil.isNullOrEmpty(str) && !"历史搜索".equals(str)&& !"热门搜索".equals(str)){
                    keyword.setText(str);
                }
                Intent searchIntent = new Intent(SearchHotActivity.this, SearchRecordActivity.class);
                searchIntent.putExtra("content" , keyword.getText().toString());
                startActivity(searchIntent);
                finish();
                break;
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
    }

}
