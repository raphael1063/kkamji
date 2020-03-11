package com.wonjin.android.Items;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerInfoItem implements Parcelable {

   private String nickname;
   private String profileImageURI;

   public PlayerInfoItem(String nickname, String profileImageURI) {
      this.nickname = nickname;
      this.profileImageURI = profileImageURI;
   }

   private PlayerInfoItem(Parcel in) {
      nickname = in.readString();
      profileImageURI = in.readString();
   }

   public static final Creator<PlayerInfoItem> CREATOR = new Creator<PlayerInfoItem>() {
      @Override
      public PlayerInfoItem createFromParcel(Parcel in) {
         return new PlayerInfoItem(in);
      }

      @Override
      public PlayerInfoItem[] newArray(int size) {
         return new PlayerInfoItem[size];
      }
   };

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   public String getProfileImageURI() {
      return profileImageURI;
   }

   public void setProfileImageURI(String profileImageURI) {
      this.profileImageURI = profileImageURI;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(nickname);
      dest.writeString(profileImageURI);
   }
}
