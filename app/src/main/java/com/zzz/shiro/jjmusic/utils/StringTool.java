package com.zzz.shiro.jjmusic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTool {
	public final static SimpleDateFormat sdf_notime = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 判斷該字元是否為中文字
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c){
//		4E00..9FFF
		return c>=19968 && c<=40959;
	}
	
	/**
	 * 判斷是否為空字串
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if( str==null || str.replaceAll("\\p{Space}", "").equals("") ){
			return true;
		}
		return false;
	}
	
	
	public static String StringZero(String str){
	  if(isEmpty(str)){
	    return "0";
	  }
	  else{
	    return str;
	  }
	}
	
	public static String handleJson(String json){
		if(isEmpty(json))
			return json;
		
		return json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
	}
	
	
	/**
	 * String 轉 Date
	 * 2016年4月29日 上午11:47:08
	 */
	public static Date stringToDate(String dateStr){
		if(StringTool.isEmpty(dateStr))
			return null;
		
		
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	@SuppressWarnings("deprecation")
	public static String urlConverter(String str){
		if(isEmpty(str)){
			return str;
		}
		
		return java.net.URLEncoder.encode(str);
	}
	
	/**
	 * 去換行字元
	 * 2016年5月3日 下午2:31:34
	 */
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
