package com.example.android.Activities.StudyActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.Adapters.BlackPaperAdapter;
import com.example.android.Items.BlackPaperItem;
import com.example.android.Items.LogItem;
import com.example.android.Items.SelectedChaptersItem;
import com.example.android.LinearLayoutManagerWrapper;
import com.example.android.Methods;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlackPaperActivity extends AppCompatActivity {

    RecyclerView.Adapter blackPaperAdapter;
    ArrayList<SelectedChaptersItem> selectedArray = new ArrayList<>();
    ArrayList<BlackPaperItem> unmemorizedArray = new ArrayList<>();
    ArrayList<BlackPaperItem> memorizedArray = new ArrayList<>();
    ArrayList<LogItem> logArray = new ArrayList<>();
    String TAG = "BlackPaperActivity";
    String vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage;
    public String currentWord;
    int correctCount, total, turn, checkCorrect, index;
    double randomValue;
    String progress;
    boolean isPlaying, isTimerRunning;
    MediaPlayer mediaPlayer;

@BindView(R.id.blackPaperProgress)
    ProgressBar blackPaperProgress;
    @BindView(R.id.progressText)
    TextView progressText;
    @BindView(R.id.text_learning_timer)
    TextView textLearningTimer;
    @BindView(R.id.textWord)
    TextView textWord;
    @BindView(R.id.editMeaning)
    EditText editMeaning;
    @BindView(R.id.recyclerviewLog)
    RecyclerView recyclerviewLog;
    @BindView(R.id.black_paper_layout)
    ConstraintLayout blackPaperLayout;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_paper);
        ButterKnife.bind(this);

