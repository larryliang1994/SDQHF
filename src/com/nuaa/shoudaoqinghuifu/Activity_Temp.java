package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class Activity_Temp extends AppCompatActivity {
    @Bind(R.id.gridView_temp)
    GridView gv_temp;

    @Bind(R.id.textView_temp_empty)
    TextView tv_empty;

    @Bind(R.id.toolbar_temp)
    Toolbar tb_temp;

    @Bind(R.id.navigationView_temp)
    NavigationView nv_temp;

    @Bind(R.id.drawerLayout_temp)
    DrawerLayout dw_temp;

    public static Vector<Temp> vector_temps = new Vector<>();
    private DBHelper helper = new DBHelper(this, "TempTbl");
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_temp);

        ButterKnife.bind(this);

        helper.creatTable();
        readDataBase();

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.green_status));
        }

        setSupportActionBar(tb_temp);
        tb_temp.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dw_temp.openDrawer(GravityCompat.START);
            }
        });
        tb_temp.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_add:
                        Intent intent = new Intent(Activity_Temp.this, Activity_AddTemp.class);
                        startActivityForResult(intent, Value.ADD_TEMP);
                        overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                        break;
                }
                return true;
            }
        });

        nv_temp.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
        nv_temp.setItemIconTintList(null);
        nv_temp.getMenu().getItem(3).setChecked(true);
        nv_temp.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                dw_temp.closeDrawer(GravityCompat.START);
                switch (menuItem.getItemId()) {
                    case R.id.navItem_msg:
                        intent = new Intent(Activity_Temp.this, Activity_Msg.class);
                        Activity_Temp.this.finish();
                        break;

                    case R.id.navItem_group:
                        intent = new Intent(Activity_Temp.this, Activity_Group.class);
                        Activity_Temp.this.finish();
                        break;

                    case R.id.navItem_memo:
                        intent = new Intent(Activity_Temp.this, Activity_Memo.class);
                        Activity_Temp.this.finish();
                        break;

                    case R.id.navItem_temp:
                        intent = null;
                        break;

                    case R.id.navItem_setting:
                        intent = new Intent(Activity_Temp.this, Activity_Preference.class);
                        intent.putExtra("isTemp", true);
                        Activity_Temp.this.finish();
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                }

                return true;
            }
        });

        gv_temp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // 创建布局填充器
                LayoutInflater inflater = getLayoutInflater();

                // 实例化布局组件
                @SuppressLint("InflateParams")
                final View v = inflater.inflate(R.layout.xml_temp_dialog, null);

                TextView tv_content = (TextView) v.findViewById(R.id.textView_temp_dialog_content);
                CardView cv = (CardView) v.findViewById(R.id.cardView_dialog_item);

                // 设置属性
                Temp temp = vector_temps.get(position);
                tv_content.setText(temp.content);
                cv.setCardBackgroundColor(getResources().getColor(Value.colors[position % Value.colors.length]));

                // 设置单击事件
                RippleView tv_title = (RippleView) v.findViewById(R.id.textView_temp_dialog_title);
                tv_title.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent intent = new Intent(Activity_Temp.this, Activity_SendMsg.class);
                        intent.putExtra("temp", vector_temps.get(position).content);
                        startActivity(intent);
                        Activity_Temp.this.finish();
                        overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                    }
                });

                // 创建对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Temp.this);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setView(v, 0, 0, 0, 0);

                // 设置动画
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.97f;
                lp.dimAmount = 0.7f;
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setWindowAnimations(R.style.mystyle); //设置窗口弹出动画

                dialog.show();
            }
        });

        gv_temp.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Activity_Temp.this);

                final String[] names = {"使用该模板", "修改", "删除"};

                builder.setItems(names, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:  // 使用
                                intent = new Intent(Activity_Temp.this, Activity_SendMsg.class);
                                intent.putExtra("temp", vector_temps.get(position).content);
                                startActivity(intent);
                                Activity_Temp.this.finish();
                                overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                                break;

                            case 1:  // 修改
                                intent = new Intent(Activity_Temp.this, Activity_AddTemp.class);
                                intent.putExtra("temp", vector_temps.get(position));
                                intent.putExtra("position", position);

                                startActivityForResult(intent, Value.MODIFY_TEMP);
                                overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                                break;

                            case 2:  // 删除
                                final MaterialDialog mDialog = new MaterialDialog(Activity_Temp.this)
                                        .setCanceledOnTouchOutside(true)
                                        .setTitle("删除")
                                        .setMessage("真的要删除该模板吗？");
                                mDialog.setPositiveButton("真的",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (position != -1) {
                                                    // 设置退出动画
                                                    setListViewAnimation();

                                                    // 刷新页面
                                                    Cursor cursor = helper.query();
                                                    cursor.moveToPosition(vector_temps.size() - position);
                                                    helper.delete(cursor.getInt(0));
                                                    vector_temps.remove(position);
                                                    setAdapter();

                                                    Toast.makeText(
                                                            Activity_Temp.this,
                                                            "已删除",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {
                                                    Toast.makeText(
                                                            Activity_Temp.this,
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
                            default:
                                break;
                        }

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();
                return true;
            }
        });
    }

    // 从数据库中读取数据
    private void readDataBase() {
        vector_temps.clear();

        Cursor cursor = helper.query();

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Temp temp = readDataWithCursor(cursor);

            vector_temps.add(0, temp);

            cursor.moveToNext();
        }

        setAdapter();
    }

    // 读取一条模板
    private Temp readDataWithCursor(Cursor cursor) {
        String title = cursor.getString(1);
        String content = cursor.getString(2);

        return new Temp(title, content);
    }

    // 刷新列表
    private void setAdapter() {
        MyGridViewAdapter adapter = new MyGridViewAdapter();
        gv_temp.setAdapter(adapter);
        setListViewAnimation();

        if (vector_temps.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.INVISIBLE);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    // 设置列表动画
    private void setListViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(500);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.3f);

        gv_temp.setLayoutAnimation(lac);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Value.ADD_TEMP:
                if (resultCode == RESULT_OK) {
                    setListViewAnimation();

                    // 获取最后一条数据，也就是新插入的数据
                    Cursor cursor = helper.query();
                    cursor.moveToPosition(cursor.getCount() - 1);

                    Temp temp = readDataWithCursor(cursor);

                    vector_temps.add(0, temp);

                    setAdapter();
                }
                break;

            case Value.MODIFY_TEMP:
                if (resultCode == RESULT_OK) {
                    setListViewAnimation();

                    int position = data.getIntExtra("position", -1);

                    // 获取更新过的数据
                    Cursor cursor = helper.query();
                    cursor.moveToPosition(position);

                    Temp temp = readDataWithCursor(cursor);

                    vector_temps.set(position, temp);

                    setAdapter();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 收起抽屉
            if (dw_temp.isDrawerOpen(GravityCompat.START)) {
                dw_temp.closeDrawer(GravityCompat.START);
            } else if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("launch", "temp");
                editor.apply();

                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyGridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return vector_temps.size();
        }

        @Override
        public Object getItem(int position) {
            return vector_temps.get(position);
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
                convertView = getLayoutInflater().inflate(R.layout.xml_temp_item, null);

                holder = new ViewHolder();
                holder.tv_content = (TextView) convertView.findViewById(R.id.textView_temp_item_content);
                holder.tv_title = (TextView) convertView.findViewById(R.id.textView_temp_item_title);
                holder.cv = (CardView) convertView.findViewById(R.id.cardView_temp_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Temp temp = vector_temps.get(position);

            holder.tv_title.setText(temp.title);
            holder.tv_content.setText(temp.content);
            holder.cv.setCardBackgroundColor(getResources().getColor(Value.colors[position % Value.colors.length]));

            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_content;
        TextView tv_title;
        CardView cv;
    }
}
