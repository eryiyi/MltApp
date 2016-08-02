package com.Lbins.Mlt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.module.Notice;
import com.Lbins.Mlt.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemNoticeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Notice> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemNoticeAdapter(List<Notice> lists, Context mContect) {
        this.lists = lists;
        this.mContect = mContect;
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_notice, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Notice cell = lists.get(position);
        if (cell != null) {
            holder.title.setText(cell.getMm_notice_title());
            holder.content.setText(cell.getMm_notice_content());
            holder.dateline.setText(cell.getDateline());

            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontSize)) {
                holder.title.setTextSize(Float.valueOf(UniversityApplication.fontSize));
                holder.content.setTextSize(Float.valueOf(UniversityApplication.fontSize));
                holder.dateline.setTextSize(Float.valueOf(UniversityApplication.fontSize));
            }
            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontColor)) {
                if ("black".equals(UniversityApplication.fontColor)) {
                    holder.title.setTextColor(Color.BLACK);
                    holder.content.setTextColor(Color.BLACK);
                    holder.dateline.setTextColor(Color.BLACK);
                }
                if ("gray".equals(UniversityApplication.fontColor)) {
                    holder.title.setTextColor(Color.GRAY);
                    holder.content.setTextColor(Color.GRAY);
                    holder.dateline.setTextColor(Color.GRAY);
                }
                if ("blue".equals(UniversityApplication.fontColor)) {
                    holder.title.setTextColor(Color.BLUE);
                    holder.content.setTextColor(Color.BLUE);
                    holder.dateline.setTextColor(Color.BLUE);
                }
                if ("orange".equals(UniversityApplication.fontColor)) {
                    holder.title.setTextColor(Color.YELLOW);
                    holder.content.setTextColor(Color.YELLOW);
                    holder.dateline.setTextColor(Color.YELLOW);
                }
                if ("red".equals(UniversityApplication.fontColor)) {
                    holder.title.setTextColor(Color.RED);
                    holder.content.setTextColor(Color.RED);
                    holder.dateline.setTextColor(Color.RED);
                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView content;
        TextView dateline;
    }
}
