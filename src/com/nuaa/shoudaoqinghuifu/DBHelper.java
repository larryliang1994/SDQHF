package com.nuaa.shoudaoqinghuifu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by howell on 2015/8/25.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sdqhf.db";
    private String TBL_NAME = null;

    private SQLiteDatabase db;

    public DBHelper(Context context, String tableName) {
        // 2表示版本号
        super(context, DB_NAME, null, 2);

        TBL_NAME = tableName;
    }

    public void creatTable(){
        SQLiteDatabase db = getWritableDatabase();
        switch (TBL_NAME){
            case "MsgTbl":
                db.execSQL(Value.CREATE_TBL_MSG);
                break;

            case "GroupTbl":
                db.execSQL(Value.CREATE_TBL_GROUP);
                break;

            case "MemoTbl":
                db.execSQL(Value.CREATE_TBL_MEMO);
                break;

            case "TempTbl":
                db.execSQL(Value.CREATE_TBL_TEMP);
                break;
        }
    }

    // 创建数据库后调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
    }

    // 插入
    public void insert(String type, Object item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        switch (type){
            case "msg":
                Msg msg = (Msg)item;
                values.put("name", msg.getName());
                values.put("content", msg.getContent());
                values.put("sendtime", msg.getSendtime());
                break;

            case "group":
                Group group = (Group)item;
                values.put("name", group.getName());
                values.put("members", group.getMembers());
                break;

            case "memo":
                Memo memo = (Memo)item;
                values.put("content", memo.getContent());
                values.put("address", memo.getAddress());
                values.put("date_happen", memo.getDate_happen().toSave());
                values.put("date_memo", memo.getDate_memo().toSave());
                values.put("needNotify", memo.isNeedNotify()+"");
                values.put("id", memo.getId());
                break;

            case "temp":
                Temp temp = (Temp)item;
                values.put("title", temp.getTitle());
                values.put("content", temp.getContent());
                break;
        }

        db.insert(TBL_NAME, null, values);
        db.close();
    }

    public Cursor query() {
        SQLiteDatabase db = getReadableDatabase();
        /**
         参数1：表名
         参数2：返回数据包含的列信息，String数组里放的都是列名
         参数3：相当于sql里的where，sql里where后写的内容放到这就行了，例如：tage>?
         参数4：如果你在参数3里写了?（知道我为什么写tage>?了吧），那个这里就是代替?的值 接上例：new String[]{"30"}
         参数5：分组，不解释了，不想分组就传null
         参数6：having，想不起来的看看SQL
         参数7：orderBy排序
         */
        return db.query(TBL_NAME, null, null, null, null, null, null);
    }

    // 删除
    public void delete(int id) {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.delete(TBL_NAME, "_id=?", new String[]{String.valueOf(id)});
    }

    // 更新
    public void update(String _id, String type, Object item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        switch (type){
            case "msg":
                Msg msg = (Msg)item;
                values.put("name", msg.getName());
                values.put("content", msg.getContent());
                values.put("sendtime", msg.getSendtime());
                break;

            case "group":
                Group group = (Group)item;
                values.put("name", group.getName());
                values.put("members", group.getMembers());
                break;

            case "memo":
                Memo memo = (Memo)item;
                values.put("content", memo.getContent());
                values.put("address", memo.getAddress());
                values.put("date_happen", memo.getDate_happen().toSave());
                values.put("date_memo", memo.getDate_memo().toSave());
                values.put("needNotify", memo.isNeedNotify()+"");
                values.put("id", memo.getId());
                break;

            case "temp":
                Temp temp = (Temp)item;
                values.put("title", temp.getTitle());
                values.put("content", temp.getContent());
                break;
        }

        db.update(TBL_NAME, values, "_id=?", new String[]{_id});
    }

    // 清空
    public void clear(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_NAME);
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    // 更改数据库版本的操作
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table " + TBL_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
