package com.example.android.Activities.AccountActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.GMailSender;
import com.example.android.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BugReportActivity extends AppCompatActivity {

    @BindView(R.id.edit_bug_report)
    EditText editBugReport;
    String TAG = "BugReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);
        ButterKnife.bind(this);

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() .permitDiskReads() .permitDiskWrites() .permitNetwork().build());
    }

    @OnClick(R.id.btn_send_bug_report)
    public void onViewClicked() {
        SendBugReport();
        Toast.makeText(this, "소중한 제보 감사합니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public String getUserEmail() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        return settings.getString("loginEmail", "");
    }

    public void SendBugReport(){

        Intent sendEmail = new Intent(Intent.ACTION_SEND);
        sendEmail.setType("plain/text");
        // email setting 배열로 해놔서 복수 발송 가능
        String[] address = {"raphael1050302@gmail.com"};
        sendEmail.putExtra(Intent.EXTRA_EMAIL, address);
        sendEmail.putExtra(Intent.EXTRA_SUBJECT,"[깜지] 버그제보");
        sendEmail.putExtra(Intent.EXTRA_TEXT, editBugReport.getText().toString());
        startActivity(sendEmail);
    }
}
