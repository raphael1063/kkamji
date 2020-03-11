package com.wonjin.android.Items;

import android.os.Parcel;
import android.os.Parcelable;

public class BlackPaperItem implements Parcelable {
    public String vocabookTitle;
    public String chapterTitle;
    public String word;
    public String meaning1;
    public String meaning2;
    public String meaning3;
    public String meaning4;
    public String meaning5;
    public String wordImageURI;
    public int correctCount;
    public int testTime;

    public BlackPaperItem(String vocabookTitle, String chapterTitle, String word, String meaning1, String meaning2, String meaning3, String meaning4, String meaning5, String wordImageURI, int correctCount, int testTime) {
        this.vocabookTitle = vocabookTitle;
        this.chapterTitle = chapterTitle;
        this.word = word;
        this.meaning1 = meaning1;
        this.meaning2 = meaning2;
        this.meaning3 = meaning3;
        this.meaning4 = meaning4;
        this.meaning5 = meaning5;
        this.wordImageURI = wordImageURI;
        this.correctCount = correctCount;
        this.testTime = testTime;
    }

    protected BlackPaperItem(Parcel in) {
        vocabookTitle = in.readString();
        chapterTitle = in.readString();
        word = in.readString();
        meaning1 = in.readString();
        meaning2 = in.readString();
        meaning3 = in.readString();
        meaning4 = in.readString();
        meaning5 = in.readString();
        wordImageURI = in.readString();
        correctCount = in.readInt();
        testTime = in.readInt();
    }

    public static final Creator<BlackPaperItem> CREATOR = new Creator<BlackPaperItem>() {
        @Override
        public BlackPaperItem createFromParcel(Parcel in) {
            return new BlackPaperItem(in);
        }

        @Override
        public BlackPaperItem[] newArray(int size) {
            return new BlackPaperItem[size];
        }
    };

    public String getVocabookTitle() {
        return vocabookTitle;
    }

    public void setVocabookTitle(String vocabookTitle) {
        this.vocabookTitle = vocabookTitle;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning1() {
        return meaning1;
    }

    public void setMeaning1(String meaning1) {
        this.meaning1 = meaning1;
    }

    public String getMeaning2() {
        return meaning2;
    }

    public void setMeaning2(String meaning2) {
        this.meaning2 = meaning2;
    }

    public String getMeaning3() {
        return meaning3;
    }

    public void setMeaning3(String meaning3) {
        this.meaning3 = meaning3;
    }

    public String getMeaning4() {
        return meaning4;
    }

    public void setMeaning4(String meaning4) {
        this.meaning4 = meaning4;
    }

    public String getMeaning5() {
        return meaning5;
    }

    public void setMeaning5(String meaning5) {
        this.meaning5 = meaning5;
    }

    public String getWordImageURI() {
        return wordImageURI;
    }

    public void setWordImageURI(String wordImageURI) {
        this.wordImageURI = wordImageURI;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getTestTime() {
        return testTime;
    }

    public void setTestTime(int testTime) {
        this.testTime = testTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vocabookTitle);
        dest.writeString(chapterTitle);
        dest.writeString(word);
        dest.writeString(meaning1);
        dest.writeString(meaning2);
        dest.writeString(meaning3);
        dest.writeString(meaning4);
        dest.writeString(meaning5);
        dest.writeString(wordImageURI);
        dest.writeInt(correctCount);
        dest.writeInt(testTime);
    }
}
