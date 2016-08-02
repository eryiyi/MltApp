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
import com.Lbins.Mlt.module.KefuTel;
import com.Lbins.Mlt.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemTelAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<KefuTel> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemTelAdapter(List<KefuTel> lists, Context mContect) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_tel, null);
            holder.tel = (TextView) convertView.findViewById(R.id.tel);
            holder.address = (TextView) convertView.findViewById(R.id.address);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final KefuTel cell = lists.get(position);
        if (cell != null) {
            holder.tel.setText(cell.getMm_tel());
            if ("0".equals(cell.getMm_tel_type())) {
                holder.address.setText((cell.getProvinceName()==null?"":cell.getProvinceName()) + (cell.getCityName()==null?"":cell.getCityName()) + (cell.getAreaName()==null?"":cell.getAreaName()));
            } else {
                holder.address.setText("全国客服");
            }

            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontSize)) {
                holder.address.setTextSize(Float.valueOf(UniversityApplication.fontSize));
                holder.tel.setTextSize(Float.valueOf(UniversityApplication.fontSize));
            }
            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontColor)) {
                if ("black".equals(UniversityApplication.fontColor)) {
                    holder.address.setTextColor(Color.BLACK);
                    holder.tel.setTextColor(Color.BLACK);
                }
                if ("gray".equals(UniversityApplication.fontColor)) {
                    holder.address.setTextColor(Color.GRAY);
                    holder.tel.setTextColor(Color.GRAY);
                }
                if ("blue".equals(UniversityApplication.fontColor)) {
                    holder.address.setTextColor(Color.BLUE);
                    holder.tel.setTextColor(Color.BLUE);
                }
                if ("orange".equals(UniversityApplication.fontColor)) {
                    holder.address.setTextColor(Color.YELLOW);
                    holder.tel.setTextColor(Color.YELLOW);
                }
                if ("red".equals(UniversityApplication.fontColor)) {
                    holder.address.setTextColor(Color.RED);
                    holder.tel.setTextColor(Color.RED);
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView address;
        TextView tel;

    }
}
