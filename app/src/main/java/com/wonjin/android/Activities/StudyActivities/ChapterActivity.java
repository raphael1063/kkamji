package com.wonjin.android.Activities.StudyActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.wonjin.android.Adapters.ChapterAdapter;
import com.wonjin.android.ChapterItemMoveCallback;
import com.wonjin.android.Items.ChapterItem;
import com.wonjin.android.LinearLayoutManagerWrapper;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;
import com.wonjin.android.StartDragListener;

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
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterActivity extends AppCompatActivity implements StartDragListener {

    @BindView(R.id.chapter_tool_bar_title)
    TextView chapterToolBarTitle;
    @BindView(R.id.chapter_toolbar)
    Toolbar chapterToolbar;
    @BindView(R.id.recyclerview_chapter_list)
    RecyclerView recyclerviewChapterList;
    ChapterAdapter chapterAdapter;
    ArrayList<ChapterItem> chapterArray = new ArrayList<>();
    ItemTouchHelper touchHelper;
    String TAG = "ChapterActivity";
    String vocabookTitle;
    int editPosition = -1;
    int numOfWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        ButterKnife.bind(this);

        Intent getTitle = getIntent();
        vocabookTitle = getTitle.getStringExtra("vocabookTitle");
        chapterToolBarTitle.setText(vocabookTitle);
        getChapterList();
        setRecyclerviewChapterList();

        chapterAdapter.setOnClickListener((position) -> {
            editPosition = position;
            Intent openChapter = new Intent(this, AddChapterActivity.class);
            openChapter.putExtra("chapterTitle", chapterArray.get(position).getChapterTitle());
            openChapter.putExtra("chapterPosition", position);
            openChapter.putExtra("vocabookTitle", vocabookTitle);
            startActivityForResult(openChapter, 20);
        });
    }

    public void setRecyclerviewChapterList() {
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerviewChapterList.setHasFixedSize(true);
        recyclerviewChapterList.setLayoutManager(new LinearLayoutManagerWrapper(this));
        recyclerviewChapterList.addItemDecoration(new DividerItemDecoration(recyclerviewChapterList.getContext(), new LinearLayoutManagerWrapper(this).getOrientation()));
        Log.e(TAG, "vocabookArray.size : " + chapterArray.size());
        chapterAdapter = new ChapterAdapter(this, chapterArray, this);
        touchHelper = new ItemTouchHelper(new ChapterItemMoveCallback(chapterAdapter));
        touchHelper.attachToRecyclerView(recyclerviewChapterList);
        recyclerviewChapterList.setAdapter(chapterAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.not_move, R.anim.slide_out_right);

    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
        for (int idx = 0; idx < chapterArray.size(); idx++) {
            chapterArray.get(idx).setChapterNum(idx);
            Log.e(TAG, "chapterTitle : " + chapterArray.get(idx).getChapterTitle() + " / position : " + chapterArray.get(idx).getChapterNum());
        }
    }

    @OnClick(R.id.btn_add_chapter)
    public void onViewClicked() {
        Intent intent = new Intent(this, AddChapterActivity.class);
        intent.putExtra("vocabookTitle", vocabookTitle);
        intent.putExtra("chapterArray.size", chapterArray.size());
        startActivityForResult(intent, 30);
    }

    public void getChapterList() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");

        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_chapter_list(userEmail, vocabookTitle);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String chapterJson = response.body().string();
                    jsonToArrayListChapter(chapterJson);
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

    private void jsonToArrayListChapter(String chapterJson) {

        String TAG_JSON = "webnautes";
        String TAG_CHAPTER_NUM = "chapter_num";
        String TAG_CHAPTER_TITLE = "chapter_title";
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

                    String recentStudyDate;
                    if (item.getString(TAG_RECENT_TEST_DATE).equals("null")) {
                        recentStudyDate = null;
                    } else {
                        recentStudyDate = item.getString(TAG_RECENT_TEST_DATE);
                    }
                    chapterArray.add(new ChapterItem(chapterTitle, chapterNum, vocabookTitle, 0, recentStudyDate, null, false));
                    chapterAdapter.notifyItemInserted(chapterArray.size());
                }
                chapterArray.sort(Comparator.naturalOrder());

                getNumOfWords();

            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
        }

    }

    public void getNumOfWords() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");

        for (int index = 0; index < chapterArray.size(); index++) {
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_num_of_word_chapter(userEmail, vocabookTitle, chapterArray.get(index).getChapterTitle());
            int finalIndex = index;
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        int numOfWords = Integer.parseInt(response.body().string());
                        Log.e(TAG, "요청값 // userEmail : " + userEmail + " / vocabookTitle : " + vocabookTitle + " / chapterTitle : " + chapterArray.get(finalIndex).getChapterTitle());
                        Log.e(TAG, "결과값 : " + numOfWords);
                        chapterArray.get(finalIndex).setNumOfWords(numOfWords);
                        chapterAdapter.notifyItemChanged(finalIndex);
                        chapterAdapter.notifyDataSetChanged();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 30 || requestCode == 20) && resultCode == RESULT_OK) {
            chapterArray.clear();
            getChapterList();
        }
    }
}
