package com.zzz.shiro.jjmusic.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBOpenHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "jMusic";   // 資料庫名稱
	private static final int DATABASE_VERSION = 1;  // 版本,此一數字一改(不管變大變小),資料即刪並重建!
	
	private static TaskDBOpenHelper mInstance;
	

	public static final String PLAYLIST_TABLE = "playlist";


	public TaskDBOpenHelper(Context context)
	  {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	public synchronized static TaskDBOpenHelper getInstance(Context context){
		if (mInstance == null) {
			mInstance = new TaskDBOpenHelper(context);
		}
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ PLAYLIST_TABLE
				+ " (unid TEXT PRIMARY KEY,name TEXT,content TEXT,"
				+ "buildTime DATETIME , musicList TEXT);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS "+EMPLOYEE_TABLE);
		
		dropTable(db);
		onCreate(db);
	}
	

	public void dropTable(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE);

		//===========上傳DB不DROP============================
	}

}
