package com.wonjin.android.Items;

import android.os.Parcel;
import android.os.Parcelable;

public class LogItem implements Parcelable {

    private int index;
    private String word;
    private String meaningsInput;
    private String meaningsCorrect;
    private boolean isCorrect;

    public LogItem(int index, String word, String meaningsInput, String meaningsCorrect, boolean isCorrect) {
        this.index = index;
        this.word = word;
        this.meaningsInput = meaningsInput;
        this.meaningsCorrect = meaningsCorrect;
        this.isCorrect = isCorrect;
    }

    protected LogItem(Parcel in) {
        index = in.readInt();
        word = in.readString();
        meaningsInput = in.readString();
        meaningsCorrect = in.readString();
        isCorrect = in.readByte() != 0;
    }

    public static final Creator<LogItem> CREATOR = new Creator<LogItem>() {
        @Override
        public LogItem createFromParcel(Parcel in) {
            return new LogItem(in);
        }

        @Override
        public LogItem[] newArray(int size) {
            return new LogItem[size];
        }
    };

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaningsInput() {
        return meaningsInput;
    }

    public void setMeaningsInput(String meaningsInput) {
        this.meaningsInput = meaningsInput;
    }

    public String getMeaningsCorrect() {
        return meaningsCorrect;
    }

    public void setMeaningsCorrect(String meaningsCorrect) {
        this.meaningsCorrect = meaningsCorrect;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(word);
        dest.writeString(meaningsInput);
        dest.writeString(meaningsCorrect);
        dest.writeByte((byte) (isCorrect ? 1 : 0));
    }
}
