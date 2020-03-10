package com.example.android.Activities.GameActivities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.Methods;
import com.example.android.R;

public class GameMethods {


    public static void setUserImage(String userID, ImageView userImage, TextView testUserID) {
        if (userID.isEmpty()) {
            userImage.setVisibility(View.GONE);
            testUserID.setVisibility(View.GONE);
        } else {
            userImage.setVisibility(View.VISIBLE);
            testUserID.setVisibility(View.VISIBLE);
        }
    }

    public static void setPaintingImage(Context context, String drawer, String userNickname, String userImageURI, ImageView userImage) {
        if (drawer.equals(userNickname)) {
            userImage.setImageResource(R.drawable.painting);
        } else {
            Log.e("GameMethods", "출력할 이미지 uri : " + userImageURI);
            Glide.with(context).load(userImageURI).centerCrop().circleCrop()
                    .override(200, 200)
                    .into(userImage);
        }
    }

    public static void setUserImage(Context context, String imageURI, ImageView imageView){
        Glide.with(context).load(imageURI).centerCrop().circleCrop()
                .override(200, 200)
                .into(imageView);
    }
}
