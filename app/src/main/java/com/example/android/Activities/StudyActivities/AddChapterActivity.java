package com.example.android.Activities.StudyActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.Adapters.MeaningAdapter;
import com.example.android.Adapters.WordAdapter;
import com.example.android.Items.MeaningItem;
import com.example.android.Items.WordItem;
import com.example.android.LinearLayoutManagerWrapper;
import com.example.android.Methods;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;
import com.example.android.UploadObject;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.android.Methods.encodeFileName;
import static com.example.android.Methods.getExtension;

public class AddChapterActivity extends AppCompatActivity implements WordAdapter.WordItemClickListener {

    @BindView(R.id.btnAddMeaning)
    Button btnAddMeaning;
    @BindView(R.id.editChapterTitle)
    EditText editChapterTitle;
    @BindView(R.id.editWord)
    EditText editWord;
    @BindView(R.id.btnSearchMeaning)
    Button btnSearchMeaning;
    @BindView(R.id.meaningList)
    RecyclerView meaningList;
    @BindView(R.id.btnWordImage)
    ImageView btnWordImage;
    @BindView(R.id.btnAddWord)
    Button btnAddWord;
    @BindView(R.id.wordList)
    RecyclerView wordList;
    @BindView(R.id.btnChapterSave)
    Button btnChapterSave;
    @BindView(R.id.totalWord)
    TextView totalWord;

    ArrayList<MeaningItem> meaningArray = new ArrayList<>();
    MeaningAdapter meaningAdapter;

