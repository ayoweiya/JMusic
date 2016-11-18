package com.zzz.shiro.jjmusic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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

/**
 * Created by wc on 2016/11/18.
 */
public class BottomBar  extends Application {

    private static String className = "BottomBar";
    private static SeekBar seek_bar;
    private static TextView playback_current_time_tv;
    private static TextView playback_total_time_tv;
    private static ImageButton playback_toggle_btn;


    private static ImageView s_image_album;
    private static TextView s_name;
    private static TextView s_singer;

    private static BottomBar instance;

    static private Handler seek_bar_handler = new Handler();


    private BottomBar(){}

    public static BottomBar getInstance(Activity act) {

        if(null == instance){
            if(playback_current_time_tv==null)
                playback_current_time_tv = (TextView)act.findViewById(R.id.playback_current_time);
            if(playback_total_time_tv ==null)
                playback_total_time_tv = (TextView) act.findViewById(R.id.playback_total_time);
            if(seek_bar==null)
                seek_bar = (SeekBar) act.findViewById(R.id.playback_seeker);
            if(playback_toggle_btn == null)
                playback_toggle_btn = (ImageButton) act.findViewById(R.id.playback_toggle);


            s_image_album = (ImageView) act.findViewById(R.id.s_imageView);
            s_name = (TextView) act.findViewById(R.id.s_name);
            s_singer = (TextView) act.findViewById(R.id.s_singer);
        }

        return instance;
    }

    public static void init(final  Activity act){
        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) act.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(className,"onPanelSlide");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d(className,"onPanelCollapsed");
                LinearLayout gg = (LinearLayout) act.findViewById(R.id.musicBar);
                gg.setVisibility(View.VISIBLE);

                openPanel(act);
            }

            @Override
            public void onPanelExpanded(View panel) {//展開後

                Log.d(className,"onPanelExpanded");

//                panel.findViewById(R.id.sliding_layout)
//                        .setBackgroundColor(000000);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                LinearLayout musicBar = (LinearLayout) act.findViewById(R.id.musicBar);
                musicBar.setVisibility(View.GONE);



                openPanel(act);
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



    }


    /**
     * 展開播放面板
     */
    private static void openPanel(final Activity act ){
        BelmotPlayer belmotPlayer =null;
        if (null == belmotPlayer) {
            belmotPlayer = BelmotPlayer.getInstance();
        }








        final Handler seek_bar_handler = new Handler();


        if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
                && null != belmotPlayer.getPlayerEngine().getPlayingPath()) {
            playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
                    .getCurrentTime());
            playback_total_time_tv.setText(belmotPlayer.getPlayerEngine()
                    .getDurationTime());
        }




        if (belmotPlayer.getPlayerEngine().isPlaying()) {
            seek_bar.setMax(Integer.valueOf(belmotPlayer.getPlayerEngine()
                    .getDuration()));
            seek_bar_handler.postDelayed(refresh, 1000);//每一秒刷新秒數顯示器
            playback_toggle_btn
                    .setBackgroundResource(R.drawable.play_button_default);
//            p_title.setText(s_name.getText()==null?"":s_name.getText().toString());
//TODO
        } else {
            playback_toggle_btn
                    .setBackgroundResource(R.drawable.pause_button_default);
        }
    }

    private static Runnable refresh = new Runnable() {
        public void run() {
            int currently_Progress = seek_bar.getProgress() + 1000;
            playback_current_time_tv.setText(BelmotPlayer.getInstance().getPlayerEngine()
                    .getCurrentTime());
            seek_bar_handler.postDelayed(refresh, 1000);
        }
    };


    public static void setSongData(Song song){
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
