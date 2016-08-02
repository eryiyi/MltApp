package com.Lbins.Mlt.module;

/**
 * Created by Administrator on 2016/3/26 0026.
 */
public class Notice {
    private String mm_notice_id;
    private String mm_manager_id;
    private String mm_notice_title;
    private String mm_notice_content;
    private String dateline;
    private String is_del;

    public String getMm_notice_id() {
        return mm_notice_id;
    }

    public void setMm_notice_id(String mm_notice_id) {
        this.mm_notice_id = mm_notice_id;
    }

    public String getMm_manager_id() {
        return mm_manager_id;
    }

    public void setMm_manager_id(String mm_manager_id) {
        this.mm_manager_id = mm_manager_id;
    }

    public String getMm_notice_title() {
        return mm_notice_title;
    }

    public void setMm_notice_title(String mm_notice_title) {
        this.mm_notice_title = mm_notice_title;
    }

    public String getMm_notice_content() {
        return mm_notice_content;
    }

    public void setMm_notice_content(String mm_notice_content) {
        this.mm_notice_content = mm_notice_content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }
}
