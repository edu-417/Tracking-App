package com.example.eduardo.tabtest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class NetworkUtilities {

    private static final String TAG = NetworkUtilities.class.getSimpleName();

    public static final String BASE_DOMAIN = "https://pure-bayou-70833.herokuapp.com/";


    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String getJSON(String baseURL){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String json = null;

        try {
            URL url = new URL(baseURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                json = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                json = null;
            }
            json = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null)urlConnection.disconnect();
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return json;
    }


    @Nullable
    public static Pair<Integer, String> postJSON(String baseURL, JSONObject json){
        HttpURLConnection urlConnection = null;
        OutputStreamWriter osw;

        try {
            URL url = new URL(baseURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty( "Content-Type", "application/json" );
            urlConnection.setRequestProperty( "Accept", "application/json" );
            urlConnection.connect();

            OutputStream os = urlConnection.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");
            Log.d(TAG, "POST JSON: "  + json.toString());
            osw.write(json.toString());
            osw.flush();
            osw.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = urlConnection.getResponseCode();
            String HttpResponseMessage = urlConnection.getResponseMessage();
            if (HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_CREATED) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                HttpResponseMessage = sb.toString();
            }

            Log.d(TAG, "RESPONSE POST JSON: " + HttpResponseMessage);
            return Pair.create(HttpResult, HttpResponseMessage);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return Pair.create(-1, "Error Response Posting Json");
    }

}
