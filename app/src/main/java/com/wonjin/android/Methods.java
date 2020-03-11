package com.wonjin.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@SuppressLint("Registered")
public class Methods extends AppCompatActivity {

    //현재시간
    public static String currentDate() {
        // 현재시간을 mSec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd");
        // nowDate 변수에 값을 저장한다.
        return sdfNow.format(date);
    }

    public static String getExtension(String fileStr) {
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".") + 1);
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }

    public static String encodeFileName(String fileName) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static String getFileNameFromUri(String imageUri) {
        String fileName = imageUri.substring(imageUri.lastIndexOf("vocabook_cover_images/") + 22);
        fileName = encodeFileName(fileName);
        fileName = fileName.replaceAll("%", "%25");
        return fileName;
    }

    public static String getVocabookImageFromServer(String imageUri) {
        String fileName = imageUri.substring(imageUri.lastIndexOf("vocabook_cover_images/") + 22);
        fileName = encodeFileName(fileName);
        fileName = fileName.replaceAll("%", "%25");
        return "http://13.124.111.245/android/vocabook/vocabook_cover_images/" + fileName;
    }

    public static String getWordImageFromServer(String imageUri) {
        String fileName = imageUri.substring(imageUri.lastIndexOf("word_images/") + 12);
        fileName = encodeFileName(fileName);
        fileName = fileName.replaceAll("%", "%25");
        return "http://13.124.111.245/android/vocabook/word_images/" + fileName;
    }

    public static String getWordFileNameFromUri(String imageUri) {
        String fileName = imageUri.substring(imageUri.lastIndexOf("word_images/") + 12);
        fileName = encodeFileName(fileName);
//        fileName = fileName.replaceAll("%", "%25");
        return fileName;
    }

    public static void threadSleep(int milliSec) {
        try {
            Thread.sleep(milliSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
    }
    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
        }
    }

    public static String getStudyDaysPassed(String dateCurrent, String datePast){

        String result = "기록 없음";
        if(dateCurrent != null && datePast != null){
            try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                // dateCurrent, datePast 두 날짜를 parse()를 통해 Date형으로 변환.
                Date CurrentDate = format.parse(dateCurrent);
                Date PastDate = format.parse(datePast);

                // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
                // 연산결과 -950400000. long type 으로 return 된다.
                long calDate = 0;
                if (CurrentDate != null) {
                    if (PastDate != null) {
                        calDate = CurrentDate.getTime() - PastDate.getTime();
                    }
                }

                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                long calDateDays = calDate / ( 24*60*60*1000);

                calDateDays = Math.abs(calDateDays);

                if(calDateDays <= 30){
                    if(calDateDays == 0){
                        result = "오늘";
                    } else {
                        result = calDateDays + "일 전";
                    }

                } else if(calDateDays < 365) {
                    calDateDays = calDateDays/30;
                    result = calDateDays + "달 전";
                } else {
                    calDateDays = calDateDays / 365 ;
                    result = calDateDays + "년 전";
                }

}
            catch(ParseException e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public static long getStudyDaysPassedInteger(String dateCurrent, String datePast){

        String result = "기록 없음";
        long calDateDays = 0;
        if(dateCurrent != null && datePast != null){
            try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                // dateCurrent, datePast 두 날짜를 parse()를 통해 Date형으로 변환.
                Date CurrentDate = format.parse(dateCurrent);
                Date PastDate = format.parse(datePast);
                // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
                // 연산결과 -950400000. long type 으로 return 된다.
                long calDate = 0;
                if (CurrentDate != null) {
                    if (PastDate != null) {
                        calDate = CurrentDate.getTime() - PastDate.getTime();
                    }
                }
                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                calDateDays = calDate / ( 24*60*60*1000);
                calDateDays = Math.abs(calDateDays);
            }
            catch(ParseException e){
                e.printStackTrace();
            }
        }
        return calDateDays;
    }

}
