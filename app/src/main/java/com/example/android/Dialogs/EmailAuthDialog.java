package com.example.android.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.GMailSender;
import com.example.android.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailAuthDialog extends AppCompatActivity {

    String TAG = "EmailAuthDialog";
    String email;
    @BindView(R.id.editAuthText)
    EditText editAuthText;
    @BindView(R.id.auth_timer)
    TextView authTimer;
    String authCode;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_email_auth);
        ButterKnife.bind(this);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() .permitDiskReads() .permitDiskWrites() .permitNetwork().build());

        new AuthTimer(600000, 1).start();
        Intent getEmail = getIntent();
        email = getEmail.getStringExtra("email");
        Log.e(TAG, "email : " + email);
        SendAuthCode(email);
        Toast.makeText(getApplicationContext(), "인증코드가 발송되었습니다. 네트워크 상황에 따라 3~5분정도 소요될 수 있습니다.", Toast.LENGTH_LONG).show();

    }

    @OnClick(R.id.btnAuthFinish)
    public void onViewClicked() {

        if(editAuthText.getText().toString().equals(authCode)){
            Intent auth = new Intent();
            auth.putExtra("auth", "verified");
            setResult(RESULT_OK, auth);
            finish();
        } else {
            Toast.makeText(this, "인증코드가 일치하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    class AuthTimer extends CountDownTimer {
        AuthTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long sec = (millisUntilFinished / 1000) % 60;
            long min = (millisUntilFinished / 1000) / 60;
            String result = String.format("%d : %02d ", min, sec);
            authTimer.setText(result);
        }

        @Override
        public void onFinish() {
            Intent auth = new Intent();
            auth.putExtra("auth", "time out");
            setResult(RESULT_OK, auth);
            finish();
        }
    }

    public void SendAuthCode(String email){

        try {
            GMailSender gMailSender = new GMailSender("raphael1050302@gmail.com", "awdrg1063!");
            authCode = gMailSender.getEmailCode();
            //GMailSender.sendMail(본문내용, 받는사람);
            gMailSender.sendEmail("인증코드 : " + authCode, email);
            Log.e(TAG, "이메일 인증 코드 : " + authCode);
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            Log.e(TAG , "SendFailedException : " + e);
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), "에러 : " + e, Toast.LENGTH_SHORT).show();
            Log.e(TAG , "MessagingException : " + e);
        } catch (Exception e) {
            Log.e(TAG , "Exception : " + e);
        }
    }
}
