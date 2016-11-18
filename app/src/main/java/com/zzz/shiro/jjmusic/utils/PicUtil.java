package com.zzz.shiro.jjmusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;

/**
 * Created by wc on 2016/10/5.
 */
public class PicUtil {

    public static Bitmap  adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }


    public static Bitmap resize(byte[] picByte){
        Bitmap songImage = null;
        //改圖片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(picByte, 0, picByte.length, options);

        options.inSampleSize = calculateSize(options, Constants.album_width, Constants.album_height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        songImage = BitmapFactory.decodeByteArray(picByte, 0, picByte.length, options);

        return songImage;
    }

    private static int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int size = 1;

        if (height > reqHeight || width > reqWidth)
        {
            if (width > height)
            {
                size = Math.round((float) height / (float) reqHeight);
            }
            else
            {
                size = Math.round((float) width / (float) reqWidth);
            }
        }
        return size;
    }
}
