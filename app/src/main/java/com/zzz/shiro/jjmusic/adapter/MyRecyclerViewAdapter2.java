/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 作者：安卓猴
 *  *  *  * 微博：@安卓猴
 *  *  *  * 博客：http://sunjiajia.com
 *  *  *  * Github：https://github.com/opengit
 *  *  *  *
 *  *  *  * 注意**：如果您使用或者修改该代码，请务必保留此版权信息。
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.zzz.shiro.jjmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzz.shiro.jjmusic.R;
import com.zzz.shiro.jjmusic.Song;

import java.util.List;


public class MyRecyclerViewAdapter2 extends RecyclerView.Adapter<MyRecyclerViewHolder> {


  //自定義一個Listener 介面
  public interface AdapterOnItemClickListener2 {
    void aOnItemClick(View view, int position, MyRecyclerViewHolder holder);


    void aOnItemLongClick(View view, int position);
  }



  public Context mContext;
  public LayoutInflater mLayoutInflater;
  public AdapterOnItemClickListener2 mOnItemClickListener;
  public BtnListenerInterface btnListenerInterface;

  public List songList; //歌們


    /**
     * 建構子
     * @param mContext
     * @param songList
     */
  public MyRecyclerViewAdapter2(Context mContext, List songList) {
    this.mContext = mContext;
    mLayoutInflater = LayoutInflater.from(mContext);

    this.songList = songList;

  }


  /**
   * 建立ViewHolder
   */
  @Override public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View mView = mLayoutInflater.inflate(R.layout.item_main, parent, false);
    MyRecyclerViewHolder mViewHolder = new MyRecyclerViewHolder(mView);
    return mViewHolder;
  }

  /**
   * 綁定ViewHoler，给item中的控件設置data
   */
  @Override public void onBindViewHolder(final MyRecyclerViewHolder holder, final int position) {
    //將 Data 填入
    Song song = (Song) songList.get(position);
    holder.mTextView.setText(song.getTitle());
    holder.image_album.setImageBitmap(song.getPic());


//    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ggg);
    holder.image_album.setBackgroundResource(R.drawable.nopic);


    //設置Listener
    if (mOnItemClickListener != null) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnItemClickListener.aOnItemClick(holder.itemView, position,holder);
        }
      });

      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
          mOnItemClickListener.aOnItemLongClick(holder.itemView, position);
          return true;
        }
      });
    }

    holder.ib_edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d("","RecyclerAdapter btn click");
        btnListenerInterface.onClick(position);
      }
    });


  }




  @Override public int getItemCount() {
    return songList.size();
  }



  public void setOnItemClickListener(AdapterOnItemClickListener2 listener) {
    this.mOnItemClickListener = listener;
  }



  public void setBtnClickListener(BtnListenerInterface listener) {
    this.btnListenerInterface = listener;
  }
}
