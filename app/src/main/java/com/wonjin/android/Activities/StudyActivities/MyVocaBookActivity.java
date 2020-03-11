package com.wonjin.android.Activities.StudyActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.wonjin.android.Adapters.VocabookAdapter;
import com.wonjin.android.Dialogs.AddVocabookDialog;
import com.wonjin.android.ItemClickSupport;
import com.wonjin.android.LinearLayoutManagerWrapper;
import com.wonjin.android.VocabookItemMoveCallback;
import com.wonjin.android.Items.VocabookItem;
import com.wonjin.android.Methods;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;
import com.wonjin.android.StartDragListener;
import com.wonjin.android.UploadObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.wonjin.android.Methods.encodeFileName;
import static com.wonjin.android.Methods.getExtension;

public class MyVocaBookActivity extends AppCompatActivity implements StartDragListener {

    @BindView(R.id.vocabook_toolbar)
    Toolbar vocabookToolbar;
    @BindView(R.id.recyclerview_vocabook_list)
    RecyclerView recyclerviewVocabookList;
    VocabookAdapter vocabookAdapter;

    String TAG = "MyVocaBookActivity";
    ArrayList<VocabookItem> vocabookArray = new ArrayList<>();
    JsonArray jArray = new JsonArray();
    File file = null;
    String fileName;
    String fileExtension;
    String numSelected;
    SharedPreferences settings;
    @BindView(R.id.delete_layout)
    ConstraintLayout deleteLayout;
    public static boolean isDeleteMode, isEditMode;
    @BindView(R.id.btn_add_vocabook)
    FloatingActionButton fabAddVocabook;
    @BindView(R.id.text_num_delete_selected)
    TextView textNumDeleteSelected;
    ArrayList<Integer> deleteSelectedArray = new ArrayList<>();
    ItemTouchHelper touchHelper;
    @BindView(R.id.text_notice)
    TextView textNotice;
    Animation fabAnim;
    String original_image_route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vocabook);
        ButterKnife.bind(this);

        deleteLayout.setVisibility(View.GONE);
        isDeleteMode = false;
        textNotice.setVisibility(View.GONE);
        isEditMode = false;
        setRecyclerviewVocabookList();
        itemClickListener();
        setToolbarGoBack(vocabookToolbar);
    }

    public void setToolbarGoBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp); //뒤로가기 버튼 이미지 지정
    }

    public void setRecyclerviewVocabookList() {
        //리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerviewVocabookList.setHasFixedSize(true);
        recyclerviewVocabookList.setLayoutManager(new LinearLayoutManagerWrapper(this));
        recyclerviewVocabookList.addItemDecoration(new DividerItemDecoration(recyclerviewVocabookList.getContext(), new LinearLayoutManagerWrapper(this).getOrientation()));
        getVocabookList();
        Log.e(TAG, "vocabookArray.size : " + vocabookArray.size());
        vocabookAdapter = new VocabookAdapter(this, vocabookArray, this);
        touchHelper = new ItemTouchHelper(new VocabookItemMoveCallback(vocabookAdapter));
        touchHelper.attachToRecyclerView(recyclerviewVocabookList);
        recyclerviewVocabookList.setAdapter(vocabookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_vocabook, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String title = data.getStringExtra("title");
                String imageUri = data.getStringExtra("imageUri");
                file = new File(imageUri);
                Log.e(TAG, "다이얼로그로부터 받아온 imageUri : " + imageUri);
                vocabookArray.add(new VocabookItem(imageUri, vocabookArray.size(), title, 0, false));
                saveVocabook();
                vocabookAdapter.notifyItemInserted(vocabookArray.size());
            } else if (requestCode == 2) {
                String title = data.getStringExtra("title");
                String original = data.getStringExtra("original");
                String imageUri = data.getStringExtra("imageUri");
                int editPosition = data.getIntExtra("position", -1);
                File file = new File(imageUri);
                Log.e(TAG, "다이얼로그로부터 받아온 imageUri : " + imageUri);
                vocabookArray.set(editPosition, new VocabookItem(imageUri, vocabookArray.size(), title, vocabookArray.get(editPosition).getNumOfWords(), false));
                editVocabook(editPosition, original, imageUri, file);
                Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
                vocabookAdapter.notifyItemChanged(editPosition);
            } else if (requestCode == 100) {
                getNumOfWords();
            }
        }

    }

    public void saveVocabook() {

        int vocabookNum = vocabookArray.size() - 1;
        String vocabookTitle = vocabookArray.get(vocabookArray.size() - 1).getVocabookTitle();

        String vocabookCoverImage = "none";
        Log.e(TAG, "vocabookCoverImage : " + vocabookCoverImage);

        if (!file.getName().equals("default_cover_image")) {
            settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
            String userEmail = settings.getString("loginEmail", "");
            fileName = userEmail + "_" + vocabookTitle;
            //한글 파일명을 그대로 사용해서 파일을 업로드하면 오류가 나서 UTF-8 로 인코딩한다.
            fileName = encodeFileName(fileName);
            fileExtension = getExtension(file.getName());
            vocabookCoverImage = fileName + "." + fileExtension;
            RequestBody mFile = RequestBody.create(MediaType.parse("vocabook_cover_images/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", vocabookCoverImage, mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), vocabookCoverImage);
            Log.e(TAG, "fileName : " + filename);
            Call<UploadObject> fileUpload = RetrofitClient.getInstance().getService().upload_vocabook_cover_image(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(@NonNull Call<UploadObject> call, Response<UploadObject> response) {
                    Log.e(TAG, "image upload result(new) :  " + response.body().getSuccess());
                }

                @Override
                public void onFailure(@NonNull Call<UploadObject> call, Throwable t) {
                    Log.d(TAG, "image upload error(new) " + t.getMessage());
                }
            });
            file = null;
        }

        if (vocabookArray.size() != 0) {
            settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
            String userEmail = settings.getString("loginEmail", "");
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().save_vocabook(userEmail, vocabookNum, vocabookTitle, vocabookCoverImage);
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            String saveVocabookResponse = response.body().string();
                            Log.e(TAG, "saveVocabookResponse : " + saveVocabookResponse);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "saveVocabookResponse fail : " + t.getMessage());
                }
            });
        }

    }

    public void deleteVocabook() {

        //2차 확인 AlertDialog
        AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
        builderDelete.setTitle("단어장 삭제");
        builderDelete.setMessage("정말로 단어장을 삭제하시겠습니까?");
        builderDelete.setNegativeButton("취소", (dialogInterface, i) -> Toast.makeText(this, "단어장삭제 취소", Toast.LENGTH_SHORT).show());

        //삭제 버튼 클릭 시
        builderDelete.setPositiveButton("삭제", (dialogInterface, i) -> {
            Toast.makeText(this, "단어장삭제", Toast.LENGTH_SHORT).show();
            Collections.sort(deleteSelectedArray, new AscendingInteger());
            Log.e(TAG, "deleteArray size : " + deleteSelectedArray.size());
            for (int idx = 0; idx < deleteSelectedArray.size(); idx++) {
                Log.e(TAG, "정렬 후 : " + deleteSelectedArray.get(idx) + " ");
                int index = deleteSelectedArray.get(idx);
                settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
                String userEmail = settings.getString("loginEmail", "");
                String encoded = Methods.getFileNameFromUri(vocabookArray.get(index).getVocabookCoverImage());
                Log.e(TAG, "encoded : " + encoded);
                Call<ResponseBody> res = RetrofitClient.getInstance().getService().delete_vocabook(userEmail, vocabookArray.get(index).getVocabookTitle(), encoded);
                res.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.body() != null) {
                                String deleteVocabookResponse = response.body().string();
                                Log.e(TAG, "deleteVocabookResponse : " + deleteVocabookResponse);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "deleteVocabookResponse fail : " + t.getMessage());
                    }
                });

                vocabookArray.remove(index);
                vocabookAdapter.notifyItemRemoved(index);
            }
            deleteSelectedArray.clear();
            numSelected = "(0)";
            textNumDeleteSelected.setText(numSelected);

        });
        builderDelete.show();

    }

    public void editVocabook(int editPosition, String original, String imageUri, File file) {
        Log.e(TAG, "editPosition : " + editPosition);

        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Log.e(TAG, "userEmail : " + userEmail);
        if(!original_image_route.equals("default_cover_image")){
            original_image_route = Methods.getVocabookImageFromServer(original_image_route);
        }

        Log.e(TAG, "original_image_route : " + original_image_route);
        String vocabookCoverImage = "none";

        if (imageUri.startsWith("http")) {
            Log.e(TAG, "이미지 변화없음");
            vocabookCoverImage = "no change";
        } else if (imageUri.startsWith("/storage")) {
            Log.e(TAG, "newImage : " + imageUri);
            fileName = userEmail + "_" + vocabookArray.get(editPosition).getVocabookTitle();
            //한글 파일명을 그대로 사용해서 파일을 업로드하면 오류가 나서 UTF-8 로 인코딩한다.
            fileName = encodeFileName(fileName);
            fileExtension = getExtension(file.getName());
            vocabookCoverImage = fileName + "." + fileExtension;
        }

        original_image_route = original_image_route.replaceFirst("http://13.124.111.245/android/vocabook/vocabook_cover_images/", "");

        if (vocabookArray.size() != 0) {
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().edit_vocabook(userEmail, editPosition, original, vocabookArray.get(editPosition).getVocabookTitle(), original_image_route, vocabookCoverImage);
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            String editVocabookResponse = response.body().string();
                            Log.e(TAG, "editVocabookResponse : " + editVocabookResponse);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "saveVocabookResponse fail : " + t.getMessage());
                }
            });
        }

        if (imageUri.startsWith("/storage")) {

            RequestBody mFile = RequestBody.create(MediaType.parse("vocabook_cover_image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", vocabookCoverImage, mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), vocabookCoverImage);

            Call<UploadObject> fileUpload = RetrofitClient.getInstance().getService().upload_vocabook_cover_image(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(@NonNull Call<UploadObject> call, Response<UploadObject> response) {
                    Log.e(TAG, "image upload result(edit) :  " + response.body().getSuccess());
                }

                @Override
                public void onFailure(@NonNull Call<UploadObject> call, Throwable t) {
                    Log.d(TAG, "image upload error(edit) " + t.getMessage());
                }
            });
            file = null;
        }

    }

    public void getVocabookList() {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");

        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_vocabook_list(userEmail);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String vocabookJson = response.body().string();
                    Log.e(TAG, "vocabookJson : " + vocabookJson);
                    jsonToArrayListVocabook(vocabookJson);
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

    private void jsonToArrayListVocabook(String vocabookJson) {

        String TAG_JSON = "webnautes";
        String TAG_VOCABOOK_NUM = "vocabook_num";
        String TAG_VOCABOOK_TITLE = "vocabook_title";
        String TAG_VOCABOOK_COVER_IMAGE = "vocabook_cover_image";
        Log.e(TAG, "vocabookJson : " + vocabookJson);
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
                    vocabookAdapter.notifyItemInserted(vocabookArray.size());

                }
                vocabookArray.sort(Comparator.naturalOrder());
                for (int index = 0; index < vocabookArray.size(); index++) {
                    Log.e(TAG, "vocabookTitle.numOfWords (" + index + ") : " + vocabookArray.get(index).getNumOfWords());
                    Log.e(TAG, "vocabookImageUri.index (" + index + ") : " + vocabookArray.get(index).getVocabookCoverImage());
                }
                getNumOfWords();
            } catch (JSONException e) {
                Log.e(TAG, "showError : ", e);
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getNumOfWords();
    }

    public void getNumOfWords() {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        for (int index = 0; index < vocabookArray.size(); index++) {
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_num_of_word_vocabook(userEmail, vocabookArray.get(index).getVocabookTitle());
            int finalIndex = index;
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        int numOfWords = Integer.parseInt(response.body().string());
                        Log.e(TAG, "Title : " + vocabookArray.get(finalIndex).getVocabookTitle() + " // numOfWords : " + numOfWords);
                        vocabookArray.get(finalIndex).setNumOfWords(numOfWords);
                        vocabookAdapter.notifyItemChanged(finalIndex);
                        vocabookAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.btn_add_vocabook, R.id.delete_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_vocabook:
                Intent add = new Intent(this, AddVocabookDialog.class);
                add.putExtra("mode", "add");
                startActivityForResult(add, 1);
                break;
            case R.id.delete_layout:
                deleteVocabook();
                break;
        }
    }

    public void itemClickListener() {

        ItemClickSupport.addTo(recyclerviewVocabookList).setOnItemClickListener((recyclerView, position, v) -> {

            if (isDeleteMode) {
                if (deleteSelectedArray.indexOf(position) == -1) {
                    deleteSelectedArray.add(position);
                    vocabookArray.get(position).setSelected(true);
                    vocabookAdapter.notifyItemChanged(position);
                } else {
                    Log.e(TAG, "deleteSelectedArray.indexOf(position) : " + deleteSelectedArray.indexOf(position));
                    deleteSelectedArray.remove((Integer) position);
                    vocabookArray.get(position).setSelected(false);
                    vocabookAdapter.notifyItemChanged(position);
                }

            } else if (isEditMode) {
                Intent edit = new Intent(this, AddVocabookDialog.class);
                edit.putExtra("mode", "edit");
                edit.putExtra("vocabookTitle", vocabookArray.get(position).getVocabookTitle());
                edit.putExtra("vocabookImage", vocabookArray.get(position).getVocabookCoverImage());
                original_image_route = vocabookArray.get(position).getVocabookCoverImage();
                Log.e(TAG, "original_image_route : " + original_image_route);
                edit.putExtra("vocabookPosition", position);
                startActivityForResult(edit, 2);
            } else {
                Intent openChapter = new Intent(this, ChapterActivity.class);
                openChapter.putExtra("vocabookTitle", vocabookArray.get(position).getVocabookTitle());
                startActivityForResult(openChapter, 100);
                overridePendingTransition(R.anim.slide_in_right, R.anim.not_move);
            }
            numSelected = "(" + deleteSelectedArray.size() + ")";
            textNumDeleteSelected.setText(numSelected);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e(TAG, "뒤로가기 버튼 클릭됨");
                finish();
                break;

            case R.id.action_search:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getApplicationContext(), "검색 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Log.e(TAG, "검색 버튼 클릭됨");
                break;

            case R.id.action_edit_vocabook:
                editMode("editMode");
                Log.e(TAG, "isDeleteMode : " + isDeleteMode + " // isEditMode : " + isEditMode);
                break;

            case R.id.action_delete_vocabook:
                editMode("deleteMode");
                Log.e(TAG, "isDeleteMode : " + isDeleteMode + " // isEditMode : " + isEditMode);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Log.e(TAG, "나머지 버튼 클릭됨");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    @Override
    public void onBackPressed() {
        if (isDeleteMode || isEditMode) {
            for (int index = 0; index < vocabookArray.size(); index++) {
                vocabookArray.get(index).setSelected(false);
            }

            editMode("normalMode");
            Log.e(TAG, "isDeleteMode : " + isDeleteMode + " // isEditMode : " + isEditMode);
        } else {
            finish();
        }

    }

    public void editMode(String Edit) {
        switch (Edit) {
            case "deleteMode":  //DeleteMode 일 때
                isEditMode = false;
                if (!isDeleteMode) {
                    //삭제버튼 생성
                    Animation layoutAnim = AnimationUtils.loadAnimation(MyVocaBookActivity.this, R.anim.scale_up);
                    deleteLayout.startAnimation(layoutAnim);
                    deleteLayout.setVisibility(View.VISIBLE);
                    isDeleteMode = true;
                    //수정알림 생성
                    textNotice.setVisibility(View.VISIBLE);
                    textNotice.setText("삭제할 단어장을 선택해주세요.");
                }
                //추가버튼 제거
                fabAnim = AnimationUtils.loadAnimation(MyVocaBookActivity.this, R.anim.scale_down);
                fabAddVocabook.startAnimation(fabAnim);
                fabAddVocabook.hide();

                break;

            case "editMode":  // EditMode 일 때
                isDeleteMode = false;

                //추가버튼 제거
                fabAnim = AnimationUtils.loadAnimation(MyVocaBookActivity.this, R.anim.scale_down);
                fabAddVocabook.startAnimation(fabAnim);
                fabAddVocabook.hide();

                if (!isEditMode) {
                    //삭제버튼 제거
                    Animation layoutAnim = AnimationUtils.loadAnimation(MyVocaBookActivity.this, R.anim.scale_down);
                    deleteLayout.startAnimation(layoutAnim);
                    deleteLayout.setVisibility(View.GONE);
                    //수정알림 생성
                    textNotice.setVisibility(View.VISIBLE);
                    textNotice.setText("수정할 단어장을 선택해주세요.");
                    isEditMode = true;
                }

                break;

            case "normalMode": //NormalMode 일 때
                if (isDeleteMode) {
                    //삭제버튼 제거
                    Animation layoutAnim = AnimationUtils.loadAnimation(MyVocaBookActivity.this, R.anim.scale_down);
                    deleteLayout.startAnimation(layoutAnim);
                    deleteLayout.setVisibility(View.GONE);
                }

                //수정알림 제거
                textNotice.setVisibility(View.GONE);
                //추가버튼 생성
                Animation myAnim = AnimationUtils.loadAnimation(MyVocaBookActivity.this, R.anim.scale_up);
                fabAddVocabook.startAnimation(myAnim);
                fabAddVocabook.show();

                isDeleteMode = false;
                isEditMode = false;
                break;

        }

    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
        for (int idx = 0; idx < vocabookArray.size(); idx++) {
            vocabookArray.get(idx).setVocabookNum(idx);
            Log.e(TAG, "vocabookTitle : " + vocabookArray.get(idx).getVocabookTitle() + " / position : " + vocabookArray.get(idx).getVocabookNum());
        }
    }

    //int 값을 내림차순으로 정렬해주는 클래스
    class AscendingInteger implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return b.compareTo(a);
        }
    }

}
