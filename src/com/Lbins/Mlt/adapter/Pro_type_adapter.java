package com.Lbins.Mlt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.module.CountryObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;


public class Pro_type_adapter extends BaseAdapter {
    ImageLoader imageLoader = ImageLoader.getInstance();//ͼƬ������
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    // ����Context
    private LayoutInflater mInflater;
    private List<CountryObj> list;
    private Context context;
    private CountryObj type;

    public Pro_type_adapter(Context context, List<CountryObj> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list != null && list.size() > 0)
            return list.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyView view;
        if (convertView == null) {
            view = new MyView();
            convertView = mInflater.inflate(R.layout.list_pro_type_item, null);
            view.name = (TextView) convertView.findViewById(R.id.typename);
            convertView.setTag(view);
        } else {
            view = (MyView) convertView.getTag();
        }
        if (list != null && list.size() > 0) {
            type = list.get(position);
            if (type != null) {
                view.name.setText(type.getArea());
            }
        }
        return convertView;
    }

    private class MyView {
        private TextView name;
    }

}
