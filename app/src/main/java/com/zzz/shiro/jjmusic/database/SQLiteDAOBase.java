package com.zzz.shiro.jjmusic.database;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

@SuppressLint("SimpleDateFormat")
public abstract class SQLiteDAOBase implements Serializable{

	private static final long serialVersionUID = 1L;
	protected TaskDBOpenHelper taskDBOpenHelper;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
	protected SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final String divideStr = "\u2606";
	private final String separateStr = "\u2586";
	
//	private final String divideStr = ",";
//	private final String separateStr = ":";
	
	public SQLiteDAOBase(Context context)
	{
		taskDBOpenHelper = TaskDBOpenHelper.getInstance(context);
	}
	

	protected abstract ContentValues ObjectToContentValues(Object obj) ;
	/**
	 * String => List<String> list
	 * @param value
	 * @return
	 */
	public List<String> stringToList(String value)
	{
		List<String> resultList = new ArrayList<String>();
		
		if(value!=null && !value.equals(""))
		{
			String [] array = value.split(divideStr);
			for(String item : array)
			{
				resultList.add(item);
			}
		}
		
		return resultList;
	}
	/**
	 * List<String> list => String
	 * @param list
	 * @return
	 */
	public String ListToString(List<String> list)
	{
		String result = "";
		
		if(list!=null && !list.isEmpty())
		{
			for(String item : list)
			{
				result = result+item+divideStr;
			}
		}
		
		if(result!=null && !result.equals(""))
			result = result.substring(0, result.lastIndexOf(divideStr));
		
		return result;
	}

	/**
	 * Map<String,Integer>  => String
	 * @param map
	 * @return
	 */
	public String mapIntegerToString( Map<String,Integer> map ){
		String result = "";
		if(map!=null && !map.isEmpty()){
			for(String key : map.keySet()){
				result = result + key + separateStr + String.valueOf(map.get(key)) + divideStr;
			}
		}
		
		if(result!=null && !result.equals("")){
			result = result.substring(0, result.lastIndexOf(divideStr));
		}
		
		return result;
	}
	
	/**
	 * String => Map<String , Integer> map
	 * @param str
	 * @return
	 */
	public Map<String,Integer> stringToMapInteger(String str){
		Map<String,Integer> map = new HashMap<String,Integer> ();
		
		if(str !=null && !str.equals("")){
			String [] array = str.split(divideStr);
			
			for(String item : array){
				String [] temp = item.split(separateStr);
				
				if(temp.length!=2){
					continue;
				}
				
				map.put(temp[0], Integer.parseInt(temp[1]==null? "0" : temp[1]));
				temp = null;
			}
			
			array = null;
		}
		
		return map;
	}
	
	/**
	 * Map<String,String>  => String
	 * @param map
	 * @return
	 */
	public String mapToString( Map<String,String> map ){
		String result = "";
		if(map!=null && !map.isEmpty()){
			for(String key : map.keySet()){
				result = result + key + separateStr + map.get(key) + divideStr;
			}
		}
		
		if(result!=null && !result.equals("")){
			result = result.substring(0, result.lastIndexOf(divideStr));
		}
		
		return result;
	}
	
	/**
	 * String => Map<String , String> map
	 * @param str
	 * @return
	 */
	public Map<String,String> stringToMap(String str){
		Map<String,String> map = new HashMap<String,String> ();
		
		if(str !=null && !str.equals("")){
			String [] array = str.split(divideStr);
			
			for(String item : array){
				String [] temp = item.split(separateStr);
				
				if(temp.length!=2){
					continue;
				}
				
				map.put(temp[0], temp[1]);
				temp = null;
			}
			
			array = null;
		}
		
		return map;
	}
}

