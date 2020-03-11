package com.wonjin.android.Items;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedChaptersItem implements Parcelable {

    private String vocabookTitle;
    private String ChapterTitle;

    public SelectedChaptersItem(String vocabookTitle, String chapterTitle) {
        this.vocabookTitle = vocabookTitle;
        ChapterTitle = chapterTitle;
    }

    private SelectedChaptersItem(Parcel in) {
        vocabookTitle = in.readString();
        ChapterTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vocabookTitle);
        dest.writeString(ChapterTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectedChaptersItem> CREATOR = new Creator<SelectedChaptersItem>() {
        @Override
        public SelectedChaptersItem createFromParcel(Parcel in) {
            return new SelectedChaptersItem(in);
        }

        @Override
        public SelectedChaptersItem[] newArray(int size) {
            return new SelectedChaptersItem[size];
        }
    };

    public String getVocabookTitle() {
        return vocabookTitle;
    }

    public void setVocabookTitle(String vocabookTitle) {
        this.vocabookTitle = vocabookTitle;
    }

    public String getChapterTitle() {
        return ChapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        ChapterTitle = chapterTitle;
    }

}
