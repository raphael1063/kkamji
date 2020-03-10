package com.example.android.Activities.GameActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.android.Items.PlayerInfoItem;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.btnRESET)
    ImageButton btnReset;
    @BindView(R.id.editChat)
    EditText editChat;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.chatting)
    TextView chatting;
    @BindView(R.id.sv)
    ScrollView sv;
    @BindView(R.id.userImage1)
    ImageView userImage1;
    @BindView(R.id.userImage2)
    ImageView userImage2;
    @BindView(R.id.chatBubble2)
    TextView chatBubble2;
    @BindView(R.id.chatBubble1)
    TextView chatBubble1;
    @BindView(R.id.userImage4)
    ImageView userImage4;
    @BindView(R.id.chatBubble3)
    TextView chatBubble3;
    @BindView(R.id.chatBubble4)
    TextView chatBubble4;
    @BindView(R.id.textUserID1)
    TextView textUserID1;
    @BindView(R.id.textUserID4)
    TextView textUserID4;
    @BindView(R.id.textUserID2)
    TextView textUserID2;
    @BindView(R.id.textUserID3)
    TextView textUserID3;
    @BindView(R.id.userImage3)
    ImageView userImage3;
    @BindView(R.id.btnWHITE)
    ImageButton btnWHITE;
    @BindView(R.id.btnRED)
    ImageButton btnRED;
    @BindView(R.id.btnORANGE)
    ImageButton btnORANGE;
    @BindView(R.id.btnYELLOW)
    ImageButton btnYELLOW;
    @BindView(R.id.btnGREEN)
    ImageButton btnGREEN;
    @BindView(R.id.btnBLUE)
    ImageButton btnBLUE;
    @BindView(R.id.btnPURPLE)
    ImageButton btnPURPLE;
    @BindView(R.id.btnBLACK)
    ImageButton btnBLACK;
    @BindView(R.id.btnPINK)
    ImageButton btnPINK;
    @BindView(R.id.btnBROWN)
    ImageButton btnBROWN;
    @BindView(R.id.llCanvas)
    LinearLayout llCanvas;
    @BindView(R.id.btnStart)
    ImageButton btnStart;
    @BindView(R.id.llMenu)
    LinearLayout llMenu;
    @BindView(R.id.announcement)
    TextView announcement;
    @BindView(R.id.suggestion)
    TextView suggestion;
    @BindView(R.id.textSuggest)
    TextView textSuggest;
    @BindView(R.id.timer)
    ProgressBar timer;
    @BindView(R.id.timeText)
    TextView timeText;
    @BindView(R.id.hint)
    TextView hint;
    @BindView(R.id.firework1)
    LottieAnimationView firework1;
    @BindView(R.id.firework2)
    LottieAnimationView firework2;
    @BindView(R.id.firework3)
    LottieAnimationView firework3;
    @BindView(R.id.firework4)
    LottieAnimationView firework4;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.gameLayout)
    LinearLayout gameLayout;

    private String line = "";
    private Socket socket;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private int totalNum;
    String nickname, profileImageURI;
    String user1Id;
    String user2Id;
    String user3Id;
    String user4Id;
    String msg, user1Image, user2Image, user3Image, user4Image, delete, announce, word, score1, score2, score3, score4, gameSet;
    String drawer = "";
    boolean isChat1On, isChat2On, isChat3On, isChat4On;
    PrintWriter out;
    String TAG = "GameActivity";
    private ArrayList<PlayerInfoItem> playerInfoArray = new ArrayList<>();
    private String userInfoJson, userNickName;
    DrawLine drawLine = null;
    boolean isDrawing;
    int color, totalTurn;
    int time = 110;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        initiation();//뷰를 초기화한다.
        getUserInfo();
    }

    @OnClick({R.id.btnRESET, R.id.btnSend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRESET:
                drawLine.Eraser();
                delete = "DELETE";
                new ResetCanvas(delete).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                delete = "[{\"drawer\"";
                break;
            case R.id.btnSend:
                Send send = new Send();
                send.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Log.w(TAG, "send.isCancelled() : " + send.isCancelled());
                editChat.setText("");
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class Chat extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... str) {
            try {
                // PORT 번호
                int port = 5901;
                // IP
                String IP_ADDRESS = "13.124.111.245";
                setSocket(IP_ADDRESS, port);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            out = new PrintWriter(networkWriter, true);
            String name = nickname;
            out.println(name);
            try {
                while (true) {
                    line = networkReader.readLine();
                    if (line.startsWith("chattingContent")) {
                        line = line.replaceFirst("chattingContent", "");
                    } else if (line.startsWith("[{\"drawer\"")) {
                        jsonToDrawPoint();
                    }
                    Log.e(TAG, "\n 수신 데이터 line (doInBackground) : " + line);
                    publishProgress(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return line;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
//            Log.e(TAG, "디버깅... line : " + line);
            if (line != null && line.startsWith("[{\"msg\"")) {
                Log.e(TAG, "첫번째 if 문 실행");
                jsonToData(line);
                announcement.setVisibility(View.VISIBLE);
//                Log.e(TAG, "announcement 상태 : 1. 드러남");
                announcement.setTextSize(40);
                announcement.setText("시작 대기중...( " + totalNum + " / 4 )");
                runOnUiThread(() -> {
                    chatting.append(msg + "\n");
                    sv.fullScroll(View.FOCUS_DOWN);
                });
                textUserID1.setText(user1Id);
                textUserID2.setText(user2Id);
                textUserID3.setText(user3Id);
                textUserID4.setText(user4Id);
                setBalloonColor(nickname);
            } else {
                Log.e(TAG, "두 번째 if 문 실행");
                assert line != null;
                if (line.startsWith("[{\"drawer\"")) {
                    return;
                } else if (line.startsWith("isDrawing")) {
                    line = line.replaceFirst("isDrawing", "");
                    chatting.append(line + "\n");
                    sv.fullScroll(View.FOCUS_DOWN);

                } else if (line.equals("DELETE")) {
                    drawLine.Eraser();
                } else if (line.startsWith("[{\"announcement\"")) {
                    jsonAnnouncement();
                } else if (line.startsWith("[{\"startDrawing\"")) {
                    jsonToSuggestion();
                    if (drawer.equals(user1Id)) {
                        isDrawing = user1Id.equals(nickname);
                        resetCurrentMode(1);
                        announcement.setTextSize(40);
                        hint.setText(word.length() + " 글자");
                        showView();
                        //Timer.sendEmptyMessage(0);
                        new TimerThread().start();
                    } else if (drawer.equals(user2Id)) {
                        isDrawing = user2Id.equals(nickname);
                        resetCurrentMode(1);
                        announcement.setTextSize(40);
                        hint.setText(word.length() + " 글자");
                        showView();
                        //Timer.sendEmptyMessage(0);
                        new TimerThread().start();
                    } else if (drawer.equals(user3Id)) {
                        isDrawing = user3Id.equals(nickname);
                        resetCurrentMode(1);
                        announcement.setTextSize(40);
                        hint.setText(word.length() + " 글자");
                        showView();
                        //Timer.sendEmptyMessage(0);
                        new TimerThread().start();
                    } else if (drawer.equals(user4Id)) {
                        isDrawing = user4Id.equals(nickname);
                        resetCurrentMode(1);
                        announcement.setTextSize(40);
                        hint.setText(word.length() + " 글자");
                        showView();
                        //Timer.sendEmptyMessage(0);
                        new TimerThread().start();
                    }
                } else if (line.startsWith("correct")) {
                    line = line.replaceFirst("correct", "");
                    Toast.makeText(GameActivity.this, line + "정답!", Toast.LENGTH_SHORT).show();
                } else if (line.startsWith("result")) {
                    line = line.replaceFirst("result", "");
                    announcement.setVisibility(View.VISIBLE);
                    announcement.setText(line);
                    textSuggest.setVisibility(View.VISIBLE);
                    suggestion.setVisibility(View.VISIBLE);

                } else if (line.startsWith("[{\"score1\"")) {
                    jsonToScore();
                } else if (line.startsWith(user1Id) || line.startsWith(user2Id) || line.startsWith(user3Id) || line.startsWith(user4Id)) {
                    runOnUiThread(() -> {
                        chatting.append(line + "\n");
                        sv.fullScroll(View.FOCUS_DOWN);
                        new SpeechBubble().start();
                    });
                }
            }

            Log.e(TAG, "user1Id : " + user1Id + " / user2Id : " + user2Id + " / user3Id : " + user3Id + " / user4Id : " + user4Id);
            Log.e(TAG, "-------------------------------------------------------------------------");

            GameMethods.setUserImage(user1Id, userImage1, textUserID1);
            GameMethods.setUserImage(user2Id, userImage2, textUserID2);
            GameMethods.setUserImage(user3Id, userImage3, textUserID3);
            GameMethods.setUserImage(user4Id, userImage4, textUserID4);
        }

    }

    public void setBalloonColor(String nickname) {
        if (nickname.equals(user1Id)) { // 1번 자리에 들어갔을 때.. 1번 말풍선 초록색으로 설정
            textUserID1.setTextColor(Color.argb(100, 0, 150, 136));
            chatBubble1.setBackgroundResource(R.drawable.balloon_green_left);
        } else if (nickname.equals(user2Id)) {// 2번 자리에 들어갔을 때.. 2번 말풍선 초록색으로 설정
            textUserID2.setTextColor(Color.argb(100, 0, 150, 136));
            chatBubble2.setBackgroundResource(R.drawable.balloon_green_right);
        } else if (nickname.equals(user3Id)) {// 3번 자리에 들어갔을 때.. 3번 말풍선 초록색으로 설정
            textUserID3.setTextColor(Color.argb(100, 0, 150, 136));
            chatBubble3.setBackgroundResource(R.drawable.balloon_green_left);
        } else if (nickname.equals(user4Id)) {// 4번 자리에 들어갔을 때.. 4번 말풍선 초록색으로 설정
            textUserID4.setTextColor(Color.argb(100, 0, 150, 136));
            chatBubble4.setBackgroundResource(R.drawable.balloon_green_right);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class Send extends AsyncTask<Integer, Integer, Integer> {

        String return_msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            return_msg = editChat.getText().toString();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                out = new PrintWriter(networkWriter, true);
                if (!return_msg.equals("")) {
                    out.println("chattingContent" + return_msg);
                }

                if (return_msg.equals(word)) {
                    out.println("correct" + nickname);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (line.startsWith(user1Id)) {
                chatBubble1.setVisibility(View.VISIBLE);
                String bubble = line.replaceFirst(user1Id + " : ", "");
                chatBubble1.setText(bubble);

                isChat1On = true;
            } else if (line.startsWith(user2Id)) {
                chatBubble2.setVisibility(View.VISIBLE);
                String bubble = line.replaceFirst(user2Id + " : ", "");
                chatBubble2.setText(bubble);

                isChat2On = true;
            } else if (line.startsWith(user3Id)) {
                chatBubble3.setVisibility(View.VISIBLE);
                String bubble = line.replaceFirst(user3Id + " : ", "");
                chatBubble3.setText(bubble);
                isChat3On = true;
            } else if (line.startsWith(user4Id)) {
                chatBubble4.setVisibility(View.VISIBLE);
                String bubble = line.replaceFirst(user4Id + " : ", "");
                chatBubble4.setText(bubble);
                isChat4On = true;
            }

        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (isChat1On) {
                chatBubble1.setVisibility(View.GONE);
                isChat1On = false;
            } else if (isChat2On) {
                chatBubble2.setVisibility(View.GONE);
                isChat2On = false;
            } else if (isChat3On) {
                chatBubble3.setVisibility(View.GONE);
                isChat3On = false;
            } else if (isChat4On) {
                chatBubble4.setVisibility(View.GONE);
                isChat4On = false;
            }

        }
    };

    class SpeechBubble extends Thread {

        @Override
        public void run() {
            super.run();
            Message message = new Message();
            Message message2 = new Message();
            message.arg1 = 1;
            handler.sendMessage(message);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message2.arg1 = 2;
            handler2.sendMessage(message2);

        }
    }

    public void setSocket(String ip, int port) throws IOException {
        try {
            socket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserEmail() {
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        return settings.getString("loginEmail", "");
    }

    public void getUserInfo() {
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_user_Info(getUserEmail());
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String userInfo = response.body().string();
                    Log.e(TAG, "userInfo : " + userInfo);
                    jsonToArrayListUserInfo(userInfo);
                } catch (IOException e) {
                    Log.e(TAG, "Retrofit Error : " + e);
                }
                new Chat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });
    }

    private void jsonToArrayListUserInfo(String userInfoJson) {

        String TAG_JSON = "webnautes";
        String TAG_NICKNAME = "user_nickname";
        String TAG_PROFILE_IMAGE_URI = "user_profile_image";

        try {
            JSONObject jsonObject = new JSONObject(userInfoJson);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                nickname = item.getString(TAG_NICKNAME);
                profileImageURI = item.getString(TAG_PROFILE_IMAGE_URI);
                playerInfoArray.add(new PlayerInfoItem(nickname, profileImageURI));
            }

        } catch (JSONException e) {
            Log.e(TAG, "showResult : ", e);
        }
    }

    public void getUserImage(int userIndex, String nickname, ImageView imageView) {
        final String[] profileImageURI = new String[1];
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_user_image_by_nickname(nickname);
        Log.e(TAG, "1(getUserImage)DB에 확인할 닉네임 : " + nickname);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String userInfo = response.body().string();
                    Log.e(TAG, "2(getUserImage)userInfo : " + userInfo);
                    String TAG_JSON = "webnautes";
                    String TAG_PROFILE_IMAGE_URI = "user_profile_image";
                    if (!userInfo.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(userInfo);
                            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                profileImageURI[0] = item.getString(TAG_PROFILE_IMAGE_URI);
                                Log.e(TAG, "3(getUserImage).." + nickname + "의 이미지 : " + profileImageURI[0]);
                                setUserImage(profileImageURI[0], imageView);
                                switch (userIndex){
                                    case 1:
                                        user1Image = profileImageURI[0];
                                        break;
                                    case 2:
                                        user2Image = profileImageURI[0];
                                        break;
                                    case 3:
                                        user3Image = profileImageURI[0];
                                        break;
                                    case 4:
                                        user4Image = profileImageURI[0];
                                        break;
                                }
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "getUserImage Error : ", e);
                        }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Retrofit Error : " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });
        Log.e(TAG, "4(getUserImage).." + nickname + "의 최종 이미지 : " + profileImageURI[0]);
    }

//    @SuppressLint("StaticFieldLeak")
//    public String getUserImage(String nickname) {
//        final String[] profileImageURI = new String[1];
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                    Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_user_image_by_nickname(nickname);
//                    try {
//                        profileImageURI[0] = Objects.requireNonNull(res.execute().body()).string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                return profileImageURI[0];
//            }
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                Log.e(TAG, "getChapterListSync...DONE");
//            }
//        }.execute();
//        return profileImageURI[0];
//    }

    private void jsonToData(String line) {
        StringBuilder sb = new StringBuilder();
        try {
            //SharedPreference 에 저장되어있던 데이터를 담을 JSONArray 를 선언하여 데이터를 넣는다.
            JSONArray jArray = new JSONArray(line);
            //JSON 에 담긴 데이터를 관리해주는 JSONObject 를 선언해 JSONArray 의 값들을 해석한다.
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                msg = jObject.getString("msg");
                int roomNum = jObject.getInt("roomNum");
                totalNum = jObject.getInt("totalUser");
                user1Id = jObject.getString("user1Id");
                user2Id = jObject.getString("user2Id");
                user3Id = jObject.getString("user3Id");
                user4Id = jObject.getString("user4Id");
                sb.append("msg:").append(msg).append("roomNum:").append(roomNum).append("totalNum:").append(totalNum).append("user1Id:")
                        .append(user1Id).append("user2Id:").append(user2Id).append("user3Id:").append(user3Id).append("user4Id:").append(user4Id).append("\n");
            }
            getUserImage(1, user1Id, userImage1);
            getUserImage(2, user2Id, userImage2);
            getUserImage(3, user3Id, userImage3);
            getUserImage(4, user4Id, userImage4);

            Log.e(TAG, "jsonToData = " + sb.toString());
        } catch (JSONException e) {
            Log.e(TAG, "오류. 오류내용 = " + e);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //hasFocus : 앱이 화면에 보여졌을때 true 로 설정되어 호출됨.
        //만약 그리기 뷰 전역변수에 값이 없을경우 전역변수를 초기화 시킴.
        if (hasFocus && drawLine == null) {
            //그리기 뷰가 보여질(나타날) 레이아웃 찾기..
            LinearLayout llCanvas = findViewById(R.id.llCanvas);
            if (llCanvas != null) { //그리기 뷰가 보여질 레이아웃이 있으면...
                //그리기 뷰 레이아웃의 넓이와 높이를 찾아서 Rect 변수 생성.
                Rect rect = new Rect(0, 0,
                        llCanvas.getMeasuredWidth(), llCanvas.getMeasuredHeight());

                //그리기 뷰 초기화..
                drawLine = new DrawLine(this, rect);

                //그리기 뷰를 그리기 뷰 레이아웃에 넣기 -- 이렇게 하면 그리기 뷰가 화면에 보여지게 됨.
                llCanvas.addView(drawLine);
            }
            //색깔 초기설정(검정색)
            resetCurrentMode(1);
            color = 1;
        }

        super.onWindowFocusChanged(hasFocus);
    }

    //코딩 하기 쉽게 하기 위해서.. 사용할 상단 메뉴 버튼들의 아이디를 배열에 넣는다..
    private int[] btns = {R.id.btnWHITE, R.id.btnBLACK, R.id.btnRED, R.id.btnORANGE, R.id.btnYELLOW, R.id.btnGREEN, R.id.btnBLUE, R.id.btnPURPLE, R.id.btnPINK, R.id.btnBROWN};
    //코딩 하기 쉽게 하기 위해서.. 상단 메뉴 버튼의 배열과 똑같이 실제 색상값을 배열로 만든다.
    private int[] colors = {Color.WHITE, Color.BLACK, Color.RED, Color.argb(100, 255, 87, 34), Color.YELLOW, Color.GREEN, Color.BLUE,
            Color.argb(100, 156, 39, 179), Color.argb(100, 248, 106, 154), Color.argb(100, 150, 75, 0)};

    //선택한 색상에 맞도록 버튼의 배경색과 글자색을 바꾸고, 그리기 뷰에도 알려 준다.
    private void resetCurrentMode(int curMode) {
        for (int value : btns) {
            //이건.. 배열 뒤지면서... 버튼이 있는지 체크..
            ImageButton btn = findViewById(value);
        }

        //만약 그리기 뷰가 초기화 되었으면, 그리기 뷰에 글자색을 알려줌..
        if (drawLine != null) drawLine.setLineColor(colors[curMode]);
    }

    /*
    버튼을 클릭했을때 호출 되는 함수.
    이 함수가 호출될때 어떤 버튼(뷰)에서 호출했는지를 같이 알려준다.
    버튼 클릭시 이 함수를 호출 하게 하기 위해서는...
    activity_main.xml 에서
    <Button ~~~~ android:onClick="btnClick" ~~~~ />
    이렇게 btnClick 이라는 함수명을 넣어 줘야함.
    */
    public void btnClick(View view) {
        if (view == null) return;

        for (int i = 0; i < btns.length; i++) {
            //배열 뒤지면서 클릭한 버튼이 있는지 확인..
            if (btns[i] == view.getId()) {
                //만약 선택한 버튼이 있으면.. 버튼모양 및 그리기 뷰 설정을 하기 위해서 함수 호출..
                resetCurrentMode(i);
                color = i;
                //더이상 처리를 할 필요가 없으니까.. for 문을 빠져 나옴..
                break;
            }
        }
    }

    public class DrawLine extends View {
        //현재 그리기 조건(색상, 굵기, 등등.)을 기억 하는 변수.
        private Paint paint = null;

        //그리기를 할 bitmap 객체. -- 도화지라고 생각하면됨.
        private Bitmap bitmap = null;

        //bitmap 객체의 canvas 객체. 실제로 그리기를 하기 위한 객체.. -- 붓이라고 생각하면됨.
        private Canvas canvas = null;

        //마우스 포인터(손가락)이 이동하는 경로 객체.
        private Path path;

        //마우스 포인터(손가락)이 가장 마지막에 위치한 x좌표값 기억용 변수.
        private float oldX;

        //마우스 포인터(손가락)이 가장 마지막에 위치한 y좌표값 기억용 변수.
        private float oldY;

        String TAG = "DrawLine";

        /**
         * 생성자.. new DrawLine(this, rect) 하면 여기가 호출됨.
         *
         * @param context Context 객체
         * @param rect    그리기 범위 화면 사이즈
         */
        public DrawLine(Context context, Rect rect) {
            this(context);

            //그리기를 할 bitmap 객체 생성.
            bitmap = Bitmap.createBitmap(rect.width(), rect.height(),
                    Bitmap.Config.ARGB_8888);
            //그리기 bitmap 에서 canvas 를 알아옴.
            canvas = new Canvas(bitmap);

            //경로 초기화.
            path = new Path();
        }

        @Override
        protected void onDetachedFromWindow() {
            //앱 종료시 그리기 bitmap 초기화 시킴...
            if (bitmap != null) bitmap.recycle();
            bitmap = null;

            super.onDetachedFromWindow();
        }

        @Override
        public void onDraw(Canvas canvas) {
            //그리기 bitmap 이 있으면 현재 화면에 bitmap 을 그린다.
            //자바의 view 는 onDraw 할때 마다 화면을 싹 지우고 다시 그리게 됨.
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
        }

        public void Eraser() {
            bitmap.eraseColor(Color.argb(0, 255, 255, 255));
            invalidate();
        }

        //이벤트 처리용 함수..
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isDrawing) {
                return false;
            }

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (!isDrawing) {
                        return false;
                    }
                    new SendPoint(false, x, y, color).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //최초 마우스를 눌렀을때(손가락을 댓을때) 경로를 초기화 시킨다.
                    path.reset();

                    //그다음.. 현재 경로로 경로를 이동 시킨다.
                    path.moveTo(x, y);

                    //포인터 위치값을 기억한다.
                    oldX = x;
                    oldY = y;

                    //계속 이벤트 처리를 하겠다는 의미.
                    return true;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!isDrawing) {
                        return false;
                    }
                    //포인트가 이동될때 마다 두 좌표(이전에눌렀던 좌표와 현재 이동한 좌료)간의 간격을 구한다.
                    float dx = Math.abs(x - oldX);
                    float dy = Math.abs(y - oldY);

                    //두 좌표간의 간격이 4px 이상이면 (가로든, 세로든) 그리기 bitmap 에 선을 그린다.
                    if (dx >= 4 || dy >= 4) {
                        //path 에 좌표의 이동 상황을 넣는다. 이전 좌표에서 신규 좌표로..
                        //lineTo를 쓸수 있지만.. 좀더 부드럽게 보이기 위해서 quadTo를 사용함.
                        path.quadTo(oldX, oldY, x, y);
                        new SendPoint(true, x, y, color).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        Log.e(TAG, "color : " + color);
                        //포인터의 마지막 위치값을 기억한다.
                        oldX = x;
                        oldY = y;
                        Log.e(TAG, "oldX : " + oldX + " / oldY : " + oldY);

                        //그리기 bitmap 에 path 를 따라서 선을 그린다.
                        canvas.drawPath(path, paint);
                    }

                    //화면을 갱신시킴.. 이 함수가 호출 되면 onDraw 함수가 실행됨.
                    invalidate();
                    //계속 이벤트 처리를 하겠다는 의미.
                    return true;

                }

            }
            //더이상 이벤트 처리를 하지 않겠다는 의미.
            return false;
        }

        /**
         * 펜 색상 세팅
         *
         * @param color 색상
         */
        public void setLineColor(int color) {
            paint = new Paint();
            paint.setColor(color);

            paint.setAlpha(255);
            paint.setDither(true);
            paint.setStrokeWidth(10);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
        }

        public DrawLine(Context context) {
            super(context);
        }

        public void Draw(boolean isDrawing, float x, float y, int color) {

            int drawing;
            resetCurrentMode(color);
            if (isDrawing) {
                drawing = 0;
            } else {
                drawing = 1;
            }
            Log.e(TAG, "과정0. isDrawing 판별 / isDrawing : " + isDrawing);
            switch (drawing) {
                case 1:
                    //최초 마우스를 눌렀을때(손가락을 댓을때) 경로를 초기화 시킨다.
                    path.reset();
                    Log.e(TAG, "과정1. path.reset();");
                    //그다음.. 현재 경로로 경로를 이동 시킨다.
                    path.moveTo(x, y);
                    Log.e(TAG, "과정2. path.moveTo(x, y);");
                    oldX = x;
                    oldY = y;

                case 0:
                    path.quadTo(oldX, oldY, x, y);
                    Log.e(TAG, "과정3. path.quadTo(oldX, oldY, x, y);");
                    //포인터의 마지막 위치값을 기억한다.
                    oldX = x;
                    oldY = y;
                    Log.e(TAG, "oldX : " + oldX + " / oldY : " + oldY);

                    //그리기 bitmap 에 path 를 따라서 선을 그린다.
                    canvas.drawPath(path, paint);
                    Log.e(TAG, "과정4. canvas.drawPath(path, paint);");
                    invalidate();
                    Log.e(TAG, "과정5. invalidate();");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SendPoint extends AsyncTask<Integer, Integer, Integer> {

        String return_msg, xR, yR;
        boolean isDrawing;
        float x, y;
        int color;

        SendPoint(boolean isDrawing, float x, float y, int color) {
            this.isDrawing = isDrawing;
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                xR = String.valueOf(x);
                yR = String.valueOf(y);
                out = new PrintWriter(networkWriter, true);
                return_msg = "[{\"drawer\" : " + nickname + ", \"isDrawing\" : " + isDrawing + ", \"xR\" : " + xR + ", \"yR\" : " + yR + ", \"color\" : " + color + "}]";
                out.println(return_msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //모두 지우기 버튼을 누르면 나머지 사용자의 캔버스도 비운다.
    @SuppressLint("StaticFieldLeak")
    class ResetCanvas extends AsyncTask<Integer, Integer, Integer> {
        String return_msg, delete;

        ResetCanvas(String delete) {
            this.delete = delete;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                out = new PrintWriter(networkWriter, true);
                return_msg = delete;
                out.println(return_msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void jsonToDrawPoint() {
        //StringBuffer?
        StringBuilder sb = new StringBuilder();
        try {
            //SharedPreference 에 저장되어있던 데이터를 담을 JSONArray 를 선언하여 데이터를 넣는다.
            JSONArray jArray = new JSONArray(line);
            Log.e(TAG, "변환할 좌표데이터 : " + line);
            //JSON 에 담긴 데이터를 관리해주는 JSONObject 를 선언해 JSONArray 의 값들을 해석한다.
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String drawer = jObject.getString("drawer");
                boolean isDrawing = jObject.getBoolean("isDrawing");
                String xR = jObject.getString("xR");
                String yR = jObject.getString("yR");
                int color = jObject.getInt("color");
                float x = Float.parseFloat(xR);
                float y = Float.parseFloat(yR);
//                sb.append("drawer:").append(drawer).append("oldXR:").append(oldXR).append(" / oldYR:").append(oldYR).append(" / xR:").append(xR).append(" / yR:").append(yR).append("\n");
                sb.append("drawer:").append(drawer).append(" / isDrawing:").append(isDrawing).append(" / xR:").append(xR).append(" / yR:").append(yR).append(" / color:").append(color).append("\n");
                Log.e(TAG, "drawer : " + drawer);
                if (!drawer.equals(nickname)) {
                    drawLine.Draw(isDrawing, x, y, color);
                    Log.e(TAG, "jsonToDrawPoint / color : " + color);
                }
            }
//            Log.e(TAG, "jsonToDrawPoint = " + sb.toString());
        } catch (JSONException e) {
            Log.e(TAG, "jsonToDrawPoint / 오류. 오류내용 = " + e);
        }
    }

    private void jsonToSuggestion() {
        //StringBuffer?
        StringBuilder sb = new StringBuilder();
        try {
            //SharedPreference 에 저장되어있던 데이터를 담을 JSONArray 를 선언하여 데이터를 넣는다.
            JSONArray jArray = new JSONArray(line);
            Log.e(TAG, "변환할 좌표데이터 : " + line);
            //JSON 에 담긴 데이터를 관리해주는 JSONObject 를 선언해 JSONArray 의 값들을 해석한다.
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                drawer = jObject.getString("startDrawing");
                word = jObject.getString("suggestion");
                sb.append("drawer:").append(drawer).append(" / word:").append(word).append("\n");
                Log.e(TAG, "drawer : " + drawer);
                suggestion.setText(word);

                GameMethods.setPaintingImage(this, drawer, user1Id, user1Image, userImage1);
                GameMethods.setPaintingImage(this, drawer, user2Id, user2Image, userImage2);
                GameMethods.setPaintingImage(this, drawer, user3Id, user3Image, userImage3);
                GameMethods.setPaintingImage(this, drawer, user4Id, user4Image, userImage4);

            }
//            Log.e(TAG, "jsonToDrawPoint = " + sb.toString());
        } catch (JSONException e) {
            Log.e(TAG, "jsonToSuggestion / 오류. 오류내용 = " + e);
        }
    }


    private void jsonToScore() {
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray jArray = new JSONArray(line);
            Log.e(TAG, "변환할 좌표데이터 : " + line);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                score1 = jObject.getString("score1");
                score2 = jObject.getString("score2");
                score3 = jObject.getString("score3");
                score4 = jObject.getString("score4");
                totalTurn = jObject.getInt("totalTurn");
                gameSet = jObject.getString("gameSet");
                sb.append("score1:").append(score1).append(" / score2:").append(score2).append(" / score3:").append(score3).append(" / score4:").append(score4).append("\n");
            }
//            Log.e(TAG, "jsonToScore = " + sb.toString());
            switch (gameSet) {
                case "gameInterrupted":
                    Toast.makeText(this, "게임 종료", Toast.LENGTH_SHORT).show();
                    drawer = ""; //drawer 초기화
                    showView();
                    announcement.setVisibility(View.VISIBLE);
                    announcement.setText("시작 대기중...( " + totalNum + " / 4 )");
                    color = 1;
                    drawLine.Eraser();
                    timeText.setVisibility(View.GONE);
                    hint.setVisibility(View.GONE);
                    setUserImage(user1Image, userImage1);
                    setUserImage(user2Image, userImage2);
                    setUserImage(user3Image, userImage3);
                    setUserImage(user4Image, userImage4);
                    Log.e(TAG, "이미지 설정3");
                    break;
                case "gameSet":
                    Toast.makeText(this, "게임 종료", Toast.LENGTH_SHORT).show();
                    drawer = "";
                    showView();
                    announcement.setVisibility(View.VISIBLE);
                    announcement.setText("시작 대기중...( " + totalNum + " / 4 )");
                    color = 1;
                    drawLine.Eraser();
                    timeText.setVisibility(View.GONE);
                    hint.setVisibility(View.GONE);
                    setUserImage(user1Image, userImage1);
                    setUserImage(user2Image, userImage2);
                    setUserImage(user3Image, userImage3);
                    setUserImage(user4Image, userImage4);
                    Log.e(TAG, "이미지 설정4");
                    Intent result = new Intent(this, GameResultActivity.class);
                    result.putExtra("score1", score1);
                    result.putExtra("score2", score2);
                    result.putExtra("score3", score3);
                    result.putExtra("score4", score4);
                    result.putExtra("user1", user1Id);
                    result.putExtra("user2", user2Id);
                    result.putExtra("user3", user3Id);
                    result.putExtra("user4", user4Id);
                    startActivityForResult(result, 1);
                    break;
            }

        } catch (JSONException e) {
            Log.e(TAG, "jsonToScore / 오류. 오류내용 = " + e);
        }
    }

    private void jsonAnnouncement() {
        //StringBuffer?
        StringBuilder sb = new StringBuilder();
        try {
            //SharedPreference 에 저장되어있던 데이터를 담을 JSONArray 를 선언하여 데이터를 넣는다.
            JSONArray jArray = new JSONArray(line);
            Log.e(TAG, "변환할 좌표데이터 : " + line);
            //JSON 에 담긴 데이터를 관리해주는 JSONObject 를 선언해 JSONArray 의 값들을 해석한다.
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                announce = jObject.getString("announcement");
                sb.append("announcement:").append(announce).append("\n");
//                Log.e(TAG, "announce : " + announce);
                if (announce.equals("gone")) {
                    announcement.setVisibility(View.GONE);
//                    Log.e(TAG, "announcement 상태 : 2. 가려짐");
                    drawer = "";
                    showView();
                    drawLine.Eraser();
                    hint.setVisibility(View.GONE);
                    timeText.setVisibility(View.GONE);
                } else if (announce.startsWith("[" + user1Id) || announce.startsWith("[" + user2Id) || announce.startsWith("[" + user3Id) || announce.startsWith("[" + user4Id)) {
                    announcement.setVisibility(View.VISIBLE);
//                    Log.e(TAG, "announcement 상태 : 2. 드러남");
                    announcement.setTextSize(40);
                    announcement.setText(announce);
                    timeText.setVisibility(View.GONE);
                    drawLine.Eraser();
                    drawer = "";
                    showView();
                    announcement.setVisibility(View.VISIBLE);
                    hint.setVisibility(View.GONE);
                } else if (announce.startsWith("timer")) { // 타이머
                    announce = announce.replaceFirst("timer", "");
//                    Log.e(TAG, "타이머 결과값 : " + announce);
                    double time = Double.parseDouble(announce);
                    timeText.setVisibility(View.VISIBLE);
                    timeText.setText(announce);
                } else if (announce.startsWith("[{\"startsDrawing\"")) {
                    hint.setVisibility(View.VISIBLE);
                } else { //3, 2, 1 카운트 다운 시
                    timeText.setVisibility(View.GONE);
                    announcement.setVisibility(View.VISIBLE);
//                    Log.e(TAG, "announcement 상태 : 3. 드러남");
                    announcement.setTextSize(90);
                    announcement.setText(announce);
                }

            }
//            Log.e(TAG, "jsonToDrawPoint = " + sb.toString());
        } catch (JSONException e) {
            Log.e(TAG, "jsonToDrawPoint / 오류. 오류내용 = " + e);
        }
    }

    public void showView() {
//        Log.e(TAG, "showView 상태 // announce :" + announce + " / drawer : " + drawer + " / nickname : " + nickname);

        if (announce.equals("gone") || gameSet.equals("gameSet") || !drawer.equals(nickname)) { // 본인의 차례가 아닐 때
            isDrawing = false;
            editChat.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);
            llMenu.setVisibility(View.GONE);
            suggestion.setVisibility(View.GONE);
            textSuggest.setVisibility(View.GONE);
            hint.setVisibility(View.VISIBLE);
            announcement.setVisibility(View.GONE);
        } else { // 본인의 차례일 때
            isDrawing = true;
            editChat.setVisibility(View.GONE);
            btnSend.setVisibility(View.GONE);
            llMenu.setVisibility(View.VISIBLE);
            suggestion.setVisibility(View.VISIBLE);
            textSuggest.setVisibility(View.VISIBLE);
            hint.setVisibility(View.GONE);

            announcement.setVisibility(View.GONE);
        }
    }

    class TimerThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (int time = 100; time >= 0 && !announce.startsWith("score1") && !announcement.getText().toString().contains("정답!") && !announcement.getText().toString().startsWith("시작 대기중..."); time--) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.setProgress(time, true);
                if (time >= 70) {
                    timer.getProgressDrawable().setColorFilter(Color.argb(100, 0, 150, 136), PorterDuff.Mode.SRC_IN);
                } else if (time >= 30) {
                    timer.getProgressDrawable().setColorFilter(Color.argb(100, 255, 87, 34), PorterDuff.Mode.SRC_IN);
                } else {
                    timer.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }
            }
            timer.setProgress(0, true);
            timer.getProgressDrawable().setColorFilter(Color.argb(100, 0, 150, 136), PorterDuff.Mode.SRC_IN);

        }
    }

    public void initiation() {
        // 1. 방에 처음 들어오면 모든 유저의 뷰를 가린다.
        userImage1.setVisibility(View.GONE);
        userImage2.setVisibility(View.GONE);
        userImage3.setVisibility(View.GONE);
        userImage4.setVisibility(View.GONE);
        textUserID1.setVisibility(View.GONE);
        textUserID2.setVisibility(View.GONE);
        textUserID3.setVisibility(View.GONE);
        textUserID4.setVisibility(View.GONE);
        chatBubble1.setVisibility(View.GONE);
        chatBubble2.setVisibility(View.GONE);
        chatBubble3.setVisibility(View.GONE);
        chatBubble4.setVisibility(View.GONE);
        isChat1On = false;
        isChat2On = false;
        isChat3On = false;
        isChat4On = false;
        llMenu.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
        announcement.setVisibility(View.GONE);
        Log.e(TAG, "announcement 상태 : 3. 가려짐");
        suggestion.setVisibility(View.GONE);
        textSuggest.setVisibility(View.GONE);
        timeText.setVisibility(View.GONE);
        timer.setMax(100);
    }

    public void setUserImage(String imageURI, ImageView imageView) {
        Glide.with(getApplicationContext()).load(imageURI).centerCrop().circleCrop()
                .override(200, 200)
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null) {
                String code = data.getStringExtra("code");
                if (code != null && code.equals("quit")) {
                    int userNum = 0;
                    if (nickname.equals(user1Id)) {
                        userNum = 1;
                    } else if (nickname.equals(user2Id)) {
                        userNum = 2;
                    } else if (nickname.equals(user3Id)) {
                        userNum = 3;
                    } else if (nickname.equals(user4Id)) {
                        userNum = 4;
                    }
                    new SendRetry(userNum).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else if (code != null && code.equals("retry")) {
                    int userNum = 0;
                    if (nickname.equals(user1Id)) {
                        userNum = 1;
                    } else if (nickname.equals(user2Id)) {
                        userNum = 2;
                    } else if (nickname.equals(user3Id)) {
                        userNum = 3;
                    } else if (nickname.equals(user4Id)) {
                        userNum = 4;
                    }
                    new SendRetry(userNum).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SendRetry extends AsyncTask<Integer, Integer, Integer> {

        String return_msg;
        int userNum;

        SendRetry(int userNum) {
            this.userNum = userNum;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                out = new PrintWriter(networkWriter, true);
                return_msg = "retry" + userNum;
                out.println(return_msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private long pressedTime = 0;

    // 리스너 생성
    public interface OnBackPressedListener {
        void onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

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
                Snackbar.make(findViewById(R.id.gameLayout), " 한 번 더 누르면 게임이 종료됩니다.", Snackbar.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    Snackbar.make(findViewById(R.id.gameLayout),
                            " 한 번 더 누르면 게임이 종료됩니다.", Snackbar.LENGTH_LONG).show();
                    pressedTime = 0;
                } else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finish, killProcess");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
//                    Process.killProcess(Process.myPid());
                }
            }
        }
    }
}

