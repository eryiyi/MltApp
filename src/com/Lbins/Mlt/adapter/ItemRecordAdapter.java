package com.Lbins.Mlt.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.ui.Constants;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;
import  com.Lbins.Mlt.ui.GalleryUrlActivity;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemRecordAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<RecordMsg> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemRecordAdapter(List<RecordMsg> lists, Context mContect) {
        this.lists = lists;
        this.mContect = mContect;
    }

    public void refresh(List<RecordMsg> d) {
        lists = d;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_record, null);
//            holder.btn_share = (ImageView) convertView.findViewById(R.id.btn_share);
            holder.btn_tel = (TextView) convertView.findViewById(R.id.btn_tel);
//            holder.btn_pic = (ImageView) convertView.findViewById(R.id.btn_pic);
            holder.head = (ImageView) convertView.findViewById(R.id.head);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
//            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.img_xinyong = (TextView) convertView.findViewById(R.id.img_xinyong);
            holder.img_xiehui = (TextView) convertView.findViewById(R.id.img_xiehui);
//            holder.star = (ImageView) convertView.findViewById(R.id.star);
            holder.is_read = (TextView) convertView.findViewById(R.id.is_read);
            holder.btn_favour = (TextView) convertView.findViewById(R.id.btn_favour);
            holder.btn_nav = (TextView) convertView.findViewById(R.id.btn_nav);
//            holder.btn_video = (ImageView) convertView.findViewById(R.id.btn_video);
            holder.item_gf = (ImageView) convertView.findViewById(R.id.item_gf);
            holder.company = (TextView) convertView.findViewById(R.id.company);
            holder.item_address = (TextView) convertView.findViewById(R.id.item_address);
            holder.item_area = (TextView) convertView.findViewById(R.id.item_area);
            holder.gridView = (com.Lbins.Mlt.widget.PictureGridview) convertView.findViewById(R.id.gridView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.gridView.setVisibility(View.GONE);//隐藏九宫格
        holder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        final RecordMsg cell = lists.get(position);
        if (cell != null) {
            holder.nickname.setText(cell.getMm_emp_nickname() == null ? "" : cell.getMm_emp_nickname());
            if(!StringUtil.isNullOrEmpty(cell.getMm_emp_company())){
                holder.company.setVisibility(View.VISIBLE);
                holder.company.setText(cell.getMm_emp_company() == null ? "" : cell.getMm_emp_company());
            }else {
                holder.company.setVisibility(View.GONE);
            }
            holder.dateline.setText((cell.getDateline() == null ? "" : cell.getDateline()) + " ");
            String msg = cell.getMm_msg_content() == null ? "" : cell.getMm_msg_content();
            holder.content.setText(msg);
            holder.item_area.setText(cell.getArea() == null ? "" : cell.getArea());
            if(!StringUtil.isNullOrEmpty(cell.getMm_emp_company_address())){
                holder.item_address.setVisibility(View.VISIBLE);
                holder.item_address.setText(cell.getMm_emp_company_address());
            }else {
                holder.item_address.setVisibility(View.GONE);
            }
            if ("1".equals(cell.getIs_chengxin())) {
                holder.img_xinyong.setVisibility(View.VISIBLE);
            } else {
                holder.img_xinyong.setVisibility(View.GONE);
            }
            if ("1".equals(cell.getIs_miaomu())) {
                holder.img_xiehui.setVisibility(View.VISIBLE);
            } else {
                holder.img_xiehui.setVisibility(View.GONE);
            }
//            switch (Integer.parseInt((cell.getMm_level_num() == null ? "0" : cell.getMm_level_num()))) {
//                case 0:
//                    holder.star.setImageResource(R.drawable.tree_icons_star_1);
//                    break;
//                case 1:
//                    holder.star.setImageResource(R.drawable.tree_icons_star_2);
//                    break;
//                case 2:
//                    holder.star.setImageResource(R.drawable.tree_icons_star_3);
//                    break;
//                case 3:
//                    holder.star.setImageResource(R.drawable.tree_icons_star_4);
//                    break;
//                case 4:
//                    holder.star.setImageResource(R.drawable.tree_icons_star_5);
//                    break;
//            }
            imageLoader.displayImage(cell.getMm_emp_cover(), holder.head, UniversityApplication.txOptions, animateFirstListener);
//            if(!StringUtil.isNullOrEmpty(cell.getMm_msg_video())){
//                //说明存在视频
//                holder.btn_video.setVisibility(View.VISIBLE);
//                holder.btn_pic.setVisibility(View.GONE);
//            }else {
//                holder.btn_video.setVisibility(View.GONE);
//                //不存在视频
                if (!StringUtil.isNullOrEmpty(cell.getMm_msg_picurl())) {
                    //说明有图片
                    final String[] picUrls = cell.getMm_msg_picurl().split(",");//图片链接切割
                    if (picUrls.length > 0) {
                        //有多张图
                        holder.gridView.setVisibility(View.VISIBLE);
                        holder.gridView.setAdapter(new ImageGridViewAdapter(picUrls, mContect));
//                        if(picUrls.length ==1){
//                            //如果只有1张图片
//                            holder.gridview_detail_picture.setClickable(true);
//                            holder.gridview_detail_picture.setPressed(true);
//                            holder.gridview_detail_picture.setEnabled(true);
//                        }else {
                        holder.gridView.setClickable(true);
                        holder.gridView.setPressed(true);
                        holder.gridView.setEnabled(true);
//                        }
                        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(mContect, GalleryUrlActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                intent.putExtra(Constants.IMAGE_URLS, picUrls);
                                intent.putExtra(Constants.IMAGE_POSITION, position);
                                mContect.startActivity(intent);
                            }
                        });
                    }
            }

            RecordMsg recordMsg = DBHelper.getInstance(mContect).getRecord(cell.getMm_msg_id());
            if (recordMsg != null) {
                if ("1".equals(recordMsg.getIs_read())) {
                    //已读
//                    holder.is_read.setImageResource(R.drawable.tree_icons_read_1);
                    holder.is_read.setText("已读");
                    holder.is_read.setBackgroundColor(mContect.getResources().getColor(R.color.btn_green_normal));
                } else {
                    holder.is_read.setText("未读");
                    holder.is_read.setBackgroundColor(mContect.getResources().getColor(R.color.button_color_red_n));
                }
            } else {
                if ("1".equals(cell.getIs_read())) {
                    //已读
                    holder.is_read.setText("已读");
                    holder.is_read.setBackgroundColor(mContect.getResources().getColor(R.color.btn_green_normal));
                } else {
                    holder.is_read.setText("未读");
                    holder.is_read.setBackgroundColor(mContect.getResources().getColor(R.color.button_color_red_n));
                }
            }

            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontSize)) {
                holder.content.setTextSize(Float.valueOf(UniversityApplication.fontSize));
            }
            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontColor)) {
                if ("black".equals(UniversityApplication.fontColor)) {
                    holder.content.setTextColor(Color.BLACK);
                }
                if ("gray".equals(UniversityApplication.fontColor)) {
                    holder.content.setTextColor(Color.GRAY);
                }
                if ("blue".equals(UniversityApplication.fontColor)) {
                    holder.content.setTextColor(Color.BLUE);
                }
                if ("orange".equals(UniversityApplication.fontColor)) {
                    holder.content.setTextColor(Color.YELLOW);
                }
                if ("red".equals(UniversityApplication.fontColor)) {
                    holder.content.setTextColor(Color.RED);
                }
            }

