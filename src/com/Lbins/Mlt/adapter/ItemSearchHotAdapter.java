package com.Lbins.Mlt.adapter;
import java.util.List;

import java.util.Map;



import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.TextView;
import com.Lbins.Mlt.R;

/**
 * Created by zhl on 2016/8/2.
 */
public class ItemSearchHotAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    private List<Map<String, String>> listData;

    private List<Map<String, String>> splitData;

    public ItemSearchHotAdapter(Context context,

                     List<Map<String, String>> listData,

                     List<Map<String, String>> splitData) {

        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
        this.splitData = splitData;
    }

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    @Override

    public int getCount() {
        return listData.size();
    }

    @Override

    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override

    public long getItemId(int position) {
        return position;
    }

    @Override

    public boolean isEnabled(int position) {
        if (splitData.contains(listData.get(position))) {
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (splitData.contains(listData.get(position))) {
            convertView = mInflater.inflate(R.layout.item_search_hot, null);
        } else {
            convertView = mInflater.inflate(R.layout.item_search_hot_tag, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.itemTitle);
        textView.setText(listData.get(position).get("itemTitle"));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, listData.get(position).get("itemTitle"));
            }
        });
        return convertView;
    }
}
