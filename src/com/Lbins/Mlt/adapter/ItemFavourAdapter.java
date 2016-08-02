package com.Lbins.Mlt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.module.Favour;
import com.Lbins.Mlt.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemFavourAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Favour> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemFavourAdapter(List<Favour> lists, Context mContect) {
        this.lists = lists;
        this.mContect = mContect;
    }

    public void refresh(List<Favour> d) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_favour, null);
            holder.btn_share = (ImageView) convertView.findViewById(R.id.btn_share);
            holder.btn_tel = (ImageView) convertView.findViewById(R.id.btn_tel);
            holder.btn_pic = (ImageView) convertView.findViewById(R.id.btn_pic);
            holder.head = (ImageView) convertView.findViewById(R.id.head);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
//            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.img_xinyong = (ImageView) convertView.findViewById(R.id.img_xinyong);
            holder.img_xiehui = (ImageView) convertView.findViewById(R.id.img_xiehui);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Favour cell = lists.get(position);
        if (cell != null && !StringUtil.isNullOrEmpty(cell.getMm_msg_id())) {
            String title = (cell.getMm_emp_company() == null ? "" : cell.getMm_emp_company()) + " " + (cell.getMm_emp_nickname() == null ? "" : cell.getMm_emp_nickname());
            holder.nickname.setText(title);
            holder.dateline.setText(cell.getDatelineRecord());
            String msg = cell.getMm_msg_content() == null ? "" : cell.getMm_msg_content();
//            if(msg.length() > 80){
//                msg = msg.substring(0,79)+"...";
//            }
//            holder.title.setText(cell.getMm_msg_title()==null?"":cell.getMm_msg_title());
            holder.content.setText(msg);

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

            imageLoader.displayImage(cell.getMm_emp_cover(), holder.head, UniversityApplication.txOptions, animateFirstListener);
            if (StringUtil.isNullOrEmpty(cell.getMm_msg_picurl())) {
                holder.btn_pic.setVisibility(View.GONE);
            } else {
                holder.btn_pic.setVisibility(View.VISIBLE);
            }


            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontSize)) {
                holder.content.setTextSize(Float.valueOf(UniversityApplication.fontSize));
                holder.nickname.setTextSize(Float.valueOf(UniversityApplication.fontSize));
                holder.dateline.setTextSize(Float.valueOf(UniversityApplication.fontSize));
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
        }

        //
        holder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, null);
            }
        });
        holder.btn_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 3, null);
            }
        });
        holder.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 4, null);
            }
        });
        holder.btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 5, null);
            }
        });


        return convertView;
    }

    class ViewHolder {
        ImageView btn_share;
        ImageView btn_pic;
        ImageView btn_tel;
        ImageView head;
        TextView nickname;
        TextView dateline;
        //        TextView title;
        TextView content;
        ImageView img_xinyong;
        ImageView img_xiehui;


    }
}
