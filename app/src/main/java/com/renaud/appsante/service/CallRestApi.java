package com.renaud.appsante.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.renaud.appsante.model.Citizen;
import com.renaud.appsante.model.Permit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CallRestApi extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processSuccessful(Boolean output);
    }

    AsyncResponse asyncResponse = null;
    private Context context;
    private Citizen citizen;
    private Permit permit;

    public CallRestApi(Context c, Citizen citizen, AsyncResponse asyncResponse)
    {
        this.context = c;
        this.citizen = citizen;
        this.asyncResponse = asyncResponse;
    }

    public CallRestApi(Context c, Permit permit, AsyncResponse asyncResponse)
    {
        this.context = c;
        this.permit = permit;
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        HTTPHandler httpHandler = new HTTPHandler();
        if (citizen != null)
        {
            httpHandler = new HTTPHandler(citizen);
        }
        try{

            if (citizen != null)
                result = httpHandler.getCitizenCall(urls[0]);
            else if (permit != null)
                result = httpHandler.getPermitCall(urls[0]);

            Log.i("Connection", "COMPLETE");
            return result;
        }catch (Exception ex)
        {
            ex.printStackTrace();
            Log.i("Error", ex.getMessage());
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onPostExecute(String str)
    {
        super.onPostExecute(str);

        try {
            if (citizen != null) {
                asyncResponse.processSuccessful(!str.equals(""));
            }
            else {
                if (permit != null && str != null && !str.equals(""))
                {
                    createPermit(str);
                }
                else
                    asyncResponse.processSuccessful(false);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
            Log.i("ERROR", ex.getMessage());
        }
    }

    private void createPermit(String str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str);

        LocalDate dateTest = LocalDate.parse(jsonObject.get("dateTest").toString());
        if (dateTest.isAfter(LocalDate.now().minusDays(3)) &&
                !jsonObject.getString("results").equals("POSITIVE"))
        {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Permits");
            query.whereContains("citizen", ParseUser.getCurrentUser().getString("NAS"));
            query.whereEqualTo("dateTest", Date.from(dateTest.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        asyncResponse.processSuccessful(false);
                    }
                    else {
                        permit.setDateTest(dateTest);
                        try {
                            permit.setType(jsonObject.getString("type"));


                        switch (permit.getType()) {
                            case Permit.TYPE_TEST:
                                permit.setDateExpiration(permit.getDateTest().plusDays(15));
                                break;
                            case Permit.TYPE_VACCIN:
                                permit.setNbrDose(jsonObject.getInt("nbrDose"));
                                permit.setDateExpiration(permit.getNbrDose() == 1
                                        ? permit.getDateTest().plusMonths(2) : permit.getDateTest().plusYears(1));
                                break;
                        }
                        encodePermitToQR(250,200);

                        asyncResponse.processSuccessful(true);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                }
            });
        }
        else {
            asyncResponse.processSuccessful(false);
        }
    }

    private void encodePermitToQR(Integer width, Integer height) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(permit.toQrData(), BarcodeFormat.QR_CODE, width,height);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        permit.setQrCode(stream.toByteArray());
        bmp.recycle();
    }
}
