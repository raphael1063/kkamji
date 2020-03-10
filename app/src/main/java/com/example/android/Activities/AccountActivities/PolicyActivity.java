package com.example.android.Activities.AccountActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PolicyActivity extends AppCompatActivity {
    String TAG = "PolicyActivity";
    @BindView(R.id.textService)
    TextView textService;
    @BindView(R.id.textPersonalInfo)
    TextView textPersonalInfo;
    @BindView(R.id.subScrollViewService)
    ScrollView subScrollViewService;
    @BindView(R.id.subScrollViewPersonalInfo)
    ScrollView subScrollViewPersonalInfo;
    @BindView(R.id.mainScrollView)
    ScrollView mainScrollView;
    @BindView(R.id.chkService)
    CheckBox chkService;
    @BindView(R.id.chkPersonalInfo)
    CheckBox chkPersonalInfo;
    @BindView(R.id.btnNextStep)
    Button btnNextStep;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        ButterKnife.bind(this);

        chkService.setClickable(false);
        chkPersonalInfo.setClickable(false);

subScrollViewService.setOnTouchListener((v, event) -> {
            mainScrollView.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        subScrollViewPersonalInfo.setOnTouchListener((v, event) -> {
            mainScrollView.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        subScrollViewService.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (subScrollViewService.getChildAt(0).getBottom() <= (subScrollViewService.getHeight() + subScrollViewService.getScrollY())) {

                        chkService.setBackgroundResource(R.drawable.rounded);
                        chkService.setClickable(true);
                        //scroll view is at bottom
                    }
                });

        subScrollViewPersonalInfo.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (subScrollViewPersonalInfo.getChildAt(0).getBottom() <= (subScrollViewPersonalInfo.getHeight() + subScrollViewPersonalInfo.getScrollY())) {
                        chkPersonalInfo.setBackgroundResource(R.drawable.rounded);
                        chkPersonalInfo.setClickable(true);
                        //scroll view is at bottom
                    }
                });

}

    @OnClick(R.id.btnNextStep)
    public void onViewClicked() {
        if (!chkService.isChecked()) {
            Toast.makeText(getApplicationContext(), "서비스 이용약관에 동의하셔야 회원가입 진행이 가능합니다.", Toast.LENGTH_SHORT).show();
            //개인정보 취급방침에 동의체크를 하지 않았을 시
        } else if (!chkPersonalInfo.isChecked()) {
            Toast.makeText(getApplicationContext(), "개인정보 처리방침에 동의하셔야 회원가입 진행이 가능합니다.", Toast.LENGTH_SHORT).show();
            //필요한 동의를 모두 받았을 시
        } else {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }
}
