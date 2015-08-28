package com.nuaa.shoudaoqinghuifu;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class Group implements Serializable {
    public String name;
    public ArrayList<Member> members = new ArrayList<>();

    public Group() {

    }

    public Group(String name, ArrayList<Member> members) {
        super();
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public String getMembers(){
        String member = "";

        for (int i = 0; i < members.size(); i++) {
            member += members.get(i).toSave();
        }

        return member;
    }

    @Override
    public String toString() {
        return name + "#!#" + members.toString();
    }

    // 保存时的格式
    public String toSave() {
        String text = name;
        text += "#!#";

        for (int i = 0; i < members.size(); i++) {
            text += members.get(i).toSave();
        }
        text = text + "@@@";
        return text;
    }
}
