package com.Lbins.Mlt.module;

/**
 * Created by Administrator on 2016/3/27 0027.
 */
public class NewsClassify {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NewsClassify(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
