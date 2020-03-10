package com.example.android.Activities.StudyActivities;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.Adapters.SelectChapterAdapter;
import com.example.android.Dialogs.TestTimePickerDialog;
import com.example.android.Items.ChapterItem;
import com.example.android.Items.ChapterSelectItem;
import com.example.android.Items.SelectedChaptersItem;
import com.example.android.Items.VocabookItem;
import com.example.android.LinearLayoutManagerWrapper;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SelectChapterActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_select)
    RecyclerView recyclerviewSelect;
    SelectChapterAdapter selectChapterAdapter;
    String TAG = "SelectChapterActivity";
    ArrayList<ChapterSelectItem> selectListArray = new ArrayList<>();
    ArrayList<ChapterItem> chapterArray = new ArrayList<>();
    ArrayList<VocabookItem> vocabookArray = new ArrayList<>();
    ArrayList<SelectedChaptersItem> selectedArray = new ArrayList<>();
    String mode;
    @BindView(R.id.loading_anim)
    LottieAnimationView loadingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);
        ButterKnife.bind(this);

        mode = getIntent().getStringExtra("mode");
        loadingAnim.setAnimation("loading_anim.json");
        loadingAnim.playAnimation();
        loadingAnim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadingAnim.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        getVocabookListSync();
        getNumOfWordsVocabookSync();
        getChapterListSync();
        getNumOfWordsChapterSync();
    }

    public void setRecyclerviewSelect(RecyclerView recyclerviewSelect) {
        this.recyclerviewSelect = recyclerviewSelect;
        recyclerviewSelect.setLayoutManager(new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false));
        Log.e(TAG, "setRecyclerviewSelect...DONE");
        for (int index = 0; index < vocabookArray.size(); index++) {
            Log.e(TAG, vocabookArray.get(index).getVocabookNum() + ". vocabookTitle : " + vocabookArray.get(index).getVocabookTitle() + " (" + vocabookArray.get(index).getNumOfWords() + ")");
        }
        for (int index = 0; index < chapterArray.size(); index++) {
            Log.e(TAG, chapterArray.get(index).getChapterNum() + ". vocabookTitle : " + chapterArray.get(index).getVocabookTitle() + "/ chapterTitle : " + chapterArray.get(index).getChapterTitle() + " (" + chapterArray.get(index).getNumOfWords() + ")");
        }

        selectChapterAdapter = new SelectChapterAdapter(selectListArray);
        recyclerviewSelect.setAdapter(selectChapterAdapter);
    }

    @SuppressLint("StaticFieldLeak")
    public void getVocabookListSync() {
        final String[] vocabookJson = {null};
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_vocabook_list(userEmail);
                try {
                    vocabookJson[0] = res.execute().body().string();
                    jsonToArrayListVocabook(vocabookJson[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getVocabookListSync...DONE");
            }
        }.execute();
    }

    private void jsonToArrayListVocabook(String vocabookJson) {
        String TAG_JSON = "webnautes";
        String TAG_VOCABOOK_NUM = "vocabook_num";
        String TAG_VOCABOOK_TITLE = "vocabook_title";
        String TAG_VOCABOOK_COVER_IMAGE = "vocabook_cover_image";
//        Log.e(TAG, "vocabookJson : " + vocabookJson);
        if (!vocabookJson.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(vocabookJson);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    int vocabookNum = item.getInt(TAG_VOCABOOK_NUM);
                    String vocabookTitle = item.getString(TAG_VOCABOOK_TITLE);
                    String vocabookCoverImage = item.getString(TAG_VOCABOOK_COVER_IMAGE);
                    vocabookArray.add(new VocabookItem(vocabookCoverImage, vocabookNum, vocabookTitle, 0, false));
                }
                vocabookArray.sort(Comparator.naturalOrder());
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
        }

    }

    public void getNumOfWordsVocabookSync() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                for (int index = 0; index < vocabookArray.size(); index++) {
                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_num_of_word_vocabook(userEmail, vocabookArray.get(index).getVocabookTitle());
                    try {
                        int numOfWords = Integer.parseInt(res.execute().body().string());
                        vocabookArray.get(index).setNumOfWords(numOfWords);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getNumOfWordsVocabookSync...DONE");
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getChapterListSync() {
        final String[] chapterJson = {null};
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_all_chapter_list(userEmail);
                try {
                    chapterJson[0] = res.execute().body().string();
                    jsonToArrayListChapter(chapterJson[0]);
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void jsonToArrayListChapter(String chapterJson) {

        String TAG_JSON = "webnautes";
        String TAG_CHAPTER_NUM = "chapter_num";
        String TAG_CHAPTER_TITLE = "chapter_title";
        String TAG_VOCABOOK_TITLE = "vocabook_title";
        String TAG_RECENT_TEST_DATE = "recent_study_date";
//        Log.e(TAG, "chapterJson : " + chapterJson);
        if (!chapterJson.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(chapterJson);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    int chapterNum = item.getInt(TAG_CHAPTER_NUM);
                    String chapterTitle = item.getString(TAG_CHAPTER_TITLE);
                    String vocabookTitle = item.getString(TAG_VOCABOOK_TITLE);
                    String recentStudyDate;
                    if (item.getString(TAG_RECENT_TEST_DATE).equals("null")) {
                        recentStudyDate = null;
                    } else {
                        recentStudyDate = item.getString(TAG_RECENT_TEST_DATE);
                    }
                    chapterArray.add(new ChapterItem(chapterTitle, chapterNum, vocabookTitle, 0, recentStudyDate, null, false));

                }
                chapterArray.sort(Comparator.naturalOrder());
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
        }
    }

    public void getNumOfWordsChapterSync() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                for (int index = 0; index < chapterArray.size(); index++) {
                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_num_of_word_chapter(userEmail, chapterArray.get(index).getVocabookTitle(), chapterArray.get(index).getChapterTitle());
                    try {
                        int numOfWords = Integer.parseInt(res.execute().body().string());
                        chapterArray.get(index).setNumOfWords(numOfWords);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getNumOfWordsChapterSync...DONE");
                setSelectListArray();
                setRecyclerviewSelect(recyclerviewSelect);
            }
        }.execute();
    }

    public void testDialogAction() {
        Intent setTestTime = new Intent(this, TestTimePickerDialog.class);
        final int[] numOfWordsResult = {0};
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                for (int index = 0; index < selectedArray.size(); index++) {
                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_num_of_word_chapter(userEmail, selectedArray.get(index).getVocabookTitle(), selectedArray.get(index).getChapterTitle());
                    try {
                        int numOfWords = Integer.parseInt(res.execute().body().string());
                        numOfWordsResult[0] += numOfWords;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getNumOfWordsChapterSync...DONE");
                int totalNum =numOfWordsResult[0];
                setTestTime.putExtra("totalNum", totalNum);
                startActivityForResult(setTestTime, 50);
            }
        }.execute();
    }

    public void setSelectListArray() {
        for (int i = 0; i < vocabookArray.size(); i++) {
            selectListArray.add(new ChapterSelectItem(SelectChapterAdapter.VOCABOOK, vocabookArray.get(i).getVocabookTitle(), vocabookArray.get(i).getNumOfWords(), false));
            for (int j = 0; j < chapterArray.size(); j++) {
                if (chapterArray.get(j).getVocabookTitle().equals(vocabookArray.get(i).getVocabookTitle())) {
                    selectListArray.add(new ChapterSelectItem(SelectChapterAdapter.CHAPTER, chapterArray.get(j).getChapterTitle(), chapterArray.get(j).getNumOfWords(), false));
                }
            }
        }
        Log.e(TAG, "setSelectListArray...DONE");
        loadingAnim.cancelAnimation();
        loadingAnim.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_select_finish)
    public void onViewClicked() {
        selectedArray.clear();
        for (int index = 0; index < selectListArray.size(); index++) {
            Log.e(TAG, "[selectListArray] " + index + ". title : " + selectListArray.get(index).getTitle() + " (" + selectListArray.get(index).type + ")");
            if (selectListArray.get(index).type == SelectChapterAdapter.CHAPTER && selectListArray.get(index).getItemChecked()) {
                //Log.e(TAG, "클릭된 챕터를 포함한 단어장의 첫번째 챕터 : " + data.get(firstChapterIndex).title);
                selectedArray.add(new SelectedChaptersItem(selectListArray.get(vocabookTitleIndex(index)).getTitle(), selectListArray.get(index).getTitle()));
            }
        }
        for (int index = 0; index < selectedArray.size(); index++) {
            Log.e(TAG, "selectedArray : " + selectedArray.get(index).getChapterTitle() + " [" + selectedArray.get(index).getVocabookTitle() + "]");
        }
        if (selectedArray.size() != 0) {
            switch (mode) {
                case "test":
                   testDialogAction();
                    break;
                case "autoLearning":
                    Intent autoLearning = new Intent(this, AutoLearningActivity.class);
                    autoLearning.putParcelableArrayListExtra("selectedArray", selectedArray);
                    startActivity(autoLearning);
                    finish();
                    break;
                case "blackPaper":
                    Intent blackPaper = new Intent(this, BlackPaperActivity.class);
                    blackPaper.putParcelableArrayListExtra("selectedArray", selectedArray);
                    startActivity(blackPaper);
                    finish();
                    break;
                case "pronun":
                    Intent pronun = new Intent(this, PronunActivity.class);
                    pronun.putParcelableArrayListExtra("selectedArray", selectedArray);
                    startActivity(pronun);
                    finish();
                    break;
            }
        } else {
            Toast.makeText(this, "학습할 단어장 또는 챕터를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    public int vocabookTitleIndex(int position) {
        int vocabookIndex = -1;
        for (int idx = position; idx >= 0; idx--) {
            if (selectListArray.get(idx).type == SelectChapterAdapter.VOCABOOK) {
                vocabookIndex = idx;
                break;
            }
        }
        return vocabookIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && resultCode == RESULT_OK) {
            int testTime = 0;
            if (data != null) {
                testTime = data.getIntExtra("testTime", -1);
            }
            Intent test = new Intent(this, TestActivity.class);
            test.putParcelableArrayListExtra("selectedArray", selectedArray);
            Log.e(TAG, "testTime : " + testTime);
            test.putExtra("testTime", testTime);
            startActivity(test);
            finish();
        }

    }
}
