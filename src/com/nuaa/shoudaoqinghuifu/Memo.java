package com.nuaa.shoudaoqinghuifu;

import android.support.annotation.NonNull;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Memo implements Serializable, Comparable<Memo> {
    public String content;
    public String address;
    public MyDate date_happen;
    public MyDate date_memo;
    public boolean needNotify;
    public int id;

    public Memo(String content, String address, MyDate date_happen,
                MyDate date_memo, boolean needNotify, int id) {
        super();
        this.content = content;
        this.address = address;
        this.date_happen = date_happen;
        this.date_memo = date_memo;
        this.needNotify = needNotify;
        this.id = id;
    }

    public Memo() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MyDate getDate_happen() {
        return date_happen;
    }

    public void setDate_happen(MyDate date_happen) {
        this.date_happen = date_happen;
    }

    public MyDate getDate_memo() {
        return date_memo;
    }

    public void setDate_memo(MyDate date_memo) {
        this.date_memo = date_memo;
    }

    public boolean isNeedNotify() {
        return needNotify;
    }

    public void setNeedNotify(boolean needNotify) {
        this.needNotify = needNotify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return content + "#!#" + address + "#!#" + date_happen.toString()
                + "#!#" + date_memo + "#!#" + needNotify;
    }

    // 保存时的格式
    public String toSave() {
        return content + "#!#" + address + "#!#" + date_happen.toSave() + "#!#"
                + date_memo.toSave() + "#!#" + needNotify + "#!#" + id + "#!#";
    }

    @Override
    public int compareTo(@NonNull Memo another) {

        if (this.date_happen.compareTo(another.date_happen) == -1) {
            return 1;
        } else if (this.date_happen.compareTo(another.date_happen) == 1) {
            return -1;
        }

        return 0;
    }
}
