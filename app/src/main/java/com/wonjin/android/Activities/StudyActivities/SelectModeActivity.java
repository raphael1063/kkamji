package com.wonjin.android.Activities.StudyActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wonjin.android.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_auto_learning, R.id.btn_blackPaper})
    public void onViewClicked(View view) {
        Intent intent = new Intent(this, SelectChapterActivity.class);
        switch (view.getId()) {
            case R.id.btn_auto_learning:
                intent.putExtra("mode", "autoLearning");
                startActivity(intent);
                break;
            case R.id.btn_blackPaper:
                intent.putExtra("mode", "blackPaper");
                startActivity(intent);
                break;
        }
    }
}
