package com.delauneconsulting.AMION;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import android.net.ParseException;
import android.util.Log;

public class Helper {

	public static boolean isDateInRange(String date, AMIONAssignment a) {
		boolean inRange = false;
		
		Date theDate = Helper.parseDateString(date);
		
		if (a.endDate != null) {
			if (theDate.getTime() >= a.startDate.getTime() && theDate.getTime() <= a.endDate.getTime())
				return true;
		}
		
		if (theDate.getTime() == a.startDate.getTime()) {
			return true;
		}
				
		return inRange;
	}
	
	public static String getFormattedDate(Calendar c) {
		return AMION.sdf.format(c.getTime());
	}
	
	public static Date parseDateString(String dateString) {
		Date d = null;
        
		try {    
			
			if (dateString.length() > 0) {
				d = (Date)AMION.sdf.parse(dateString);
			}
            
		} catch (Exception e) {
			d = null;
		}
		return d;
	}
	public static Calendar getDateFromString(String dateString) {
		
		Calendar cal=Calendar.getInstance();
        
		try {    
			
			if (dateString.length() > 0) {
				//DateFormat formatter ; 
				Date date ; 
				//formatter = new SimpleDateFormat("dd-MMM-yy");
	            //date = (Date)formatter.parse(dateString); 
				date = (Date)AMION.sdf.parse(dateString);
	            cal.setTime(date);
			}
            
		} catch (Exception e) {
			//System.out.println("Exception :"+e);    
		}
		
	    return cal;
	}
	
    public static String getHttpResponseAsString(String url) {

        String response = "";
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            URL connectURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.connect();
            conn.getOutputStream().flush();

            is = conn.getInputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }

            response = sb.toString();
        } catch (Exception e) {
            Log.e("ERR", "biffed it getting HTTPResponse");
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
            }
        }

        return response;
    }

}
