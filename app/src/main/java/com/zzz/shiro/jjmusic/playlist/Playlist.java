package com.zzz.shiro.jjmusic.playlist;

import java.util.Date;
import java.util.List;

/**
 * Created by wc on 2016/10/31.
 */
public class Playlist {
    private String unid;
    private String name;
    private Date buildTime;



    private List<String> musicList;


    public List<String> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<String> musicList) {
        this.musicList = musicList;
    }
    public Date getBuildTime() {
        return buildTime;
    }

    public String getUnid() {
        return unid;
    }

    public void setUnid(String unid) {
        this.unid = unid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }
}
