package com.zzz.shiro.jjmusic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zzz.shiro.jjmusic.adapter.BtnListenerInterface;
import com.zzz.shiro.jjmusic.adapter.MyListView;
import com.zzz.shiro.jjmusic.adapter.PlaylistAdapter;
import com.zzz.shiro.jjmusic.playlist.Playlist;
import com.zzz.shiro.jjmusic.playlist.PlaylistDAO;

import java.util.List;

public class Main3Activity extends AppCompatActivity {

    private String className = "Main3Activity";


    private MyListView listview;
    private PlaylistAdapter playlistAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        listview = (MyListView) findViewById(R.id.listView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.d(className,"listview click");

            }


        });


        List<Playlist> playlistList = new PlaylistDAO(Main3Activity.this).getAll();
        for(Playlist pp:playlistList){
            if(pp.getMusicList()==null)
                return;
            for(String str:pp.getMusicList()){
                Log.d(className," == " + str);
            }
        }
        playlistAdapter = new PlaylistAdapter(Main3Activity.this,playlistList );
        playlistAdapter.setBtnClickListener(new BtnListenerInterface() {
            @Override
            public void onClick(final int position) {
                Log.d(className,"btn click "+position);

            }
        });
        listview.setAdapter(playlistAdapter);

        listview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);

                        Log.d(className,"ddd:DDD");
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });




    }

}
