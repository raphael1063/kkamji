package com.example.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.Activities.AccountActivities.AccountActivity;
import com.example.android.Activities.AccountActivities.BugReportActivity;
import com.example.android.Activities.AccountActivities.LoginActivity;
import com.example.android.Adapters.FragmentAdapter;
import com.example.android.Fragments.GameFragment;
import com.example.android.Fragments.HomeFragment;
import com.example.android.Fragments.StudyFragment;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_tool_bar_title)
    TextView mainToolBarTitle;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    String TAG = "MainActivity";
    String userInfoJson, name, nickname, email, profileImageUrl;
    public SharedPreferences settings;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    // 3개의 메뉴에 들어갈 Fragment들
    private HomeFragment homeFragment = new HomeFragment();
    private StudyFragment studyFragment = new StudyFragment();
    private GameFragment gameFragment = new GameFragment();
    //Drawer Views
    ImageView nav_profile_image;
    TextView nav_name, nav_email;

    FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pushService();

        View hView = navView.getHeaderView(0);
        nav_profile_image = hView.findViewById(R.id.drawer_profile_image);
        nav_name = hView.findViewById(R.id.text_drawer_name);
        nav_email = hView.findViewById(R.id.text_drawer_email);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(3);
        fragmentAdapter.addItem(homeFragment);
        fragmentAdapter.addItem(studyFragment);
        fragmentAdapter.addItem(gameFragment);
        fragmentAdapter.notifyDataSetChanged();
        SetNavigationDrawer();



        // bottomNavigationView 의 아이템이 선택될 때 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_menu_home: {
                    viewPager.setCurrentItem(0);
                    mainToolBarTitle.setText("깜지 홈");
                    break;
                }
                case R.id.navigation_menu_study: {
                    viewPager.setCurrentItem(1);
                    mainToolBarTitle.setText("단어학습");
                    break;
                }

                case R.id.navigation_menu_game: {
                    viewPager.setCurrentItem(2);
                    mainToolBarTitle.setText("그림맞추기 게임");
                    break;
                }
            }

            return true;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {// 왼쪽 상단 메뉴 버튼 눌렀을 때
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private long pressedTime = 0;

    @OnClick(R.id.btn_logout)
    public void onViewClicked() {

        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
        logout.putExtra("CODE", "logout");
        startActivity(logout);

        finish();
    }

    // 리스너 생성
    public interface OnBackPressedListener {
        void onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

// 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
        } else if (mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
        } else {
            Log.e("!!!", "Listener is null");
            if (pressedTime == 0) {
                Snackbar.make(findViewById(R.id.drawer_layout), " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    Snackbar.make(findViewById(R.id.drawer_layout),
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

    public void SetNavigationDrawer() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 메뉴 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_48dp); //메뉴 버튼 이미지 지정

        navView.setNavigationItemSelectedListener(menuItem -> {
            Log.e(TAG, "menuItem.isChecked() : " + menuItem.isChecked());
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();

            switch (menuItem.getItemId()) {
                case R.id.account:
                    startActivity(new Intent(this, AccountActivity.class));
                    break;
                case R.id.bug_report:
                    startActivity(new Intent(this, BugReportActivity.class));
                    break;
            }
            return true;
        });
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().get_user_Info(userEmail);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });
    }

    public void setUserInfo(String name, String nickname, String email, String profileImageUrl) {
        Glide.with(getApplicationContext()).load(profileImageUrl).centerCrop().circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(200, 200)
                .into(nav_profile_image);
        nav_email.setText(email);
        nav_name.setText(name);
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
    public void pushService(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM Log", "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    Log.d("FCM Log", "FCM 토큰: " + token);
                    Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                });

    }
}
