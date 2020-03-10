package com.example.android.Activities.AccountActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;
import com.example.android.UploadObject;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.internal.$Gson$Preconditions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

import static com.example.android.Methods.getExtension;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.my_page_profile_image)
    ImageView myPageProfileImage;
    @BindView(R.id.my_page_nickname)
    TextView myPageNickname;
    @BindView(R.id.my_page_name)
    TextView myPageName;
    @BindView(R.id.my_page_email)
    TextView myPageEmail;
    @BindView(R.id.my_page_pw)
    TextView myPagePw;
    public SharedPreferences settings;
    String TAG = "AccountActivity";
    String name, nickname, email, profileImageUrl;
    boolean isPwOK = false;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_user_Info(userEmail);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    assert response.body() != null;
                    String userInfo = response.body().string();
                    Log.e(TAG, "userInfo : " + userInfo);
                    jsonToArrayListUserInfo(userInfo);
                    Log.e(TAG, "userInfo // name : " + name + " / nickname : " + nickname + " / email : " + email + " / profileImageUrl : " + profileImageUrl);

                    setUserInfo(name, nickname, email, profileImageUrl);
                } catch (IOException e) {
                    Log.e(TAG, "Retrofit Error : " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });

    }

    @OnClick({R.id.btn_withdrawal, R.id.btn_edit_profile_image, R.id.btn_edit_name, R.id.btn_edit_pw, R.id.btn_edit_nickname})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_withdrawal:
                checkPasswordDialog("withdrawal");
                break;
            case R.id.btn_edit_profile_image:
                editProfileImage("editProfileImage");
                break;
            case R.id.btn_edit_name:
                editNameDialog("editName");
                break;
            case R.id.btn_edit_pw:
                checkPasswordDialog("editPassword");
                break;
            case R.id.btn_edit_nickname:
                editNicknameDialog("editNickname");
                break;
        }
    }

    public void setUserInfo(String name, String nickname, String email, String profileImageUrl) {
        Glide.with(getApplicationContext()).load(profileImageUrl).centerCrop().circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(200, 200)
                .into(myPageProfileImage);
        myPageEmail.setText(email);
        myPageName.setText(name);
        myPageNickname.setText(nickname);
    }

    public void checkPasswordDialog(String code) {
        AlertDialog.Builder ad = new AlertDialog.Builder(AccountActivity.this);
        switch (code) {
            case "withdrawal":
                ad.setTitle("회원 탈퇴");       // 제목 설정.
                break;
            case "editPassword":
                ad.setTitle("패스워드 변경");       // 제목 설정
                break;
        }
        ad.setMessage("패스워드를 입력해주세요");   // 내용 설정
        // EditText 삽입하기
        final EditText et = new EditText(AccountActivity.this);
        et.setInputType(0x00000081); // 패스워드
        ad.setView(et);
        // 확인 버튼 설정
        ad.setPositiveButton("확인", (dialog, which) -> {
            // Text 값 받아서 로그 남기기
            String value = et.getText().toString();
            checkPassword(value, code);

            dialog.dismiss();//닫기
            // Event
        });
        // 취소 버튼 설정
        ad.setNegativeButton("취소", (dialog, which) -> {
            dialog.dismiss();   //닫기
        });
        // 창 띄우기
        ad.show();

    }

    public void withdrawalDialog(String code) {
        AlertDialog.Builder ad = new AlertDialog.Builder(AccountActivity.this);
        ad.setTitle("회원 탈퇴");       // 제목 설정
        ad.setMessage("정말로 탈퇴하시겠습니까?\n (저장된 모든 정보가 삭제됩니다.)");   // 내용 설정
        // EditText 삽입하기
        // 확인 버튼 설정
        ad.setPositiveButton("탈퇴하기", (dialog, which) -> {
            setServerDate(email, null, code);
            dialog.dismiss();//닫기
        });
        // 취소 버튼 설정
        ad.setNegativeButton("취소", (dialog, which) -> {
            dialog.dismiss();   //닫기
        });
        // 창 띄우기
        ad.show();
    }

    public void editNameDialog(String code) {
        AlertDialog.Builder ad = new AlertDialog.Builder(AccountActivity.this);
        ad.setTitle("이름 변경");       // 제목 설정
        ad.setMessage("변경할 이름을 입력해주세요");   // 내용 설정
        // EditText 삽입하기
        final EditText et = new EditText(AccountActivity.this);
        ad.setView(et);
        // 확인 버튼 설정
        ad.setPositiveButton("확인", (dialog, which) -> {
            // Text 값 받아서 로그 남기기
            String value = et.getText().toString();
            myPageName.setText(value);
            setServerDate(email, value, code);
            dialog.dismiss();//닫기
            // Event
        });
        // 취소 버튼 설정
        ad.setNegativeButton("취소", (dialog, which) -> {
            dialog.dismiss();   //닫기
        });
        // 창 띄우기
        ad.show();
    }

    public void editNicknameDialog(String code) {
        AlertDialog.Builder ad = new AlertDialog.Builder(AccountActivity.this);
        ad.setTitle("닉네임 변경");       // 제목 설정
        ad.setMessage("변경할 닉네임을 입력해주세요");   // 내용 설정
        // EditText 삽입하기
        final EditText et = new EditText(AccountActivity.this);
        ad.setView(et);
        // 확인 버튼 설정
        ad.setPositiveButton("확인", (dialog, which) -> {
            // Text 값 받아서 로그 남기기
            String value = et.getText().toString();
            myPageNickname.setText(value);
            setServerDate(email, value, code);
            dialog.dismiss();//닫기
            // Event
        });
        // 취소 버튼 설정
        ad.setNegativeButton("취소", (dialog, which) -> {
            dialog.dismiss();   //닫기
        });
        // 창 띄우기
        ad.show();
    }


    public void editProfileImage(String code) {
        Pix.start(this, Options.init().setRequestCode(100));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                if (returnValue != null) {
                    Log.e(TAG, "returnValue : " + returnValue.get(0));
                    Glide.with(this).load(returnValue.get(0)).centerCrop().circleCrop()
                            .override(200, 200)
                            .into(myPageProfileImage);
                    file = new File(returnValue.get(0));
                    uploadProfileImage(file, email);
                }
            } else if (requestCode == 20) {
                String newPW = data.getStringExtra("newPW");
                Log.e(TAG, "다이얼로그로부터 받은 새 패스워드 : " + newPW);
                setServerDate(email, newPW, "editPassword");

            } else {
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextStep(String code) {
        Log.e(TAG, "패스워드 일치");
        switch (code) {
            case "withdrawal":
                withdrawalDialog(code);
                break;
            case "editPassword":
                Intent intent = new Intent(this, ChangePasswordDialog.class);
                intent.putExtra("email", email);
                startActivityForResult(intent, 20);
                break;
        }
        Toast.makeText(this, "Code : " + code, Toast.LENGTH_SHORT).show();
    }

    public void checkPassword(String pw, String code) {

        Call<ResponseBody> res = RetrofitClient.getInstance().getService().login(myPageEmail.getText().toString(), pw);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    assert response.body() != null;
                    String result = response.body().string();
                    Log.e(TAG, "result : " + result);

                    if (result.equals("login success")) {
                        isPwOK = true;
                        nextStep(code);
                    } else {
                        Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        isPwOK = false;
                        Log.e(TAG, result);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "result Retrofit Error : " + e);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "user login failed : " + t.getMessage());
            }
        });
        Log.e(TAG, "isPwOK : " + isPwOK);
    }

    private void jsonToArrayListUserInfo(String userInfoJson) {

        String TAG_JSON = "webnautes";
        String TAG_NAME = "user_name";
        String TAG_NICKNAME = "user_nickname";
        String TAG_EMAIL = "user_email";
        String TAG_PROFILE_IMAGE_URI = "user_profile_image";

        try {
            JSONObject jsonObject = new JSONObject(userInfoJson);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                name = item.getString(TAG_NAME);
                nickname = item.getString(TAG_NICKNAME);
                email = item.getString(TAG_EMAIL);
                profileImageUrl = item.getString(TAG_PROFILE_IMAGE_URI);
            }

        } catch (JSONException e) {
            Log.e(TAG, "showResult : ", e);
        }
    }

    public void setServerDate(String userEmail, String value, String code){
        Call<ResponseBody> res = null;
        switch (code){
            case "editName" :
               res = RetrofitClient.getInstance().getService().edit_name(value, userEmail);
                break;
            case "editNickname" :
                res = RetrofitClient.getInstance().getService().edit_nickname(value, userEmail);
                break;
            case "editPassword" :
                res = RetrofitClient.getInstance().getService().edit_password(value, userEmail);
                break;



            case "withdrawal" :
                res = RetrofitClient.getInstance().getService().withdrawal(userEmail);
                break;
        }

        assert res != null;
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    assert response.body() != null;
                    String result = response.body().string();
                    Log.e(TAG, "result : " + result);
                    if(result.equals("delete words success")){
                        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                        logout.putExtra("CODE", "withdrawal");
                        startActivity(logout);

                        finish();
                    } else if(result.equals("user_password has updated")){
                        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                        logout.putExtra("CODE", "logout");
                        startActivity(logout);
                        finish();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Retrofit Error : " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });
    }

    public void uploadProfileImage(File file, String email){
        String fileName;
        if (file != null) {
            fileName = file.getName();
            String fileExtension = getExtension(fileName);
            Log.e(TAG, "Filename = " + fileName);
            Log.e(TAG, "fileExtension = "+ fileExtension);
            RequestBody mFile = RequestBody.create(MediaType.parse("profile_image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", email + "." + fileExtension, mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), email + "." + fileExtension);

            Call<UploadObject> fileUpload = RetrofitClient.getInstance().getService().uploadFile(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
                    Log.e(TAG, "image upload result :  " + response.body().getSuccess());
                }

                @Override
                public void onFailure(Call<UploadObject> call, Throwable t) {
                    Log.d(TAG, "image upload error " + t.getMessage());
                }
            });
        } else {
            fileName = "default_profile_image";
        }
    }
}
