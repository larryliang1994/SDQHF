package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_CheckMsgList extends AppCompatActivity {
    @Bind(R.id.listView_checkList)
    ListView lv_checkList;

    @Bind(R.id.toolbar_checklist)
    Toolbar tb_checkList;

    private static final String NAME = "name";
    private List<ContentValues> list = new ArrayList<>();
    private BaseAdapter adapter;
    private Vector<Boolean> isChecked = new Vector<>(); // 保存item的选取情况
    private Msg msg;
    private boolean isGroup;
    private String msg_name = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onSetTheme(this, "msg");

        setContentView(R.layout.xml_checkmsg_list);

        ButterKnife.bind(this);

        initView();
        initData();
        setListViewAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_other, menu);
        return true;
    }

    //根据isGroup获取数据
    private void initData() {
        list.clear();
        if (!isGroup) {
            String[] names = msg.getName().split(",");
            for (String m_name : names) {
                ContentValues cv = new ContentValues();
                cv.put(NAME, m_name);
                list.add(cv);
            }
        } else {
            String[] names = msg.getName().split("@@@");
            String g_name = names[1];
            String[] g_names = g_name.split(",");
            for (String group_name : g_names) {
                ContentValues cv = new ContentValues();
                String gn = group_name.substring(1);
                cv.put(NAME, gn);
                list.add(cv);
            }
        }
        adapter = new ListAdapter(this);
        if (list.size() > 0) {
            lv_checkList.setAdapter(adapter);
            String[] names = getIntent().getStringArrayExtra("names");
            // 初始化勾选
            if (!list.isEmpty() && names != null) {
                int i = 0, j = 0;
                while (j < names.length && i < list.size()) {
                    if (names[j].equals(list.get(i).getAsString(NAME))) {
                        isChecked.set(i, true);
                        j++;
                    }
                    i++;
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //列表动画
    private void setListViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(500);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.3f);

        lv_checkList.setLayoutAnimation(lac);
    }

    private void initView() {
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        msg = (Msg) getIntent().getSerializableExtra("Msg");

        setSupportActionBar(tb_checkList);
        tb_checkList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_CheckMsgList.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        tb_checkList.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_ok:
                        Intent backIntent = getIntent();
                        if (isGroup) {
                            String[] name = msg.getName().split("@@@");
                            String g_name = name[1];
                            String[] g_names = g_name.split(",");
                            String gn = "";
                            for (int i = 0; i < g_names.length; i++) {
                                if (isChecked.get(i)) {
                                    g_names[i] = g_names[i].substring(1);
                                    msg_name += g_names[i] + ",";
                                    g_names[i] = "1" + g_names[i];

                                } else {
                                    g_names[i] = g_names[i].substring(1);
                                    g_names[i] = "0" + g_names[i];
                                }
                                if (i != g_names.length - 1) {
                                    gn += g_names[i] + ",";
                                } else {
                                    gn += g_names[i];
                                }
                            }
                            gn = name[0] + "@@@" + gn;
                            Msg new_msg = new Msg();
                            new_msg.setName(gn);
                            new_msg.setSendtime(msg.sendtime);
                            new_msg.setContent(msg.content);

                            //写数据库
                            DBHelper helper = new DBHelper(Activity_CheckMsgList.this, "MsgTbl");
                            helper.creatTable();
                            Cursor cursor = helper.query();
                            cursor.moveToFirst();
                            for (int i = 0; i < cursor.getCount(); i++) {
                                String M_name = cursor.getString(1);
                                String[] M_names = M_name.split("@@@");
                                if (name[0].equals(M_names[0])) {
                                    helper.update(String.valueOf(cursor.getInt(0)), "msg", new_msg);
                                    helper.close();
                                    break;
                                } else {
                                    cursor.moveToNext();
                                }
                            }
                            backIntent.putExtra("msg_name", msg_name);
                        } else {
                            String[] m_name = msg.getName().split(",");
                            String mn = "";
                            for (int i = 0; i < m_name.length; i++) {
                                if (isChecked.get(i)) {
                                    msg_name += m_name[i] + ",";
                                    m_name[i] = "1" + m_name[i];

                                } else {
                                    m_name[i] = "0" + m_name[i];
                                }
                                if (i != m_name.length - 1) {
                                    mn += m_name[i] + ",";
                                } else {
                                    mn += m_name[i];
                                }
                            }
                            Msg new_msg = new Msg();
                            new_msg.setName(mn);
                            new_msg.setContent(msg.content);
                            new_msg.setSendtime(msg.sendtime);

                            //写数据库
                            DBHelper helper = new DBHelper(Activity_CheckMsgList.this, "MsgTbl");
                            helper.creatTable();
                            Cursor cursor = helper.query();
                            cursor.moveToFirst();
                            for (int i = 0; i < cursor.getCount(); i++) {
                                String content = cursor.getString(2);
                                if (content.equals(msg.content)) {
                                    helper.update(String.valueOf(cursor.getInt(0)), "msg", new_msg);
                                    helper.close();
                                    break;
                                } else {
                                    cursor.moveToNext();
                                }
                            }
                            backIntent.putExtra("msg_name", msg_name);
                        }

                        // 结果编码
                        Activity_CheckMsgList.this.setResult(RESULT_OK, backIntent);
                        Activity_CheckMsgList.this.finish();
                        overridePendingTransition(R.anim.scale_stay,
                                R.anim.out_left_right);

                        Toast.makeText(Activity_CheckMsgList.this, "已保存", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        // 初始化
        for (int i = 0; i < 500; i++) {
            isChecked.add(false);
        }

        lv_checkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 每次点击该项，CheckBox的内容就取反
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                isChecked.set(position, !isChecked.get(position)); // 取反
                // 并且刷新适配器
                adapter.notifyDataSetChanged();
            }
        });

    }

    // 按下返回键返回上层
    @SuppressLint("DefaultLocale")
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent backIntent = getIntent();

            // 设置返回的结果
            backIntent.putExtra("checklist", "");

            // 结果编码
            this.setResult(RESULT_CANCELED, backIntent);

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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
                convertView = inflater.inflate(R.layout.xml_checklist_item, null);
                holder = new ViewHolder();

                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.textView_checklist_item_name);
                holder.tv_photo = (TextView) convertView
                        .findViewById(R.id.textView_checklist_item_photo);
                holder.cb_isChosen = (CheckBox) convertView
                        .findViewById(R.id.checkBox_checklist_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ContentValues cv = list.get(position);

            holder.tv_name.setText(cv.getAsString(NAME));

            holder.cb_isChosen.setChecked(isChecked.get(position));

            if (isGroup) {
                holder.tv_photo.setText(cv.getAsString(NAME).substring(0, 1));
            } else {
                holder.tv_photo.setText(cv.getAsString(NAME).substring(0, 1));
            }

            // 设置头像颜色
            GradientDrawable bgShape = (GradientDrawable) holder.tv_photo.getBackground();
            bgShape.setColor(getResources().getColor(Value.colors[position % Value.colors.length]));

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tv_photo = null;
        TextView tv_name = null;
        CheckBox cb_isChosen = null;
    }
}
