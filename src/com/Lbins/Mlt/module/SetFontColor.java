package com.Lbins.Mlt.module;

/**
 * Created by Administrator on 2016/2/25.
 */
public class SetFontColor {
    private String title;
    private String fontColor;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public SetFontColor(String title, String fontColor) {
        this.title = title;
        this.fontColor = fontColor;
    }
}
