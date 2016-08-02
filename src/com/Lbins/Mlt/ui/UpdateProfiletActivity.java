package com.Lbins.Mlt.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.AnimateFirstDisplayListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.util.CompressPhotoUtil;
import com.Lbins.Mlt.util.FileUtils;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.SelectPhoPop;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/18.
 * 更新个人资料
 */
public class UpdateProfiletActivity extends BaseActivity implements View.OnClickListener {
    Resources res;
    private EditText mm_emp_company;
    private EditText mm_emp_card;
    private EditText mm_emp_company_address;

    private ImageView pic_one;
    private ImageView pic_two;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");
    private String txpic = "";
    private String pics = "";

    private String piconeStr = "";
    private String pictwoStr = "";

    //七牛
    AsyncHttpClient client = new AsyncHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_ziliao_activity);
        res = getResources();
        initView();
    }

    void initView() {
        this.findViewById(R.id.btn_one).setOnClickListener(this);
        this.findViewById(R.id.btn_two).setOnClickListener(this);
        this.findViewById(R.id.btn).setOnClickListener(this);
        pic_one = (ImageView) this.findViewById(R.id.pic_one);
        pic_two = (ImageView) this.findViewById(R.id.pic_two);
        pic_one.setOnClickListener(this);
        pic_two.setOnClickListener(this);
        mm_emp_company = (EditText) this.findViewById(R.id.mm_emp_company);
        mm_emp_card = (EditText) this.findViewById(R.id.mm_emp_card);
        mm_emp_company_address = (EditText) this.findViewById(R.id.mm_emp_company_address);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn:
                //确定
                if (StringUtil.isNullOrEmpty(mm_emp_card.getText().toString())) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_card));
                    return;
                }
                if (StringUtil.isNullOrEmpty(mm_emp_company.getText().toString())) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_company));
                    return;
                }
                if (mm_emp_company.getText().toString().length() > 14) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_company_one));
                    return;
                }
                if (StringUtil.isNullOrEmpty(mm_emp_company_address.getText().toString())) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_company_address));
                    return;
                }
                if (mm_emp_company_address.getText().toString().length() > 20) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_company_address_one));
                    return;
                }
                if (StringUtil.isNullOrEmpty(txpic)) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_company_faren));
                    return;
                }
                if (StringUtil.isNullOrEmpty(pics)) {
                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.please_input_zhizhao));
                    return;
                }
                progressDialog = new ProgressDialog(UpdateProfiletActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                uploadPicMine(txpic, "1");
                uploadPicMine(pics, "2");
                break;
            case R.id.btn_one:
                //点击实例
            {
//                Intent ViewOne = new Intent(UpdateProfiletActivity.this, DemoOneActivity.class);
//                startActivity(ViewOne);
            }
            break;
            case R.id.btn_two:
                //点击实例
            {
//                Intent ViewOne = new Intent(UpdateProfiletActivity.this, DemoTwoActivity.class);
//                startActivity(ViewOne);
            }
            break;
            case R.id.pic_one:
                //
                tmpSelect = "1";
                showSelectImageDialog();
                break;
            case R.id.pic_two:
                //
                tmpSelect = "2";
                showSelectImageDialog();
                break;
        }
    }

    //
