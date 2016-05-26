package com.tone.dragandswipe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView addRecycler;
    private DragAdapter adapter;
    private ListAdapter listAdapter;
    private LinearLayout add_subscribe_layout;
    private TextView btn_complete;
    ArrayList<Channel> unchannels = new ArrayList<>();
    ArrayList<Channel> mychannels = new ArrayList<>();
    ArrayList<Channel> channels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        addRecycler = (RecyclerView) findViewById(R.id.addrecycler);

        add_subscribe_layout = (LinearLayout) findViewById(R.id.add_subscribe_layout);
        btn_complete = (TextView) findViewById(R.id.btn_complete);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new GridLayoutManager(this, 4);
        addRecycler.setLayoutManager(layoutManager);

        adapter = new DragAdapter(this, recyclerView);
        adapter.setmItems(mychannels);
        adapter.setDragListener(dragListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        listAdapter = new ListAdapter(this);
        listAdapter.addItems(unchannels);
        listAdapter.setOnItemClickLister(onItemClickLister);
        addRecycler.setHasFixedSize(true);
        addRecycler.setAdapter(listAdapter);

        initData();
    }


    private void initData() {
        for (int i = 0; i < 20; i++) {
            Channel channel = new Channel(i, "新闻订阅" + i, "url", i < 3 ? 0 : 1, i < 10 ? 0 : 1);
            channels.add(channel);
        }
        for (Channel channel : channels) {
            if (channel.getIsShow() == 0) {
                mychannels.add(channel);
            } else {
                unchannels.add(channel);
            }
        }
        adapter.notifyDataSetChanged();
        listAdapter.notifyDataSetChanged();
    }

    public void onComplete(View view) {
        adapter.setDragComplete();
        btn_complete.setVisibility(View.GONE);
        add_subscribe_layout.setVisibility(View.VISIBLE);
    }

    private ListAdapter.OnItemClickLister<Channel> onItemClickLister = new ListAdapter.OnItemClickLister<Channel>() {
        @Override
        public void onItemClick(Channel channel) {
            channel.setIsShow(0);
            adapter.addItem(channel);
        }
    } ;

    private DragAdapter.DragListener<Channel> dragListener = new DragAdapter.DragListener<Channel>() {
        @Override
        public void onLongClick(View view) {
            btn_complete.setVisibility(View.VISIBLE);
            add_subscribe_layout.setVisibility(View.GONE);
        }

        @Override
        public void onItemRemoved(Channel channel) {
            channel.setIsShow(1);
            listAdapter.addItem(channel);
        }
    };
}
