package com.nuaa.shoudaoqinghuifu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_CheckMsg extends AppCompatActivity {
    @Bind(R.id.textView_checkmsg_content)
    TextView tv_content;

    @Bind(R.id.textView_checkmsg_sendtime)
    TextView tv_sendtime;

    @Bind(R.id.toolbar_checkmsg)
    Toolbar tb_checkmsg;

    private float startX = 0.0f;
    private Msg msg;
    private boolean isGroup = false;
    private String[] names = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onSetTheme(this, "msg");

        setContentView(R.layout.xml_checkmsg);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_msg, menu);
        return true;
    }

    private void initView() {
        setSupportActionBar(tb_checkmsg);
        tb_checkmsg.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_CheckMsg.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });

        msg = (Msg) getIntent().getSerializableExtra("msg");
        tv_content.setText(msg.content);
        tv_sendtime.setText(msg.sendtime.toString());
        tb_checkmsg.setTitle(msg.name);

        //处理否来自群组的信息
        String msg_name = msg.name;
        DBHelper helper = new DBHelper(Activity_CheckMsg.this, "MsgTbl");
        helper.creatTable();
        Cursor cursor = helper.query();
        cursor.moveToFirst();

        //名字相同则说明来自群组
        for (int i = 0; i < cursor.getCount(); i++) {
            String M_name = cursor.getString(1);
            String[] M_names = M_name.split("@@@");
            if (msg_name.equals(M_names[0])) {
                msg.setName(M_name);
                isGroup = true;
                break;
            } else {
                cursor.moveToNext();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_check:
                Intent intent = new Intent(Activity_CheckMsg.this, Activity_CheckMsgList.class);
                intent.putExtra("names", names);
                intent.putExtra("Msg", msg);
                intent.putExtra("isGroup", isGroup);
                startActivityForResult(intent, Value.CHECK_MEMBER);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    // 按下返回键返回上层
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 按下
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        }
        // 松开且为从左向右滑
        else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }

    // 重写返回结果方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // 返回结果适用对象为选择成员
            case Value.CHECK_MEMBER:
                if (resultCode == RESULT_OK) {
                    names = data.getStringExtra("msg_name").split(",");
                }
                break;

            default:
                break;
        }
    }
}
