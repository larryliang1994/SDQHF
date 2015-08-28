package com.nuaa.shoudaoqinghuifu;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Member implements Serializable {
    // 将内存中的对象保存到硬盘中的时候需要实现序列化
    public String name;
    public String phone;

    public Member(String name, String phone) {
        super();
        this.name = name;
        this.phone = phone;
    }

    // 保存时的格式
    public String toSave() {
        return name + ":" + phone + "#!#";
    }

    @Override
    public String toString() {
        return name + ":" + phone;
    }

}
