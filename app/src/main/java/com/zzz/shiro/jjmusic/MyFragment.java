package com.zzz.shiro.jjmusic;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.zzz.shiro.jjmusic.adapter.BtnListenerInterface;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewAdapter;
import com.zzz.shiro.jjmusic.adapter.MyRecyclerViewHolder;
import com.zzz.shiro.jjmusic.playlist.Playlist;
import com.zzz.shiro.jjmusic.playlist.PlaylistDAO;
import com.zzz.shiro.jjmusic.utils.BelmotPlayer;
import com.zzz.shiro.jjmusic.utils.Constants;
import com.zzz.shiro.jjmusic.utils.DlgUtil;
import com.zzz.shiro.jjmusic.utils.PicUtil;
import com.zzz.shiro.jjmusic.utils.StringTool;


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


    private static BottomBar bottomBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className,"onCreate");
        if (getArguments() != null) {
            idx = (int) getArguments().get("idx");
        }

        isPause = true;



        if(bottomBar ==null){
            bottomBar = BottomBar.getInstance(getActivity());
        }


    }



    public void initComponent(){
        Log.d(className,"initComponent");
        //注意! 如果這邊的getActivity() 改成用inflater.inflate取得的layoutout 會造成set無效


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
                mRecyclerViewAdapter.setBtnClickListener(new BtnListenerInterface() {
                    @Override
                    public void onClick(final int position) {
                        Log.d(className,"btn click "+position);

                        openActionDlg(position);
                    }
                });


                bottomBar.init();
                bottomBar.setData(getMusicList(),getMusicMap());
                closePlayIcon();

                break;
        }


    }

    /**
     * 關掉Play的圖示
     */
    private void closePlayIcon(){
        Song song = bottomBar.belmotPlayer.getPlayerEngine().getCurrentSong();

        if(song ==null)
            return;

        int i = findCurrentIndex(songList,song.getPathId());
        if(i <0)
            return;
        View vv = mRecyclerView.getChildAt(i);
        ImageView image_play = (ImageView) vv.findViewById(R.id.imageView2);
        image_play.setBackgroundResource(0);
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

                Log.d(className,"pathId="+pathId);
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

        if (null != bottomBar.belmotPlayer.getPlayerEngine()) {

            song = songList.get(position);

            if(song == null || song.getPathId()==null)
                return;

            if(bottomBar.belmotPlayer.getPlayerEngine().isPlaying()){//正播放

                if(song.getPathId().equals(bottomBar.belmotPlayer.getPlayerEngine().getPlayingPath())){
                    //同首
                    bottomBar.belmotPlayer.getPlayerEngine().pause();
                    //處理cardView
                    holder.image_play.setBackgroundResource(0);
                    bottomBar.closeBottomBar();
                }
                else{//不同首

                    bottomBar.belmotPlayer.getPlayerEngine().reset();


                    //先處理上一首
                    View vv = mRecyclerView.getChildAt(bottomBar.belmotPlayer.getPlayerEngine().getOriIdx());
                    ImageView image_play = (ImageView) vv.findViewById(R.id.imageView2);
                    image_play.setBackgroundResource(0);
                    bottomBar.closeBottomBar();


                    //再處理這首
                    playing(holder ,song);
                }

            }
            else{ //未播放


                playing(holder ,song);

            }


            //播完後
            bottomBar.belmotPlayer.getPlayerEngine().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    belmotPlayer.getPlayerEngine().release(); //當我們呼叫 release() 方法時，則會釋放掉這個被佔用的資源
                    closePicture(holder,position);
                    bottomBar.closeBottomBar();
                }
            });


            bottomBar.belmotPlayer.getPlayerEngine().setOriIdx(position);
        }





//        holder.image_album.setBackgroundResource(R.drawable.play);
//        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.play);
//        holder.image_album.setImageBitmap(PicUtil.adjustOpacity(bitmap,40));

    }

    @Override
    public void aOnItemLongClick(View view, int position) {

    }



    private void playing(MyRecyclerViewHolder holder, Song song){
        bottomBar.belmotPlayer.getPlayerEngine().setPlayingPath(song.getPathId());
        if(bottomBar.belmotPlayer.getPlayerEngine().isPause()){
            bottomBar.belmotPlayer.getPlayerEngine().start();
        }
        else{
            bottomBar.belmotPlayer.getPlayerEngine().play();
        }


        //處理cardView
        holder.image_album.setImageBitmap(song.getPic()); //重新set圖片
        holder.image_play.setBackgroundResource(R.drawable.play);


        //處理bottom bar
        bottomBar.setBarData();
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


    /**
     * 找出這首歌在播放list的index
     */
    public int findCurrentIndex(List<Song>songList,String pathId){
        int rtn = -1;
        if(StringTool.isEmpty(pathId))
            return rtn;


        int i=0;
        for(Song song:songList){
            if(StringTool.isEmpty(song.getPathId()) &&
                    song.getPathId().equals(pathId)){
                rtn = i;
            }
            i++;
        }

        return rtn;
    }


    private void openActionDlg(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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


                            DlgUtil.getInstance().getDeleteDialog(getActivity())
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
        final PlaylistDAO playlistDAO =new PlaylistDAO(getActivity());
        List<Playlist> playlists = playlistDAO.getAll();

        final String[] plAarray = new String[playlists.size()];
        int i=0;
        for(Playlist pl:playlists){
            plAarray[i] = pl.getName();
            i++;
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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


                            DlgUtil.getInstance().getDeleteDialog(getActivity())
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

}
