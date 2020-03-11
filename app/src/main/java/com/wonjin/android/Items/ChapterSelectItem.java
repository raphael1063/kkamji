package com.wonjin.android.Items;

import java.util.List;

public class ChapterSelectItem {
    public int type;
    public String title;
    public int numOfWords;
    public List<ChapterSelectItem> invisibleChapters;
    public Boolean isItemChecked;

    public ChapterSelectItem(int type, String title, int numOfWords, Boolean isItemChecked) {
        this.type = type;
        this.title = title;
        this.numOfWords = numOfWords;
        this.isItemChecked = isItemChecked;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumOfWords() {
        return numOfWords;
    }

    public void setNumOfWords(int numOfWords) {
        this.numOfWords = numOfWords;
    }

    public List<ChapterSelectItem> getInvisibleChapters() {
        return invisibleChapters;
    }

    public void setInvisibleChapters(List<ChapterSelectItem> invisibleChapters) {
        this.invisibleChapters = invisibleChapters;
    }

    public Boolean getItemChecked() {
        return isItemChecked;
    }

    public void setItemChecked(Boolean itemChecked) {
        isItemChecked = itemChecked;
    }
}