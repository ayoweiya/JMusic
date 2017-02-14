package com.zzz.shiro.jjmusic;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zzz.shiro.jjmusic.adapter.BtnListenerInterface;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewHolder;
import com.zzz.shiro.jjmusic.adapter.PlaylistAdapter;
import com.zzz.shiro.jjmusic.playlist.Playlist;
import com.zzz.shiro.jjmusic.playlist.PlaylistDAO;
import com.zzz.shiro.jjmusic.utils.BelmotPlayer;
import com.zzz.shiro.jjmusic.utils.Constants;
import com.zzz.shiro.jjmusic.utils.DlgUtil;
import com.zzz.shiro.jjmusic.utils.StringTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wc on 2016/8/22.
 */
public class MyFragment2_re extends Fragment {

    private String className = "MyFragment2";
    private int idx = 0;

    //CardView List
    private View mView;
    private ListView listview;
    private RecyclerView.LayoutManager mLayoutManager;
    private PlaylistAdapter playlistAdapter;


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
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageView s_image_album;
    private TextView s_name;
    private TextView s_singer;

    //panel
    private TextView p_title;

    private Handler seek_bar_handler = new Handler();


    private ImageButton ib;

    private AlertDialog detailDlg;


    private List<Playlist> playlistList;


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
    }



    public void initComponent(){
        //注意! 如果這邊的getActivity() 改成用inflater.inflate取得的layoutout 會造成set無效
        s_image_album = (ImageView) getActivity().findViewById(R.id.s_imageView);
        s_name = (TextView) getActivity().findViewById(R.id.s_name);
        s_singer = (TextView) getActivity().findViewById(R.id.s_singer);



        slidingUpPanelLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(className,"onPanelSlide");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d(className,"onPanelCollapsed");
                LinearLayout gg = (LinearLayout) getActivity().findViewById(R.id.musicBar);
                gg.setVisibility(View.VISIBLE);

                openPanel();

            }

            @Override
            public void onPanelExpanded(View panel) {//展開後

                Log.d(className,"onPanelExpanded");

//                panel.findViewById(R.id.sliding_layout)
//                        .setBackgroundColor(000000);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                LinearLayout musicBar = (LinearLayout) getActivity().findViewById(R.id.musicBar);
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


        ib = (ImageButton) mView.findViewById(R.id.ib_addPlaylist);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener commonDialogOnClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                EditText et = (EditText) detailDlg.findViewById(R.id.et_addplaylist);
                                String name = et.getText()==null?null:et.getText().toString();

                                if(StringTool.isEmpty(name))
                                    break;

                                Playlist old = new Playlist();
                                old.setBuildTime(new Date());
                                old.setName(name);
                                new PlaylistDAO(getActivity()).save(old);

                                playlistList = new PlaylistDAO(getActivity()).getAll();
                                playlistAdapter.reload(playlistList);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };


                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.dlg_detail,null);
                detailDlg =  new AlertDialog.Builder(getActivity()).setTitle("新增")
                        .setPositiveButton("確定",commonDialogOnClickListener)
                        .setNegativeButton("關閉",commonDialogOnClickListener)
                        .setView(layout).show();
                detailDlg.setCanceledOnTouchOutside(true);
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.d(className,"listview click");

                Intent it = new Intent();
                it.putExtra("name",playlistList.get(position).getName());
                it.setClass(getActivity(),Main3Activity.class);
                startActivity(it);
            }


        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment 把view建立起來
        Log.d(className,"onCreateView!222 ");


        mView = inflater.inflate(R.layout.myfragment2, container, false);




        listview = (ListView) mView.findViewById(R.id.lv_playlist);

        configRecyclerView();
        initComponent();






        return mView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void configRecyclerView() {


        switch (idx) {
            case Constants.Tab_1:
                mLayoutManager =
                        new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);



                playlistList = new PlaylistDAO(getActivity()).getAll();
                for(Playlist pp:playlistList){
                    if(pp.getMusicList()==null)
                        return;
                    for(String str:pp.getMusicList()){
                        Log.d(className," == " + str);
                    }
                }

                playlistAdapter = new PlaylistAdapter(getActivity(),playlistList );
                playlistAdapter.setBtnClickListener(new BtnListenerInterface() {
                    @Override
                    public void onClick(final int position) {
                        Log.d(className,"btn click "+position);

                        openActionDlg(position);
                    }
                });
                listview.setAdapter(playlistAdapter);
                playlistAdapter.notifyDataSetChanged();

                break;
        }


    }



    private void openActionDlg(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setItems(new String[]{"刪除", "更改名稱"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(className,"選到:" +i);

                        if(i==0){//選到刪除

                            DlgUtil.getInstance().getDeleteDialog(getActivity())
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        new PlaylistDAO(getActivity()).delete(playlistList.get(position));

                                        playlistAdapter.reload(new PlaylistDAO(getActivity()).getAll());

                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //放生
                                        dialogInterface.dismiss();
                                    }
                            }).show();

                        }
                        else if(i==1){ //選到更改名稱

                            final Playlist pl = playlistList.get(position);
                            String name = pl.getName();

                            View layout =  getActivity().getLayoutInflater().inflate(R.layout.dlg_detail,null);
                            final EditText ed = (EditText) layout.findViewById(R.id.et_addplaylist);
                            ed.setText(name);

                            new AlertDialog.Builder(getActivity())
                                .setTitle("改")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String name = ed.getText()==null?null:ed.getText().toString();

                                        if(StringTool.isEmpty(name))
                                            return;

                                        pl.setName(name);
                                        new PlaylistDAO(getActivity()).save(pl);
                                        playlistAdapter.reload(playlistList);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setView(layout)
                                .show();
                        }
                    }
                }
        ).setNegativeButton("取消",null).create().show();
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


    /**
     * 關掉BottomBar
     */
    private void closeBottomBar(){
        s_image_album.setImageBitmap(null);
        s_name.setText(null);
        s_singer.setText(null);
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
            Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
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


    /**
     * 展開播放面板
     */
    private void openPanel(){
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
            p_title.setText(s_name.getText()==null?"":s_name.getText().toString());

        } else {
            playback_toggle_btn
                    .setBackgroundResource(R.drawable.pause_button_default);
        }
    }
}
