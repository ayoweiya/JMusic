package com.zzz.shiro.jjmusic;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.zzz.shiro.jjmusic.adapter.BtnListenerInterface;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewAdapter;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewAdapter2;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewHolder;
import com.zzz.shiro.jjmusic.playlist.Playlist;
import com.zzz.shiro.jjmusic.playlist.PlaylistDAO;
import com.zzz.shiro.jjmusic.utils.DlgUtil;
import com.zzz.shiro.jjmusic.utils.PicUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity
        implements MyRecyclerViewAdapter2.AdapterOnItemClickListener2{

    private String className = "Main2Activity";
    private static BottomBar bottomBar;

    //CardView List
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter2 mRecyclerViewAdapter;

    private LinkedList<Song> songList = null;
    private Playlist playlist;
    private PlaylistDAO playlistDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actoinBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(bottomBar ==null){
            bottomBar = BottomBar.getInstance(Main2Activity.this);
        }

        bottomBar.setActivity(Main2Activity.this);
        bottomBar.init();
        bottomBar.openPanel();

        songList = new LinkedList<Song>();
        playlist = new Playlist();
        playlistDAO = new PlaylistDAO(this);

        initData();
        initComponent();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void initComponent(){
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rview);

        mLayoutManager = new GridLayoutManager(Main2Activity.this, 1, GridLayoutManager.VERTICAL, false);

        mRecyclerViewAdapter = new MyRecyclerViewAdapter2(Main2Activity.this, songList );
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter.setOnItemClickListener(this);
        mRecyclerViewAdapter.setBtnClickListener(new BtnListenerInterface() {
            @Override
            public void onClick(final int position) {
                Log.d(className,"btn click "+position);

//                openActionDlg(position);
            }
        });
    }


    /**
     * 初始化資料
     */
    private void initData(){
        Bundle bData = this.getIntent().getExtras();// 取得 Bundle 物件
        String name = bData.getString( "name" ); // 取得 Bundle 中的資料
        Log.d(className,"name = " + name);
        playlist = playlistDAO.getByName(name);

        List<String> pathList;
        pathList = playlist.getMusicList();
        Map<String, Song>musicMap = bottomBar.belmotPlayer.getPlayerEngine().getMusicMap();


        for(String str:pathList){
            Song song  = musicMap.get(str);
            songList.add(song);
        }
    }


    private void openActionDlg(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(Main2Activity.this);
        alert.setItems(new String[]{"加入播放清單", "刪除"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(className,"選到:" +i);

                        if(i==0){//選到0

                            Song song = songList.get(position);
                            if(song ==null)
                                return;

                            openAddPlayListDlg(song.getPathId());

                        }
                        else if(i==1){ //選到1


                            DlgUtil.getInstance().getDeleteDialog(Main2Activity.this)
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {


                                            //TODO
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //放生
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                }
        ).setNegativeButton("取消",null).create().show();
    }


    private void openAddPlayListDlg(final String pathId){
        final PlaylistDAO playlistDAO =new PlaylistDAO(Main2Activity.this);
        List<Playlist> playlists = playlistDAO.getAll();

        final String[] plAarray = new String[playlists.size()];
        int i=0;
        for(Playlist pl:playlists){
            plAarray[i] = pl.getName();
            i++;
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(Main2Activity.this);
        alert.setItems(plAarray,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(className,"選到:" +i);


                        Playlist playlist = playlistDAO.getByName(plAarray[i]);

                        if(playlist ==null)
                            return;


                        List<String> sList = playlist.getMusicList();

                        if(sList == null)
                            sList = new ArrayList<String>();


                        sList.add(pathId);
                        playlist.setMusicList(sList);
                        playlistDAO.save(playlist);

                        if(i==0){//選到0



                        }
                        else if(i==1){ //選到1


                            DlgUtil.getInstance().getDeleteDialog(Main2Activity.this)
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {


                                            //TODO
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //放生
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                }
        ).setNegativeButton("取消",null).create().show();

    }

    @Override
    public void aOnItemClick(View view, int position, MyRecyclerViewHolder holder) {
        Log.d(className," ---aOnItemClick --");
    }

    @Override
    public void aOnItemLongClick(View view, int position) {
        Log.d(className," ---aOnItemLongClick --");
    }
}
