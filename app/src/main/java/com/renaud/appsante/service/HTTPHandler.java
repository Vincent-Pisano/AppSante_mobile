package com.renaud.appsante.service;

import android.util.Log;

import com.renaud.appsante.model.Citizen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class HTTPHandler {

    private static final String TAG = HTTPHandler.class.getSimpleName();

    private Citizen citizen;

    public HTTPHandler(Citizen citizen) {
        this.citizen = citizen;
    }
    public HTTPHandler() { }

    public String getCitizenCall(String reqUrl) {
        String response = null;

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("nas", citizen.getNAS());
            jsonParam.put("firstName", citizen.getFirstName());
            jsonParam.put("lastName", citizen.getLastName());
            jsonParam.put("password", citizen.getPassword());
            jsonParam.put("birthDate", citizen.getBirthDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty( "Content-Type", "application/json" );
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(jsonParam.toString());
            osw.flush();
            osw.close();
            os.close();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public String getPermitCall(String reqUrl) {
        String response = null;

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}