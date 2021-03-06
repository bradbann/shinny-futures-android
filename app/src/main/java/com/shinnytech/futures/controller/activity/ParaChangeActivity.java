package com.shinnytech.futures.controller.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.shinnytech.futures.R;
import com.shinnytech.futures.application.BaseApplication;
import com.shinnytech.futures.constants.SettingConstants;
import com.shinnytech.futures.databinding.ActivityParaChangeBinding;
import com.shinnytech.futures.model.adapter.ParaContentAdapter;
import com.shinnytech.futures.model.bean.eventbusbean.ParaChangeEvent;
import com.shinnytech.futures.utils.SPUtils;
import com.shinnytech.futures.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ParaChangeActivity extends BaseActivity {

    private ActivityParaChangeBinding mBinding;
    private ParaContentAdapter mParaContentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayoutID = R.layout.activity_para_change;
        mTitle = SettingConstants.PARA_CHANGE;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        mBinding = (ActivityParaChangeBinding) mViewDataBinding;
        mBinding.contentRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.contentRv.setItemAnimator(new DefaultItemAnimator());
        String data = (String) SPUtils.get(sContext, SettingConstants.CONFIG_PARA_MA, SettingConstants.PARA_MA);
        mParaContentAdapter = new ParaContentAdapter(this, data.split(","));
        mBinding.contentRv.setAdapter(mParaContentAdapter);
    }

    @Override
    protected void initEvent() {
        mBinding.paraNav.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.para_ma:
                        String data = (String) SPUtils.get(sContext, SettingConstants.CONFIG_PARA_MA, SettingConstants.PARA_MA);
                        mParaContentAdapter.setData(data.split(","));
                        break;
//                    case R.id.para_expma:
//                        mParaContentAdapter.setData(new String[]{"1","2"});
//                        break;
//                    case R.id.para_sar:
//                        mParaContentAdapter.setData(new String[]{"3","4"});
//                        break;
                    default:
                        break;
                }
            }
        });

        mBinding.paraSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = mBinding.paraNav.getCheckedRadioButtonId();
                String[] mData = mParaContentAdapter.getData();
                if (mData == null || mData.length == 0) return;
                List<String> paras = new ArrayList<>();
                for (int i = 0; i < mData.length; i++) {
                    try {
                        ParaContentAdapter.ItemViewHolder itemViewHolder = (ParaContentAdapter.ItemViewHolder)
                                mBinding.contentRv.findViewHolderForAdapterPosition(i);
                        String data = itemViewHolder.mBinding.edValue.getText().toString();
                        int value = Integer.parseInt(data);
                        if (value < 0 && id == R.id.para_ma) {
                            ToastUtils.showToast(BaseApplication.getContext(), "参数需要大于等于0");
                            return;
                        }
                        paras.add(data);
                    } catch (Exception e) {
                        ToastUtils.showToast(BaseApplication.getContext(), "输入参数需为整数");
                        return;
                    }
                }
                switch (id) {
                    case R.id.para_ma:
                        SPUtils.putAndApply(BaseApplication.getContext(), SettingConstants.CONFIG_PARA_MA, TextUtils.join(",", paras));
                        break;
//                    case R.id.para_expma:
//                        break;
//                    case R.id.para_sar:
//                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(new ParaChangeEvent());
                ToastUtils.showToast(sContext, "参数已保存");
            }
        });

        mBinding.paraReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mBinding.paraNav.getCheckedRadioButtonId()) {
                    case R.id.para_ma:
                        mParaContentAdapter.setData(SettingConstants.PARA_MA.split(","));
                        SPUtils.putAndApply(BaseApplication.getContext(), SettingConstants.CONFIG_PARA_MA, SettingConstants.PARA_MA);
                        break;
//                    case R.id.para_expma:
//                        break;
//                    case R.id.para_sar:
//                        break;
                    default:
                        return;
                }
                EventBus.getDefault().post(new ParaChangeEvent());
                ToastUtils.showToast(sContext, "恢复默认");
            }
        });
    }

}
