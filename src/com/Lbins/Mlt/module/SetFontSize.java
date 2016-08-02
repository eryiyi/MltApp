package com.Lbins.Mlt.module;

/**
 * Created by Administrator on 2016/2/25.
 */
public class SetFontSize {
    private String title;
    private String sizeStr;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public void setSizeStr(String sizeStr) {
        this.sizeStr = sizeStr;
    }

    public SetFontSize(String title, String sizeStr) {
        this.title = title;
        this.sizeStr = sizeStr;
    }
}
