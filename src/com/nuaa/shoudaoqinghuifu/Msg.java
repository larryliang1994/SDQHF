package com.nuaa.shoudaoqinghuifu;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Msg implements Serializable {
    public String name;
    public String content;
    public MyDate sendtime;

    public Msg(String name, String content, MyDate sendtime) {
        super();
        this.name = name;
        this.content = content;
        this.sendtime = sendtime;
    }

    public Msg() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSendtime(MyDate sendtime) {
        this.sendtime = sendtime;
    }

    public String getSendtime(){
        return sendtime.toSave();
    }

    @Override
    public String toString() {
        return name + "#!#" + content + "#!#" + sendtime + "#!#";
    }

    // 保存时的格式
    public String toSave() {
        return name + "#!#" + content + "#!#" + sendtime.toSave() + "#!#";
    }
}
