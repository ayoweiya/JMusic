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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzz.shiro.jjmusic.R;


public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

  public TextView mTextView;
  public ImageView image_album;
  public ImageView image_play;
  public ImageButton ib_edit;

  public MyRecyclerViewHolder(View itemView) {
    super(itemView);
    mTextView = (TextView) itemView.findViewById(R.id.id_title);
    image_album = (ImageView) itemView.findViewById(R.id.imageView1);
    image_album.setMaxWidth(100);
    image_album.setMaxHeight(100);

    image_play = (ImageView) itemView.findViewById(R.id.imageView2);
    image_play.setMaxWidth(100);
    image_play.setMaxHeight(100);


    ib_edit = (ImageButton) itemView.findViewById(R.id.ib_itemEdit);
  }
}