getSelectedArray();
        getWordsListSync();
        setRecyclerviewBlackPaperLog();
        isPlaying = true;
        isTimerRunning = true;
        new TimerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public String getCurrentWord(){
        return currentWord;
    }

    @OnClick({R.id.btn_pass, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pass:
                editMeaning.setText("");
                randomValue = Math.random();
                index = (int) (randomValue * unmemorizedArray.size());
                Log.e(TAG, "index : " + index);
                textWord.setText(unmemorizedArray.get(index).getWord());
                setStrings(index);
                break;
            case R.id.btn_submit:
                Log.e(TAG, "turn : " + turn);
                if (isAnswerCorrect(editMeaning.getText().toString())) {
                    new CorrectThread().start();
                } else {
                    new IncorrectThread().start();
                }
                break;
        }
    }

    public boolean isAnswerCorrect(String meanings) {
        boolean isCorrect;
        checkCorrect = 0;
        String[] splitMeanings = meanings.split(",");
        for (String splitMeaning : splitMeanings) {
            if (splitMeaning.replaceAll(" ", "").equals(meaning1)) {
                checkCorrect++;
            }
            if (splitMeaning.replaceAll(" ", "").equals(meaning2)) {
                checkCorrect++;
            }
            if (splitMeaning.replaceAll(" ", "").equals(meaning3)) {
                checkCorrect++;
            }
            if (splitMeaning.replaceAll(" ", "").equals(meaning4)) {
                checkCorrect++;
            }
            if (splitMeaning.replaceAll(" ", "").equals(meaning5)) {
                checkCorrect++;
            }
        }
        Log.e(TAG, "meaning1 : " + meaning1);
        Log.e(TAG, "meaning2 : " + meaning2);
        Log.e(TAG, "meaning3 : " + meaning3);
        Log.e(TAG, "meaning4 : " + meaning4);
        Log.e(TAG, "meaning5 : " + meaning5);
        Log.e(TAG, "입력값 : " + editMeaning.getText().toString());
        Log.e(TAG, "checkCorrect : " + checkCorrect);
        Log.e(TAG, "splitMeanings.length : " + splitMeanings.length);
        isCorrect = checkCorrect == splitMeanings.length;
        return isCorrect;
    }

    //테스트 스레드 핸들러.
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) { // 맞췄을 때
                logArray.add(0, new LogItem(turn + 1, unmemorizedArray.get(index).getWord(), editMeaning.getText().toString(), meanings(unmemorizedArray,index), true)); // 맞췄을 때
                blackPaperAdapter.notifyItemInserted(0);
                unmemorizedArray.get(index).setCorrectCount(unmemorizedArray.get(index).getCorrectCount() + 1); // 맞춘 횟수에 1 추가
                unmemorizedArray.get(index).setTestTime(unmemorizedArray.get(index).getTestTime() + 1); // 테스트 횟수 1 추가
                memorizedArray.get(index).setCorrectCount(memorizedArray.get(index).getCorrectCount() + 1); // 맞춘 횟수에 1 추가
                memorizedArray.get(index).setTestTime(memorizedArray.get(index).getTestTime() + 1); // 테스트 횟수 1 추가
                if (unmemorizedArray.get(index).getCorrectCount() == 4) {
                    unmemorizedArray.remove(index);
                }

                Log.e(TAG, "msg.arg1 수신 = " + msg.arg1);
                blackPaperLayout.setBackgroundColor(Color.argb(100, 194, 255, 194));
                mediaPlayer = MediaPlayer.create(BlackPaperActivity.this, R.raw.correct);
                mediaPlayer.start(); // 맞췄음을 표시하는 효과
            } else if (msg.arg1 == 0) { // 효과 표현 후 View 원상복구 (맞췄을 때, 틀렸을 때)
                Log.e(TAG, "msg.arg2 수신 = " + msg.arg2);
                if (unmemorizedArray.size() != 0) {
                    blackPaperLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
                    editMeaning.setTextColor(Color.BLACK);
                    btnSubmit.setVisibility(View.VISIBLE);
                    editMeaning.setText("");
                    turn++;
                    randomValue = Math.random();
                    index = (int) (randomValue * unmemorizedArray.size());
                    Log.e(TAG, "index : " + index);
                    currentWord = unmemorizedArray.get(index).getWord();
                    textWord.setText(currentWord);
                    setStrings(index);
                    setProgressText(turn, total);
                    blackPaperAdapter.notifyDataSetChanged(); // currentWord 갱신
                } else { // 테스트 종료 시
                    for(int index =  0 ; index < memorizedArray.size() ; index ++ ){
                        updateStudyDate(memorizedArray.get(index).vocabookTitle, memorizedArray.get(index).chapterTitle);
                    }
                    Toast.makeText(getApplicationContext(), "테스트 종료", Toast.LENGTH_SHORT).show();
                    Intent testFinish = new Intent(BlackPaperActivity.this, BlackPaperResultActivity.class);
                    testFinish.putParcelableArrayListExtra("resultArray", logArray);
                    testFinish.putParcelableArrayListExtra("memorizedArray", memorizedArray);
                    testFinish.putExtra("studyTime", textLearningTimer.getText().toString());
                    Log.e(TAG, "memorizedArray.size() = " + memorizedArray.size());
                    startActivity(testFinish);
                    finish();

                }

} else if (msg.arg1 == 2) { // 틀렸을 때
                Log.e(TAG, "msg.arg1 수신 = " + msg.arg1);
                logArray.add(0, new LogItem(turn + 1, unmemorizedArray.get(index).getWord(), editMeaning.getText().toString(), meanings(unmemorizedArray, index), false)); // 틀렸을 때
                blackPaperAdapter.notifyItemInserted(0);
                unmemorizedArray.get(index).setTestTime(unmemorizedArray.get(index).getTestTime() + 1); //테스트 횟수 1 추가
                memorizedArray.get(index).setTestTime(memorizedArray.get(index).getTestTime() + 1); // 테스트 횟수 1 추가
                total++;
                Methods.hideSoftKeyboard(BlackPaperActivity.this); //틀렸을 땐 키보드 내림

                blackPaperLayout.setBackgroundColor(Color.argb(100, 255, 194, 194));
                mediaPlayer = MediaPlayer.create(BlackPaperActivity.this, R.raw.incorrect);
                editMeaning.setText(meanings(unmemorizedArray,index));
                editMeaning.setTextColor(Color.BLUE);
                btnSubmit.setVisibility(View.GONE);
                mediaPlayer.start();

}

        }
    };

public class CorrectThread extends Thread {

