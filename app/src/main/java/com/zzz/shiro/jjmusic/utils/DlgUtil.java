package com.zzz.shiro.jjmusic.utils;

import android.support.v7.app.AlertDialog;
import android.content.Context;

/**
 * Created by wc on 2016/11/18.
 */
public  class DlgUtil {
    private static DlgUtil instance;

    public static DlgUtil getInstance() {
        return instance;
    }


    /**
     * 確定刪除對話框
     * @param context
     * @return
     */
    public static AlertDialog.Builder getDeleteDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //設定對話框內的項目
        builder.setTitle("提醒").setMessage("確定刪除?");

        return builder;
    }

}
