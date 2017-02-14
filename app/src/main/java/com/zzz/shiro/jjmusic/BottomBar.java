package com.zzz.shiro.jjmusic;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zzz.shiro.jjmusic.utils.BelmotPlayer;

import java.util.List;
import java.util.Map;

/**
 * Created by wc on 2016/11/18.
 */
public class BottomBar  extends Application {

    private static String className = "BottomBar";

    //控制盤
    private static TextView p_title;
    private static SeekBar p_seek_bar;
    private static TextView p_playback_current_time_tv;
    private static TextView p_playback_total_time_tv;
    private static ImageButton p_playback_toggle_btn;

    //bottom bar
    private static ImageView s_image_album;
    private static TextView s_name;
    private static TextView s_singer;

    private static BottomBar instance;

    private static Handler seek_bar_handler = new Handler();


    private static Activity activty;


    public static BelmotPlayer belmotPlayer; //播放器

    public static BottomBar getInstance(Activity activity) {

        if(null == instance){
            activty = activity;
//            if(p_playback_current_time_tv==null)
                p_playback_current_time_tv = (TextView)activity.findViewById(R.id.playback_current_time);
//            if(p_playback_total_time_tv ==null)
                p_playback_total_time_tv = (TextView) activity.findViewById(R.id.playback_total_time);
//            if(p_seek_bar==null)
                p_seek_bar = (SeekBar) activity.findViewById(R.id.playback_seeker);
//            if(p_playback_toggle_btn == null)
                p_playback_toggle_btn = (ImageButton) activity.findViewById(R.id.playback_toggle);

//            if(p_title ==null)
                p_title = (TextView) activity.findViewById(R.id.panel_title);

            s_image_album = (ImageView) activity.findViewById(R.id.s_imageView);
            s_name = (TextView) activity.findViewById(R.id.s_name);
            s_singer = (TextView) activity.findViewById(R.id.s_singer);

            belmotPlayer = BelmotPlayer.getInstance();
            init();

        }



        return instance;
    }


    public static void setActivity(Activity activity){
        activty = activity;
    }

    public static void setData(List<String> mediaList,
                               Map<String,Song> musicMap){
        belmotPlayer.getPlayerEngine().setMediaPathList(mediaList);
        belmotPlayer.getPlayerEngine().setMusicMap(musicMap);
    }



    public static void init(){
        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) activty.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);//TODO 等刪

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(className,"onPanelSlide");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d(className,"onPanelCollapsed");
                LinearLayout gg = (LinearLayout) activty.findViewById(R.id.musicBar);
                gg.setVisibility(View.VISIBLE);

                openPanel();
            }

            @Override
            public void onPanelExpanded(View panel) {//展開後

                Log.d(className,"onPanelExpanded");

//                panel.findViewById(R.id.sliding_layout)
//                        .setBackgroundColor(000000);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                LinearLayout musicBar = (LinearLayout) activty.findViewById(R.id.musicBar);
                musicBar.setVisibility(View.GONE);



                openPanel();
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.d(className,"onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.d(className,"onPanelHidden");
            }
        });




        SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if (fromUser) {
                    if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
                            && null != belmotPlayer.getPlayerEngine().getPlayingPath()) {

                        seek_bar_handler.removeCallbacks(refresh);
                        p_playback_current_time_tv.setText(
                                belmotPlayer.getPlayerEngine().getTime(seekBar.getProgress())
                        );

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

        p_seek_bar.setOnSeekBarChangeListener(seekbarListener);


        p_playback_toggle_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                play();

            }
        });


        ImageButton playback_pre_btn = (ImageButton) activty.findViewById(R.id.playback_pre);
        playback_pre_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(className,"playback_pre_btn");

                belmotPlayer.getPlayerEngine().previous();

            }
        });

        ImageButton playback_next_btn = (ImageButton) activty.findViewById(R.id.playback_next);

        playback_next_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(className,"playback_next_btn");

                belmotPlayer.getPlayerEngine().next();

            }
        });


        openPanel();

    }

    public static void open100(){
        if ( belmotPlayer.getPlayerEngine().isPlaying()) {
            p_seek_bar.setMax(Integer.valueOf( belmotPlayer.getPlayerEngine()
                    .getDuration()));
            seek_bar_handler.postDelayed(refresh, 1000);//每一秒刷新秒數顯示器
            p_playback_toggle_btn.setBackgroundResource(R.drawable.play_button_default);


            p_title.setText(s_name.getText()==null?"":s_name.getText().toString());



        } else {
            p_playback_toggle_btn
                    .setBackgroundResource(R.drawable.pause_button_default);
        }

    }

    public static void setBarData(){
        setSongData( belmotPlayer.getPlayerEngine().getCurrentSong());

        p_seek_bar.setMax(Integer.valueOf( belmotPlayer.getPlayerEngine()
                .getDuration()));

        seek_bar_handler.postDelayed(refresh, 1000); //每一秒刷新秒數顯示器


        setBarTime();
    }




    public static void setBarTime(){
        if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
                && null != belmotPlayer.getPlayerEngine().getPlayingPath()) {//目前沒有播放曲目
            p_playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
                    .getCurrentTime());
            p_playback_total_time_tv.setText(belmotPlayer.getPlayerEngine()
                    .getDurationTime());

            setSongData( belmotPlayer.getPlayerEngine().getCurrentSong());
        }
    }

    /**
     * 展開播放面板
     */
    public static void openPanel(){
        setBarTime();

        if (belmotPlayer.getPlayerEngine().isPlaying()) {
            p_seek_bar.setMax(Integer.valueOf(belmotPlayer.getPlayerEngine()
                    .getDuration()));
            seek_bar_handler.postDelayed(refresh, 1000);//每一秒刷新秒數顯示器
            p_playback_toggle_btn.setBackgroundResource(R.drawable.play_button_default);
            Song song = belmotPlayer.getPlayerEngine().getCurrentSong();
            p_title.setText(song==null?"":song.getTitle());

            setSongData(song);

        } else {
            p_playback_toggle_btn
                    .setBackgroundResource(R.drawable.pause_button_default);
        }


    }


    /**
     * 計時器
     */
    private static Runnable refresh = new Runnable() {
        public void run() {

            int currently_Progress = p_seek_bar.getProgress() + 100;
            p_playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
                    .getCurrentTime());
            seek_bar_handler.postDelayed(refresh, 1000);

            int current_position = belmotPlayer.getPlayerEngine().getCurrentPosition();
//            Log.d(className,"refresh " + current_position);
            p_seek_bar.setProgress(current_position);
        }
    };


    private static void play() {
        if (belmotPlayer.getPlayerEngine().isPlaying()) {//播放中
            belmotPlayer.getPlayerEngine().pause();
            seek_bar_handler.removeCallbacks(refresh);
            p_playback_toggle_btn
                    .setBackgroundResource(R.drawable.play_button_default);
        } else if (belmotPlayer.getPlayerEngine().isPause()) {//暫停中
            belmotPlayer.getPlayerEngine().start();
            seek_bar_handler.postDelayed(refresh, 1000); //實現一個N秒的一定時器
            p_playback_toggle_btn
                    .setBackgroundResource(R.drawable.pause_button_default);
        }

    }


    /**
     * 填音樂bar上的Data
     * @param song
     */
    private static void setSongData(Song song){
        if(song ==null)
            return;
        s_image_album.setImageBitmap(song.getPic());
        s_name.setText(song.getTitle());
        s_singer.setText(song.getSinger());


    }

    public static void closeBottomBar(){
        s_image_album.setImageBitmap(null);
        s_name.setText(null);
        s_singer.setText(null);
    }
}
