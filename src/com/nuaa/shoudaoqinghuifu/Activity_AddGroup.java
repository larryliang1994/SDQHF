package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_AddGroup extends AppCompatActivity {
    @Bind(R.id.editText_addgroup_name)
    EditText et_name;

    @Bind(R.id.listView_addgroup)
    ListView lv_addgroup;

    @Bind(R.id.toolbar_addgroup)
    Toolbar tb_addgroup;

    private boolean isModify = false;
    private float startX = 0.0f; // 起始坐标
    private String[] names = null;
    private ArrayList<String> phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onSetTheme(this, "group");

        setContentView(R.layout.xml_addgroup);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_other, menu);
        return true;
    }

    public void initView() {
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
                        } else if (names == null || names.length == 0) {
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
        }
    }

    // 自定义单击事件
    @OnClick({R.id.editText_addgroup_member, R.id.imageView_addgroup_addmember})
    public void onClick(View v) {
        Intent openActivity;

        switch (v.getId()) {
            case R.id.imageView_addgroup_addmember:
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

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        lv_addgroup.setAdapter(new MyAdapter());
    }

    // 重写返回结果方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // 返回结果适用对象为选择成员
            case Value.CHOOSE_MEMBER:
                if (resultCode == RESULT_OK) {
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

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names == null ? 0 : names.length;
        }

        @Override
        public Object getItem(int position) {
            return names == null ? null : names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.xml_addgroup_item, null);

                holder = new ViewHolder();
                holder.tv_photo = (TextView) convertView.findViewById(R.id.textView_addgroup_item_photo);
                holder.tv_name = (TextView) convertView.findViewById(R.id.textView_addgroup_item_name);
                holder.tv_phone = (TextView) convertView.findViewById(R.id.textView_addgroup_item_phone);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置头像颜色
            GradientDrawable bgShape = (GradientDrawable) holder.tv_photo.getBackground();
            bgShape.setColor(getResources().getColor(Value.colors[position % Value.colors.length]));

            holder.tv_photo.setText(names[position].substring(0, 1));
            holder.tv_name.setText(names[position]);

            holder.tv_phone.setText(phones.get(position).replace(" ", ""));

            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_photo = null;
        TextView tv_name = null;
        TextView tv_phone = null;
    }

}
