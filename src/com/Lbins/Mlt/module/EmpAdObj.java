package com.Lbins.Mlt.module;

/**
 * Created by Administrator on 2016/2/23.
 */
public class EmpAdObj {
    private String mm_emp_ad_id;
    private String mm_emp_ad_pic;
    private String mm_emp_id;
    private String mm_emp_ad_url;
    private String mm_emp_ad_num;
    private String mm_emp_nickname;

    public String getMm_emp_nickname() {
        return mm_emp_nickname;
    }

    public void setMm_emp_nickname(String mm_emp_nickname) {
        this.mm_emp_nickname = mm_emp_nickname;
    }

    public String getMm_emp_ad_url() {
        return mm_emp_ad_url;
    }

    public void setMm_emp_ad_url(String mm_emp_ad_url) {
        this.mm_emp_ad_url = mm_emp_ad_url;
    }

    public String getMm_emp_ad_id() {
        return mm_emp_ad_id;
    }

    public void setMm_emp_ad_id(String mm_emp_ad_id) {
        this.mm_emp_ad_id = mm_emp_ad_id;
    }

    public String getMm_emp_ad_pic() {
        return mm_emp_ad_pic;
    }

    public void setMm_emp_ad_pic(String mm_emp_ad_pic) {
        this.mm_emp_ad_pic = mm_emp_ad_pic;
    }

    public String getMm_emp_id() {
        return mm_emp_id;
    }

    public void setMm_emp_id(String mm_emp_id) {
        this.mm_emp_id = mm_emp_id;
    }

    public String getMm_emp_ad_num() {
        return mm_emp_ad_num;
    }

    public void setMm_emp_ad_num(String mm_emp_ad_num) {
        this.mm_emp_ad_num = mm_emp_ad_num;
    }

    public EmpAdObj() {
    }

    public EmpAdObj(String mm_emp_ad_pic, String mm_emp_ad_url) {
        this.mm_emp_ad_pic = mm_emp_ad_pic;
        this.mm_emp_ad_url = mm_emp_ad_url;
    }
}
