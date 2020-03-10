package com.example.android.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.Adapters.HomeAdapter;
import com.example.android.ChapterItemMoveCallback;
import com.example.android.Items.ChapterItem;
import com.example.android.Items.HomeItem;
import com.example.android.LinearLayoutManagerWrapper;
import com.example.android.Methods;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    @BindView(R.id.recyclerView_home)
    RecyclerView recyclerViewHome;
    RecyclerView.Adapter homeAdapter;
    private String TAG = "HomeFragment";
    private ArrayList<HomeItem> homeArray = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        getChapterListSync();



        return view;
    }

    private void setRecyclerViewHome() {
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerViewHome.setHasFixedSize(true);
        recyclerViewHome.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        recyclerViewHome.addItemDecoration(new DividerItemDecoration(recyclerViewHome.getContext(), new LinearLayoutManagerWrapper(getContext()).getOrientation()));
        Log.e(TAG, "homeArray.size : " + homeArray.size());
        homeArray.sort(Comparator.reverseOrder());
        homeAdapter = new HomeAdapter(homeArray);
        recyclerViewHome.setAdapter(homeAdapter);
        Log.e(TAG, "setRecyclerViewHome");
    }

    @SuppressLint("StaticFieldLeak")
    private void getChapterListSync() {
        final String[] chapterJson = {null};
        SharedPreferences settings = Objects.requireNonNull(getContext()).getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Log.e(TAG, "userEmail : " + userEmail);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_all_chapter_list(userEmail);
                try {
                    chapterJson[0] = res.execute().body().string();
                    Log.e(TAG, "chapterJson[0] : " + chapterJson[0]);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getChapterListSync...DONE");
                jsonToArrayListChapter(chapterJson[0]);
            }
        }.execute();
    }

    private void jsonToArrayListChapter(String chapterJson) {

        String TAG_JSON = "webnautes";
        String TAG_CHAPTER_NUM = "chapter_num";
        String TAG_CHAPTER_TITLE = "chapter_title";
        String TAG_VOCABOOK_TITLE = "vocabook_title";
        String TAG_RECENT_TEST_DATE = "recent_study_date";
        Log.e(TAG, "chapterJson : " + chapterJson);
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

                    homeArray.add(new HomeItem(vocabookTitle, null, chapterTitle, 0, recentStudyDate, 99999));
                    if(recentStudyDate != null){
                        homeArray.get(homeArray.size()-1).setPriority((int)Methods.getStudyDaysPassedInteger(Methods.currentDate(), homeArray.get(homeArray.size()-1).getStudyDate()));
                    }
                }

                getVocabookImage();
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
        }
        Log.e(TAG, "jsonToArrayListChapter");
    }

    @SuppressLint("StaticFieldLeak")
    private void getNumOfWordsChapterSync() {
        SharedPreferences settings = Objects.requireNonNull(getContext()).getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                for (int index = 0; index < homeArray.size(); index++) {
                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_num_of_word_chapter(userEmail, homeArray.get(index).getVocabookTitle(), homeArray.get(index).getChapterTitle());
                    try {
                        int numOfWords = Integer.parseInt(Objects.requireNonNull(res.execute().body()).string());
                        homeArray.get(index).setChapterWordCount(numOfWords);

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
                setRecyclerViewHome();
            }
        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    private void getVocabookImage(){
        SharedPreferences settings = Objects.requireNonNull(getContext()).getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.e(TAG, "(getVocabookImage)homeArray 의 사이즈 :" + homeArray.size());
                for (int index = 0; index < homeArray.size(); index++) {
                    @SuppressLint("StaticFieldLeak") Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_vocabook_cover_image(userEmail, homeArray.get(index).getVocabookTitle());
                    try {
                        String vocabookImageJson = Objects.requireNonNull(res.execute().body()).string();

                        String TAG_JSON = "webnautes";
                        String TAG_VOCABOOK_COVER_IMAGE = "vocabook_cover_image";
                        Log.e(TAG, "vocabookImageJson : " + vocabookImageJson);
                        if (!vocabookImageJson.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(vocabookImageJson);
                                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    String vocabookCoverImage = item.getString(TAG_VOCABOOK_COVER_IMAGE);
                                    homeArray.get(index).setVocabookImage(vocabookCoverImage);
                                }
                                homeArray.sort(Comparator.naturalOrder());
                            } catch (JSONException e) {
                                Log.e(TAG, "showError : ", e);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "getVocabookImage...DONE");
                getNumOfWordsChapterSync();

            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}
