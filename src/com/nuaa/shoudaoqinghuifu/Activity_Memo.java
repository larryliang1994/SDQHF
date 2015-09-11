package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class Activity_Memo extends AppCompatActivity {
    @Bind(R.id.listView_memo)
    ListView lv_memo;

    @Bind(R.id.textView_memo_empty)
    TextView tv_empty;

    @Bind(R.id.toolbar_memo)
    Toolbar tb_memo;

    @Bind(R.id.navigationView_memo)
    NavigationView nv_memo;

    @Bind(R.id.drawerLayout_memo)
    DrawerLayout dw_memo;

    public static Vector<Memo> vector_memos = new Vector<>();
    public static boolean needNotify = false;
    public static boolean needSort = false;
    private boolean isBroadcast = false;
    private NotificationManager notificationManager;
    private DBHelper helper = new DBHelper(this, "MemoTbl");
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onSetTheme(this, "memo");

        setContentView(R.layout.xml_memo);

        ButterKnife.bind(this);

        helper.creatTable();

        readDataBase();

        Memo memo = (Memo) getIntent().getSerializableExtra("memo");
        if (memo != null) {
            vector_memos.add(memo);

            // 排序——清空表——写入表——刷新
            reflash();
        }

        if (getIntent().getBooleanExtra("isBroadcast", false)) {
            isBroadcast = true;
        }

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_main, menu);
        return true;
    }

    private void initView() {
        setSupportActionBar(tb_memo);
        tb_memo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dw_memo.openDrawer(GravityCompat.START);
            }
        });
        tb_memo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_add:
                        Intent intent = new Intent(Activity_Memo.this, Activity_AddMemo.class);
                        startActivityForResult(intent, Value.ADD_MEMO);
                        overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                        break;
                }
                return true;
            }
        });

        nv_memo.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
        nv_memo.setItemIconTintList(null);
        nv_memo.getMenu().getItem(2).setChecked(true);
        nv_memo.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                dw_memo.closeDrawer(GravityCompat.START);
                switch (menuItem.getItemId()) {
                    case R.id.navItem_msg:
                        intent = new Intent(Activity_Memo.this, Activity_Msg.class);
                        Activity_Memo.this.finish();
                        break;

                    case R.id.navItem_group:
                        intent = new Intent(Activity_Memo.this, Activity_Group.class);
                        Activity_Memo.this.finish();
                        break;

                    case R.id.navItem_memo:
                        intent = null;
                        break;

                    case R.id.navItem_temp:
                        intent = new Intent(Activity_Memo.this, Activity_Temp.class);
                        Activity_Memo.this.finish();
                        break;

                    case R.id.navItem_setting:
                        intent = new Intent(Activity_Memo.this, Activity_Preference.class);
                        Activity_Memo.this.finish();
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                }

                return true;
            }
        });

        lv_memo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Activity_Memo.this,
                                Activity_CheckMemo.class);
                        intent.putExtra("memo", vector_memos.get(position));
                        intent.putExtra("position", position);
                        startActivityForResult(intent, Value.CHECK_MEMO);
                        overridePendingTransition(R.anim.in_right_left,
                                R.anim.out_right_left);
                    }
                }, 300);
            }
        });

        lv_memo.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int p, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Activity_Memo.this);

                final int position = p;

                final String[] names = {"修改", "删除", "设为常驻提醒", "取消常驻提醒"};

                builder.setItems(names, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 修改
                                Intent intent = new Intent(Activity_Memo.this,
                                        Activity_AddMemo.class);
                                intent.putExtra("memo", vector_memos.get(position));
                                intent.putExtra("position", position);
                                startActivityForResult(intent, Value.MODIFY_MEMO);
                                overridePendingTransition(R.anim.in_right_left,
                                        R.anim.out_right_left);
                                break;

                            case 1:  // 删除
                                final MaterialDialog mDialog = new MaterialDialog(Activity_Memo.this)
                                        .setCanceledOnTouchOutside(true)
                                        .setTitle("删除")
                                        .setMessage("真的要删除该备忘事件吗？");
                                mDialog.setPositiveButton("真的",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (position != -1) {

                                                    if (Activity_Memo.vector_memos
                                                            .get(position).needNotify) {
                                                        // 删除该闹钟
                                                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                        Intent myIntent = new Intent(
                                                                getApplicationContext(),
                                                                BroadcastReceiver_Alarm.class);
                                                        myIntent.setAction(Value.ACTION_ALARM);
                                                        PendingIntent pi = PendingIntent
                                                                .getBroadcast(
                                                                        getApplicationContext(),
                                                                        Activity_Memo.vector_memos
                                                                                .get(position).id,
                                                                        myIntent,
                                                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                                        am.cancel(pi);
                                                    }

                                                    // 可能为常驻提醒，cancel掉
                                                    notificationManager = (NotificationManager) Activity_Memo.this
                                                            .getSystemService(Context.NOTIFICATION_SERVICE);
                                                    notificationManager.cancel(vector_memos.get(position).id);

                                                    // 设置退出动画
                                                    setListViewAnimation();

                                                    // 刷新页面
                                                    Cursor cursor = helper.query();
                                                    cursor.moveToPosition(position);
                                                    helper.delete(cursor.getInt(0));
                                                    vector_memos.remove(position);
                                                    setAdapter();

                                                    Toast.makeText(
                                                            Activity_Memo.this,
                                                            "已删除",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {
                                                    Toast.makeText(
                                                            Activity_Memo.this,
                                                            "Oops...好像出错了，再来一次试试？",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                                mDialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("假的", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mDialog.dismiss();
                                            }
                                        });
                                mDialog.show();
                                break;

                            case 2:  // 设为常驻
                                NotificationCompat.Builder ncBuilder = new NotificationCompat.Builder(
                                        Activity_Memo.this);
                                Memo ncMemo = vector_memos.get(position);
                                ncBuilder.setContentTitle(ncMemo.content);
                                ncBuilder.setSmallIcon(R.drawable.icon_outside);
                                ncBuilder.setContentText(ncMemo.address);

                                // 设置为常驻通知
                                ncBuilder.setOngoing(true);

                                // 不设置为自动清除
                                ncBuilder.setAutoCancel(false);

                                Notification n = ncBuilder.build();
                                notificationManager = (NotificationManager) Activity_Memo.this
                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(ncMemo.id, n);

                                Toast.makeText(Activity_Memo.this, "设置成功",
                                        Toast.LENGTH_SHORT).show();
                                break;

                            case 3:  // 取消常驻
                                notificationManager = (NotificationManager) Activity_Memo.this
                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                                Memo memo = vector_memos.get(position);
                                notificationManager.cancel(memo.id);
                                Toast.makeText(Activity_Memo.this, "已取消",
                                        Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();
                return false;
            }

        });

    }

    // 从数据库中读取数据
    private void readDataBase() {
        vector_memos.clear();

        Cursor cursor = helper.query();

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Memo memo = readDataWithCursor(cursor);

            vector_memos.add(memo);

            cursor.moveToNext();
        }

        setAdapter();
    }

    // 读取一条备忘
    private Memo readDataWithCursor(Cursor cursor) {
        String content = cursor.getString(1);
        String address = cursor.getString(2);
        String date_happen_s = cursor.getString(3);
        String date_memo_s = cursor.getString(4);
        String needNotify_s = cursor.getString(5);
        int id = cursor.getInt(6);

        String[] date_happen_array = date_happen_s.split("-");
        String[] date_memo_array = date_memo_s.split("-");

        MyDate date_happen = new MyDate(
                Integer.parseInt(date_happen_array[0]),
                Integer.parseInt(date_happen_array[1]),
                Integer.parseInt(date_happen_array[2]),
                Integer.parseInt(date_happen_array[3]),
                Integer.parseInt(date_happen_array[4]));

        MyDate date_memo = new MyDate(
                Integer.parseInt(date_memo_array[0]),
                Integer.parseInt(date_memo_array[1]),
                Integer.parseInt(date_memo_array[2]),
                Integer.parseInt(date_memo_array[3]),
                Integer.parseInt(date_memo_array[4]));

        boolean needNotify = "true".equals(needNotify_s);

        // 准备开启闹钟监听
        if (needNotify) {
            Activity_Memo.needNotify = true;
        }

        return new Memo(content, address, date_happen, date_memo, needNotify, id);
    }

    // 刷新列表
    private void setAdapter() {
        ListAdapter adapter = new ListAdapter();
        lv_memo.setAdapter(adapter);
        setListViewAnimation();

        if (vector_memos.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.GONE);
        }
    }

    // 排序——清空表——写入表——刷新
    private void reflash() {
        sort();
        helper.clear();
        helper.creatTable();
        for (int i = 0; i < vector_memos.size(); i++) {
            helper.insert("memo", vector_memos.get(i));
        }
        setAdapter();
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
        // 启动闹钟监听
        if (needNotify && !isBroadcast) {
            Intent service = new Intent(this, Service_Alarm.class);
            startService(service);

            needNotify = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    // 设置列表动画
    private void setListViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_fullscale);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.3f);

        lv_memo.setLayoutAnimation(lac);
    }

    // 给列表排序
    public void sort() {
        Collections.sort(vector_memos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Value.ADD_MEMO:
                if (resultCode == RESULT_OK) {
                    Memo memo = (Memo) data.getSerializableExtra("memo");
                    vector_memos.add(memo);

                    // 排序——清空表——写入表——刷新
                    reflash();
                }
                break;

            case Value.MODIFY_MEMO:
                if (resultCode == RESULT_OK) {
                    Memo memo = (Memo) data.getSerializableExtra("memo");
                    int position = data.getIntExtra("position", -1);
                    vector_memos.set(position, memo);

                    // 排序——清空表——写入表——刷新
                    reflash();
                }
                break;

            case Value.CHECK_MEMO:
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra("hasDelete", false)) {
                        vector_memos.remove(data.getIntExtra("position", -1));
                        setAdapter();
                    } else if (data.getBooleanExtra("hasModify", false)) {
                        Memo memo = (Memo) data.getSerializableExtra("memo");
                        int position = data.getIntExtra("position", -1);
                        vector_memos.set(position, memo);

                        // 排序——清空表——写入表——刷新
                        reflash();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 收起抽屉
            if (dw_memo.isDrawerOpen(GravityCompat.START)) {
                dw_memo.closeDrawer(GravityCompat.START);
            } else if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("launch", "memo");
                editor.apply();

                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return vector_memos.size();
        }

        @Override
        public Object getItem(int position) {
            return vector_memos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = getLayoutInflater().inflate(
                        R.layout.xml_memo_item, null);

                holder.tv_content = (TextView) convertView
                        .findViewById(R.id.textView_memo_item_content);
                holder.tv_time = (TextView) convertView
                        .findViewById(R.id.textView_memo_item_time);
                holder.iv_alarm = (ImageView) convertView
                        .findViewById(R.id.imageView_memo_item_alarm);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Memo tmp = vector_memos.get(position);

            holder.tv_content.setText(tmp.content);

            // 获取当前时间
            Calendar current = Calendar.getInstance();
            Calendar tmpTime = Calendar.getInstance();

            MyDate date_happen_tmp = tmp.date_happen;
            tmpTime.set(date_happen_tmp.year, date_happen_tmp.month - 1,
                    date_happen_tmp.day, date_happen_tmp.hour,
                    date_happen_tmp.minute);

            // 是否已过期
            if (current.before(tmpTime)) {
                holder.tv_time.setText(tmp.date_happen.toString());
            } else {
                holder.tv_time.setText(tmp.date_happen.toString() + "(已过期)");
            }

            // 未过期且设置了提醒时间的为蓝色
            if (current.before(tmpTime) && tmp.needNotify) {
                holder.iv_alarm.setImageResource(R.drawable.alarm_on);
            } else {
                holder.iv_alarm.setImageResource(R.drawable.alarm_off);
            }

            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_content;
        TextView tv_time;
        ImageView iv_alarm;
    }
}
