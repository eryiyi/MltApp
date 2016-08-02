package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.AnimateFirstDisplayListener;
import com.Lbins.Mlt.adapter.ItemDetailPhotoAdapter;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.VideoPlayer;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/22.
 */
public class DetailRecordActivity extends BaseActivity implements View.OnClickListener {
    private ImageView head;
    private TextView nickname;
    private TextView dateline;
    private LinearLayout liner_type;
    private ImageView type_one;
    private ImageView type_two;
    private ImageView type_three;
    private TextView content;
    private GridView gridView;
    private ImageView sharebtn;
    private TextView telbtn;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private List<String> lists = new ArrayList<String>();
    ItemDetailPhotoAdapter adapterPhot;

    private RecordMsg recordVO;

    private RelativeLayout video_liner;
    private ImageView detail_video_pic;
    private ImageView detail_player_icon_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_record_activity);
        recordVO = (RecordMsg) getIntent().getExtras().get("info");
        initView();
        initData();
    }

    void initView() {
        this.findViewById(R.id.mback).setOnClickListener(this);
        head = (ImageView) this.findViewById(R.id.head);
        nickname = (TextView) this.findViewById(R.id.nickname);
        dateline = (TextView) this.findViewById(R.id.dateline);
        liner_type = (LinearLayout) this.findViewById(R.id.liner_type);
        type_one = (ImageView) this.findViewById(R.id.type_one);
        type_two = (ImageView) this.findViewById(R.id.type_two);
        type_three = (ImageView) this.findViewById(R.id.type_three);
        content = (TextView) this.findViewById(R.id.content);
        gridView = (GridView) this.findViewById(R.id.gridView);
        sharebtn = (ImageView) this.findViewById(R.id.sharebtn);
        telbtn = (TextView) this.findViewById(R.id.telbtn);
        video_liner = (RelativeLayout) this.findViewById(R.id.video_liner);
        detail_video_pic = (ImageView) this.findViewById(R.id.detail_video_pic);
        detail_player_icon_video = (ImageView) this.findViewById(R.id.detail_player_icon_video);


        sharebtn.setOnClickListener(this);
        telbtn.setOnClickListener(this);
        head.setOnClickListener(this);

        adapterPhot = new ItemDetailPhotoAdapter(lists, DetailRecordActivity.this);
        gridView.setAdapter(adapterPhot);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        this.findViewById(R.id.reportbtn).setOnClickListener(this);
        this.findViewById(R.id.detail_player_icon_video).setOnClickListener(this);
    }

    void initData() {
        //
        imageLoader.displayImage(recordVO.getMm_emp_cover(), head, UniversityApplication.txOptions, animateFirstListener);
        //判断视频是否有
        if(!StringUtil.isNullOrEmpty(recordVO.getMm_msg_video())){
            //有视频
            video_liner.setVisibility(View.VISIBLE);
            imageLoader.displayImage(recordVO.getMm_msg_picurl(), head, UniversityApplication.options, animateFirstListener);
        }else {
            video_liner.setVisibility(View.GONE);
        }
        nickname.setText(recordVO.getMm_emp_company() + recordVO.getMm_emp_nickname());
        dateline.setText((recordVO.getDateline() == null ? "" : recordVO.getDateline()) + " " + (recordVO.getArea() == null ? "" : recordVO.getArea()));
        content.setText(recordVO.getMm_msg_content());
        if ("1".equals(recordVO.getIs_chengxin())) {
            type_one.setVisibility(View.VISIBLE);
        } else {
            type_one.setVisibility(View.GONE);
        }
        if ("1".equals(recordVO.getIs_miaomu())) {
            type_two.setVisibility(View.VISIBLE);
        } else {
            type_two.setVisibility(View.GONE);
        }
        switch (Integer.parseInt((recordVO.getMm_level_num() == null ? "0" : recordVO.getMm_level_num()))) {
            case 0:
                type_three.setImageResource(R.drawable.tree_icons_star_1);
                break;
            case 1:
                type_three.setImageResource(R.drawable.tree_icons_star_2);
                break;
            case 2:
                type_three.setImageResource(R.drawable.tree_icons_star_3);
                break;
            case 3:
                type_three.setImageResource(R.drawable.tree_icons_star_4);
                break;
            case 4:
                type_three.setImageResource(R.drawable.tree_icons_star_5);
                break;
        }
        telbtn.setText(recordVO.getMm_emp_nickname() + recordVO.getMm_emp_mobile());
        if (StringUtil.isNullOrEmpty(recordVO.getMm_msg_video()) && !StringUtil.isNullOrEmpty(recordVO.getMm_msg_picurl())) {
            //视频为空 且有图片 说明是图片的
            final String[] picUrls = recordVO.getMm_msg_picurl().split(",");//图片链接切割
            for (String str : picUrls) {
                lists.add(str);
            }
            adapterPhot.notifyDataSetChanged();
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(DetailRecordActivity.this, GalleryUrlActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.putExtra(Constants.IMAGE_URLS, picUrls);
                    intent.putExtra(Constants.IMAGE_POSITION, position);
                    startActivity(intent);
                }
            });
        }


        if (!StringUtil.isNullOrEmpty(UniversityApplication.fontSize)) {
            nickname.setTextSize(Float.valueOf(UniversityApplication.fontSize));
            dateline.setTextSize(Float.valueOf(UniversityApplication.fontSize));
            content.setTextSize(Float.valueOf(UniversityApplication.fontSize));
        }
        if (!StringUtil.isNullOrEmpty(UniversityApplication.fontColor)) {
            if ("black".equals(UniversityApplication.fontColor)) {
                nickname.setTextColor(Color.BLACK);
                dateline.setTextColor(Color.BLACK);
                content.setTextColor(Color.BLACK);
            }
            if ("gray".equals(UniversityApplication.fontColor)) {
                nickname.setTextColor(Color.GRAY);
                dateline.setTextColor(Color.GRAY);
                content.setTextColor(Color.GRAY);
            }
            if ("blue".equals(UniversityApplication.fontColor)) {
                nickname.setTextColor(Color.BLUE);
                dateline.setTextColor(Color.BLUE);
                content.setTextColor(Color.BLUE);
            }
            if ("orange".equals(UniversityApplication.fontColor)) {
                nickname.setTextColor(Color.YELLOW);
                dateline.setTextColor(Color.YELLOW);
                content.setTextColor(Color.YELLOW);
            }
            if ("red".equals(UniversityApplication.fontColor)) {
                nickname.setTextColor(Color.RED);
                dateline.setTextColor(Color.RED);
                content.setTextColor(Color.RED);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mback:
                finish();
                break;
            case R.id.sharebtn:
                //分享
                break;
            case R.id.telbtn:
                //电话
                showTel(recordVO.getMm_emp_mobile(), recordVO.getMm_emp_nickname());
                break;
            case R.id.head:
                //头像
            {
                Intent profileV = new Intent(DetailRecordActivity.this, ProfileActivity.class);
                profileV.putExtra("id", recordVO.getMm_emp_id());
                startActivity(profileV);
            }
            break;
            case R.id.reportbtn:
                //举报
                showJubao();
                break;
            case R.id.detail_player_icon_video:
                String videoUrl = recordVO.getMm_msg_video();
                Intent intent = new Intent(DetailRecordActivity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
                break;
        }
    }

    // 拨打电话窗口
    private void showTel(final String tel, String nickname) {
        final Dialog picAddDialog = new Dialog(DetailRecordActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(DetailRecordActivity.this, R.layout.tel_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(tel + " " + nickname);
        //提交
        btn_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (!StringUtil.isNullOrEmpty(contreport)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                }
                picAddDialog.dismiss();
            }
        });

        //取消
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(DetailRecordActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(DetailRecordActivity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
                    return;
                }
                report(contreport);
                picAddDialog.dismiss();
            }
        });

        //举报取消
        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }


    public void report(final String contReport) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    showMsg(DetailRecordActivity.this, getResources().getString(R.string.report_success));
                                } else if (Integer.parseInt(code1) == 9) {
                                    Toast.makeText(DetailRecordActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(DetailRecordActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    Toast.makeText(DetailRecordActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(DetailRecordActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailRecordActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_id", recordVO.getMm_msg_id());
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_id_t", recordVO.getMm_emp_id());
                params.put("mm_report_content", contReport);
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
