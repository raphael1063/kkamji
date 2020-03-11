package com.wonjin.android.Items;

public class HomeItem implements Comparable<HomeItem>{

    private String vocabookTitle;
    private String vocabookImage;
    private String chapterTitle;
    private int chapterWordCount;
    private String studyDate;
    private int priority;

    public HomeItem(String vocabookTitle, String vocabookImage, String chapterTitle, int chapterWordCount, String studyDate, int priority) {
        this.vocabookTitle = vocabookTitle;
        this.vocabookImage = vocabookImage;
        this.chapterTitle = chapterTitle;
        this.chapterWordCount = chapterWordCount;
        this.studyDate = studyDate;
        this.priority = priority;
    }

    public String getVocabookTitle() {
        return vocabookTitle;
    }

    public void setVocabookTitle(String vocabookTitle) {
        this.vocabookTitle = vocabookTitle;
    }

    public String getVocabookImage() {
        return vocabookImage;
    }

    public void setVocabookImage(String vocabookImage) {
        this.vocabookImage = vocabookImage;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public int getChapterWordCount() {
        return chapterWordCount;
    }

    public void setChapterWordCount(int chapterWordCount) {
        this.chapterWordCount = chapterWordCount;
    }

    public String getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(HomeItem o) {
        int targetNum = o.getPriority();
        return Integer.compare(priority, targetNum);
    }
}
