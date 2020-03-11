package com.wonjin.android.Items;

import org.json.JSONObject;

public class ChapterItem  implements Comparable<ChapterItem>{

    private String chapterTitle;
    private int chapterNum;
    private String vocabookTitle;
    private int numOfWords;
    private String recentStudyDate;
    private JSONObject wordContent;
    private boolean isSelected;

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getVocabookTitle() {
        return vocabookTitle;
    }

    public void setVocabookTitle(String vocabookTitle) {
        this.vocabookTitle = vocabookTitle;
    }

    public int getNumOfWords() {
        return numOfWords;
    }

    public void setNumOfWords(int numOfWords) {
        this.numOfWords = numOfWords;
    }

    public String getRecentStudyDate() {
        return recentStudyDate;
    }

    public void setRecentStudyDate(String recentStudyDate) {
        this.recentStudyDate = recentStudyDate;
    }

    public JSONObject getWordContent() {
        return wordContent;
    }

    public void setWordContent(JSONObject wordContent) {
        this.wordContent = wordContent;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ChapterItem(String chapterTitle, int chapterNum, String vocabookTitle, int numOfWords, String recentStudyDate, JSONObject wordContent, boolean isSelected) {
        this.chapterTitle = chapterTitle;
        this.chapterNum = chapterNum;
        this.vocabookTitle = vocabookTitle;
        this.numOfWords = numOfWords;
        this.recentStudyDate = recentStudyDate;
        this.wordContent = wordContent;
        this.isSelected = isSelected;
    }

    @Override
    public int compareTo(ChapterItem o) {
        int targetNum = o.getChapterNum();
        return Integer.compare(chapterNum, targetNum);
    }
}