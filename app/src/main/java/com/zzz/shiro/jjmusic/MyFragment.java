package com.zzz.shiro.jjmusic;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewAdapter;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewHolder;
import com.zzz.shiro.jjmusic.utils.BelmotPlayer;
import com.zzz.shiro.jjmusic.utils.Constants;
import com.zzz.shiro.jjmusic.utils.PicUtil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wc on 2016/8/22.
 */
public class MyFragment extends Fragment
    implements MyRecyclerViewAdapter.AdapterOnItemClickListener {

    private String className = "MyFragment";
    private int idx = 0;

    //CardView List
    private View mView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter mRecyclerViewAdapter;


    //MediaPlayer物件
    private MediaPlayer mediaPlayer;

    private boolean isPause;   //是否為暫停狀態

    private LinkedList<Song> songList = null;


    private BelmotPlayer belmotPlayer;

    private ImageButton playback_toggle_btn;
    private SeekBar seek_bar;
    private TextView playback_current_time_tv;
    private TextView playback_total_time_tv;

    //Bottom bar
    private static SlidingUpPanelLayout slidingUpPanelLayout;
//    private static ImageView s_image_album;
//    private static TextView s_name;
//    private static TextView s_singer;

    //panel
    private TextView p_title;

    private Handler seek_bar_handler = new Handler();



//    public static MyFragment newInstance(String param1, String param2) {
//        MyFragment fragment = new MyFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idx = (int) getArguments().get("idx");
        }

        isPause = true;

        if (null == belmotPlayer) {
            belmotPlayer = BelmotPlayer.getInstance();
        }

        Log.d(className,"onCreate");



    }



    public void initComponent(){
        //注意! 如果這邊的getActivity() 改成用inflater.inflate取得的layoutout 會造成set無效




        Log.d(className,"initComponent");
        belmotPlayer.getBottomBar(getActivity()).init(getActivity());
        belmotPlayer.getBottomBar(getActivity())
                .setSongData(belmotPlayer.getPlayerEngine().getCurrentSong());

        Log.d(className,"bbbb " +belmotPlayer.getPlayerEngine().getCurrentSongName());
        ;

        ImageButton playback_pre_btn = (ImageButton) getActivity().findViewById(R.id.playback_pre);
        playback_pre_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(className,"playback_pre_btn");

                belmotPlayer.getPlayerEngine().previous();

            }
        });


        ImageButton playback_next_btn = (ImageButton) getActivity().findViewById(R.id.playback_next);

        playback_next_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(className,"playback_next_btn");

                belmotPlayer.getPlayerEngine().next();

            }
        });


        playback_toggle_btn = (ImageButton) getActivity().findViewById(R.id.playback_toggle);
        playback_toggle_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                play();

            }
        });

        seek_bar = (SeekBar) getActivity().findViewById(R.id.playback_seeker);
        playback_current_time_tv = (TextView) getActivity().findViewById(R.id.playback_current_time);




        SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
                            && null != belmotPlayer.getPlayerEngine().getPlayingPath()) {

                        seek_bar_handler.removeCallbacks(refresh);
                        playback_current_time_tv.setText(
                                belmotPlayer.getPlayerEngine().getTime(seekBar.getProgress())
                        );

                    }
                    else {
                        seek_bar.setProgress(0);
                    }

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                belmotPlayer.getPlayerEngine().forward(seekBar.getProgress());
                seek_bar_handler.postDelayed(refresh, 1000);
            }
        };

        seek_bar.setOnSeekBarChangeListener(seekbarListener);

        playback_total_time_tv = (TextView) getActivity().findViewById(R.id.playback_total_time);

        p_title = (TextView) getActivity().findViewById(R.id.panel_title);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment 把view建立起來
        Log.d(className,"onCreateView! ");


        mView = inflater.inflate(R.layout.myfragment, container, false);
//        TextView textView = (TextView) view.findViewById(R.id.textView2);
//        textView.setText("第"+(idx +1)+"頁");



        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);

        configRecyclerView();
        initComponent();




        //設定slidingPane一為顯示
