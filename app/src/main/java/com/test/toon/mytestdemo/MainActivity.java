package com.test.toon.mytestdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.toon.R;
import com.test.toon.demo_animation.AnimationActivity;
import com.test.toon.demo_contact.ContactMainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.test.toon.util.ViewHolder.get;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView mListView=(ListView)findViewById(R.id.list_item);
        List<String> array=new ArrayList<>();
        array.add("动画展示");
        array.add("手机");
        MyAdapter adapter=new MyAdapter(this,array);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent=new Intent(MainActivity.this, AnimationActivity.class);
                    startActivity(intent);
                }else if (position==1){
                    Intent intent=new Intent(MainActivity.this, ContactMainActivity.class);
                    startActivity(intent);
                }

            }
        });

    }
    class MyAdapter extends BaseAdapter{
        private Context context;
        private List<String> dataList;



        public  MyAdapter(Context mcontext,List<String> list) {
            context=mcontext;
            dataList=list;
        }
        @Override
        public int getCount() {
            return null == dataList ? 0 :dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null == dataList ? null : dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null){
                convertView=View.inflate(context,R.layout.activity_list_item,null);
            }
            TextView textView=get(convertView,R.id.item_text);
            textView.setText(dataList.get(position));
            return convertView;
        }
    }
}
