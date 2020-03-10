package com.example.android.Dialogs;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.Methods;
import com.example.android.R;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVocabookDialog extends AppCompatActivity {

    @BindView(R.id.image_edit_vocabook_cover)
    ImageView imageEditVocabookCover;
    @BindView(R.id.edit_vocabook_title)
    TextInputEditText editVocabookTitle;
    @BindView(R.id.edit_vocabook_title_layout)
    TextInputLayout editVocabookTitleLayout;
    private static final int REQUEST_CODE_CHOOSE = 23;
    File file;
    boolean isTitleOk;
    String TAG = "AddVocabookDialog";
    String imageUri, mode, vocabookTitle, vocabookCoverImage, vocabookTitleOriginal;
    int position;
    @BindView(R.id.vocabook_tool_bar_title)
    TextView vocabookToolBarTitle;
    @BindView(R.id.btn_add_vocabook)
    Button btnAddVocabook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_vocabook);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        isTitleOk = false;
        vocabookTitleRegularForm();

        if (mode != null && mode.equals("edit")) {
            isTitleOk = true;
            vocabookTitle = intent.getStringExtra("vocabookTitle");
            vocabookTitleOriginal = vocabookTitle;
            vocabookCoverImage = intent.getStringExtra("vocabookImage");
            Log.e(TAG, "vocabookCoverImage : " + vocabookCoverImage);
            String coverImageUri = null;
            if (vocabookCoverImage != null && vocabookCoverImage.startsWith("http")) {
                coverImageUri = Methods.getVocabookImageFromServer(vocabookCoverImage);
            } else {
                coverImageUri = vocabookCoverImage;
            }
            position = intent.getIntExtra("vocabookPosition", -1);
            vocabookToolBarTitle.setText("단어장 수정");
            btnAddVocabook.setText("수정");
            Log.e(TAG, "단어장 이미지 : " + vocabookCoverImage);
            editVocabookTitle.setText(vocabookTitle);
            imageUri = vocabookCoverImage;
            setImage(coverImageUri);
        }

}

@OnClick({R.id.btn_edit_vocabook_cover, R.id.btn_add_vocabook})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.btn_edit_vocabook_cover:
                openGallery();
                break;
            case R.id.btn_add_vocabook:
                if (isTitleOk) {
                    if (mode.equals("add")) {
                        Intent addVocabook = getIntent();
                        if (imageUri != null) {
                            addVocabook.putExtra("title", editVocabookTitle.getText().toString());
                            addVocabook.putExtra("imageUri", imageUri);
                        } else {
                            addVocabook.putExtra("title", editVocabookTitle.getText().toString());
                            addVocabook.putExtra("imageUri", "default_cover_image");
                        }
                        setResult(RESULT_OK, addVocabook);
                        finish();
                    } else if (mode.equals("edit")) {
                        Intent editVocabook = getIntent();
                        if (imageUri != null) {
                            editVocabook.putExtra("title", editVocabookTitle.getText().toString());
                            editVocabook.putExtra("original", vocabookTitleOriginal);
                            editVocabook.putExtra("imageUri", imageUri);
                            editVocabook.putExtra("position", position);
                        } else {
                            editVocabook.putExtra("title", editVocabookTitle.getText().toString());
                            editVocabook.putExtra("original", vocabookTitleOriginal);
                            editVocabook.putExtra("imageUri", "default_cover_image");
                            editVocabook.putExtra("position", position);
                        }
                        setResult(RESULT_OK, editVocabook);
                        finish();
                    }

                } else {
                    Toast.makeText(this, "isTitleOk : false", Toast.LENGTH_SHORT).show();
                }
                if (editVocabookTitle.getText().toString().equals("")) {
                    Toast.makeText(this, "단어장명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void openGallery() {
        Pix.start(this, Options.init().setRequestCode(100));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 100) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            setImage(returnValue.get(0));
            imageUri = returnValue.get(0);
        }
    }

    public void vocabookTitleRegularForm() {
        //이름 정규표현식과 에러메시지
        editVocabookTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력창이 비어있는 경우
                if (Objects.requireNonNull(editVocabookTitle.getText()).toString().equals("")) {
                    isTitleOk = false;
                    editVocabookTitleLayout.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                    Log.e(TAG, "이름 / 비어있음.");

                    //정규표현식에 맞지 않는 경우
                } else if (editVocabookTitle.getText().toString().length() < 1 || editVocabookTitle.getText().toString().length() > 10) {
                    isTitleOk = false;
                    editVocabookTitleLayout.setError("1~10자 이내의 단어장명 사용가능.");
                    Log.e(TAG, "제목 / 정규표현식에 맞지 않음.");

                    //입력창이 비어있지 않고 올바르게 입력된 경우
                } else {
                    isTitleOk = true;
                    editVocabookTitleLayout.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                    Log.e(TAG, "제목 / 이상없음.");
                }
                Log.e(TAG, "isTitleOk = " + (isTitleOk));
            }
        });
    }

    public void setImage(String imageUri) {
        Glide.with(this)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .override(200, 200)
                .into(imageEditVocabookCover);
    }

}
