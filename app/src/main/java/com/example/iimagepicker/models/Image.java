package com.example.iimagepicker.models;

import java.io.File;

public class Image {
    private String imagePath;
    private boolean selected = false;
    private int countPosition = 0;

    public Image(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getCountPosition() {
        return countPosition;
    }

    public void setCountPosition(int countPosition) {
        this.countPosition = countPosition;
    }
}