        @Override
        public void run() {
            super.run();
            Message msg = new Message();
            Message msg2 = new Message();
            msg.arg1 = 1;
            handler.sendMessage(msg);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            msg2.arg2 = turn;
            handler.sendMessage(msg2);
        }
    }

    public class IncorrectThread extends Thread {

        @Override
        public void run() {
            super.run();
            Message msg = new Message();
            Message msg2 = new Message();
            msg.arg1 = 2;
            handler.sendMessage(msg);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            msg2.arg2 = turn;
            handler.sendMessage(msg2);
        }
    }

    public void setProgressText(int turn, int total) {
        progress = turn + " / " + total;
        progressText.setText(progress);
        blackPaperProgress.setProgress(100 * turn / total);
    }

    public void init() {
        index = 0;
        turn = 0;
        checkCorrect = 0;

        Log.e(TAG, "unmemorizedArray.size() : " + unmemorizedArray.size());
        total = (unmemorizedArray.size() * 4);
        Log.e(TAG, "total : " + total);
        setProgressText(turn, total);
        Collections.shuffle(unmemorizedArray);

        textWord.setText(unmemorizedArray.get(index).getWord());
        setStrings(index);

        editMeaning.setFocusableInTouchMode(true); // 시작과 동시에 EditText 에 포커스를 준다.
        editMeaning.requestFocus();
        Methods.showSoftKeyboard(this);
    }

    public void setRecyclerviewBlackPaperLog() {
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerviewLog.setHasFixedSize(true);
        recyclerviewLog.setLayoutManager(new LinearLayoutManagerWrapper(this));
        Log.e(TAG, "logArray.size : " + logArray.size());
        blackPaperAdapter = new BlackPaperAdapter(logArray, this);
        recyclerviewLog.setAdapter(blackPaperAdapter);
    }

    public void getSelectedArray() {
        selectedArray = getIntent().getParcelableArrayListExtra("selectedArray");
        if (selectedArray != null) {
            for (int index = 0; index < selectedArray.size(); index++) {
                Log.e(TAG, "selectedArray : " + selectedArray.get(index).getChapterTitle() + " [" + selectedArray.get(index).getVocabookTitle() + "]");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void getWordsListSync() {
        final String[] wordsJson = {null};
        String userEmail = getUserEmail();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                for (int index = 0; index < selectedArray.size(); index++) {
                    vocabookTitle = selectedArray.get(index).getVocabookTitle();
                    chapterTitle = selectedArray.get(index).getChapterTitle();
                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_word_list(userEmail, vocabookTitle, chapterTitle);
                    try {
                        wordsJson[0] = Objects.requireNonNull(res.execute().body()).string();
                        jsonToArrayListWord(wordsJson[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getChapterListSync...DONE");
                init();
            }

}.execute();

    }

    private void jsonToArrayListWord(String wordsJson) {

        String TAG_JSON = "webnautes";
        String TAG_WORD = "word";
        String TAG_MEANING1 = "meaning1";
        String TAG_MEANING2 = "meaning2";
        String TAG_MEANING3 = "meaning3";
        String TAG_MEANING4 = "meaning4";
        String TAG_MEANING5 = "meaning5";
        String TAG_WORD_IMAGE_URI = "word_image_uri";
        Log.e(TAG, "wordsJson : " + wordsJson);
        if (!wordsJson.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(wordsJson);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    word = item.getString(TAG_WORD);
                    if (item.getString(TAG_MEANING1).equals("null")) {
                        meaning1 = null;
                    } else {
                        meaning1 = item.getString(TAG_MEANING1);
                    }
                    if (item.getString(TAG_MEANING2).equals("null")) {
                        meaning2 = null;
                    } else {
                        meaning2 = item.getString(TAG_MEANING2);
                    }
                    if (item.getString(TAG_MEANING3).equals("null")) {
                        meaning3 = null;
                    } else {
                        meaning3 = item.getString(TAG_MEANING3);
                    }
                    if (item.getString(TAG_MEANING4).equals("null")) {
                        meaning4 = null;
                    } else {
                        meaning4 = item.getString(TAG_MEANING4);
                    }
                    if (item.getString(TAG_MEANING5).equals("null")) {
                        meaning5 = null;
                    } else {
                        meaning5 = item.getString(TAG_MEANING5);
                    }
                    if (item.getString(TAG_WORD_IMAGE_URI).equals("null")) {
                        wordImage = null;
                    } else {
                        wordImage = item.getString(TAG_WORD_IMAGE_URI);
                    }

                    unmemorizedArray.add(new BlackPaperItem(vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage, 0, 0));
                    memorizedArray.add(new BlackPaperItem(vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage, 0, 0));
                }

            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
            Log.e(TAG, "단어 로딩 완료...");

//            for (int index = 0; index < unmemorizedArray.size(); index++) {
//                Log.e(TAG, "unmemorizedArray(" + index + "). vocabookTitle : " + unmemorizedArray.get(index).getVocabookTitle() + " / chapterTitle : " + unmemorizedArray.get(index).getChapterTitle()
//                        + " / meaning1 : " + unmemorizedArray.get(index).getMeaning1() + " / meaning2 : " + unmemorizedArray.get(index).getMeaning2() + " / meaning3 : " + unmemorizedArray.get(index).getMeaning3()
//                        + " / meaning4 : " + unmemorizedArray.get(index).getMeaning4() + " / meaning5 : " + unmemorizedArray.get(index).getMeaning5() + " / wordImageUri : " + unmemorizedArray.get(index).getWordImageURI()
//                        + " / correctCount : " + unmemorizedArray.get(index).getCorrectCount());
//            }

        }
    }

    public String getUserEmail() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        return settings.getString("loginEmail", "");
    }

    public String meanings(ArrayList<BlackPaperItem> targetArrayList, int index) {
        if (targetArrayList.get(index).getMeaning1() == null) {
            meaning1 = "";
        } else {
            meaning1 = targetArrayList.get(index).getMeaning1();
        }
        if (targetArrayList.get(index).getMeaning2() == null) {
            meaning2 = "";
        } else {
            meaning2 = ", " + targetArrayList.get(index).getMeaning2();
        }
        if (targetArrayList.get(index).getMeaning3() == null) {
            meaning3 = "";
        } else {
            meaning3 = ", " + targetArrayList.get(index).getMeaning3();
        }
        if (targetArrayList.get(index).getMeaning4() == null) {
            meaning4 = "";
        } else {
            meaning4 = ", " + targetArrayList.get(index).getMeaning4();
        }
        if (targetArrayList.get(index).getMeaning5() == null) {
            meaning5 = "";
        } else {
            meaning5 = ", " + targetArrayList.get(index).getMeaning5();
        }

        return meaning1 + meaning2 + meaning3 + meaning4 + meaning5;
    }

    public void setStrings(int index) {
        word = unmemorizedArray.get(index).getWord();

        if (unmemorizedArray.get(index).getMeaning1() == null) {
            meaning1 = null;
        } else {
            meaning1 = unmemorizedArray.get(index).getMeaning1().replaceAll(" ", "");
        }
        if (unmemorizedArray.get(index).getMeaning2() == null) {
            meaning2 = null;
        } else {
            meaning2 = unmemorizedArray.get(index).getMeaning2().replaceAll(" ", "");
        }
        if (unmemorizedArray.get(index).getMeaning3() == null) {
            meaning3 = null;
        } else {
            meaning3 = unmemorizedArray.get(index).getMeaning3().replaceAll(" ", "");
        }
        if (unmemorizedArray.get(index).getMeaning4() == null) {
            meaning4 = null;
        } else {
            meaning4 = unmemorizedArray.get(index).getMeaning4().replaceAll(" ", "");
        }
        if (unmemorizedArray.get(index).getMeaning5() == null) {
            meaning5 = null;
        } else {
            meaning5 = unmemorizedArray.get(index).getMeaning5().replaceAll(" ", "");
        }
        if (unmemorizedArray.get(index).getWordImageURI() == null) {
            wordImage = null;
        } else {
            wordImage = Methods.getWordImageFromServer(unmemorizedArray.get(index).getWordImageURI());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builderExitLearning = new AlertDialog.Builder(BlackPaperActivity.this);
        builderExitLearning.setMessage("학습을 종료하시겠습니까?");
        builderExitLearning.setNegativeButton("학습 종료", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "학습 종료", Toast.LENGTH_SHORT).show();
            Intent testFinish = new Intent(BlackPaperActivity.this, BlackPaperResultActivity.class);
            testFinish.putParcelableArrayListExtra("resultArray", logArray);
            testFinish.putParcelableArrayListExtra("memorizedArray", memorizedArray);
            testFinish.putExtra("studyTime", textLearningTimer.getText().toString());
            Log.e(TAG, "memorizedArray.size() = " + memorizedArray.size());
            startActivity(testFinish);
            finish();
        });
        builderExitLearning.setPositiveButton("취소", (dialog, which) -> {
        });
        builderExitLearning.show();
    }

    public void updateStudyDate(String vocabookTitle, String chapterTitle){
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");

        Call<ResponseBody> res = RetrofitClient.getInstance().getService().update_study_date(userEmail, vocabookTitle, chapterTitle, Methods.currentDate());
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String updateDateResult = response.body().string();
                    Log.e(TAG, updateDateResult);
                } catch (IOException e) {
                    Log.e(TAG, "Retrofit Error : " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });
    }

    @SuppressLint("StaticFieldLeak")//학습 시간을 표시하는 타이머 스레드
    public class TimerThread extends AsyncTask<Integer, Integer, Integer> {
        int milliSecond;

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (isPlaying) {
                Log.e(TAG, "Timer is working...");
                while (isTimerRunning) {
                    if (isCancelled()) {
                        break;
                    }
                    publishProgress(1);
                    milliSecond++;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.e(TAG, "Timer has been stopped");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(1);
//            int mSec = milliSecond % 100;
            int sec = (milliSecond / 100) % 60;
            int min = (milliSecond / 100) / 60;
            if (min > 0) {
                @SuppressLint("DefaultLocale") String result = String.format("%02d : %02d", min, sec);
                textLearningTimer.setText(result);
            } else {
                @SuppressLint("DefaultLocale") String result = String.format("00 : %02d", sec);
                textLearningTimer.setText(result);
            }
        }
    }
}
