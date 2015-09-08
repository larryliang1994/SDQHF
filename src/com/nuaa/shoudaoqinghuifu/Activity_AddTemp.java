package com.nuaa.shoudaoqinghuifu;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
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

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_AddTemp extends AppCompatActivity implements View.OnTouchListener {
    @Bind(R.id.editText_addtemp_title)
    EditText edt_title;

    @Bind(R.id.editText_addtemp_content)
    EditText edt_content;

    @Bind(R.id.toolbar_addtemp)
    Toolbar tb_addtemp;

    private DBHelper helper = new DBHelper(this, "TempTbl");
    private boolean isModify = false;
    private int position;
    private float startX = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onSetTheme(this, "temp");

        setContentView(R.layout.xml_addtemp);

        ButterKnife.bind(this);

        initView();

        helper.creatTable();

        position = getIntent().getIntExtra("position", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_other, menu);
        return true;
    }

    public void initView() {
        setSupportActionBar(tb_addtemp);
        tb_addtemp.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                Activity_AddTemp.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        tb_addtemp.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_ok:
                        String title = edt_title.getText().toString();
                        String content = edt_content.getText().toString();

                        if (title.isEmpty()) {
                            Toast.makeText(Activity_AddTemp.this, "请输入模板标题", Toast.LENGTH_SHORT).show();
                        } else if (content.isEmpty()) {
                            Toast.makeText(Activity_AddTemp.this, "请输入模板内容", Toast.LENGTH_SHORT).show();
                        } else {
                            Temp temp = new Temp(title, content);
                            if (isModify && position != -1) {
                                // 更新数据库
                                Cursor cursor = helper.query();
                                cursor.moveToPosition(position);
                                helper.update(String.valueOf(cursor.getInt(0)), "temp", temp);
                                helper.close();

                                Toast.makeText(Activity_AddTemp.this, "修改成功", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.putExtra("position", position);
                                setResult(RESULT_OK, intent);
                                Activity_AddTemp.this.finish();
                                overridePendingTransition(R.anim.scale_stay,
                                        R.anim.out_left_right);
                            } else {
                                // 插入数据库
                                helper.insert("temp", temp);
                                helper.close();

                                Toast.makeText(Activity_AddTemp.this, "制作成功", Toast.LENGTH_SHORT).show();

                                setResult(RESULT_OK);
                                Activity_AddTemp.this.finish();
                                overridePendingTransition(R.anim.scale_stay,
                                        R.anim.out_left_right);
                            }
                        }
                        break;
                }
                return true;
            }
        });


        // 来自修改
        Temp temp = (Temp) getIntent().getSerializableExtra("temp");
        if (temp != null) {
            isModify = true;
        }

        // 来自修改，则初始化内容
        if (isModify) {
            assert temp != null;
            edt_title.setText(temp.title);
            edt_content.setText(temp.content);
        }

        edt_title.setOnTouchListener(this);
        edt_content.setOnTouchListener(this);
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

            setResult(RESULT_CANCELED);
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
                && event.getX() - startX > 100) {

            setResult(RESULT_CANCELED);
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        }
        // 松开且为从左向右滑
        else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {

            setResult(RESULT_CANCELED);
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }
}
