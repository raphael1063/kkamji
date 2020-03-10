package com.example.android.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestTimePickerDialog extends AppCompatActivity {

    @BindView(R.id.text_num_of_words)
    TextView textNumOfWords;
    @BindView(R.id.edit_test_time)
    EditText editTestTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_test_time_picker);
        ButterKnife.bind(this);
        int num = getIntent().getIntExtra("totalNum", -1);
        textNumOfWords.setText(num + " 개");
    }

    @OnClick(R.id.btn_time_setting_finish)
    public void onViewClicked() {
        if(editTestTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "테스트 시간을 설정해주세요.", Toast.LENGTH_SHORT).show();
        } else if(editTestTime.getText().toString().equals("0")){
            Toast.makeText(this, "1분 이상의 시간을 설정해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("testTime", Integer.parseInt(editTestTime.getText().toString()));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
