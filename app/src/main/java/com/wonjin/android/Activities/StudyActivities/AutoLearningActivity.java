package com.wonjin.android.Activities.StudyActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.wonjin.android.Items.SelectedChaptersItem;
import com.wonjin.android.Items.WordsForTestItem;
import com.wonjin.android.Methods;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoLearningActivity extends AppCompatActivity {

@BindView(R.id.word_image_view)
    ImageView wordImageView;
    @BindView(R.id.text_learning_timer)
    TextView textLearningTimer;
    @BindView(R.id.textWord)
    TextView textWord;
    @BindView(R.id.textMeaning1)
    TextView textMeaning1;
    @BindView(R.id.textMeaning2)
    TextView textMeaning2;
    @BindView(R.id.textMeaning3)
    TextView textMeaning3;
    @BindView(R.id.textMeaning4)
    TextView textMeaning4;
    @BindView(R.id.textMeaning5)
    TextView textMeaning5;
    @BindView(R.id.btn_repeat)
    ImageButton btnRepeat;
    @BindView(R.id.btn_play_and_pause)
    ImageButton btnPlayAndPause;
    @BindView(R.id.btn_shuffle)
    ImageButton btnShuffle;
    @BindView(R.id.btn_tts)
    LottieAnimationView btnTTS;
    @BindView(R.id.autoLearningProgress)
    ProgressBar autoLearningProgress;
    @BindView(R.id.progressText)
    TextView progressText;

    boolean isActivityRunning, isPlaying, isTimerRunning, isRepeat, isLoading;
    ArrayList<SelectedChaptersItem> selectedArray = new ArrayList<>();
    ArrayList<WordsForTestItem> wordsArray = new ArrayList<>();
    String TAG = "AutoLearningActivity";
    String vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage;
    int on = 1;
    int off = 0;
    int next = 1;
    int previous = -1;
    int isSkipped, skipIndex, step, shuffle;
    TextToSpeech ttsEng, ttsKor;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_learning);
        ButterKnife.bind(this);
        setTTS();
        getSelectedArray();
        getWordsListSync();
        init();
        new LoadingThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void init() {
        shuffle = off;
        wordImageView.setVisibility(View.GONE);
        btnTTS.setVisibility(View.GONE);
        isActivityRunning = true;
        isLoading = true;
        isPlaying = true;
        isTimerRunning = true;
        isRepeat = false;
        isSkipped = 0;

    }

    public void getSelectedArray() {
        selectedArray = getIntent().getParcelableArrayListExtra("selectedArray");
        if (selectedArray != null) {
            for (int index = 0; index < selectedArray.size(); index++) {
                Log.e(TAG, "selectedArray : " + selectedArray.get(index).getChapterTitle() + " [" + selectedArray.get(index).getVocabookTitle() + "]");
            }
        }
    }

    @OnClick({R.id.btn_tts, R.id.btn_repeat, R.id.btn_skip_previous, R.id.btn_play_and_pause, R.id.btn_skip_next, R.id.btn_shuffle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tts:
                ttsEng.speak(wordsArray.get(skipIndex).getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
                break;
            case R.id.btn_play_and_pause:
                if (isPlaying) { // 일시정지 버튼을 눌렀을 때
                    btnPlayAndPause.setImageResource(R.drawable.play_circle_outline_72dp);
                    btnTTS.setVisibility(View.VISIBLE);
                    isPlaying = !isPlaying;
                } else { // 재생 버튼을 눌렀을 때
                    btnPlayAndPause.setImageResource(R.drawable.pause_circle_outline_72dp);
                    btnTTS.setVisibility(View.GONE);
                    isPlaying = !isPlaying;
                }
                break;
            case R.id.btn_repeat:
                if (isRepeat) {
                    btnRepeat.setImageResource(R.drawable.repeat_grey_32dp);
                    isRepeat = !isRepeat;
                    Toast.makeText(this, "반복재생 OFF", Toast.LENGTH_SHORT).show();
                } else {
                    btnRepeat.setImageResource(R.drawable.repeat_32dp);
                    isRepeat = !isRepeat;
                    Toast.makeText(this, "반복재생 ON", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_shuffle:
                Toast.makeText(this, "단어를 섞습니다.", Toast.LENGTH_SHORT).show();
                shuffle = on;

                break;
            case R.id.btn_skip_previous:
                isSkipped = previous;
                break;
            case R.id.btn_skip_next:
                isSkipped = next;
                break;
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

                    wordsArray.add(new WordsForTestItem(vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage));
                }
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
            Log.e(TAG, "단어 로딩 완료...");
            for (int index = 0; index < wordsArray.size(); index++) {
                Log.e(TAG, "wordsArray(" + index + "). vocabookTitle : " + wordsArray.get(index).getVocabookTitle() + " / chapterTitle : " + wordsArray.get(index).getChapterTitle()
                        + " / meaning1 : " + wordsArray.get(index).getMeaning1() + " / meaning2 : " + wordsArray.get(index).getMeaning2() + " / meaning3 : " + wordsArray.get(index).getMeaning3()
                        + " / meaning4 : " + wordsArray.get(index).getMeaning4() + " / meaning5 : " + wordsArray.get(index).getMeaning5() + " / wordImageUri : " + wordsArray.get(index).getWordImageURI());
            }

        }
    }

    @SuppressLint("StaticFieldLeak")//학습 시작 전 로딩 스레드
    public class LoadingThread extends AsyncTask<Integer, Integer, Integer> {
        int count;

        protected void onPreExecute() {
            count = 0;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            for (count = 3; count >= 0 && isPlaying && isLoading; count--) {
                if (isCancelled()) {
                    break;
                }
                publishProgress(count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isLoading = false;
            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            String announceMsg;
            Log.e(TAG, "(onProgressUpdate) value[0] : " + value[0]);
            if (value[0] == 3 || value[0] == 2) {
                announceMsg = "Loading...";
                textWord.setText(announceMsg);
            } else if (value[0] == 1) {
                announceMsg = "START";
                textWord.setText(announceMsg);
            } else {
                new TimerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new LearningTread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }
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

    @SuppressLint("StaticFieldLeak")//자동학습 스레드
    public class LearningTread extends AsyncTask<Integer, Integer, Integer> {
        boolean checkFinish = false;
        @Override
        protected Integer doInBackground(Integer... integers) {
            while (isActivityRunning) {
                for (; skipIndex < wordsArray.size() && isPlaying && isActivityRunning && !checkFinish; skipIndex++) {
                    for (step = 1; step <= 4; step++) {
                        switch (step) {
                            case 1: //단어 노출
                                publishProgress(skipIndex);
                                term(120);
                                break;
                            case 2: //단어 가림
                                publishProgress(skipIndex);
                                term(20);
                                break;
                            case 3: //단어 노출, 단어 이미지 노출
                                publishProgress(skipIndex);
                                term(121);
                                break;
                            case 4: //의미 노출
                                publishProgress(skipIndex);
                                term(200);
                                while (ttsKor.isSpeaking()){
                                    Log.e(TAG, "TTS 아직 작동중...");
                                }
                                term(100);
                                break;
                        }
                        if (isRepeat && skipIndex == wordsArray.size() - 1) { // 반복재생을 할 경우 마지막 인덱스에서 첫 인덱스로 초기화한다.
                            skipIndex = 0;
                        } else if(step == 4 && !isRepeat && skipIndex == wordsArray.size() - 1) {
                            isPlaying = false;
                            publishProgress(-1);
                            while (!isPlaying && isActivityRunning){
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Log.e(TAG, "isActivityRunning : " + isActivityRunning);
                            }
                            skipIndex = -1;

                        }
                        if(step == 4 && shuffle == on){
                            Collections.shuffle(wordsArray);
                            shuffle = off;
                        }
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }//무한루프로 인한 연산처리 과부하 해소

            }

return step;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
                if(value[0] >= 0){
                    setStrings(value[0]);
                    String progress = (skipIndex + 1) + " / " + wordsArray.size();
                    progressText.setText(progress);
                    autoLearningProgress.setProgress(100 * (skipIndex + 1) / wordsArray.size());
                    switch (step) {
                        case 1: //단어 노출, 단어 이미지 가림, 의미 가림
                            setWordImageView(wordImage);//이미지 미리 로드
                            textWord.setText(word);
                            wordImageView.setImageURI(null);
                            textMeaning1.setVisibility(View.GONE);
                            textMeaning2.setVisibility(View.GONE);
                            textMeaning3.setVisibility(View.GONE);
                            textMeaning4.setVisibility(View.GONE);
                            textMeaning5.setVisibility(View.GONE);
                            wordImageView.setVisibility(View.GONE);
                            ttsEng.speak(wordsArray.get(skipIndex).getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                        case 2: //단어 가림
                            textWord.setText("");
                            break;
                        case 3: //단어 노출, 단어 이미지 노출
                            textWord.setText(word);
                            if (wordImage == null) {
                                wordImageView.setVisibility(View.GONE);
                            } else {
                                wordImageView.setVisibility(View.VISIBLE);
                            }
                            ttsEng.speak(wordsArray.get(skipIndex).getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                        case 4: //의미 노출
                            if (meaning1 != null) {
                                textMeaning1.setVisibility(View.VISIBLE);
                                textMeaning1.setText(meaning1);
                            }
                            if (meaning2 != null) {
                                textMeaning2.setVisibility(View.VISIBLE);
                                textMeaning2.setText(meaning2);
                            }
                            if (meaning3 != null) {
                                textMeaning3.setVisibility(View.VISIBLE);
                                textMeaning3.setText(meaning3);
                            }
                            if (meaning4 != null) {
                                textMeaning4.setVisibility(View.VISIBLE);
                                textMeaning4.setText(meaning4);
                            }
                            if (meaning5 != null) {
                                textMeaning5.setVisibility(View.VISIBLE);
                                textMeaning5.setText(meaning5);
                            }

                            ttsKor.speak(meaningToTTS(), TextToSpeech.QUEUE_FLUSH, null, null);
                            break;
                    }
                } else {

                    finished();
                }

}

}

    public void finished(){
        btnPlayAndPause.setImageResource(R.drawable.play_circle_outline_72dp);
        btnTTS.setVisibility(View.VISIBLE);
        AlertDialog.Builder builderExitLearning = new AlertDialog.Builder(AutoLearningActivity.this);
        builderExitLearning.setMessage("학습을 종료하시겠습니까?");
        builderExitLearning.setNegativeButton("학습 종료", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "학습 종료", Toast.LENGTH_SHORT).show();
            isActivityRunning = false;
            for(int index = 0; index < wordsArray.size() ; index ++ ){
                updateStudyDate(wordsArray.get(index).vocabookTitle, wordsArray.get(index).chapterTitle);
            }

            finish();
        });
        builderExitLearning.setPositiveButton("다시 시작", (dialog, which) -> {
            for(int index = 0; index < wordsArray.size() ; index ++ ){
                updateStudyDate(wordsArray.get(index).vocabookTitle, wordsArray.get(index).chapterTitle);
            }
            isPlaying = true;
            btnPlayAndPause.setImageResource(R.drawable.pause_circle_outline_72dp);
            btnTTS.setVisibility(View.GONE);
        });
        builderExitLearning.show();
    }

    public String meaningToTTS() {
        if (wordsArray.get(skipIndex).getMeaning1() == null) {
            meaning1 = "";
        } else {
            meaning1 = wordsArray.get(skipIndex).getMeaning1();
        }
        if (wordsArray.get(skipIndex).getMeaning2() == null) {
            meaning2 = "";
        } else {
            meaning2 = wordsArray.get(skipIndex).getMeaning2();
        }
        if (wordsArray.get(skipIndex).getMeaning3() == null) {
            meaning3 = "";
        } else {
            meaning3 = wordsArray.get(skipIndex).getMeaning3();
        }
        if (wordsArray.get(skipIndex).getMeaning4() == null) {
            meaning4 = "";
        } else {
            meaning4 = wordsArray.get(skipIndex).getMeaning4();
        }
        if (wordsArray.get(skipIndex).getMeaning5() == null) {
            meaning5 = "";
        } else {
            meaning5 = wordsArray.get(skipIndex).getMeaning5();
        }

        return meaning1 + ". " + meaning2 + ". " + meaning3 + ". " + meaning4 + ". " + meaning5 + ". ";
    }

    public String getUserEmail() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        return settings.getString("loginEmail", "");
    }

    public void setWordImageView(String imageURI) {

        if (imageURI != null) {
            Glide.with(this)
                    .load(imageURI)
                    .centerCrop()
                    .override(200, 200)
                    .into(wordImageView);
        } else {
            wordImageView.setImageURI(null);
        }

    }

    public void setStrings(int index) {
        word = wordsArray.get(index).getWord();

        if (wordsArray.get(index).getMeaning1() == null) {
            meaning1 = null;
        } else {
            meaning1 = "1. " + wordsArray.get(index).getMeaning1();
        }
        if (wordsArray.get(index).getMeaning2() == null) {
            meaning2 = null;
        } else {
            meaning2 = "2. " + wordsArray.get(index).getMeaning2();
        }
        if (wordsArray.get(index).getMeaning3() == null) {
            meaning3 = null;
        } else {
            meaning3 = "3. " + wordsArray.get(index).getMeaning3();
        }
        if (wordsArray.get(index).getMeaning4() == null) {
            meaning4 = null;
        } else {
            meaning4 = "4. " + wordsArray.get(index).getMeaning4();
        }
        if (wordsArray.get(index).getMeaning5() == null) {
            meaning5 = null;
        } else {
            meaning5 = "5. " + wordsArray.get(index).getMeaning5();
        }
        if (wordsArray.get(index).getWordImageURI() == null) {
            wordImage = null;
        } else {
            wordImage = Methods.getWordImageFromServer(wordsArray.get(index).getWordImageURI());
        }
    }

    public void term(int time) {//자동학습스레드의 단어노출시간간격을 관리
        for (int term = 0; term <= time && isActivityRunning; ) {
            if (isPlaying && isSkipped == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(TAG, "term 메소드 오류 = " + e);
                }
                term++;
            } else if (isPlaying && isSkipped == next) {
                skipIndex = skipIndex + 1;
                if (skipIndex >= wordsArray.size()) {
                    skipIndex = wordsArray.size();
                }
                isSkipped = 0;
                step = 0;
                break;
            } else if (isPlaying && isSkipped == previous) {
                skipIndex = skipIndex - 1;
                if (skipIndex <= 0) {
                    skipIndex = 0;
                }
                isSkipped = 0;
                step = 0;
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        btnPlayAndPause.setImageResource(R.drawable.play_circle_outline_72dp);
        isPlaying = false;
        AlertDialog.Builder builderExitLearning = new AlertDialog.Builder(AutoLearningActivity.this);
        builderExitLearning.setCancelable(false);
        builderExitLearning.setMessage("학습을 종료하시겠습니까?");
        builderExitLearning.setNegativeButton("학습 종료", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "학습 종료", Toast.LENGTH_SHORT).show();
            isActivityRunning = false;
            finish();
        });
        builderExitLearning.setPositiveButton("학습 재개", (dialog, which) -> {
            isPlaying = true;
            btnPlayAndPause.setImageResource(R.drawable.pause_circle_outline_72dp);
        });
        builderExitLearning.show();
    }

    public void setTTS() {
        ttsEng = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int ttsLang = ttsEng.setLanguage(Locale.US);

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language is not supported!");
                } else {
                    Log.i("TTS", "Language Supported.");
                }
                Log.i("TTS", "Initialization success.");
            } else {
                Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });

ttsKor = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int ttsLang = ttsKor.setLanguage(Locale.KOREAN);

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language is not supported!");
                } else {
                    Log.i("TTS", "Language Supported.");
                }
                Log.i("TTS", "Initialization success.");
            } else {
                Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onStop() {
        super.onStop();
        if(isPlaying){
            isPlaying = false;
            btnPlayAndPause.setImageResource(R.drawable.pause_circle_outline_72dp);
            btnTTS.setVisibility(View.GONE);
            ttsEng.shutdown();
            ttsKor.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isPlaying || isActivityRunning){
            isPlaying = false;
            btnPlayAndPause.setImageResource(R.drawable.pause_circle_outline_72dp);
            btnTTS.setVisibility(View.GONE);
            isActivityRunning = false;
            ttsEng.shutdown();
            ttsKor.shutdown();
        }

    }
}