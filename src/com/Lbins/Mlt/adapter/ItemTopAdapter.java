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
import com.Lbins.Mlt.module.PaihangObj;
import com.Lbins.Mlt.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemTopAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PaihangObj> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemTopAdapter(List<PaihangObj> lists, Context mContect) {
        this.lists = lists;
        this.mContect = mContect;
    }

    public void refresh(List<PaihangObj> d) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_top, null);
            holder.btn_tel = (TextView) convertView.findViewById(R.id.btn_tel);
            holder.head = (ImageView) convertView.findViewById(R.id.head);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.img_xinyong = (TextView) convertView.findViewById(R.id.img_xinyong);
            holder.img_xiehui = (TextView) convertView.findViewById(R.id.img_xiehui);
            holder.star = (ImageView) convertView.findViewById(R.id.star);
            holder.btn_nav = (TextView) convertView.findViewById(R.id.btn_nav);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PaihangObj cell = lists.get(position);
        if (cell != null) {
            String title = (cell.getMm_emp_nickname() == null ? "" : cell.getMm_emp_nickname());
            holder.nickname.setText(title);
            holder.title.setText(cell.getMm_emp_company());
            String content = cell.getMm_emp_company_detail();
            if (!StringUtil.isNullOrEmpty(content)) {
                if (content.length() > 200) {
                    holder.content.setText(content.substring(0, 199));
                } else {
                    holder.content.setText(content);
                }
            } else {
                holder.content.setText(mContect.getString(R.string.no_content));
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
            switch (Integer.parseInt((cell.getMm_level_num() == null ? "0" : cell.getMm_level_num()))) {
                case 0:
                    holder.star.setImageResource(R.drawable.tree_icons_star_1);
                    break;
                case 1:
                    holder.star.setImageResource(R.drawable.tree_icons_star_2);
                    break;
                case 2:
                    holder.star.setImageResource(R.drawable.tree_icons_star_3);
                    break;
                case 3:
                    holder.star.setImageResource(R.drawable.tree_icons_star_4);
                    break;
                case 4:
                    holder.star.setImageResource(R.drawable.tree_icons_star_5);
                    break;
            }
            imageLoader.displayImage(cell.getMm_emp_cover(), holder.head, UniversityApplication.txOptions, animateFirstListener);

            if (!StringUtil.isNullOrEmpty(UniversityApplication.fontSize)) {
                holder.content.setTextSize(Float.valueOf(UniversityApplication.fontSize));
                holder.title.setTextSize(Float.valueOf(UniversityApplication.fontSize));
//                holder.nickname.setTextSize(Float.valueOf(UniversityApplication.fontSize));
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
        holder.btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 5, "111");
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView btn_tel;
        ImageView head;
        TextView nickname;
        TextView title;
        TextView content;
        TextView img_xinyong;
        TextView img_xiehui;
        ImageView star;
        TextView btn_nav;
    }
}
