package com.Lbins.Mlt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.module.FeiyongObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemVipAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<FeiyongObj> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemVipAdapter(List<FeiyongObj> lists, Context mContect) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_feiyong, null);
            holder.item_cart_select = (ImageView) convertView.findViewById(R.id.item_cart_select);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.btn_detail = (TextView) convertView.findViewById(R.id.btn_detail);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FeiyongObj cell = lists.get(position);
        if (cell != null) {
            holder.title.setText(cell.getMm_feiyong_name());
            holder.price.setText("￥" + cell.getMm_feiyong_jine());
            if ("1".equals(cell.getIs_select())) {
                holder.item_cart_select.setImageResource(R.drawable.selector_fill);
            } else {
                holder.item_cart_select.setImageResource(R.drawable.selector_hollow);
            }
            holder.item_cart_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, null);
                }
            });
            holder.btn_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 2, null);
                }
            });

        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_cart_select;
        TextView title;
        TextView price;
        TextView btn_detail;
    }
}
