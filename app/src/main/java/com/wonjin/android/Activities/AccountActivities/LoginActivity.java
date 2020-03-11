package com.wonjin.android.Activities.AccountActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wonjin.android.Activities.MainActivity;
import com.wonjin.android.R;
import com.wonjin.android.Retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
    @BindView(R.id.edit_email)
    TextInputEditText editEmail;
    @BindView(R.id.edit_email_layout)
    TextInputLayout editEmailLayout;
    @BindView(R.id.edit_pw)
    TextInputEditText editPw;
    @BindView(R.id.edit_pw_layout)
    TextInputLayout editPwLayout;
    String loginCheck;
    public SharedPreferences settings;
    String loginEmail, loginPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 폰에 저장된 로그인 정보 가져오기
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        loginEmail = settings.getString("loginEmail", "");
        loginPW = settings.getString("loginPW", "");

        //로그아웃으로 인해 로그인 액티비티로 온 경우
        Intent logout = getIntent();
        String code = logout.getStringExtra("CODE");
        Log.e(TAG, "code : " + code);
        if (code != null && code.equals("logout")) {
            settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editEmail.setText(loginEmail);
            editor.putString("loginEmail", "");
            editor.putString("loginPW", "");
            editor.apply();
            editor.commit();
        }

        if (!settings.getString("loginEmail", "").equals("")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @OnClick({R.id.text_find_password, R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_find_password:
                break;
            case R.id.btn_login:
                if (editEmail.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (editPw.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Login();
                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, PolicyActivity.class));
                break;
        }
    }

    public void Login() {
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().login(editEmail.getText().toString(), editPw.getText().toString());
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    loginCheck = response.body().string();
                    Log.e(TAG, "user login : " + loginCheck);

                    if (loginCheck.equals("login success")) {
                        loginEmail = editEmail.getText().toString();
                        loginPW = editPw.getText().toString();
                        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("loginEmail", loginEmail);
                        editor.putString("loginPW", loginPW);
                        editor.apply();
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디 혹은 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, loginCheck);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Login Retrofit Error : " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "user login failed : " + t.getMessage());
            }
        });
    }

    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private long pressedTime = 0;

    // 리스너 생성
    public interface OnBackPressedListener {
        void onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

    // 리스너 설정 메소드
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    @Override
    public void onBackPressed() {

        // 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
        if (mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
        } else {
            Log.e("!!!", "Listener is null");
            if (pressedTime == 0) {
                Snackbar.make(findViewById(R.id.login_layout), " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    Snackbar.make(findViewById(R.id.login_layout),
                            " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show();
                    pressedTime = 0;
                } else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finish, killProcess");
                    finish();
                    Process.killProcess(Process.myPid());
                }
            }
        }
    }
}
