package com.example.android.Items;

import org.json.JSONArray;
import org.json.JSONObject;

public class VocabookItem implements Comparable<VocabookItem>{

    private String vocabookCoverImage;
    private int vocabookNum;
    private String vocabookTitle;
    private int numOfWords;
    private boolean isSelected;

    public VocabookItem(String vocabookCoverImage, int vocabookNum, String vocabookTitle, int numOfWords, boolean isSelected) {
        this.vocabookCoverImage = vocabookCoverImage;
        this.vocabookNum = vocabookNum;
        this.vocabookTitle = vocabookTitle;
        this.numOfWords = numOfWords;
        this.isSelected = isSelected;
    }

    public String getVocabookCoverImage() {
        return vocabookCoverImage;
    }

    public void setVocabookCoverImage(String vocabookCoverImage) {
        this.vocabookCoverImage = vocabookCoverImage;
    }

    public int getVocabookNum() {
        return vocabookNum;
    }

    public void setVocabookNum(int vocabookNum) {
        this.vocabookNum = vocabookNum;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int compareTo(VocabookItem o) {
        int targetNum = o.getVocabookNum();
        return Integer.compare(vocabookNum, targetNum);
    }
}
