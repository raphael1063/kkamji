package com.wonjin.android.Items;

public class WordsForTestItem {

    public String vocabookTitle;
    public String chapterTitle;
    public String word;
    public String meaning1;
    public String meaning2;
    public String meaning3;
    public String meaning4;
    public String meaning5;
    public String wordImageURI;

    public WordsForTestItem(String vocabookTitle, String chapterTitle, String word, String meaning1, String meaning2, String meaning3, String meaning4, String meaning5, String wordImageURI) {
        this.vocabookTitle = vocabookTitle;
        this.chapterTitle = chapterTitle;
        this.word = word;
        this.meaning1 = meaning1;
        this.meaning2 = meaning2;
        this.meaning3 = meaning3;
        this.meaning4 = meaning4;
        this.meaning5 = meaning5;
        this.wordImageURI = wordImageURI;
    }

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
}