    ArrayList<WordItem> wordArray = new ArrayList<>();
    WordAdapter wordAdapter;
    String TAG = "AddChapterActivity";
    String meaning1, meaning2, meaning3, meaning4, meaning5, translateResult, wordResult, chapterTitle, vocabookTitle, fileName, fileExtension, oldChapterTitle;
    String wordImageURI = "";
    boolean isComplete;
    int isChapterEdit, isWordEdit, editPosition;
    int CREATING = 1001;
    int EDITING = 1002;
    int chapterArraySize, chapterPosition;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);
        ButterKnife.bind(this);

        //어느 단어장, 어느 챕터인지 받아온다.
        Intent getInfo = getIntent();
        chapterTitle = getInfo.getStringExtra("chapterTitle");
        vocabookTitle = getInfo.getStringExtra("vocabookTitle");
        chapterArraySize = getInfo.getIntExtra("chapterArray.size", -1);
        chapterPosition = getInfo.getIntExtra("chapterPosition", -1);
        Log.e(TAG, "chapterTitle : " + chapterTitle);
        Log.e(TAG, "vocabookTitle : " + vocabookTitle);
        Log.e(TAG, "chapterArray.size : " + chapterArraySize);
        Log.e(TAG, "chapterPosition : " + chapterPosition);
        isChapterEdit = CREATING; //1001
        isWordEdit = CREATING; // 단어 생성시
        if (chapterTitle != null) {
            editChapterTitle.setText(chapterTitle);
            isChapterEdit = EDITING; //1002
        }
        Log.e(TAG, "isChapterEdit = " + isChapterEdit);

        setMeaningListRecyclerview();
        setWordListRecyclerview();

        View.OnClickListener listener = view -> {
            switch (view.getId()) {
                case R.id.btnSearchMeaning:
                    new TranslateTask().execute();
                    break;
                case R.id.btnWordImage:
                    Pix.start(this, Options.init().setRequestCode(100));
                    break;
                case R.id.btnAddWord:
                    if (btnAddWord.getText().toString().equals("수정 완료")) {
                        btnAddWord.setText("단어 등록");
                    }
                    if (editWord.getText().toString().equals("")) { //단어가 입력되지 않았을 때
                        Toast.makeText(this, "단어 또는 숙어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (meaningArray.size() == 0) { //뜻이 입력되지 않았을 때
                        Toast.makeText(this, "의미를 추가해주세요.", Toast.LENGTH_SHORT).show();

                    } else {
                        //단어를 등록하기 전 비어있는 의미 입력창은 제거한다.
                        for (int index = 0; index < meaningArray.size(); index++) {
                            if (meaningArray.get(index).meaning.equals("")) {
                                meaningArray.remove(index);
                                meaningAdapter.notifyItemRemoved(index);
                                meaningAdapter.notifyItemRangeChanged(index, meaningArray.size());
                            }
                        }
                        Log.e(TAG, "meaningArray 의 size : " + meaningArray.size());

                        if (isWordEdit == EDITING) { // 단어 수정시
                            wordArray.get(editPosition).setWord(editWord.getText().toString());
                            switch (meaningArray.size()) {
                                case 1:
                                    Log.e(TAG, "의미 1개 등록");
                                    wordArray.set(editPosition, new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, null, null, null, null, wordImageURI));
                                    break;
                                case 2:
                                    Log.e(TAG, "의미 2개 등록");
                                    wordArray.set(editPosition, new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, null, null, null, wordImageURI));
                                    break;
                                case 3:
                                    Log.e(TAG, "의미 3개 등록");
                                    wordArray.set(editPosition, new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, meaningArray.get(2).meaning, null, null, wordImageURI));
                                    break;
                                case 4:
                                    Log.e(TAG, "의미 4개 등록");
                                    wordArray.set(editPosition, new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, meaningArray.get(2).meaning, meaningArray.get(3).meaning, null, wordImageURI));
                                    break;
                                case 5:
                                    Log.e(TAG, "의미 5개 등록");
                                    wordArray.set(editPosition, new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, meaningArray.get(2).meaning, meaningArray.get(3).meaning, meaningArray.get(4).meaning, wordImageURI));
                                    break;

                            }
                            for (int index = 0; index < wordArray.size(); index++) {
                                String wordImage = wordArray.get(index).getWordImageURI();
                                Log.e(TAG, "Check(onClickBtnAddWord)...(" + index + ") wordImage : " + wordImage);
                            }
                            wordAdapter.notifyItemChanged(editPosition);
                            wordAdapter.notifyDataSetChanged();
                            isWordEdit = CREATING;
                        } else { //단어 등록시
                            for (int index = 0; index < wordArray.size(); index++) {
                                if (wordArray.get(index).getWord().equals(editWord.getText().toString())) {
                                    Toast.makeText(this, "이미 등록되어있는 단어입니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            switch (meaningArray.size()) {
                                case 1:
                                    Log.e(TAG, "의미 1개 등록");
                                    wordArray.add(new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, null, null, null, null, wordImageURI));
                                    break;
                                case 2:
                                    Log.e(TAG, "의미 2개 등록");
                                    wordArray.add(new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, null, null, null, wordImageURI));
                                    break;
                                case 3:
                                    Log.e(TAG, "의미 3개 등록");
                                    wordArray.add(new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, meaningArray.get(2).meaning, null, null, wordImageURI));
                                    break;
                                case 4:
                                    Log.e(TAG, "의미 4개 등록");
                                    wordArray.add(new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, meaningArray.get(2).meaning, meaningArray.get(3).meaning, null, wordImageURI));
                                    break;
                                case 5:
                                    Log.e(TAG, "의미 5개 등록");
                                    wordArray.add(new WordItem(editWord.getText().toString(), meaningArray.get(0).meaning, meaningArray.get(1).meaning, meaningArray.get(2).meaning, meaningArray.get(3).meaning, meaningArray.get(4).meaning, wordImageURI));
                                    break;

                            }
                            for (int index = 0; index < wordArray.size(); index++) {
                                String wordImage = wordArray.get(index).getWordImageURI();
                                Log.e(TAG, "Check(onClickBtnAddWord)...(" + index + ") wordImage : " + wordImage);
                            }
                            Log.e(TAG, "wordArray 의 size : " + wordArray.size());
                            totalWord.setText("총 단어 : " + wordArray.size());
                            wordAdapter.notifyItemInserted(wordArray.size());
                        }

                        meaningAdapter.notifyItemRangeRemoved(0, meaningArray.size());
                        meaningArray.clear();
                        meaningArray.add(new MeaningItem(""));
                        meaningAdapter.notifyItemInserted(meaningArray.size());
                        meaningAdapter.notifyDataSetChanged();
                        editWord.setText("");
                        ResetMeaningLayoutHeight();

                        wordImageURI = "";
                        btnWordImage.setImageResource(R.drawable.ic_image_grey_24dp);

                    }

                    isWordEdit = CREATING;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);

                    break;
                case R.id.btnChapterSave:
                    if (editChapterTitle.getText().toString().isEmpty()) {
                        Toast.makeText(this, "단어장 이름을 작성해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (isChapterEdit == CREATING) {
                            Log.e(TAG, "챕터를 생성합니다.");
                            saveNewChapter();
                        } else if (isChapterEdit == EDITING) {
                            Log.e(TAG, "챕터를 리셋합니다.");
                            resetChapter();
                        }

                        Intent intent = new Intent();
                        intent.putExtra("numOfWords", wordArray.size());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;
                case R.id.btnAddMeaning:
                    if (meaningArray.size() < 5) {
                        meaningArray.add(new MeaningItem(""));
                        meaningAdapter.notifyItemInserted(meaningArray.size());
                        ResetMeaningLayoutHeight();
                    } else {
                        Toast.makeText(this, "의미는 최대 5개까지만 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        };
        btnSearchMeaning.setOnClickListener(listener);
        btnWordImage.setOnClickListener(listener);
        btnAddWord.setOnClickListener(listener);
        btnChapterSave.setOnClickListener(listener);
        btnAddMeaning.setOnClickListener(listener);

    }

    public void setMeaningListRecyclerview() {
        meaningList = findViewById(R.id.meaningList);
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        meaningList.setHasFixedSize(true);
        meaningList.setLayoutManager(new LinearLayoutManagerWrapper(this));
        meaningArray.add(new MeaningItem(""));

        meaningAdapter = new MeaningAdapter(meaningArray);
        meaningList.setAdapter(meaningAdapter);

        meaningAdapter.setOnMeaningDeleteListener((v, position) -> {
            Toast.makeText(this, position + "번 아이템 삭제됨", Toast.LENGTH_SHORT).show();

            meaningArray.remove(position);
            meaningAdapter.notifyItemRemoved(position);
            meaningAdapter.notifyItemRangeChanged(position, meaningArray.size());
            ResetMeaningLayoutHeight();
        });
    }

    public void setWordListRecyclerview() {
        wordList = findViewById(R.id.wordList);
        wordList.setHasFixedSize(true);
        wordList.setLayoutManager(new LinearLayoutManagerWrapper(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(wordList.getContext(), new LinearLayoutManagerWrapper(this).getOrientation());
        wordList.addItemDecoration(dividerItemDecoration);
        totalWord.setText("총 단어 : " + wordArray.size());

        if (isChapterEdit == EDITING) {
            getWordList();
        }

        wordAdapter = new WordAdapter(wordArray);
        wordAdapter.setOnClickListener(AddChapterActivity.this);
        wordList.setAdapter(wordAdapter);
    }

    public void getWordList() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");

        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_word_list(userEmail, vocabookTitle, chapterTitle);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String chapterJson = response.body().string();
                    jsonToArrayListWord(chapterJson);
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

    private void jsonToArrayListWord(String chapterJson) {

        String TAG_JSON = "webnautes";
        String TAG_WORD = "word";
        String TAG_MEANING1 = "meaning1";
        String TAG_MEANING2 = "meaning2";
        String TAG_MEANING3 = "meaning3";
        String TAG_MEANING4 = "meaning4";
        String TAG_MEANING5 = "meaning5";
        String TAG_WORD_IMAGE_URI = "word_image_uri";
        Log.e(TAG, "chapterJson : " + chapterJson);
        if (!chapterJson.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(chapterJson);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String word = item.getString(TAG_WORD);
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

                    String wordImageUri = item.getString(TAG_WORD_IMAGE_URI);
                    wordArray.add(new WordItem(word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImageUri));
                    wordAdapter.notifyItemInserted(wordArray.size());
                }
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                if (returnValue != null) {
                    Log.e(TAG, "returnValue : " + returnValue.get(0));
                    Glide.with(this)
                            .load(returnValue.get(0))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .centerCrop()
                            .override(200, 200)
                            .into(btnWordImage);
                    file = new File(returnValue.get(0));
                    wordImageURI = returnValue.get(0);
                }
            } else {
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
        }

    }

    public void ResetMeaningLayoutHeight() {
        ViewGroup.LayoutParams params = meaningList.getLayoutParams();
        params.height = WRAP_CONTENT;
        meaningList.setLayoutParams(params);
    }

    @Override
    public void onItemClicked(int position) {
        isWordEdit = EDITING;
        editPosition = position;
        editWord.setText(wordArray.get(position).word);
        btnAddWord.setText("수정 완료");
        wordImageURI = wordArray.get(editPosition).wordImageURI;
        meaningAdapter.notifyItemRangeRemoved(0, meaningArray.size());
        meaningArray.clear();
        if (wordArray.get(position).meaning1 != null) {
            meaningArray.add(new MeaningItem(wordArray.get(position).meaning1));
            meaningAdapter.notifyItemInserted(0);
            ResetMeaningLayoutHeight();
        }
        if (wordArray.get(position).meaning2 != null) {
            meaningArray.add(new MeaningItem(wordArray.get(position).meaning2));
            meaningAdapter.notifyItemInserted(1);
            ResetMeaningLayoutHeight();
        }
        if (wordArray.get(position).meaning3 != null) {
            meaningArray.add(new MeaningItem(wordArray.get(position).meaning3));
            meaningAdapter.notifyItemInserted(2);
            ResetMeaningLayoutHeight();
        }
        if (wordArray.get(position).meaning4 != null) {
            meaningArray.add(new MeaningItem(wordArray.get(position).meaning4));
            meaningAdapter.notifyItemInserted(3);
            ResetMeaningLayoutHeight();
        }
        if (wordArray.get(position).meaning5 != null) {
            meaningArray.add(new MeaningItem(wordArray.get(position).meaning5));
            meaningAdapter.notifyItemInserted(4);
            ResetMeaningLayoutHeight();
        }
        if (wordArray.get(position).wordImageURI != null) {
            if (wordArray.get(position).wordImageURI.startsWith("http")) {
                Glide.with(this)
                        .load(Methods.getWordImageFromServer(wordArray.get(position).wordImageURI))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .override(200, 200)
                        .into(btnWordImage);
            } else if (wordArray.get(position).wordImageURI.startsWith("/storage")) {
                Glide.with(this)
                        .load(wordArray.get(position).wordImageURI)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .override(200, 200)
                        .into(btnWordImage);
            } else {
                btnWordImage.setImageResource(R.drawable.ic_image_grey_24dp);
            }

        }

    }

    @Override
    public void onDeleteButtonClicked(int position) {
        if (isWordEdit == EDITING) {
            Toast.makeText(this, "수정중에는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            wordArray.remove(position);
            wordAdapter.removeItem(position);
            totalWord.setText("총 단어 : " + wordArray.size());
        }

    }

    //새 챕터 저장
    public void saveNewChapter() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().save_chapter(userEmail, vocabookTitle, chapterArraySize, editChapterTitle.getText().toString());
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String saveChapterResponse = response.body().string();
                        Log.e(TAG, "saveChapterResponse : " + saveChapterResponse);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "saveChapterResponse fail : " + t.getMessage());
            }
        });

        for (int index = 0; index < wordArray.size(); index++) {
            String wordImage = wordArray.get(index).getWordImageURI();
            Log.e(TAG, "Check...(" + index + ") wordImage : " + wordImage);
        }

        for (int index = 0; index < wordArray.size(); index++) {
            String wordImage = wordArray.get(index).getWordImageURI();
            Log.e(TAG, "Saving...(" + index + ") wordImage : " + wordImage);
            if (wordImage.equals("")) {
                wordImage = null;
            } else {
                file = new File(wordArray.get(index).wordImageURI);
                fileName = userEmail + "_" + vocabookTitle + "_" + editChapterTitle.getText().toString() + "_" + wordArray.get(index).word;
                //한글 파일명을 그대로 사용해서 파일을 업로드하면 오류가 나서 UTF-8 로 인코딩한다.
                fileName = encodeFileName(fileName);
                fileExtension = getExtension(file.getName());
                wordImage = fileName + "." + fileExtension;
            }

            if (file != null) {
                RequestBody mFile = RequestBody.create(MediaType.parse("word_images/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", wordImage, mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), wordImage);
                Log.e(TAG, "fileName : " + filename);
                Call<UploadObject> fileUpload = RetrofitClient.getInstance().getService().upload_word_image(fileToUpload, filename);
                fileUpload.enqueue(new Callback<UploadObject>() {
                    @Override
                    public void onResponse(@NonNull Call<UploadObject> call, Response<UploadObject> response) {
                        Log.e(TAG, "image upload result :  " + response.body().getSuccess());
                    }

                    @Override
                    public void onFailure(@NonNull Call<UploadObject> call, Throwable t) {
                        Log.d(TAG, "image upload error " + t.getMessage());
                    }
                });
                file = null;
            }

            Log.e(TAG, "userEmail : " + userEmail);
            Log.e(TAG, "vocabookTitle : " + vocabookTitle);
            Log.e(TAG, "chapterTitle : " + editChapterTitle.getText().toString());
            Log.e(TAG, "word : " + wordArray.get(index).getWord());
            Log.e(TAG, "meaning1 : " + wordArray.get(index).getMeaning1());
            Log.e(TAG, "meaning2 : " + wordArray.get(index).getMeaning2());
            Log.e(TAG, "meaning3 : " + wordArray.get(index).getMeaning3());
            Log.e(TAG, "meaning4 : " + wordArray.get(index).getMeaning4());
            Log.e(TAG, "meaning5 : " + wordArray.get(index).getMeaning5());
            Log.e(TAG, "wordImage : " + wordImage);

            Call<ResponseBody> res2 = RetrofitClient.getInstance().getService().save_chapter_content(userEmail, vocabookTitle, editChapterTitle.getText().toString(), wordArray.get(index).getWord(), wordArray.get(index).getMeaning1(),
                    wordArray.get(index).getMeaning2(), wordArray.get(index).getMeaning3(), wordArray.get(index).getMeaning4(), wordArray.get(index).getMeaning5(), wordImage);
            res2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            String saveChapterResponse = response.body().string();
                            Log.e(TAG, "saveChapterResponse : " + saveChapterResponse);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "saveChapterResponse fail : " + t.getMessage());
                }
            });
        }
    }

    //기존 챕터 수정
    public void resetChapter() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().reset_chapter(userEmail, vocabookTitle, chapterPosition, chapterTitle);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String resetChapterResponse = response.body().string();
                        Log.e(TAG, "resetChapterResponse : " + resetChapterResponse);
                        if (resetChapterResponse.equals("chapter contents deletedchapter title has updated")) {
                            updateChapter();
                        } else {
                            Log.e(TAG, "reset has failed");
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "resetChapterResponse fail : " + t.getMessage());
            }
        });

    }

    public void updateChapter() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        for (int index = 0; index < wordArray.size(); index++) {
            String wordImage = wordArray.get(index).getWordImageURI();
            Log.e(TAG, "Check(updateChapter)...(" + index + ") wordImage : " + wordImage);
        }

        for (int index = 0; index < wordArray.size(); index++) {
            String wordImage = wordArray.get(index).getWordImageURI();
            String wordImageURI = null;
            if (wordImage.isEmpty()) { // 이미지가 비어진 경우
                Log.e(TAG, index + "번 단어 이미지 비어있음");
                file = null;
                wordImage = null;
            } else if (wordImage.startsWith("/storage")) { //새로 추가된 이미지인 경우
                Log.e(TAG, index + "번 단어 이미지 새로 추가");
                file = new File(wordArray.get(index).wordImageURI);
                fileName = userEmail + "_" + vocabookTitle + "_" + editChapterTitle.getText().toString() + "_" + wordArray.get(index).word;
                //한글 파일명을 그대로 사용해서 파일을 업로드하면 오류가 나서 UTF-8 로 인코딩한다.
                fileName = encodeFileName(fileName);
                fileExtension = getExtension(file.getName());
                wordImage = fileName + "." + fileExtension;
                wordImageURI = "http://13.124.111.245/android/word_images/" + wordImage;
                Log.e(TAG, "추가되는 이미지 URI: " + wordImageURI);
            } else if (wordImage.startsWith("http")) { // 기존에 이미지가 있던 단어인 경우
                Log.e(TAG, index + "번 단어 이미지 변경");
                file = new File(wordArray.get(index).wordImageURI);
                wordImage = Methods.getWordFileNameFromUri(wordImage);
                wordImageURI = "http://13.124.111.245/android/word_images/" + wordImage;
                Log.e(TAG, "encoded wordImage Name : " + wordImage);
            }

            Call<ResponseBody> res2 = RetrofitClient.getInstance().getService().edit_chapter(userEmail, vocabookTitle, editChapterTitle.getText().toString(), wordArray.get(index).getWord(), wordArray.get(index).getMeaning1(),
                    wordArray.get(index).getMeaning2(), wordArray.get(index).getMeaning3(), wordArray.get(index).getMeaning4(), wordArray.get(index).getMeaning5(), wordImage, wordImageURI);
            res2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            String saveChapterResponse = response.body().string();
                            Log.e(TAG, "updateChapterResponse : " + saveChapterResponse);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "updateChapterResponse fail : " + t.getMessage());
                }
            });
            if (file != null) {
                RequestBody mFile = RequestBody.create(MediaType.parse("word_images/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", wordImage, mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), wordImage);
                Call<UploadObject> fileUpload = RetrofitClient.getInstance().getService().upload_word_image(fileToUpload, filename);
                fileUpload.enqueue(new Callback<UploadObject>() {
                    @Override
                    public void onResponse(@NonNull Call<UploadObject> call, Response<UploadObject> response) {
                        Log.e(TAG, "image upload result :  " + response.body().getSuccess());
                    }

                    @Override
                    public void onFailure(@NonNull Call<UploadObject> call, Throwable t) {
                        Log.d(TAG, "image upload error " + t.getMessage());
                    }
                });
                file = null;
            }
        }
    }

    //파파고 통신
    public class TranslateTask extends AsyncTask<String, Void, String> {
        String word = editWord.getText().toString();

        @Override
        protected String doInBackground(String... strings) {
            String clientId = "3pGtHgmQnXc5bCkUUEKZ";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "n3Wzpu8Wz7";//애플리케이션 클라이언트 시크릿값";
            StringBuilder response = new StringBuilder();
            try {
                String text = URLEncoder.encode(word, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=en&target=ko&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                Log.e(TAG, response.toString());
            } catch (Exception e) {
                Log.e(TAG, "오류났다 ㅎㅎ 오류내용 = " + e);
            }
            translateResult = response.toString();
            translateResult = translateResult.replaceAll("[.]", "");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(translateResult);
            if (element.getAsJsonObject().get("errorMessage") != null) {
                Log.e("번역 오류", "번역 오류가 발생했습니다. " +
                        "[오류 코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if (element.getAsJsonObject().get("message") != null) {
//                 번역 결과 출력
                Log.e(TAG, "번역결과 = " + translateResult);
                if (meaningArray.size() == 0) {
                    meaningArray.add(new MeaningItem(element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                            .getAsJsonObject().get("translatedText").getAsString()));
                    meaningAdapter.notifyItemInserted(meaningArray.size());
                    ResetMeaningLayoutHeight();
                } else if (meaningArray.get(meaningArray.size() - 1).meaning.equals("")) {
                    meaningArray.set(meaningArray.size() - 1, new MeaningItem(element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                            .getAsJsonObject().get("translatedText").getAsString()));
                    meaningAdapter.notifyDataSetChanged();
                } else {
                    meaningArray.add(new MeaningItem(element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                            .getAsJsonObject().get("translatedText").getAsString()));
                    meaningAdapter.notifyItemInserted(meaningArray.size());
                    ResetMeaningLayoutHeight();
                }
            }
        }
    }



}
