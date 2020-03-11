package com.wonjin.android.Activities.AccountActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.wonjin.android.Dialogs.EmailAuthDialog;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;
import com.wonjin.android.UploadObject;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
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

import static com.wonjin.android.Methods.getExtension;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.btn_email_auth)
    Button btnEmailAuth;
    @BindView(R.id.edit_name_register)
    TextInputEditText editNameRegister;
    @BindView(R.id.edit_email_register)
    TextInputEditText editEmailRegister;
    @BindView(R.id.edit_pw_check_register)
    TextInputEditText editPwCheckRegister;
    @BindView(R.id.edit_pw_register)
    TextInputEditText editPwRegister;
    @BindView(R.id.edit_nickname_register)
    TextInputEditText editNicknameRegister;
    @BindView(R.id.name_layout_register)
    TextInputLayout nameLayoutRegister;
    @BindView(R.id.email_layout_register)
    TextInputLayout emailLayoutRegister;
    @BindView(R.id.pw_check_layout_register)
    TextInputLayout pwCheckLayoutRegister;
    @BindView(R.id.pw_layout_register)
    TextInputLayout pwLayoutRegister;
    @BindView(R.id.nickname_layout_register)
    TextInputLayout nicknameLayoutRegister;
    @BindView(R.id.profile_image_register)
    ImageView profileImageRegister;

    //입력된 회원정보 유효성 검사 boolean
    boolean isNameOk, isNicknameOk, isEmailOk, isPWOk, isPWCheckOk, nicknameCheck, emailCheck, isComplete;
    String TAG = "RegisterActivity";
    private Uri photoUri;
    private File file;
    String fileName;
    String fileExtension;
    private static final int REQUEST_CODE_CHOOSE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        isNameOk = false;
        isNicknameOk = false;
        isEmailOk = false;
        emailCheck = false;
        NameRegularForm();
        NicknameRegularForm();
        EmailRegularForm();
        PWRegularForm();

    }

    @OnClick({R.id.btn_register_complete, R.id.btn_email_auth, R.id.text_go_back_login, R.id.btn_edit_profile_image_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register_complete:
                if (isNameOk && isEmailOk && isNicknameOk && isPWOk && isPWCheckOk && editPwRegister.getText().toString().equals(editPwCheckRegister.getText().toString())) {
                    SendUserData();
                    finish();
                } else {
                    Toast.makeText(this, "입력하신 회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_email_auth:
                if (isEmailOk) {
                    Intent authDialog = new Intent(this, EmailAuthDialog.class);
                    authDialog.putExtra("email", Objects.requireNonNull(editEmailRegister.getText()).toString());
                    startActivityForResult(authDialog, 2);
                } else {
                    Toast.makeText(this, "이메일을 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.text_go_back_login:
                finish();
                break;
            case R.id.btn_edit_profile_image_register:

                Pix.start(this, Options.init().setRequestCode(100));

                break;
        }
    }

    // 회원가입시 유저가 작성한 데이터 서버로 전송
    private void SendUserData() {

        if (file != null) {
            fileName = file.getName();
            fileExtension = getExtension(fileName);
            Log.e(TAG, "Filename = " + fileName);
            Log.e(TAG, "fileExtension = "+ fileExtension);
            RequestBody mFile = RequestBody.create(MediaType.parse("profile_image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", editEmailRegister.getText().toString() + "." + fileExtension, mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), editEmailRegister.getText().toString() + "." + fileExtension);

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

        Call<ResponseBody> res = RetrofitClient.getInstance().getService().user_Info(editNameRegister.getText().toString(), editNicknameRegister.getText().toString(),
                editEmailRegister.getText().toString(), editPwRegister.getText().toString(), 1, editEmailRegister.getText().toString() + "." + fileExtension);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e(TAG, "user data upload : " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "user data upload failed : " + t.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                String resultMsg = data.getStringExtra("image");
                if (resultMsg != null) {
                    if (resultMsg.equals("null")) {
                        Toast.makeText(this, "이미지 가져오기 실패", Toast.LENGTH_SHORT).show();
                    } else {
                        photoUri = Uri.parse(resultMsg);
                        Glide.with(this).load(resultMsg).centerCrop().circleCrop()
                                .override(200, 200)
                                .into(profileImageRegister);
                        file = new File(resultMsg);
                    }
                }
            } else if (requestCode == 2) {
                String resultMsg = data.getStringExtra("auth");
                if (resultMsg.equals("verified")) {
                    emailCheck = true;
                    Toast.makeText(this, "이메일 인증 성공", Toast.LENGTH_SHORT).show();
                } else if (resultMsg.equals("time out")) {
                    emailCheck = false;
                    Toast.makeText(this, "인증 제한시간 초과", Toast.LENGTH_SHORT).show();
                } else {
                    emailCheck = false;
                    Toast.makeText(this, "이메일 인증 실패", Toast.LENGTH_SHORT).show();
                }
            } else if(requestCode == 100){
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                if (returnValue != null) {
                    Log.e(TAG, "returnValue : " + returnValue.get(0));
                    Glide.with(this).load(returnValue.get(0)).centerCrop().circleCrop()
                            .override(200, 200)
                            .into(profileImageRegister);
                            file = new File(returnValue.get(0));
                }
            }

        } else {
            emailCheck = false;
            Toast.makeText(this, "이메일 인증 실패", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "emailCheck : " + emailCheck);
    }

    public void NameRegularForm() {
        //이름 정규표현식과 에러메시지
        editNameRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력창이 비어있는 경우
                if (Objects.requireNonNull(editNameRegister.getText()).toString().equals("")) {
                    isNameOk = false;
                    nameLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                    Log.e(TAG, "이름 / 비어있음.");

                    //정규표현식에 맞지 않는 경우
                } else if (!Pattern.matches("^[가-힣]{2,10}$", editNameRegister.getText().toString()) && !Pattern.matches("^[a-zA-Z]{2,20}$", editNameRegister.getText().toString())) {
                    isNameOk = false;
                    nameLayoutRegister.setError("올바른 이름형식이 아닙니다");
                    Log.e(TAG, "이름 / 정규표현식에 맞지 않음.");

                    //입력창이 비어있지 않고 올바르게 입력된 경우
                } else {
                    isNameOk = true;
                    nameLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                    Log.e(TAG, "이름 / 이상없음.");
                }
                Log.e(TAG, "isNameOk = " + (isNameOk));
            }
        });
    }

    public void NicknameRegularForm() {
        editNicknameRegister.setOnFocusChangeListener((v, gainFocus) -> {
            //포커스가 주어졌을 때
            if (gainFocus) {
                //닉네임 정규표현식과 에러메시지
                editNicknameRegister.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.e(TAG, "nicknameCheck = " + (nicknameCheck));
                        //정규표현식에 맞지 않는 경우
                        if (!Pattern.matches("^[가-힣0-9]*$", Objects.requireNonNull(editNicknameRegister.getText()).toString())) {
                            isNicknameOk = false;
                            nicknameLayoutRegister.setError("한글 및 숫자만 사용가능");

                            //입력창이 비어있는 경우
                        } else if (editNicknameRegister.getText().toString().equals("")) {
                            isNicknameOk = false;
                            nicknameLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                            //입력창이 비어있지 않고 올바르게 입력된 경우
                        } else {
                            isNicknameOk = true;
                            nicknameLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                        }
                        Log.e(TAG, "isNicknameOk = " + (isNicknameOk));
                    }
                });

            }

            //포커스를 잃었을 때

            else {
                if (editNicknameRegister != null) {
                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().nicknameCheck(editNicknameRegister.getText().toString());
                    res.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.body() != null) {
                                    String nicknameResponse = response.body().string();
                                    Log.e(TAG, "data response : " + nicknameResponse);

                                    nicknameCheck = nicknameResponse.equals("Enable");
                                    Log.e(TAG, "nicknameCheck : " + nicknameCheck);

                                    if (!nicknameCheck) {
                                        nicknameLayoutRegister.setError("이미 사용중인 닉네임");
                                        isNicknameOk = false;
                                    } else {
                                        isNicknameOk = true;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "data fail : " + t.getMessage());
                        }
                    });
                }

            }
            Log.e(TAG, "isNicknameOk : " + isNicknameOk);
        });
    }

    public void EmailRegularForm() {
        editEmailRegister.setOnFocusChangeListener((v, gainFocus) -> {
            //포커스가 주어졌을 때
            if (gainFocus) {
                //이메일 정규표현식과 에러메시지
                editEmailRegister.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        Log.e(TAG, "emailCheck = " + (emailCheck));
                        //정규표현식에 맞지 않는 경우
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmailRegister.getText().toString()).matches()) {
                            isEmailOk = false;
                            emailLayoutRegister.setError("올바른 이메일 형식이 아닙니다");
                            //입력창이 비어있는 경우
                        } else if (editEmailRegister.getText().toString().equals("")) {
                            isEmailOk = false;
                            emailLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                            //입력창이 비어있지 않고 올바르게 입력된 경우
                        } else {
                            CheckEmail();
                            emailLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.

                        }
                        Log.e(TAG, "isEmailOk = " + (isEmailOk));
                    }
                });
            }

            //포커스를 잃었을 때

            else {
                CheckEmail();
            }
            Log.e(TAG, "isEmailOk : " + isEmailOk);
        });
    }

    public void CheckEmail() {
        if (editEmailRegister != null) {
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().emailCheck(editEmailRegister.getText().toString());
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            String emailResponse = response.body().string();
                            Log.e(TAG, "email check response : " + emailResponse);

                            isEmailOk = emailResponse.equals("Enable");
                            Log.e(TAG, "emailCheck : " + emailCheck);
                            Log.e(TAG, "isEmailOk : " + isEmailOk);
                            if (!isEmailOk) {
                                emailLayoutRegister.setError("이미 사용중인 이메일");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "data fail : " + t.getMessage());
                }
            });
        }
    }

    public void PWRegularForm() {

        editPwRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pw = editPwRegister.getText().toString();
                Log.e(TAG, "pw = " + "'" + pw + "'");

                //정규표현식에 맞지 않는 경우
                if (pw.equals("")) {
                    isPWOk = false;
                    pwLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.

                    //입력창이 비어있는 경우
                } else if (!Pattern.matches("^[a-zA-Z0-9!@.#$%^&*?_~]{8,20}$", editPwRegister.getText().toString())) {
                    pwLayoutRegister.setError("8~20자의 패스워드를 사용할 수 있습니다.");

                    //입력창이 비어있지 않고 올바르게 입력된 경우
                } else {
                    isPWOk = true;
                    pwLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                    PWCHeck();
                }
                Log.e(TAG, "isPWOk = " + (isPWOk));
            }
        });

}

    public void PWCHeck() {
        editPwCheckRegister.setOnFocusChangeListener((v, gainFocus) -> {
            //포커스가 주어졌을 때
            if (gainFocus) {
                //패스워드 입력창에 현재 입력된 텍스트를 담는다.
                String pw = editPwRegister.getText().toString();
                //패스워드가 일치하지 않는 경우
                if (!editPwCheckRegister.getText().toString().equals(pw)) {
                    pwCheckLayoutRegister.setError("패스워드가 일치하지 않습니다.");
                    //입력창이 비어있는 경우
                }
                //패스워드 확인 정규표현식과 에러메시지
                editPwCheckRegister.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        //패스워드가 일치하지 않는 경우
                        if (!editPwCheckRegister.getText().toString().equals(pw)) {
                            pwCheckLayoutRegister.setError("패스워드가 일치하지 않습니다.");
                            //입력창이 비어있는 경우
                        } else if (editPwCheckRegister.getText().toString().equals("")) {
                            isPWCheckOk = false;
                            pwCheckLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                            //입력창이 비어있지 않고 올바르게 입력된 경우
                        } else {
                            isPWCheckOk = true;
                            pwCheckLayoutRegister.setError(null); // 문제가 되는 문자가 지워지면 에러메시지도 없앤다.
                        }
                        Log.e(TAG, "isPWCheckOk = " + (isPWCheckOk) + " // pw = " + pw);
                    }
                });
            }
        });

    }

}
