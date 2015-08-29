package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("DefaultLocale")
public class Activity_Contacts extends AppCompatActivity {
    @Bind(R.id.listView_contacts)
    ListView lv_contacts;

    @Bind(R.id.fast_scroller)
    QuickAlphabeticBar alpha;

    @Bind(R.id.progressBar_contacts)
    ProgressBar pb;

    @Bind(R.id.toolbar_contacts)
    Toolbar tb_contacts;

    private static final String NAME = "name", NUMBER = "number",
            SORT_KEY = "sort_key";
    private HanyuPinyinOutputFormat pyFormat = new HanyuPinyinOutputFormat();
    private List<ContentValues> list = new ArrayList<>();
    private BaseAdapter adapter;
    private AsyncQueryHandler asyncQuery;
    private Vector<Boolean> isChecked = new Vector<>(); // 保存item的选取情况

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_contacts);

        ButterKnife.bind(this);

        initView();

        setListViewAnimation();

        Uri uri = Uri.parse("content://com.android.contacts/data/phones"); // 联系人的Uri
        String[] projection = {"_id", "display_name", "data1", "sort_key"}; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");// 按照sort_key升序查询
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_other, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#0288d1"));
        }

        setSupportActionBar(tb_contacts);
        tb_contacts.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = getIntent();
                // 设置返回的结果
                backIntent.putExtra("contacts", "");

                // 结果编码
                Activity_Contacts.this.setResult(RESULT_CANCELED, backIntent);

                Activity_Contacts.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        tb_contacts.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_ok:
                        Intent backIntent = getIntent();
                        String names = "";
                        ArrayList<String> phones = new ArrayList<>(); // 联系人号码

                        for (int i = 0; i < list.size(); i++) {
                            if (isChecked.get(i)) {
                                ContentValues cv = list.get(i);

                                names += "; ";
                                names += cv.getAsString(NAME);

                                phones.add(cv.getAsString(NUMBER));
                            }
                        }

                        // 设置返回的结果
                        backIntent.putExtra("names", names.substring(2));
                        backIntent.putExtra("phones", phones);

                        // 结果编码
                        Activity_Contacts.this.setResult(RESULT_OK, backIntent);
                        Activity_Contacts.this.finish();
                        overridePendingTransition(R.anim.scale_stay,
                                R.anim.out_left_right);
                        break;
                }
                return true;
            }
        });


        // 初始化
        for (int i = 0; i < 500; i++) {
            isChecked.add(false);
        }

        // 配置拼音属性
        pyFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        pyFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pyFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        asyncQuery = new MyAsyncQueryHandler(getContentResolver());

        lv_contacts.setOnItemClickListener(new OnItemClickListener() {
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

    private void setAdapter(List<ContentValues> list) {
        adapter = new ListAdapter(this, list);
        lv_contacts.setAdapter(adapter);
        alpha.init(Activity_Contacts.this);
        alpha.setListView(lv_contacts);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
        pb = null;

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

    // 设置列表动画
    public void setListViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(500);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.5f);

        lv_contacts.setLayoutAnimation(lac);
    }

    // 提取英文的首字母，非英文字母用#代替
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 大写输出
        } else {
            return "#";
        }
    }

    // 按下返回键返回上层
    @SuppressLint("DefaultLocale")
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent backIntent = getIntent();

            // 设置返回的结果
            backIntent.putExtra("contacts", "");

            // 结果编码
            this.setResult(RESULT_CANCELED, backIntent);

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    private static class ViewHolder {
        TextView tv_alpha = null;
        TextView tv_photo = null;
        TextView tv_name = null;
        TextView tv_phone = null;
        CheckBox cb_isChosen = null;
    }

    // 数据库异步查询类AsyncQueryHandler
    @SuppressLint("HandlerLeak")
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        @SuppressLint("HandlerLeak")
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        // 查询结束的回调函数
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();

                for (int i = 0; i < cursor.getCount(); i++) {
                    ContentValues cv = new ContentValues();
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    // String sortKey = cursor.getString(3);

                    String sortKey = PinyinHelper.toHanyuPinyinString(name,
                            pyFormat, "").substring(0, 1);

                    if (number.startsWith("+86")) { // 去除多余的中国地区号码标志，对这个程序没有影响
                        cv.put(NAME, name);
                        cv.put(NUMBER, number.substring(3));
                        cv.put(SORT_KEY, sortKey);
                    } else {
                        cv.put(NAME, name);
                        cv.put(NUMBER, number);
                        cv.put(SORT_KEY, sortKey);
                    }
                    list.add(cv);

                }
                if (list.size() >= 0) {
                    setAdapter(list);
                }
            }
        }

    }

    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        // private List<ContentValues> list;
        private HashMap<String, Integer> alphaIndexer; // 保存每个索引在list中的位置【#-0，A-4，B-10】
        private String[] sections; // 每个分组的索引表【A,B,C,F...】

        public ListAdapter(Context context, List<ContentValues> list) {
            this.inflater = LayoutInflater.from(context);
            this.alphaIndexer = new HashMap<>();
            this.sections = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                String name = getAlpha(list.get(i).getAsString(SORT_KEY));
                if (!alphaIndexer.containsKey(name)) {// 只记录在list中首次出现的位置
                    alphaIndexer.put(name, i);
                }
            }
            Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<>(
                    sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);

            alpha.setAlphaIndexer(alphaIndexer);
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
                convertView = inflater.inflate(R.layout.xml_contact_item, null);
                holder = new ViewHolder();

                holder.tv_alpha = (TextView) convertView
                        .findViewById(R.id.alpha);
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.textView_contact_item_name);
                holder.tv_phone = (TextView) convertView
                        .findViewById(R.id.textView_contact_item_phone);
                holder.tv_photo = (TextView) convertView
                        .findViewById(R.id.textView_contact_item_photo);
                holder.cb_isChosen = (CheckBox) convertView
                        .findViewById(R.id.checkBox_contact_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ContentValues cv = list.get(position);

            holder.tv_name.setText(cv.getAsString(NAME));
            holder.tv_phone.setText(cv.getAsString(NUMBER));
            holder.cb_isChosen.setChecked(isChecked.get(position));
            holder.tv_photo.setText(cv.getAsString(NAME).substring(0, 1));

            // 设置头像颜色
            GradientDrawable bgShape = (GradientDrawable) holder.tv_photo.getBackground();
            bgShape.setColor(Color.parseColor(Value.colors[position % Value.colors.length]));

            // 当前联系人的sortKey
            String currentStr = getAlpha(list.get(position).getAsString(
                    SORT_KEY));
            // 上一个联系人的sortKey
            String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
                    position - 1).getAsString(SORT_KEY)) : " ";

            // 判断显示#、A-Z的TextView隐藏与可见
            // 当前联系人的sortKey！=上一个联系人的sortKey，说明当前联系人是新组
            if (!previewStr.equals(currentStr)) {
                holder.tv_alpha.setVisibility(View.VISIBLE);
                holder.tv_alpha.setText(currentStr);
            } else {
                holder.tv_alpha.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
}