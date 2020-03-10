package com.example.android.Activities.StudyActivities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.Adapters.BlackPaperResultAdapter;
import com.example.android.Items.BlackPaperItem;
import com.example.android.Items.LogItem;
import com.example.android.LinearLayoutManagerWrapper;
import com.example.android.Methods;
import com.example.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlackPaperResultActivity extends AppCompatActivity {

    RecyclerView.Adapter blackPaperResultAdapter;
    ArrayList<BlackPaperItem> memorizedArray = new ArrayList<>();
    ArrayList<LogItem> resultArray = new ArrayList<>();
    String totalStudyTime;
    String TAG = "BlackPaperResultActivity";
    @BindView(R.id.study_date)
    TextView studyDate;
    @BindView(R.id.study_time)
    TextView studyTime;
    @BindView(R.id.study_words_count)
    TextView studyWordsCount;
    @BindView(R.id.recyclerview_result)
    RecyclerView recyclerviewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_paper_result);
        ButterKnife.bind(this);
        init();
        setRecyclerviewBlackPaperResult();
    }

    public void init() {
        resultArray = getIntent().getParcelableArrayListExtra("resultArray");
        memorizedArray = getIntent().getParcelableArrayListExtra("memorizedArray");
        totalStudyTime = getIntent().getStringExtra("studyTime");
        studyDate.setText(Methods.currentDate());
        studyTime.setText(totalStudyTime);
        studyWordsCount.setText(memorizedArray.size() + " 개");

    }

    public void setRecyclerviewBlackPaperResult() {
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerviewResult.setHasFixedSize(true);
        recyclerviewResult.setLayoutManager(new LinearLayoutManagerWrapper(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerviewResult.getContext(), new LinearLayoutManager(this).getOrientation());
        Log.e(TAG, "logArray.size : " + resultArray.size());
        blackPaperResultAdapter = new BlackPaperResultAdapter(memorizedArray, resultArray);
        recyclerviewResult.setAdapter(blackPaperResultAdapter);
    }

    @OnClick(R.id.btn_go_back_menu)
    public void onViewClicked() {
        finish();
    }
}
