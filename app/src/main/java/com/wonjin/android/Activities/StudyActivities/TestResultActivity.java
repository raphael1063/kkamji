package com.wonjin.android.Activities.StudyActivities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.wonjin.android.Adapters.TestResultAdapter;
import com.wonjin.android.Items.LogItem;
import com.wonjin.android.Items.TestItem;
import com.wonjin.android.LinearLayoutManagerWrapper;
import com.wonjin.android.Methods;
import com.wonjin.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestResultActivity extends AppCompatActivity {

@BindView(R.id.test_date)
    TextView testDate;
    @BindView(R.id.test_time)
    TextView testTime;
    @BindView(R.id.test_words_count)
    TextView testWordsCount;
    @BindView(R.id.btn_go_back_menu)
    Button btnGoBackMenu;
    @BindView(R.id.recyclerview_test_result)
    RecyclerView recyclerviewTestResult;
    @BindView(R.id.log_score)
    TextView logScore;
    RecyclerView.Adapter testResultAdapter;

    ArrayList<TestItem> testResultArray = new ArrayList<>();
    ArrayList<LogItem> logArray = new ArrayList<>();
    int countCorrect = 0;
    String TAG = "TestResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        ButterKnife.bind(this);

        init();
        setRecyclerviewTestResultLog();
    }

    public void setRecyclerviewTestResultLog() {
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerviewTestResult.setHasFixedSize(true);
        recyclerviewTestResult.setLayoutManager(new LinearLayoutManagerWrapper(this));
        Log.e(TAG, "logArray.size : " + logArray.size());
        testResultAdapter = new TestResultAdapter(logArray);
        recyclerviewTestResult.setAdapter(testResultAdapter);
    }

    public void init(){
        testResultArray = getIntent().getParcelableArrayListExtra("testArray");
        logArray = getIntent().getParcelableArrayListExtra("logArray");
        testDate.setText(Methods.currentDate());
        String resultTestTime = getIntent().getStringExtra("timeTaken");
        testTime.setText(resultTestTime);
        String numOfWords = testResultArray.size() + "개";
        testWordsCount.setText(numOfWords);

        for(int index = 0 ; index < testResultArray.size() ; index ++ ){
            if(testResultArray.get(index).isCorrect()){
                countCorrect++;
            }
        }
        int score = (int)((double)countCorrect/ (double)testResultArray.size() * 100);
        logScore.setText(String.valueOf(score));

}

    @OnClick(R.id.btn_go_back_menu)
    public void onViewClicked() {
        finish();
    }
}
