package com.Lbins.Mlt.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.fragment.FuwuFragmentOne;
import com.Lbins.Mlt.fragment.FuwuFragmentTwo;

/**
 * Created by Administrator on 2016/2/22.
 */
public class FourFuwuActivity extends BaseActivity implements View.OnClickListener {
    private TextView back;
    public static String mm_fuwu_type;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private FuwuFragmentOne oneFragment;
    private FuwuFragmentTwo twoFragment;

    private ImageView foot_one;
    private ImageView foot_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.four_fuwu_activity);
        back = (TextView) this.findViewById(R.id.back);
        back.setOnClickListener(this);
        mm_fuwu_type = getIntent().getExtras().getString("mm_fuwu_type");
        switch (Integer.parseInt(mm_fuwu_type)) {
            case 0:
                back.setText(getResources().getString(R.string.miaomushop));
                break;
            case 1:
                back.setText(getResources().getString(R.string.zhuangchework));
                break;
            case 2:
                back.setText(getResources().getString(R.string.wuliucenter));
                break;
            case 3:
                back.setText(getResources().getString(R.string.jiajieteam));
                break;
            case 4:
                back.setText(getResources().getString(R.string.diaocheservie));
                break;
        }
        fm = getSupportFragmentManager();
        initView();

        switchFragment(R.id.foot_one);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                switchFragment(view.getId());
                break;
        }
    }

    private void initView() {
        foot_one = (ImageView) this.findViewById(R.id.foot_one);
        foot_two = (ImageView) this.findViewById(R.id.foot_two);
        foot_one.setOnClickListener(this);
        foot_two.setOnClickListener(this);
    }


    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_one:
                if (oneFragment == null) {
                    oneFragment = new FuwuFragmentOne();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }
                foot_one.setImageResource(R.drawable.toolbar_home_p);
                foot_two.setImageResource(R.drawable.toolbar_nearby);
                break;
            case R.id.foot_two:
                if (twoFragment == null) {
                    twoFragment = new FuwuFragmentTwo();
                    fragmentTransaction.add(R.id.content_frame, twoFragment);
                } else {
                    fragmentTransaction.show(twoFragment);
                }
                foot_one.setImageResource(R.drawable.toolbar_home);
                foot_two.setImageResource(R.drawable.toolbar_nearby_p);
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
        if (twoFragment != null) {
            ft.hide(twoFragment);
        }
    }


//    private class MyOnClickListener implements View.OnClickListener {
//        private int index=0;
//        public MyOnClickListener(int i){
//            index=i;
//        }
//        public void onClick(View v) {
//            viewPager.setCurrentItem(index);
//        }
//
//    }
//
//    public class MyViewPagerAdapter extends PagerAdapter {
//        private List<View> mListViews;
//
//        public MyViewPagerAdapter(List<View> mListViews) {
//            this.mListViews = mListViews;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) 	{
//            container.removeView(mListViews.get(position));
//        }
//
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(mListViews.get(position), 0);
//            return mListViews.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return  mListViews.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0==arg1;
//        }
//    }
//
//    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
//        public void onPageScrollStateChanged(int arg0) {
//        }
//
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//        public void onPageSelected(int arg0) {
//            if(arg0 == 0){
//                cursor1.setImageDrawable(getResources().getDrawable(R.drawable.line_bg));
//                cursor2.setImageDrawable(getResources().getDrawable(R.drawable.line_bg_white));
//            }
//            if(arg0 == 1){
//                cursor1.setImageDrawable(getResources().getDrawable(R.drawable.line_bg_white));
//                cursor2.setImageDrawable(getResources().getDrawable(R.drawable.line_bg));
//            }
//        }
//    }

//    void initDataLocation(){
//        if(listsAll != null && !StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)){
////            lists.clear();
//            //计算距离
//            for(int i=0;i<listsAll.size();i++){
//                FuwuObj fuwuObj = listsAll.get(i);
//                if(fuwuObj != null && !StringUtil.isNullOrEmpty(fuwuObj.getLat()) && !StringUtil.isNullOrEmpty(fuwuObj.getLng())){
//                    LatLng latLng = new LatLng(Double.valueOf(UniversityApplication.lat), Double.valueOf(UniversityApplication.lng));
//                    LatLng latLng1 = new LatLng(Double.valueOf(fuwuObj.getLat()), Double.valueOf(fuwuObj.getLng()));
//                    String distance = StringUtil.getDistance(latLng ,latLng1 );
//                    listsAll.get(i).setDistance(distance+"km");
//                    fuwuObj.setDistance(distance+"km");
//                    //判断是否有设置附近的距离
//                    String mm_distance = getGson().fromJson(getSp().getString("mm_distance", ""), String.class);
//                    if(!StringUtil.isNullOrEmpty(mm_distance)){
//                        //说明设置了附近的距离
//                        if(Double.valueOf(distance) < Integer.parseInt(mm_distance)){
//                            //设置距离以内的
//                            lists.add(fuwuObj);
//                        }
//                    }else {
//                        if(Double.valueOf(distance) < 30){
//                            //30KM以内的
//                            lists.add(fuwuObj);
//                        }
//                    }
//
//                }
//            }
//            adapter.notifyDataSetChanged();
//        }
//        if(lists.size() > 0){
//            no_data1.setVisibility(View.GONE);
//            gridView.setVisibility(View.VISIBLE);
//        }else {
//            no_data1.setVisibility(View.VISIBLE);
//            gridView.setVisibility(View.GONE);
//        }
//    }
}