//        SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout)mainView.findViewById(R.id.sliding_layout);
//        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED );
//        slidingLayout.callOnClick();



        return mView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void configRecyclerView() {


        switch (idx) {
            case Constants.Tab_0:
                mLayoutManager =
                        new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);

                getMusics();
                mRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),songList );
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerViewAdapter.setOnItemClickListener(this);

                break;
        }


    }


    private void getMusics(){
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever(); //取得媒體

        songList = new LinkedList<Song>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            Log.d("=======>", "查詢錯誤");
        } else if (!cursor.moveToFirst()) {
            Log.d("=======>", "沒有媒體檔");
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.AudioColumns.ALBUM);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int singerCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

            byte[] picByte;
            Bitmap songImage = null;
            long thisId;
            String thisTitle;
            String thisAlbum;
            String pathId;
            String singer;

            do {

                thisId = cursor.getLong(idColumn);
                thisTitle = cursor.getString(titleColumn);
                thisAlbum = cursor.getString(albumColumn);
                pathId = cursor.getString(column_index);
                singer = cursor.getString(singerCol);

                songImage = null;
                metaRetriver.setDataSource(pathId);

                try {
                    picByte = metaRetriver.getEmbeddedPicture();
                    if(picByte!=null){

                        //改圖片大小
                        songImage = PicUtil.resize(picByte);

                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                Log.d("=======>", "id: " + thisId + ", title: " + thisTitle);
                songList.add(new Song(thisId, thisTitle, thisAlbum,songImage ,pathId ,singer));
            } while (cursor.moveToNext());


            belmotPlayer.getPlayerEngine().setMediaPathList(getMusicList());
            belmotPlayer.getPlayerEngine().setMusicMap(getMusicMap());
        }
//        txtSongName.setText("共有 " + songList.size() + " 首歌曲");

    }




    @Override public void aOnItemClick(View view, final int position, final MyRecyclerViewHolder holder) {
        Log.d(className, "myOnItemClick" + position);
        if(songList.get(position)==null)
            return;


        /*
        if(mediaPlayer == null){
            doPlay(songList.get(position).getId());
        }
        else{
            if(!isPause){//己播放
                doPause();
            }
            else{
                doPlay(songList.get(position).getId());
            }
        }



        holder.image_album.setImageBitmap(songList.get(position).getPic());
        holder.image_play.setBackgroundResource(R.drawable.play);

        */


        Song song = null;

        if (null != belmotPlayer.getPlayerEngine()) {
            if (null == belmotPlayer) {
                belmotPlayer = BelmotPlayer.getInstance();
            }


            if(belmotPlayer.getPlayerEngine().isPlaying()){//正播放
                belmotPlayer.getPlayerEngine().pause();

                closePicture(holder,position);
                belmotPlayer.getBottomBar(getActivity()).closeBottomBar();
            }
            else{ //未播放

                song = songList.get(position);
                belmotPlayer.getPlayerEngine().setPlayingPath(song.getPathId());
                if(belmotPlayer.getPlayerEngine().isPause()){
                    belmotPlayer.getPlayerEngine().start();
                }
                else{
                    belmotPlayer.getPlayerEngine().play();
                }


                //處理cardView
                holder.image_album.setImageBitmap(song.getPic()); //重新set圖片
                holder.image_play.setBackgroundResource(R.drawable.play);


                //處理bottom bar

                belmotPlayer.getBottomBar(getActivity())
                        .setSongData(belmotPlayer.getPlayerEngine().getCurrentSong());


            }


            //播完後
            belmotPlayer.getPlayerEngine().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    belmotPlayer.getPlayerEngine().release(); //當我們呼叫 release() 方法時，則會釋放掉這個被佔用的資源
                    closePicture(holder,position);
                    belmotPlayer.getBottomBar(getActivity()).closeBottomBar();
                }
            });

        }





//        holder.image_album.setBackgroundResource(R.drawable.play);
//        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.play);
//        holder.image_album.setImageBitmap(PicUtil.adjustOpacity(bitmap,40));

    }

    @Override
    public void aOnItemLongClick(View view, int position) {

    }


    /**
     * 關掉圖片
     * @param holder
     * @param position
     */
    private void closePicture(MyRecyclerViewHolder holder,int position){
        holder.image_album.setImageBitmap(songList.get(position).getPic()); //重新set圖片
        holder.image_play.setBackgroundResource(0);
    }




    private void doStop() {
        if (mediaPlayer != null) {
            isPause = false;
            mediaPlayer.stop();
        }
    }

    private void doPause(){
        if (mediaPlayer != null) {
            isPause = true;
            mediaPlayer.pause();
        }
    }


    private void doPlay(long id) {
        if (isPause) {//暫停中
            playing(id);
            isPause = false;
        }else{//非暫停中(播放中)

            mediaPlayer.pause();
            isPause = true;
        }

    }


    private void playing(long id){
        if (songList == null || songList.size() == 0 ) {
            return;
        }



        if (mediaPlayer != null && !isPause) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaPlayer == null) {
            Uri songUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), songUri);

            //播完後
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release(); //當我們呼叫 release() 方法時，則會釋放掉這個被佔用的資源
                }
            });
        }
        mediaPlayer.start();

    }


    /**
     * 將songList轉成 List<String> 裡面只裝MediaStore.Audio.Media.DATA
     * @return
     */
    public List<String> getMusicList(){
        List<String> musicList = new ArrayList<String>();

        if(null == songList || songList.isEmpty())
            return musicList;


        int i = 0;
        for(Song song:songList){
            musicList.add(i,song.getPathId());
            i++;
        }

        return musicList;
    }


    public Map getMusicMap(){
        Map<String,Song> musicMap = new HashMap<String, Song>() {
        };

        if(null == songList || songList.isEmpty())
            return musicMap;


        int i = 0;
        for(Song song:songList){
            musicMap.put(song.getPathId(),song);
        }

        return musicMap;
    }


    private void play() {
        if (belmotPlayer.getPlayerEngine().isPlaying()) {//播放中
            belmotPlayer.getPlayerEngine().pause();
            seek_bar_handler.removeCallbacks(refresh);
            playback_toggle_btn
                    .setBackgroundResource(R.drawable.play_button_default);
        } else if (belmotPlayer.getPlayerEngine().isPause()) {//暫停中
            belmotPlayer.getPlayerEngine().start();
            seek_bar_handler.postDelayed(refresh, 1000); //實現一個N秒的一定時器
            playback_toggle_btn
                    .setBackgroundResource(R.drawable.pause_button_default);
        }

    }


    private Runnable refresh = new Runnable() {
        public void run() {
            int currently_Progress = seek_bar.getProgress() + 1000;
            playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
                    .getCurrentTime());
            seek_bar_handler.postDelayed(refresh, 1000);
        }
    };



}
