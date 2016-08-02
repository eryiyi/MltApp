package com.Lbins.Mlt.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/24 0024.
 */
public class GuanzhuAreaObj implements Serializable {
    private String mm_guanzhu_id;
    private String mm_emp_id;
    private String areaid;
    private String area_name;
    private String reg_time;
    private String ischeck;
    private String accessToken;

    public String getMm_guanzhu_id() {
        return mm_guanzhu_id;
    }

    public void setMm_guanzhu_id(String mm_guanzhu_id) {
        this.mm_guanzhu_id = mm_guanzhu_id;
    }

    public String getMm_emp_id() {
        return mm_emp_id;
    }

    public void setMm_emp_id(String mm_emp_id) {
        this.mm_emp_id = mm_emp_id;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