//    void uploadPic(){
//        File file = new File(txpic);
//        Map<String, File> files = new HashMap<String, File>();
//        files.put("file", file);
//        Map<String, String> params = new HashMap<String, String>();
//        CommonUtil.addPutUploadFileRequest(
//                this,
//                InternetURL.UPLOAD_FILE,
//                files,
//                params,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            SuccessData data = getGson().fromJson(s, SuccessData.class);
//                            if (Integer.parseInt(data.getCode()) == 200) {
//                                piconeStr = data.getData();
//                            } else {
//                                Toast.makeText(UpdateProfiletActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
//                                if (progressDialog != null) {
//                                    progressDialog.dismiss();
//                                }
//                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Toast.makeText(UpdateProfiletActivity.this, R.string.publish_error_two, Toast.LENGTH_SHORT).show();
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                null);
//    }
//
//    void uploadPicT(){
//        File file = new File(pics);
//        Map<String, File> files = new HashMap<String, File>();
//        files.put("file", file);
//        Map<String, String> params = new HashMap<String, String>();
//        CommonUtil.addPutUploadFileRequest(
//                this,
//                InternetURL.UPLOAD_FILE,
//                files,
//                params,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            SuccessData data = getGson().fromJson(s, SuccessData.class);
//                            if (Integer.parseInt(data.getCode()) == 200) {
//                                pictwoStr = data.getData();
//                                reg();
//                            } else {
//                                Toast.makeText(UpdateProfiletActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
//                                if (progressDialog != null) {
//                                    progressDialog.dismiss();
//                                }
//                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Toast.makeText(UpdateProfiletActivity.this, R.string.publish_error_two, Toast.LENGTH_SHORT).show();
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                null);
//    }

    void uploadPicMine(String txpic, final String tmpPic) {
        Bitmap bm = FileUtils.getSmallBitmap(txpic);
        final String cameraImagePath = FileUtils.saveBitToSD(bm, System.currentTimeMillis() + ".jpg");
        Map<String, String> map = new HashMap<String, String>();
        map.put("space", InternetURL.QINIU_SPACE);
        RequestParams params = new RequestParams(map);
        client.get(InternetURL.UPLOAD_TOKEN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String token = response.getString("data");
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(StringUtil.getBytes(cameraImagePath), StringUtil.getUUID(), token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    //key
                                    if ("1".equals(tmpPic)) {
                                        piconeStr = key;
                                    }
                                    if ("2".equals(tmpPic)) {
                                        pictwoStr = key;
                                    }
                                    if (!StringUtil.isNullOrEmpty(piconeStr) && !StringUtil.isNullOrEmpty(pictwoStr)) {
                                        //已经上传完成
                                        update();
                                    }
                                }
                            }, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private Uri uri;
    private SelectPhoPop selectPhoPop;
    String tmpSelect = "";

    // 选择相册，相机
    private void showSelectImageDialog() {
        selectPhoPop = new SelectPhoPop(UpdateProfiletActivity.this, itemsOnClick);
        //显示窗口
        selectPhoPop.showAtLocation(UpdateProfiletActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            selectPhoPop.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppCover.jpg")));
                    startActivityForResult(camera, 2);
                }
                break;
                case R.id.mapstorage: {
                    Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
                    mapstorage.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(mapstorage, 1);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
//                    setPicToView(data);
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppCover.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                if ("1".equals(tmpSelect)) {
                    txpic = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                    pic_one.setImageBitmap(photo);
                } else {
                    pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                    pic_two.setImageBitmap(photo);
                }
            }
        }
    }

    void update() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(UpdateProfiletActivity.this, getResources().getString(R.string.caozuo_success));
                                    save("is_upate_profile", "1");//1是否补充资料 0否 1是
                                    save("mm_emp_company", mm_emp_company.getText().toString());
                                    save("mm_emp_company_address", mm_emp_company_address.getText().toString());
                                    save("mm_emp_card", mm_emp_card.getText().toString());

                                    if (piconeStr.startsWith("upload")) {
                                        piconeStr = (InternetURL.INTERNAL + piconeStr);
                                    } else {
                                        piconeStr = (InternetURL.QINIU_URL + piconeStr);
                                    }
                                    if (pictwoStr.startsWith("upload")) {
                                        pictwoStr = (InternetURL.INTERNAL + pictwoStr);
                                    } else {
                                        pictwoStr = (InternetURL.QINIU_URL + pictwoStr);
                                    }

                                    save("mm_emp_cover", piconeStr);
                                    save("mm_emp_company_pic", pictwoStr);

                                    finish();
                                } else if (Integer.parseInt(code) == 1) {
                                    Toast.makeText(UpdateProfiletActivity.this, R.string.caozuo_error_one, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateProfiletActivity.this, R.string.caozuo_error_one, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(UpdateProfiletActivity.this, R.string.caozuo_error_one, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(UpdateProfiletActivity.this, R.string.caozuo_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_company", mm_emp_company.getText().toString());
                params.put("mm_emp_company_address", mm_emp_company_address.getText().toString());
                params.put("mm_emp_card", mm_emp_card.getText().toString());
                if (!StringUtil.isNullOrEmpty(piconeStr)) {
                    params.put("mm_emp_cover", piconeStr);
                }
                if (!StringUtil.isNullOrEmpty(pictwoStr)) {
                    params.put("mm_emp_company_pic", pictwoStr);
                }
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

    public void onDestroy() {
        super.onPause();
    }

    ;
}