//            if(position % 2 == 0){
//                //偶数
//                convertView.setBackgroundColor(Color.argb(250, 255, 255, 255)); //颜色设置
//            }else {
//                convertView.setBackgroundColor(Color.argb(255, 224, 243, 250));//颜色设置
//            }
        }

        //
//        holder.btn_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickContentItemListener.onClickContentItem(position, 1, "111");
//            }
//        });
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, "111");
            }
        });
        holder.btn_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 3, "111");
            }
        });
        holder.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 4, "111");
            }
        });
//        holder.btn_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickContentItemListener.onClickContentItem(position, 5, "111");
//            }
//        });
        holder.btn_favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 6, "111");
            }
        });
        holder.btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 7, "111");//导航
            }
        });
//        holder.btn_video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickContentItemListener.onClickContentItem(position, 8, "111");
//            }
//        });


        return convertView;
    }

    public static class ViewHolder {
//        ImageView btn_share;
//        ImageView btn_pic;
        TextView btn_tel;
        ImageView head;
        TextView nickname;
        TextView company;
        TextView dateline;
        TextView content;
        TextView img_xinyong;
        TextView img_xiehui;
//        ImageView star;
        TextView is_read;
        TextView btn_favour;
        TextView btn_nav;
        TextView item_area;
        TextView item_address;
        ImageView item_gf;
        com.Lbins.Mlt.widget.PictureGridview gridView;
//        ImageView btn_video;
    }
}
