package com.wonjin.android.Activities.StudyActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.wonjin.android.Adapters.PronunAdapter;
import com.wonjin.android.ItemClickSupport;
import com.wonjin.android.Items.PronunItem;
import com.wonjin.android.Items.SelectedChaptersItem;
import com.wonjin.android.Methods;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PronunActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    RecyclerView.Adapter pronunAdapter;
    RecyclerView.LayoutManager pronunLayoutManager;
    ArrayList<SelectedChaptersItem> selectedArray = new ArrayList<>();
    ArrayList<PronunItem> pronunArray = new ArrayList<>();
    String selected, vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage;
    String TAG = "PronunActivity";
    String outputWord;
    boolean isTimeRunning, isActivityRunning;
    int wordSelected;
    TextToSpeech ttsEng;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @BindView(R.id.learningTimePronun)
    TextView learningTimePronun;
    @BindView(R.id.wordPronun)
    TextView wordPronun;
    @BindView(R.id.meaningPronun)
    TextView meaningPronun;
    @BindView(R.id.recyclerViewPronun)
    RecyclerView recyclerViewPronun;
    @BindView(R.id.hmm)
    ImageView hmm;
    @BindView(R.id.micButton)
    LottieAnimationView micButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronun);
        ButterKnife.bind(this);

        isTimeRunning = !isTimeRunning;
        isActivityRunning = true;
        new TimeThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        hmm.setVisibility(View.GONE);

        micButton.setAnimation("mic.json");
        micButton.playAnimation();
        micButton.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                micButton.playAnimation();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        getSelectedArray();
        getWordsListSync();
        setRecyclerViewPronun();

        //리사이클러뷰 선언부

ttsEng = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int ttsLang = ttsEng.setLanguage(Locale.US);

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "지원하지 않는 언어입니다.");
                } else {
                    Log.i("TTS", "Language Supported.");
                }
                Log.i("TTS", "Initialization success.");
            } else {
                Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });

