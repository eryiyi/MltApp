package com.Lbins.Mlt.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseFragment;
import com.Lbins.Mlt.ui.CircleActivity;
import com.Lbins.Mlt.ui.FourFuwuActivity;
import com.Lbins.Mlt.ui.NearbyActivity;

/**
 * Created by zhl on 2016/7/3.
 */
public class FindFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_fragment, null);
        res = getActivity().getResources();
        initView();
        return view;
    }

    private void initView() {
        view.findViewById(R.id.liner_one).setOnClickListener(this);
        view.findViewById(R.id.liner_two).setOnClickListener(this);
        view.findViewById(R.id.liner_three).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.liner_one:
                //商圈
            {
                Intent circle = new Intent(getActivity(), CircleActivity.class);
                startActivity(circle);
            }
                break;
            case R.id.liner_two:
            {
                //附近的经纪人
                Intent intent = new Intent(getActivity(), NearbyActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_three:
            {
                //园林资材
                Intent shopV = new Intent(getActivity(), FourFuwuActivity.class);
                shopV.putExtra("mm_fuwu_type", "0");
                startActivity(shopV);
            }
                break;

        }
    }
}
