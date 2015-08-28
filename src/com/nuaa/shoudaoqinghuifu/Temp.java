package com.nuaa.shoudaoqinghuifu;

import java.io.Serializable;

/**
 * Created by howell on 2015/8/17.
 */
public class Temp implements Serializable{
    public String title;
    public String content;

    public Temp() {
    }

    public Temp(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toSave(){
        return title + "#!#" + content + "#!#";
    }
}