ItemClickSupport.addTo(recyclerViewPronun).setOnItemClickListener((recyclerView, position, v) -> {
            ttsEng.speak(pronunArray.get(position).getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            wordSelected = position;
            wordPronun.setText(pronunArray.get(position).getWord());
            meaningPronun.setText(meanings(position));
        });

View.OnClickListener listener = v -> {
            if (v.getId() == R.id.micButton) {
                if (wordPronun.getText().toString().equals("연습할 단어를 선택해주세요")) {
                    Toast.makeText(getApplicationContext(), "연습할 단어를 선택해주세요", Toast.LENGTH_SHORT).show();

                    return;
                } else {
                    promptSpeechInput();
                }
            }
        };
        micButton.setOnClickListener(listener);

}

    public void setRecyclerViewPronun(){
        recyclerViewPronun = findViewById(R.id.recyclerViewPronun);
        recyclerViewPronun.setHasFixedSize(true);
        pronunLayoutManager = new LinearLayoutManager(this);
        recyclerViewPronun.setLayoutManager(pronunLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPronun.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerViewPronun.addItemDecoration(dividerItemDecoration);
        pronunAdapter = new PronunAdapter(pronunArray);
        recyclerViewPronun.setAdapter(pronunAdapter);
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

                    pronunArray.add(new PronunItem(vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage, null));
                }
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
            Log.e(TAG, "단어 로딩 완료...");
            for (int index = 0; index < pronunArray.size(); index++) {
                Log.e(TAG, "pronunArray(" + index + "). vocabookTitle : " + pronunArray.get(index).getVocabookTitle() + " / chapterTitle : " + pronunArray.get(index).getChapterTitle()
                        + " / meaning1 : " + pronunArray.get(index).getMeaning1() + " / meaning2 : " + pronunArray.get(index).getMeaning2() + " / meaning3 : " + pronunArray.get(index).getMeaning3()
                        + " / meaning4 : " + pronunArray.get(index).getMeaning4() + " / meaning5 : " + pronunArray.get(index).getMeaning5() + " / wordImageUri : " + pronunArray.get(index).getWordImageURI()
                        + " / isCorrect : " + pronunArray.get(index).getCorrect());
            }

        }
    }

    public class TimeThread extends AsyncTask<Integer, Integer, Integer> {
        int milliSecond;

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (isActivityRunning) {
                while (isTimeRunning) { //일시정지를 누르면 멈추도록
                    if (isCancelled()) {
                        break;
                    }
                    publishProgress(1);
//                    Log.e(TAG, "isRunning = " + isRunning);
                    milliSecond++;
//                    Log.e(TAG, "doInBackground" + milliSecond);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                        Log.e(TAG, "오류났다 ㅎㅎ 오류내용 = " + e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(1);
//            Log.e(TAG, "isRunning = " + isRunning);
            int mSec = milliSecond % 100;
            int sec = (milliSecond / 100) % 60;
            int min = (milliSecond / 100) / 60;
            if (min > 0) {
                String result = String.format("%02d : %02d", min, sec);
                learningTimePronun.setText(result);
            } else {
                String result = String.format("00 : %02d", sec);
                learningTimePronun.setText(result);
            }

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.e(TAG, "onPostExecute" + milliSecond);
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                outputWord = result.get(0);
                Log.e(TAG, "음성인식으로 받아온 단어 = " + outputWord);
                outputWord = outputWord.toLowerCase();
                if (outputWord.equals(wordPronun.getText().toString())) {
                    mediaPlayer = MediaPlayer.create(PronunActivity.this, R.raw.clap);
                    mediaPlayer.start();
                    hmm.setImageResource(R.drawable.thumbs_up);
                    hmm.setVisibility(View.VISIBLE);
                    Animation slowlyAppear;
                    slowlyAppear = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                    hmm.setAnimation(slowlyAppear);
                    pronunArray.get(wordSelected).correct = true;
                    pronunAdapter.notifyDataSetChanged();
                } else {
                    mediaPlayer = MediaPlayer.create(PronunActivity.this, R.raw.hmm);
                    mediaPlayer.start();
                    hmm.setImageResource(R.drawable.hmm);
                    hmm.setVisibility(View.VISIBLE);
                    Animation slowlyAppear;
                    slowlyAppear = AnimationUtils.loadAnimation(this, R.anim.fade_in);

hmm.setAnimation(slowlyAppear);
//                        hmm.setAnimation(slowlyDisappear);

pronunArray.get(wordSelected).correct = false;
                    pronunAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public String meanings(int index) {
        if (pronunArray.get(index).getMeaning1() == null) {
            meaning1 = "";
        } else {
            meaning1 = pronunArray.get(index).getMeaning1();
        }
        if (pronunArray.get(index).getMeaning2() == null) {
            meaning2 = "";
        } else {
            meaning2 = ", " + pronunArray.get(index).getMeaning2();
        }
        if (pronunArray.get(index).getMeaning3() == null) {
            meaning3 = "";
        } else {
            meaning3 = ", " + pronunArray.get(index).getMeaning3();
        }
        if (pronunArray.get(index).getMeaning4() == null) {
            meaning4 = "";
        } else {
            meaning4 = ", " + pronunArray.get(index).getMeaning4();
        }
        if (pronunArray.get(index).getMeaning5() == null) {
            meaning5 = "";
        } else {
            meaning5 = ", " + pronunArray.get(index).getMeaning5();
        }

        return meaning1 + meaning2 + meaning3 + meaning4 + meaning5;
    }

    public String getUserEmail() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        return settings.getString("loginEmail", "");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builderExitLearning = new AlertDialog.Builder(PronunActivity.this);
        builderExitLearning.setMessage("학습을 종료하시겠습니까?");
        builderExitLearning.setNegativeButton("학습 종료", (dialog, which) -> {
            for(int index = 0 ; index < pronunArray.size() ; index ++){

                    if(pronunArray.get(index).getCorrect() != null && pronunArray.get(index).getCorrect()){
                        updateStudyDate(pronunArray.get(index).vocabookTitle, pronunArray.get(index).chapterTitle);
                    }

}
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

@Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
        ttsEng.shutdown();
    }
}
