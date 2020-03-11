package com.wonjin.android.Items;

public class WordImageItem {

    private String imageURI1;
    private String imageURI2;
    private String imageURI3;

    public WordImageItem(String imageURI1, String imageURI2, String imageURI3) {
        this.imageURI1 = imageURI1;
        this.imageURI2 = imageURI2;
        this.imageURI3 = imageURI3;
    }

    public String getImageURI1() {
        return imageURI1;
    }

    public void setImageURI1(String imageURI1) {
        this.imageURI1 = imageURI1;
    }

    public String getImageURI2() {
        return imageURI2;
    }

    public void setImageURI2(String imageURI2) {
        this.imageURI2 = imageURI2;
    }

    public String getImageURI3() {
        return imageURI3;
    }

    public void setImageURI3(String imageURI3) {
        this.imageURI3 = imageURI3;
    }
}
