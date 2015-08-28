package com.nuaa.shoudaoqinghuifu;

public class Value {
    public static final String ACTION_ALARM = "com.nuaa.action.alarm";
    public static final String ACTION_SENDMSG = "com.nuaa.action.sendmsg";

    public static final int CHOOSE_CONTACTS = 1;
    public static final int CHOOSE_MEMBER = 2;
    public static final int ADD_GROUP = 3;
    public static final int ADD_MEMO = 4;
    public static final int MODIFY_MEMO = 5;
    public static final int LOAD_IMAGE = 6;
    public static final int MODIFY_GROUP = 7;
    public static final int ADD_MSG = 8;
    public static final int ADD_TEMP = 9;
    public static final int MODIFY_TEMP = 10;
    public static final int CHECK_MEMO = 11;

    public static final String CREATE_TBL_MSG = " create table if not exists"
            + " MsgTbl(_id integer primary key autoincrement,name text,content text, sendtime text) ";
    public static final String CREATE_TBL_GROUP = " create table if not exists"
            + " GroupTbl(_id integer primary key autoincrement,name text,members text) ";
    public static final String CREATE_TBL_MEMO = " create table if not exists"
            + " MemoTbl(_id integer primary key autoincrement,content text,address text, date_happen text, date_memo text, needNotify text, id integer) ";
    public static final String CREATE_TBL_TEMP = " create table if not exists"
            + " TempTbl(_id integer primary key autoincrement,title text,content text) ";

    public static final String SHAER_TEXT = "为用户提供通知模板功能，让部门高管不再需要独自编写短信、逐个选择联系人进行通知；提供备忘录功能，让你不再忘记任何重要事情；点此下载【收到请回复】测试版 http://20055.jiubai.cc/uploadfile/webeditor2/android/ShouDaoQingHuiFu-debug.apk";
    public static final String[] colors = {"#E51C23", "#259b24", "#9c27b0", "#FF9800", "#9e9e9e", "#5677fc"};
    public static final String[] titles = {"开会通知1", "开会通知2", "比赛通知", "聚会通知", "出游通知", "活动通知"};
    public static final String[] temps = {
            "又临近熟悉的时间，就携手来到熟悉的地点，让我们坐下畅谈。例会将于11.13(周四)晚九点于东区值班室举行。(如确有不能到场的原因，请在短信中说明，例会乃我部集体之活动，请假需谨慎。)",
            "通知：3月20日下午2点整某单位在会议室召开全体职工大会,讨论单位年终工作事项，全体人员务必准时参加，不可迟到，自带笔纸记录，收到请回复，大家互相转告。某单位办公室。",
            "亲爱的各位参赛者，大家好。本活动将定于本月（或12月）15日上午开赛，请各位参赛者务必提前抵达会场，以免耽误您的比赛。特此短信通知。请收到短信的参赛者相互转告。谢谢  参赛办",
            "亲爱的同学，同窗四载，温馨如昨，依然常驻心头；悲欢岁月，依稀似梦，但愿记得你我！xx年x月x日让我们重新在xx地相聚，你会来吗？一双双一如当年热切而期盼的眼睛，一颗颗一如当年火热而年轻的心在等待……",
            "筒子们注意啦！鉴于前两天方山的实地考察结果不尽人意，经过与南医服务中心的协商，我们决定将秋游地点改为将军山，出游时间依旧是11月23号9:00——16:00，请各位同学于当天上午8：30在西区事服大厅集合，记得要吃早饭哦。秋游的费用为50元/人，多余的费用将会退还给大家。",
            "三周年宣传活动正式开始啦，在全校的道杆上挂上咱们的任务卷轴（卷轴内容为与事服相关的问题（答对有奖）与三周年系列活动预告），今晚九点半到事服集合哦～事服人倾巢而出去挂卷轴啦"};
}
