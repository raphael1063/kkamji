package com.wonjin.android.Activities.AccountActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wonjin.android.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordDialog extends AppCompatActivity {


    @BindView(R.id.edit_pw_check_register)
    TextInputEditText editPwCheckRegister;
    @BindView(R.id.pw_check_layout_register)
    TextInputLayout pwCheckLayoutRegister;
    @BindView(R.id.edit_pw_register)
    TextInputEditText editPwRegister;
    @BindView(R.id.pw_layout_register)
    TextInputLayout pwLayoutRegister;

    String TAG = "ChangePasswordDialog";
    boolean isPWOk, isPWCheckOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_password);
        ButterKnife.bind(this);

        PWRegularForm();
    }

    @OnClick(R.id.btn_edit_pw)
    public void onViewClicked() {
        if(isPWOk){
            if(isPWCheckOk){
                Intent intent = new Intent();
                Toast.makeText(this, "패스워드를 변경했습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                String newPW = editPwRegister.getText().toString();
                Log.e(TAG, "새로운 PW : " + newPW);
                intent.putExtra("newPW", newPW);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "패스워드 변경 실패", Toast.LENGTH_SHORT).show();
            finish();
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
