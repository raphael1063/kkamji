package com.example.android.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.Activities.AccountActivities.LoginActivity;
import com.example.android.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("Is on?", "Turning immersive mode mode off. ");
        } else {
            Log.i("Is on?", "Turning immersive mode mode on.");
        }
        // 몰입 모드를 꼭 적용해야 한다면 아래의 3가지 속성을 모두 적용시켜야 합니다
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        setContentView(R.layout.activity_intro);
        LoadingThread thread = new LoadingThread();
        thread.start();

}

    private class LoadingThread extends Thread {
        private static final String TAG = "ExampleThread";

        LoadingThread() {
            // 초기화 작업

        }

        public void run() {

            int second = 1;//인트로 시간

            while (second != 0) {

                try { // 스레드에게 수행시킬 동작들 구현
                    Thread.sleep(3000);
                    // 3초간 Thread를 잠재운다
                    second--;

} catch (InterruptedException e) {
                    Log.e(TAG, "오류났다 ㅎㅎ 오류내용 = " + e);

                }
                Log.i("경과된 시간 : ", Integer.toString(second));

}
            final Intent intentLogIn = new Intent(IntroActivity.this, LoginActivity.class);

            startActivity(intentLogIn);
            LoadingThread.interrupted();
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
