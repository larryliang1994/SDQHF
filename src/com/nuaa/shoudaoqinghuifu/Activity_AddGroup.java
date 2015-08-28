package com.nuaa.shoudaoqinghuifu;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_AddGroup extends AppCompatActivity {
    @Bind(R.id.editText_addgroup_member)
    EditText et_member;

    @Bind(R.id.editText_addgroup_name)
    EditText et_name;

    @Bind(R.id.toolbar_addgroup)
    Toolbar tb_addgroup;

    boolean isModify = false;
    float startX = 0.0f; // 起始坐标
    private String[] names = null;
    private ArrayList<String> phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_addgroup);

        ButterKnife.bind(this);









        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_other, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#0288d1"));
        }

        setSupportActionBar(tb_addgroup);
        tb_addgroup.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = getIntent();
                // 设置返回的结果
                backIntent.putExtra("contacts", "");

                // 结果编码
                Activity_AddGroup.this.setResult(RESULT_CANCELED, backIntent);

                Activity_AddGroup.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        tb_addgroup.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent backIntent = getIntent();
                switch (item.getItemId()) {
                    case R.id.item_ok:
                        if (et_name.getText().toString().isEmpty()) {
                            Toast.makeText(Activity_AddGroup.this, "请输入群组名", Toast.LENGTH_SHORT).show();
                        } else if (et_member.getText().toString().isEmpty()) {
                            Toast.makeText(Activity_AddGroup.this, "至少添加一个群组成员", Toast.LENGTH_SHORT).show();
                        } else {
                            // 获取当前群组属性
                            Group tmpGroup = new Group();
                            tmpGroup.setName(et_name.getText().toString());
                            ArrayList<Member> members_r = new ArrayList<>();

                            for (int i = 0; i < names.length; i++) {
                                members_r.add(new Member(names[i], phones.get(i)));
                            }

                            tmpGroup.setMembers(members_r);

                            DBHelper helper = new DBHelper(Activity_AddGroup.this, "GroupTbl");
                            helper.creatTable();
                            int position = getIntent().getIntExtra("position", -1);
                            if (isModify && position != -1) {
                                // 若来自修改，则直接update
                                Cursor cursor = helper.query();
                                cursor.moveToPosition(position);
                                helper.update(String.valueOf(cursor.getInt(0)), "group", tmpGroup);
                                helper.close();

                                backIntent.putExtra("group", tmpGroup);
                                backIntent.putExtra("position", position);

                                Toast.makeText(Activity_AddGroup.this, "修改完成", Toast.LENGTH_SHORT).show();
                            } else {
                                // 否则插入数据库
                                helper.insert("group", tmpGroup);
                                helper.close();

                                Toast.makeText(Activity_AddGroup.this, "创建完成", Toast.LENGTH_SHORT).show();
                            }

                            // 结果编码
                            Activity_AddGroup.this.setResult(RESULT_OK, backIntent);

                            Activity_AddGroup.this.finish();

                            Activity_AddGroup.this.overridePendingTransition(R.anim.scale_stay, R.anim.out_left_right);
                        }
                        break;
                }
                return true;
            }
        });


        Group group = (Group) getIntent().getSerializableExtra("group");
        if (group != null) {
            isModify = true;
        }

        // 若是来自修改群组，则初始化内容及names和phones
        if (isModify) {
            assert group != null;
            et_name.setText(group.name);

            String name = "";
            if (phones == null) {
                phones = new ArrayList<>();
            } else {
                phones.clear();
            }

            for (int i = 0; i < group.members.size(); i++) {
                Member member = group.members.get(i);
                name += member.name;
                name += ";";

                phones.add(member.phone);
            }

            names = name.split(";");
            et_member.setText(name);

        }
    }

    // 自定义单击事件
    @OnClick({R.id.editText_addgroup_member})
    public void onClick(View v) {
        Intent openActivity;

        switch (v.getId()) {

            case R.id.editText_addgroup_member:

                openActivity = new Intent(this, Activity_Contacts.class);
                openActivity.putExtra("names", names);
                startActivityForResult(openActivity, Value.CHOOSE_MEMBER);
                overridePendingTransition(R.anim.in_right_left,
                        R.anim.scale_stay);
                break;

            default:
                break;
        }
    }

    // 重写返回结果方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // 返回结果适用对象为选择成员
            case Value.CHOOSE_MEMBER:
                if (resultCode == RESULT_OK) {
                    et_member.setText(data.getStringExtra("names"));

                    names = data.getStringExtra("names").split("; ");
                    phones = data.getStringArrayListExtra("phones");
                }
                break;

            default:
                break;
        }
    }

    // 按下返回键返回上层
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent backIntent = getIntent();

            // 结果编码
            this.setResult(RESULT_CANCELED, backIntent);

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        }
        // 松开且为从左向右滑
        else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {
            Intent backIntent = getIntent();

            // 结果编码
            this.setResult(RESULT_CANCELED, backIntent);

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }

}
