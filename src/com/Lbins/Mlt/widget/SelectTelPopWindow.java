package com.Lbins.Mlt.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ImageView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/19
 * Time: 20:58
 * 类的功能、说明写在此处.
 */
public class SelectTelPopWindow extends PopupWindow {
    private TextView item_msg, btn_sure, btn_cancel;
    private View mMenuView;
    private ImageView item_cover;
    private String nickname;
    private String company;
    private String cover;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public SelectTelPopWindow(Activity context, View.OnClickListener itemsOnClick, String nickname, String company, String cover) {
        super(context);
        this.nickname = nickname;
        this.company = company;
        this.cover = cover;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.item_dialog_tel, null);
        item_msg = (TextView) mMenuView.findViewById(R.id.item_msg);
        btn_sure = (TextView) mMenuView.findViewById(R.id.btn_sure);
        btn_cancel = (TextView) mMenuView.findViewById(R.id.btn_cancel);
        item_cover = (ImageView) mMenuView.findViewById(R.id.item_cover);
        item_msg.setText(nickname + "\n" +company);

        imageLoader.displayImage(cover, item_cover, UniversityApplication.txOptions, animateFirstListener);
        //取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });
        //设置按钮监听
        btn_sure.setOnClickListener(itemsOnClick);
        btn_cancel.setOnClickListener(itemsOnClick);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}