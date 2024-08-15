package com.example.ember.Utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class NominatimGeocodingTask extends AsyncTask<Double, Void, String> {

    @Override
    protected String doInBackground(Double... params) {
        double latitude = params[0];
        double longitude = params[1];
        String response = "";
        try {
            String urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + latitude + "&lon=" + longitude + "&zoom=10&addressdetails=1";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            response = content.toString();

            JSONObject jsonObject = new JSONObject(response);
            JSONObject address = jsonObject.getJSONObject("address");
            if (address.has("city")) {
                return address.getString("city"); // מחזיר את שם העיר
            } else if (address.has("town")) {
                return address.getString("town"); // עבור אזורים קטנים יותר
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String cityName) {
        if (cityName != null) {
            Log.d("NominatimTask", "City Name: " + cityName);
            // ניתן להשתמש כאן בפעולה נוספת אם נדרש
        } else {
            Log.e("NominatimTask", "Failed to get city name");
        }
    }
}
