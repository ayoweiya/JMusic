package com.zzz.shiro.jjmusic.playlist;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzz.shiro.jjmusic.database.SQLiteDAOBase;
import com.zzz.shiro.jjmusic.database.TaskDBOpenHelper;

public class PlaylistDAO extends SQLiteDAOBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private final String TABLE_NAME = TaskDBOpenHelper.PLAYLIST_TABLE;



	public PlaylistDAO(Context context)
	{
		super(context);
	}
	
	public long save(Playlist pl)
	{
		if (pl.getUnid() == null) {

			pl.setUnid(UUID.randomUUID().toString());
		}

		SQLiteDatabase db = this.taskDBOpenHelper.getWritableDatabase();
		long rows = 0;

		ContentValues cv = ObjectToContentValues(pl);
		String selection = "unid=?";
		String[] selectionArgs = new String[] { pl.getUnid() };

		rows = db.update(TABLE_NAME, cv, selection, selectionArgs);
		if (rows <= 0)
			rows = db.insert(TABLE_NAME, null, cv);

		return rows;
	}


	/**
	 * 用name找出一筆資料
	 */
	public Playlist getByName(String name){

		SQLiteDatabase db = taskDBOpenHelper.getReadableDatabase();

		Playlist cr = null;

//		String[] columns = new String[] { "unid"  };
//		String selection = "publishDate_e < ? OR unitId IS null OR publishDate_e IS null";
//		String selectionArgs[] =  {date};
//		String groupBy = null;
//		String having = null;
//		String orderBy = null;
//		Cursor c = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
//
		String selectionArgs[] =  {name};
		Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE name = ?", selectionArgs);

		if(c.getCount()>0)
		{
			c.moveToFirst();
			for(int i=0; i<c.getCount();i++){

				cr = dataToObject(c);

				c.moveToNext();
			}

		}
		c.close();
		return cr;
	}



	public long delete(Playlist ace)
	{
		SQLiteDatabase db = this.taskDBOpenHelper.getWritableDatabase();
		long rows = 0;

		String selection = "unid=?";
		String[] selectionArgs = new String[] { ace.getUnid() };
		
		rows = db.delete(TABLE_NAME, selection, selectionArgs);

		return rows;
	}

	public long deleteAll()
	{
		SQLiteDatabase db = this.taskDBOpenHelper.getWritableDatabase();
		long rows = 0;


		rows = db.delete(TABLE_NAME, null,null);

		return rows;
	}

	@Override
	protected ContentValues ObjectToContentValues(Object obj) {

		Playlist playlist = (Playlist) obj;
		ContentValues cv = new ContentValues();
		
		cv.put("unid",playlist.getUnid());
		cv.put("name", playlist.getName());
		if(playlist.getBuildTime()!=null)
			cv.put("buildTime", fullFormat.format(playlist.getBuildTime()));

		cv.put("musicList", ListToString(playlist.getMusicList()));

		return cv;
	}
	
	
	
	/**
	 */
	public List<Playlist> getAll(){
		List<Playlist> aList = new ArrayList<Playlist>();
		SQLiteDatabase db = taskDBOpenHelper.getReadableDatabase();

		String selectionArgs[] =  {};
		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, selectionArgs);
		
		if(c.getCount()>0)
		{
			c.moveToFirst();
			for(int i=0; i<c.getCount();i++){

				Playlist a = dataToObject(c);
				aList.add(a);
				c.moveToNext();
			}
			
		}
		c.close();
		return aList;
	}
	
	public Playlist dataToObject(Cursor c)
	{
		Playlist a= new Playlist();
		
		a.setUnid(c.getString(c.getColumnIndex("unid")));
		a.setName(c.getString(c.getColumnIndex("name")));

		int timeCnt = c.getColumnIndex("buildTime");
		if(c.getString(timeCnt)!=null){
			try {
				a.setBuildTime(dateFormat.parse(c.getString(timeCnt)));
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		a.setMusicList(stringToList(c.getString(c.getColumnIndex("musicList"))));

		return a;
	}

	

	//清除/重建表單
	public void clearUpLoadRecord()
	{
		SQLiteDatabase db = this.taskDBOpenHelper.getWritableDatabase();
//		db.execSQL("DROP TABLE IF EXISTS " + TaskDBOpenHelper.ANN_READ_TO_UPLOAD_TABLE);
//		db.execSQL("CREATE TABLE IF NOT EXISTS " + TaskDBOpenHelper.ANN_READ_TO_UPLOAD_TABLE + " (empId TEXT,annUnid TEXT,readDate DATETIME,annType TEXT);");

	}
	


	
	/**
	 * Use @ Setting.java
	 * 依unidList 刪除
	 * @return
	 */
	public long deleteByUnidList(List<String> unidList)
	{
		SQLiteDatabase db = this.taskDBOpenHelper.getWritableDatabase();
		long rows = 0;

		if(unidList ==null || unidList.isEmpty())
			return rows;
		
		String selection = "";
		String unidStr = "unid=?";
		String orStr=" or ";
		
		for(int i=0; i<unidList.size(); i++){
			selection = selection+unidStr+orStr;
		}
		
		if(selection!=null && !selection.equals("")){
			selection = selection.substring(0, selection.lastIndexOf(orStr));
		}
		
		String[] selectionArgs =(String[]) unidList.toArray();
		
		rows = db.delete(TABLE_NAME, selection, selectionArgs);

		return rows;
	}
}
