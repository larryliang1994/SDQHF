package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class Activity_Group extends AppCompatActivity {
    @Bind(R.id.textView_group_empty)
    TextView tv_empty;

    @Bind(R.id.expandableListView_group)
    ExpandableListView elv_group;

    @Bind(R.id.toolbar_group)
    Toolbar tb_group;

    @Bind(R.id.navigationView_group)
    NavigationView nv_group;

    @Bind(R.id.drawerLayout_group)
    DrawerLayout dw_group;

    public static Vector<Group> vector_groups = new Vector<>();
    private ExpandableListAdapter myAdapter = new ExpandableListAdapter();
    private DBHelper helper = new DBHelper(this, "GroupTbl");
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_group);

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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#0288d1"));
        }

        setSupportActionBar(tb_group);
        tb_group.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dw_group.openDrawer(GravityCompat.START);
            }
        });
        tb_group.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_add:
                        Intent intent = new Intent(Activity_Group.this, Activity_AddGroup.class);
                        startActivityForResult(intent, Value.ADD_GROUP);
                        overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                        break;
                }
                return true;
            }
        });

        nv_group.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
        nv_group.setItemIconTintList(null);
        nv_group.getMenu().getItem(1).setChecked(true);
        nv_group.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                dw_group.closeDrawer(GravityCompat.START);
                switch (menuItem.getItemId()) {
                    case R.id.navItem_msg:
                        intent = new Intent(Activity_Group.this, Activity_Msg.class);
                        Activity_Group.this.finish();
                        break;

                    case R.id.navItem_group:
                        intent = null;
                        break;

                    case R.id.navItem_memo:
                        intent = new Intent(Activity_Group.this, Activity_Memo.class);
                        Activity_Group.this.finish();
                        break;

                    case R.id.navItem_temp:
                        intent = new Intent(Activity_Group.this, Activity_Temp.class);
                        Activity_Group.this.finish();
                        break;

                    case R.id.navItem_setting:
                        intent = new Intent(Activity_Group.this, Activity_Preference.class);
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, R.anim.scale_stay);
                }

                return true;
            }
        });

        elv_group.setAdapter(myAdapter);

    }

    // 从数据库中读取数据
    private void readDataBase() {
        vector_groups.clear();

        Cursor cursor = helper.query();

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Group group = readDataWithCursor(cursor);

            vector_groups.add(group);

            cursor.moveToNext();
        }

        setAdapter();
    }

    // 读取一条消息
    private Group readDataWithCursor(Cursor cursor) {
        String name = cursor.getString(1);
        String member_s = cursor.getString(2);

        ArrayList<Member> members_array = new ArrayList<>();
        String[] member_split = member_s.split("#!#");

        for (String aMember_split : member_split) {
            String[] member_each = aMember_split.split(":");

            members_array.add(new Member(member_each[0], member_each[1]));
        }

        return new Group(name, members_array);
    }

    // 刷新列表
    private void setAdapter() {
        ExpandableListAdapter adapter = new ExpandableListAdapter();
        elv_group.setAdapter(adapter);
        setListViewAnimation();

        if (vector_groups.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView iv = (ImageView) nv_group.findViewById(R.id.imageView_navigation_background);
        // 获取壁纸
        if (Activity_Welcome.picturePath != null) {
            BitmapDrawable bd = new BitmapDrawable(this.getResources(),
                    BitmapFactory.decodeFile(Activity_Welcome.picturePath));
            iv.setImageDrawable(bd);
        } else {
            iv.setImageResource(R.drawable.background_navigation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    // 设置列表动画
    public void setListViewAnimation() {

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(500);
        LayoutAnimationController lac = new LayoutAnimationController(animation);

        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.1f);

        // 调用者可选
        elv_group.setLayoutAnimation(lac);
    }

    // 处理新建的群组返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Value.ADD_GROUP:
                if (resultCode == RESULT_OK) {
                    setListViewAnimation();

                    // 获取最后一条数据，也就是新插入的数据
                    Cursor cursor = helper.query();
                    cursor.moveToPosition(cursor.getCount() - 1);

                    Group group = readDataWithCursor(cursor);

                    vector_groups.add(group);

                    setAdapter();
                }
                break;

            case Value.MODIFY_GROUP:
                if (resultCode == RESULT_OK) {
                    Group group = (Group) data.getSerializableExtra("group");
                    int position = data.getIntExtra("position", -1);
                    vector_groups.set(position, group);

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
            if (dw_group.isDrawerOpen(GravityCompat.START)) {
                dw_group.closeDrawer(GravityCompat.START);
            } else if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("launch", "group");
                editor.apply();

                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {
        // 获取vector_groups的元素个数
        @Override
        public int getGroupCount() {
            return vector_groups.size();
        }

        // 获取vector_groups里面的子列表的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return vector_groups.get(groupPosition).members.size();
        }

        // 得到根目录下的数据
        @Override
        public Object getGroup(int groupPosition) {
            return vector_groups.get(groupPosition);
        }

        // 得到子目录下的数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return vector_groups.get(groupPosition).members.get(childPosition);
        }

        // group位置编号
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        // group里面的子项目的位置编号
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 表示孩子是否和组ID是跨基础数据的更改稳定
        @Override
        public boolean hasStableIds() {
            return true;
        }

        // 设置根目录item内容
        @SuppressLint("InflateParams")
        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final GroupViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.xml_group_group, null);

                holder = new GroupViewHolder();
                holder.tv_groupname = (TextView) convertView.findViewById(R.id.textView_group_group_name);
                holder.iv_indicator = (ImageView) convertView.findViewById(R.id.imageView_group_group_indicator);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            holder.tv_groupname.setText(vector_groups.get(groupPosition).name);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (elv_group.isGroupExpanded(groupPosition)) {
                        elv_group.collapseGroup(groupPosition);
                        holder.iv_indicator.setImageResource(R.drawable.group_indicator_normal);
                    } else {
                        elv_group.expandGroup(groupPosition);
                        holder.iv_indicator.setImageResource(R.drawable.group_indicator_drop);

                        if (groupPosition == 0) {
                            elv_group.setSelection(groupPosition);
                        } else {
                            int count = 0;
                            for (int i = 0; i < groupPosition; i++) {
                                if (elv_group.isGroupExpanded(i)) {
                                    count = count + vector_groups.get(i).members.size();
                                }
                            }
                            elv_group.setSelection(count + groupPosition);
                        }


                        setListViewAnimation();
                    }
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            Activity_Group.this);

                    final String[] names = {"向该群组发送消息", "修改", "删除"};

                    builder.setItems(names, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int which) {
                            if (which == 1) {
                                Intent intent = new Intent(Activity_Group.this,
                                        Activity_AddGroup.class);
                                intent.putExtra("group", vector_groups.get(groupPosition));
                                intent.putExtra("position", groupPosition);
                                startActivityForResult(intent, Value.MODIFY_GROUP);

                                overridePendingTransition(R.anim.in_right_left,
                                        R.anim.scale_stay);
                            } else if (which == 2) {
                                final MaterialDialog mDialog = new MaterialDialog(Activity_Group.this)
                                        .setCanceledOnTouchOutside(true)
                                        .setTitle("删除")
                                        .setMessage("真的要删除这个群组吗？");
                                mDialog.setPositiveButton("真的",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // 设置退出动画
                                                setListViewAnimation();

                                                // 刷新页面
                                                Cursor cursor = helper.query();
                                                cursor.moveToPosition(groupPosition);
                                                helper.delete(cursor.getInt(0));
                                                vector_groups.remove(groupPosition);
                                                setAdapter();

                                                Toast.makeText(Activity_Group.this,
                                                        "已删除", Toast.LENGTH_SHORT)
                                                        .show();
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
                            } else if (which == 0) {
                                Intent intent = new Intent(Activity_Group.this,
                                        Activity_SendMsg.class);
                                intent.putExtra("group", vector_groups.get(groupPosition));
                                startActivity(intent);
                                Activity_Group.this.finish();
                                overridePendingTransition(R.anim.in_right_left,
                                        R.anim.scale_stay);
                            }
                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    return true;
                }
            });

            return convertView;
        }

        // 设置子目录item内容
        @SuppressLint("InflateParams")
        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final ChildViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.xml_group_child, null);

                holder = new ChildViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.textView_group_child_name);
                holder.tv_phone = (TextView) convertView.findViewById(R.id.textView_group_child_phone);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            holder.tv_name.setText(vector_groups.get(groupPosition).members
                    .get(childPosition).name);
            holder.tv_phone.setText(vector_groups.get(groupPosition).members
                    .get(childPosition).phone);

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final MaterialDialog mDialog = new MaterialDialog(Activity_Group.this)
                            .setCanceledOnTouchOutside(true)
                            .setTitle("删除")
                            .setMessage("真的要删除这个成员吗？");
                    mDialog.setPositiveButton("真的",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 设置退出动画
                                    setListViewAnimation();

                                    // 刷新页面
                                    vector_groups.get(groupPosition).members.remove(childPosition);
                                    Cursor cursor = helper.query();
                                    cursor.moveToPosition(groupPosition);
                                    helper.update(String.valueOf(cursor.getInt(0)), "group", vector_groups.get(groupPosition));
                                    setAdapter();

                                    Toast.makeText(Activity_Group.this, "已删除", Toast.LENGTH_SHORT).show();
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

                    return true;
                }
            });

            return convertView;
        }

        // 孩子在指定的位置是不可选的，即：vector_groups中的元素是不可点击的
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class ChildViewHolder {
        TextView tv_name;
        TextView tv_phone;
    }

    class GroupViewHolder {
        TextView tv_groupname;
        ImageView iv_indicator;
    }
}
