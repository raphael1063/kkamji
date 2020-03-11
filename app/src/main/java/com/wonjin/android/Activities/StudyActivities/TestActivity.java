package com.wonjin.android.Activities.StudyActivities;

import android.animation.Animator;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.wonjin.android.Items.LogItem;
import com.wonjin.android.Items.SelectedChaptersItem;
import com.wonjin.android.Items.TestItem;
import com.wonjin.android.Methods;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;

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

public class TestActivity extends AppCompatActivity {

    ArrayList<SelectedChaptersItem> selectedArray = new ArrayList<>();
    ArrayList<TestItem> testArray = new ArrayList<>();
    ArrayList<LogItem> logArray = new ArrayList<>();
    String TAG = "TestActivity";
    String vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage, progress;
    boolean isCorrect, isTimeRemained, isActivityRunning, isTesting, isTimerRunning;
    MediaPlayer mediaPlayer;
    int testTime, testIndex;
    int milliSecond;
    @BindView(R.id.testProgress)
    ProgressBar testProgress;
    @BindView(R.id.testProgressText)
    TextView testProgressText;
    @BindView(R.id.test_text_learning_timer)
    TextView testTextLearningTimer;
    @BindView(R.id.testTextWord)
    TextView testTextWord;
    @BindView(R.id.testEditMeaning)
    EditText testEditMeaning;
    @BindView(R.id.btn_test_pause)
    ImageButton btnTestPause;
    @BindView(R.id.testLayout)
    ConstraintLayout testLayout;
    @BindView(R.id.lottie_clock)
    LottieAnimationView lottieClock;
    @BindView(R.id.btn_test_submit)
    Button btnTestSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        init();
        getSelectedArray();
        getWordsListSync();
        testTime = getIntent().getIntExtra("testTime", -1);
        Log.e(TAG, "testTime : " + testTime);
        new TimerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void init() {
        testIndex = 0;
        isTimeRemained = true;
        isActivityRunning = true;
        isTesting = true;
        isTimerRunning = true;
        lottieClock.setAnimation("clock.json");
        lottieClock.playAnimation();
        lottieClock.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottieClock.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
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
                testTextWord.setText(testArray.get(testIndex).getWord());
                setStrings(testIndex);
//                init();
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
                    testArray.add(new TestItem(vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage, false));
                    Collections.shuffle(testArray);
                }

            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
            Log.e(TAG, "단어 로딩 완료...");
        }
    }

    public String getUserEmail() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        return settings.getString("loginEmail", "");
    }

    @OnClick({R.id.btn_test_submit, R.id.btn_test_pause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_submit:
                if (isAnswerCorrect(testEditMeaning.getText().toString())) {
                    new CorrectThread().start();
                } else {
                    new IncorrectThread().start();
                }
                break;
            case R.id.btn_test_pause:
                if (isTesting) {
                    btnTestPause.setImageResource(R.drawable.ic_play_circle_outline_orange_24dp);
                    testTextWord.setText("");
                    btnTestSubmit.setBackgroundResource(R.drawable.rounded_disable);
                    btnTestSubmit.setClickable(false);
                    lottieClock.pauseAnimation();
                } else {
                    btnTestPause.setImageResource(R.drawable.ic_pause_circle_outline_orange_24dp);
                    testTextWord.setText(word);
                    btnTestSubmit.setBackgroundResource(R.drawable.rounded_button_login);
                    btnTestSubmit.setClickable(true);
                    lottieClock.resumeAnimation();
                }
                isTesting = !isTesting;
                break;
        }
    }

    public boolean isAnswerCorrect(String meanings) {
        boolean isCorrect;
        int checkCorrect = 0;
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
        Log.e(TAG, "입력값 : " + testEditMeaning.getText().toString());
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
                logArray.add(new LogItem(testIndex+1, word, testEditMeaning.getText().toString(), meanings(testArray, testIndex), true));
                testArray.get(testIndex).setCorrect(true); // 맞춘 횟수에 1 추가
                testLayout.setBackgroundColor(Color.argb(100, 194, 255, 194));
                mediaPlayer = MediaPlayer.create(TestActivity.this, R.raw.correct);
                mediaPlayer.start(); // 맞췄음을 표시하는 효과
                testEditMeaning.setText("");
                testIndex++;
                if (testIndex < testArray.size()) {

                    String currentWord = testArray.get(testIndex).getWord();
                    testTextWord.setText(currentWord);
                    setStrings(testIndex);
                    setProgressText(testIndex, testArray.size());
                    Log.e(TAG, "testIndex : " + testIndex);
                } else { // 테스트 종료 시
                    for (int index = 0; index < testArray.size(); index++) {
                        updateStudyDate(testArray.get(index).vocabookTitle, testArray.get(index).chapterTitle);
                    }
                    finishTest();
                }
            } else if (msg.arg1 == 0) { // 효과 표현 후 View 원상복구 (맞췄을 때, 틀렸을 때)
                testLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));

            } else if (msg.arg1 == 2) { // 틀렸을 때
                logArray.add(new LogItem(testIndex+1, word, testEditMeaning.getText().toString(), meanings(testArray, testIndex), false));
                Log.e(TAG, "msg.arg1 수신 = " + msg.arg1);
                testLayout.setBackgroundColor(Color.argb(100, 255, 194, 194));
                mediaPlayer = MediaPlayer.create(TestActivity.this, R.raw.incorrect);
                mediaPlayer.start();
                testEditMeaning.setText("");
                testIndex++;
                if (testIndex < testArray.size()) {

                    String currentWord = testArray.get(testIndex).getWord();
                    testTextWord.setText(currentWord);
                    setStrings(testIndex);
                    setProgressText(testIndex, testArray.size());
                    Log.e(TAG, "testIndex : " + testIndex);
                } else { // 테스트 종료 시
                    for (int index = 0; index < testArray.size(); index++) {
                        updateStudyDate(testArray.get(index).vocabookTitle, testArray.get(index).chapterTitle);
                    }
                    finishTest();
                }
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
            msg2.arg2 = testIndex;
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
            msg2.arg2 = testIndex;
            handler.sendMessage(msg2);
        }
    }

    public void finishTest() {
        isActivityRunning = false;
        isTesting = false;
        isTimerRunning = false;
        Toast.makeText(getApplicationContext(), "테스트 종료", Toast.LENGTH_SHORT).show();
        Intent testFinish = new Intent(TestActivity.this, TestResultActivity.class);
        testFinish.putParcelableArrayListExtra("testArray", testArray);
        testFinish.putParcelableArrayListExtra("logArray", logArray);
        int min = ((testTime * 6000 - milliSecond) / 100) / 60;
        int sec = ((testTime * 6000 - milliSecond) / 100) % 60;
        String timeTaken = "";
        if (min > 0) {
            timeTaken = min + "분 " + sec + "초";
        } else {
            timeTaken = sec + "초";
        }
        testFinish.putExtra("timeTaken", timeTaken);
        Log.e(TAG, "testArray.size() = " + testArray.size());
        startActivity(testFinish);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builderExitLearning = new AlertDialog.Builder(TestActivity.this);
        builderExitLearning.setMessage("테스트를 종료하시겠습니까?");
        builderExitLearning.setNegativeButton("테스트 종료", (dialog, which) -> {
            finishTest();
        });
        builderExitLearning.setPositiveButton("취소", (dialog, which) -> {
        });
        builderExitLearning.show();
    }

    public void setProgressText(int turn, int total) {
        progress = turn + " / " + total;
        testProgressText.setText(progress);
        testProgress.setProgress(100 * turn / total);
    }

    public void updateStudyDate(String vocabookTitle, String chapterTitle) {
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

    public void setStrings(int index) {
        word = testArray.get(index).getWord();

        if (testArray.get(index).getMeaning1() == null) {
            meaning1 = null;
        } else {
            meaning1 = testArray.get(index).getMeaning1().replaceAll(" ", "");
        }
        if (testArray.get(index).getMeaning2() == null) {
            meaning2 = null;
        } else {
            meaning2 = testArray.get(index).getMeaning2().replaceAll(" ", "");
        }
        if (testArray.get(index).getMeaning3() == null) {
            meaning3 = null;
        } else {
            meaning3 = testArray.get(index).getMeaning3().replaceAll(" ", "");
        }
        if (testArray.get(index).getMeaning4() == null) {
            meaning4 = null;
        } else {
            meaning4 = testArray.get(index).getMeaning4().replaceAll(" ", "");
        }
        if (testArray.get(index).getMeaning5() == null) {
            meaning5 = null;
        } else {
            meaning5 = testArray.get(index).getMeaning5().replaceAll(" ", "");
        }
        if (testArray.get(index).getWordImageURI() == null) {
            wordImage = null;
        } else {
            wordImage = Methods.getWordImageFromServer(testArray.get(index).getWordImageURI());
        }
    }

    public String meanings(ArrayList<TestItem> targetArrayList, int index) {
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

@SuppressLint("StaticFieldLeak")//학습 시간을 표시하는 타이머 스레드
    public class TimerThread extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            milliSecond = testTime * 6000;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (isActivityRunning) {
                Log.e(TAG, "Timer is working...");
                while (isTesting && milliSecond != 0) {
                    if (isCancelled()) {
                        break;
                    }
                    publishProgress(1);
                    milliSecond--;
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
            if (milliSecond == 0) {
                isTesting = false;
                finishTest();
            }
            int mSec = milliSecond % 100;
            int sec = (milliSecond / 100) % 60;
            int min = (milliSecond / 100) / 60;
            @SuppressLint("DefaultLocale") String result = String.format("%02d : %02d : %02d", min, sec, mSec);
            testTextLearningTimer.setText(result);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isTesting = false;
        isTimerRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
        isTesting = false;
        isTimerRunning = false;
    }
}
