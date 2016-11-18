package com.zzz.shiro.jjmusic.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zzz.shiro.jjmusic.R;
import com.zzz.shiro.jjmusic.playlist.Playlist;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by wc on 2016/10/31.
 */
public class PlaylistAdapter extends BaseAdapter {



    private Activity activity;
    private List<Playlist> mList;

    private static LayoutInflater inflater = null;
    public BtnListenerInterface btnListenerInterface;


    public PlaylistAdapter(Activity a, List<Playlist> list)
    {
        activity = a;
        mList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final  int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view==null)
        {
            vi = inflater.inflate(R.layout.item_playlist, null);
        }

        TextView textview = (TextView) vi.findViewById(R.id.playlist_name);

        textview.setText(mList.get(position).getName());

        Button btn_del = (Button) vi.findViewById(R.id.btn_del);

        btn_del.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Log.d("","Adapter btn click");
                btnListenerInterface.onClick(position);
            }
        });



        return vi;
    }

    public void reload(List<Playlist> list){
//        mList = list;
        notifyDataSetChanged();
    }



    public void setBtnClickListener(BtnListenerInterface listener) {
        this.btnListenerInterface = listener;
    }

}
