package com.Lbins.Mlt.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.RecordSingData;
import com.Lbins.Mlt.util.HttpUtils;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.CustomerSpinner;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddVideoSuccessActivity extends BaseActivity implements OnClickListener{

//	private TextView text;//视频保存的路径
	private Button button1;//播放开关
	private Button button2;//暂停开关
	private Button button3;//重新播放开关
	private Button button4;//视频大小开关

	private VideoView videoView1;//视频播放控件
	private String file;//视频路径

	//内容
//    private EditText mm_msg_title;
	private EditText mm_msg_content;
	private CustomerSpinner msgTypeSpinner;
	private String mm_msg_type;
	ArrayAdapter<String> adapterEmpType;
	private ArrayList<String> empTypeList = new ArrayList<String>();

	AsyncHttpClient client = new AsyncHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_video_activity_success);
		Bundle bundle = getIntent().getExtras();
		file = bundle.getString("text");//获得拍摄的短视频保存地址
		init();
		setValue();
	}

	//初始化
	private void init() {
//		text = (TextView) findViewById(R.id.text);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);

		videoView1 = (VideoView) findViewById(R.id.videoView1);
		this.findViewById(R.id.back).setOnClickListener(this);
		mm_msg_content = (EditText) this.findViewById(R.id.mm_msg_content);
		msgTypeSpinner = (CustomerSpinner) this.findViewById(R.id.mm_msg_type);
		msgTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mm_msg_type = empTypeList.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		empTypeList.add(getResources().getString(R.string.please_select_msg_type));
		empTypeList.add(getResources().getString(R.string.type_qiugou));
		empTypeList.add(getResources().getString(R.string.type_gongying));
		adapterEmpType = new ArrayAdapter<String>(AddVideoSuccessActivity.this, android.R.layout.simple_spinner_item, empTypeList);
		msgTypeSpinner.setList(empTypeList);
		msgTypeSpinner.setAdapter(adapterEmpType);
		this.findViewById(R.id.btn).setOnClickListener(this);
		this.findViewById(R.id.btn_kf).setOnClickListener(this);

	}

	boolean isMobileNet, isWifiNet;
	
	//设置
	private void setValue() {
//		text.setText(file);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);

		videoView1.setVideoPath(file);
	}

	@Override
	public void onClick(View v) {
		try {
			isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
			isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
			if (!isMobileNet && !isWifiNet) {
				Toast.makeText(this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (v.getId()) {
			case R.id.button1:
				videoView1.start();
				break;

			case R.id.button2:
				videoView1.pause();
				break;

			case R.id.button3:
				videoView1.resume();
				videoView1.start();
				break;

			case R.id.button4:
				Toast.makeText(this, "视频长度："+(videoView1.getDuration()/1024)+"M", Toast.LENGTH_SHORT).show();
				break;

			case R.id.back:
				finish();
				break;
			case R.id.btn_kf:
				//联系客服
				Intent kefuV = new Intent(AddVideoSuccessActivity.this, SelectTelActivity.class);
				startActivity(kefuV);
				break;
			case R.id.btn:
				//先判断权限
				if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
					//已登录

				} else {
					//未登录
					Intent loginV = new Intent(AddVideoSuccessActivity.this, LoginActivity.class);
					startActivity(loginV);
					return;
				}
				if ("0".equals(getGson().fromJson(getSp().getString("is_fabugongying", ""), String.class)) && getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
					//没有发布苗木供应的权限
					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_error_one));
					return;
				}
				if ("0".equals(getGson().fromJson(getSp().getString("is_fabuqiugou", ""), String.class)) && getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
					//没有发布苗木供应的权限
					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_error_two));
					return;
				}

				if (StringUtil.isNullOrEmpty(mm_msg_type) || getResources().getString(R.string.add_error_three).equals(mm_msg_type)) {
					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.please_select_msg_type));
					return;
				}
				if (StringUtil.isNullOrEmpty(mm_msg_content.getText().toString())) {
					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.please_input_text));
					return;
				}

				if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_msg_length", ""), String.class))) {
					//发布信息长度不为空 判断是否超出长度
					if (mm_msg_content.getText().toString().length() > Integer.parseInt(getGson().fromJson(getSp().getString("mm_msg_length", ""), String.class))) {
						showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_error_four));
						return;
					}
				}

				if ("0".equals(getGson().fromJson(getSp().getString("is_video", ""), String.class))) {
					//不允许发布视频
					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_error_video));
					return;
				}

//				if (StringUtil.isNullOrEmpty((getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)))) {
//					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_error_nine) + (getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)) + getResources().getString(R.string.add_error_ten));
//					return;
//				}
				progressDialog = new ProgressDialog(AddVideoSuccessActivity.this);
				progressDialog.setIndeterminate(true);
				progressDialog.show();

				//检查有没有选择图片
				if (StringUtil.isNullOrEmpty(file)) {
					showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_video_error));
					return;
				} else {
						//七牛
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
									uploadManager.put(StringUtil.getBytes(file), StringUtil.getUUID(), token,
											new UpCompletionHandler() {
												@Override
												public void complete(String key, ResponseInfo info, JSONObject response) {
													//key
													publishAll(key);
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

				break;

			default:
				break;
		}
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 *
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = {0, 0};
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 *
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	//上传完后开始发布
	private void publishAll(final String key) {
		StringRequest request = new StringRequest(
				Request.Method.POST,
				InternetURL.SEND_RECORD_URL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String s) {
						if (StringUtil.isJson(s)) {
							try {
								JSONObject jo = new JSONObject(s);
								String code = jo.getString("code");
								if (Integer.parseInt(code) == 200) {
									RecordSingData data = getGson().fromJson(s, RecordSingData.class);
									showMsg(AddVideoSuccessActivity.this, getResources().getString(R.string.add_record_success));
									if (getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
										//调用广播，刷新主页
										Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS_QIUGOU);
										intent1.putExtra("addRecord", data.getData());
										sendBroadcast(intent1);
									}
									if (getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
										//调用广播，刷新主页
										Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS_GONGYING);
										intent1.putExtra("addRecord", data.getData());
										sendBroadcast(intent1);
									}
									finish();
								} else if (Integer.parseInt(code) == 3) {
									String str = getResources().getString(R.string.add_msg_one )+ (getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class)) + getResources().getString(R.string.tiao);
									showMsg(AddVideoSuccessActivity.this, str);
								} else {
									Toast.makeText(AddVideoSuccessActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(AddVideoSuccessActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
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
						Toast.makeText(AddVideoSuccessActivity.this, R.string.add_record_error_one, Toast.LENGTH_SHORT).show();
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
				params.put("mm_emp_msg_num", getGson().fromJson(getSp().getString("mm_emp_msg_num", ""), String.class));
				params.put("mm_msg_title", "");
				params.put("mm_msg_content", mm_msg_content.getText().toString());
				if (getResources().getString(R.string.type_qiugou).equals(mm_msg_type)) {
					params.put("mm_msg_type", "0");
				}
				if (getResources().getString(R.string.type_gongying).equals(mm_msg_type)) {
					params.put("mm_msg_type", "1");
				}
				params.put("mm_msg_video", key);
				params.put("provinceid", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
				params.put("cityid", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
				params.put("countryid", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
				if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
					params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
				} else {
					params.put("accessToken", "");
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

}
