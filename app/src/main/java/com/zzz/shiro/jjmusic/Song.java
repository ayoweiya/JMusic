package com.zzz.shiro.jjmusic;

import android.graphics.Bitmap;

/**
 * Created by wc on 2016/9/21.
 */
public class Song {
    public long id;
    public String title;
    public String album;
    public Bitmap pic;
    public String pathId;
    public String singer;




    public Song(long id, String title, String album, Bitmap pic , String pathId , String singer) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.pic = pic;
        this.pathId = pathId;
        this.singer = singer;

    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }
}
