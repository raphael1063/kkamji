package com.wonjin.android.Activities.GameActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wonjin.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameResultActivity extends AppCompatActivity {

String score1, score2, score3, score4, user1, user2, user3, user4;
    int s1, s2, s3, s4;

String TAG = "GameResultActivity";
    @BindView(R.id.result2)
    ImageView result2;
    @BindView(R.id.retry)
    Button retry;
    @BindView(R.id.quit)
    Button quit;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.secondEmoji)
    ImageView secondEmoji;
    @BindView(R.id.thirdEmoji)
    ImageView thirdEmoji;
    @BindView(R.id.fourthEmoji)
    ImageView fourthEmoji;
    @BindView(R.id.firstEmoji)
    ImageView firstEmoji;
    @BindView(R.id.first)
    TextView first;
    @BindView(R.id.second)
    TextView second;
    @BindView(R.id.third)
    TextView third;
    @BindView(R.id.fourth)
    TextView fourth;
    @BindView(R.id.sUser1)
    TextView sUser1;
    @BindView(R.id.sUser2)
    TextView sUser2;
    @BindView(R.id.sUser3)
    TextView sUser3;
    @BindView(R.id.sUser4)
    TextView sUser4;
    @BindView(R.id.result1)
    ImageView result1;
    @BindView(R.id.result3)
    ImageView result3;
    @BindView(R.id.result4)
    ImageView result4;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_result);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        score1 = intent.getStringExtra("score1");
        score2 = intent.getStringExtra("score2");
        score3 = intent.getStringExtra("score3");
        score4 = intent.getStringExtra("score4");
        user1 = intent.getStringExtra("user1");
        user2 = intent.getStringExtra("user2");
        user3 = intent.getStringExtra("user3");
        user4 = intent.getStringExtra("user4");

        sUser1.setText(score1);
        sUser2.setText(score2);
        sUser3.setText(score3);
        sUser4.setText(score4);

        s1 = Integer.parseInt(score1);
        s2 = Integer.parseInt(score2);
        s3 = Integer.parseInt(score3);
        s4 = Integer.parseInt(score4);

        int[] score = {s1, s2, s3, s4};
        String[] user = {user1, user2, user3, user4};
        int[] rank = {1, 1, 1, 1};

        for (int i = 0; i < score.length; i++) {
            rank[i] = 1;
            for (int value : score) {
                if (score[i] < value) {
                    rank[i]++;
                }
            }

        }

        for (int i = 0; i < score.length; i++) {
            Log.e(TAG, user[i] + " / " + score[i] + "점 : " + rank[i] + "등");
            if (i == 0) {
                first.setText(user[i]);
                sUser1.setText(score[i] + " 점");
                if (rank[i] == 1) {
                    result1.setImageResource(R.drawable.first);
                    firstEmoji.setImageResource(R.drawable.first_place);
                } else if (rank[i] == 2) {
                    result1.setImageResource(R.drawable.second);
                    firstEmoji.setImageResource(R.drawable.second_place);
                } else if (rank[i] == 3) {
                    result1.setImageResource(R.drawable.third);
                    firstEmoji.setImageResource(R.drawable.third_place);
                } else if (rank[i] == 4) {
                    result1.setImageResource(R.drawable.fourth);
                    firstEmoji.setImageResource(R.drawable.fourth_place);
                }
            } else if (i == 1) {
                second.setText(user[i]);
                sUser2.setText(score[i] + " 점");
                if (rank[i] == 1) {
                    result2.setImageResource(R.drawable.first);
                    secondEmoji.setImageResource(R.drawable.first_place);
                } else if (rank[i] == 2) {
                    result2.setImageResource(R.drawable.second);
                    secondEmoji.setImageResource(R.drawable.second_place);
                } else if (rank[i] == 3) {
                    result2.setImageResource(R.drawable.third);
                    secondEmoji.setImageResource(R.drawable.third_place);
                } else if (rank[i] == 4) {
                    result2.setImageResource(R.drawable.fourth);
                    secondEmoji.setImageResource(R.drawable.fourth_place);
                }
            } else if (i == 2) {
                third.setText(user[i]);
                sUser3.setText(score[i] + " 점");
                if (rank[i] == 1) {
                    result3.setImageResource(R.drawable.first);
                    thirdEmoji.setImageResource(R.drawable.first_place);
                } else if (rank[i] == 2) {
                    result3.setImageResource(R.drawable.second);
                    thirdEmoji.setImageResource(R.drawable.second_place);
                } else if (rank[i] == 3) {
                    result3.setImageResource(R.drawable.third);
                    thirdEmoji.setImageResource(R.drawable.third_place);
                } else if (rank[i] == 4) {
                    result3.setImageResource(R.drawable.fourth);
                    thirdEmoji.setImageResource(R.drawable.fourth_place);
                }
            } else {
                fourth.setText(user[i]);
                sUser4.setText(score[i] + " 점");
                if (rank[i] == 1) {
                    result4.setImageResource(R.drawable.first);
                    fourthEmoji.setImageResource(R.drawable.first_place);
                } else if (rank[i] == 2) {
                    result4.setImageResource(R.drawable.second);
                    fourthEmoji.setImageResource(R.drawable.second_place);
                } else if (rank[i] == 3) {
                    result4.setImageResource(R.drawable.third);
                    fourthEmoji.setImageResource(R.drawable.third_place);
                } else if (rank[i] == 4) {
                    result4.setImageResource(R.drawable.fourth);
                    fourthEmoji.setImageResource(R.drawable.fourth_place);
                }
            }

        }

    }

    Intent intent = new Intent();

    @OnClick({R.id.retry, R.id.quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.retry:
                intent.putExtra("code", "retry");
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.quit:
                intent.putExtra("code", "quit");
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게하기
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}
