package com.Lbins.Mlt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.Mlt.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/12.
 */
public class SpinnerListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;

    public SpinnerListViewAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (arg1 == null && list.size() != 0) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            arg1 = inflater.inflate(R.layout.spinner_item, null);
            viewHolder.textView = (TextView) arg1.findViewById(R.id.label);
            arg1.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) arg1.getTag();
        viewHolder.textView.setText(list.get(arg0));
        if (arg0 % 2 == 0) {
            //偶数
            viewHolder.textView.setBackgroundColor(Color.argb(250, 255, 255, 255)); //颜色设置
        } else {
            viewHolder.textView.setBackgroundColor(Color.argb(255, 224, 243, 250));//颜色设置
        }
        return arg1;
    }

    class ViewHolder {
        TextView textView;

    }
}
