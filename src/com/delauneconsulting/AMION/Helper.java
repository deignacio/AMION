package com.delauneconsulting.AMION;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class Helper {

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
