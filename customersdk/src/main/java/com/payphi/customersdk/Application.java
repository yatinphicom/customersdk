package com.payphi.customersdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by jayesh on 22-05-2017.
 */
public class Application {

    String userId;
    String paymentOptscope="";
    Boolean flag=true;
    private IAppInitializationListener listener;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String QA = "QA";
    public static final String PROD = "PROD";
    public Application() {
        // set null or default listener or accept as argument to constructor
        this.listener = null;

    }


    public interface IAppInitializationListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onSuccess(String status);

        // or when data has been loaded
        public void onFailure(String errorCode);
    }


    public void setAppInfo(final String mid, String appId, final Context context, final IAppInitializationListener listener) {


        if (!Utility.isConectionAvailable(context)) {

            if (listener != null)
                listener.onFailure("504"); // <---- fire listener here

        }

        sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString("mid", mid).commit();
        editor.putString("appId", appId).commit();
        //editor.putString("jwtTokenNew",null).commit();



        if(sharedpreferences.getString("jwtTokenNew", null)!=null){
            String data = Utility.parseJWT(sharedpreferences.getString("jwtTokenNew", null));

            try {
                JSONObject obj = new JSONObject(data);

                flag = Utility.IsTokenExpired(obj.getString("exp"));

                //Toast.makeText(context,"In flag...."+flag, Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }
        }

        if (!flag) {
            paymentOptscope="Direct";
            this.listener = listener;
            userId = getSdkUserId(context);
            getPaymentOptions(mid, context);
        } else {


            this.listener = listener;
            userId = getSdkUserId(context);
            JSONObject jsonObjectHeader = new JSONObject();
            JSONObject jsonObjectBody = new JSONObject();
            JSONObject jsonP = new JSONObject();
            JSONObject jsonReq = new JSONObject();
            ByteArrayEntity entity;
            try {
                jsonObjectHeader.put("userID", userId);
                jsonObjectHeader.put("parentMID", mid);
                jsonObjectHeader.put("requestedAt", new Date());
                // jsonObjectHeader.put("deviceID",deviceId);
                jsonObjectBody.put("password", Utility.generateSHA(appId));
                jsonObjectBody.put("userType", "APP");
                jsonP.put("authHeader", jsonObjectHeader);
                jsonP.put("payload", jsonObjectBody);

                jsonReq.put("requestData", jsonP);

                entity = new ByteArrayEntity(jsonReq.toString().getBytes("UTF-8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                AsyncHttpClient client = new AsyncHttpClient();
                client.setConnectTimeout(12000);
                client.setMaxRetriesAndTimeout(1, 12000);
                // Toast.makeText(context,"In login....", Toast.LENGTH_LONG).show();
                client.post(context, APISettings.getApiSettings().getLoginUrl(), entity, "application/json", new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();
                        try {
                            //dialog.dismiss();
                            int responseStatus = response.getJSONObject("respHeader").getInt("responseCode");

                            if (responseStatus == 200) {

                                Log.d("headers.....: ", "> " + headers.length);
                                for (int i = 0; i < headers.length; i++) {
                                    Log.d("", "header (" + headers[i].getName() + ") -> " + headers[i].getValue());
                                    if (headers[i].getName().equals("Authorization")) {
                                        editor.putString("jwtTokenNew", headers[i].getValue()).commit();
                                    }
                                }


                                getPaymentOptions(mid, context);
                                if (listener != null) {
                                    listener.onSuccess("0000"); // <---- fire listener here
                                }


                            } else {
                                String errorText = "Server communication error (" + responseStatus + ")";

                                switch (responseStatus) {
                                    case 201: {
                                        errorText = "Invalid credentials";
                                        break;
                                    }
                                    default: {
                                        responseStatus = 101;
                                    }
                                }
                                if (listener != null) {
                                    listener.onFailure(String.valueOf(responseStatus)); // <---- fire listener here
                                }
                                //    Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
                            }
                           // System.out.println("responseStatus=1111==" + responseStatus);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        //  dialog.dismiss();
                        // statusCode =504;
                        handleHttpError(statusCode, context, "login");

                        //Toast.makeText(context, "Unable to connect to server!!!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        //dialog.dismiss();
                        handleHttpError(statusCode, context, "login");
                    }
                });


                //return -1;


            } catch (Exception e) {

            }


        }
    }
    private void handleHttpError(int statusCode,Context context,String type) {
        String errorText = "Server communication error - #" + statusCode;
     //   Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
        if (statusCode == 403) {
            Application app = new Application();
            String mid= sharedpreferences.getString("mid",null).toString();
            String appId= sharedpreferences.getString("appId",null).toString();

            app.setAppInfo(mid, appId, context, new Application.IAppInitializationListener() {
                @Override
                public void onSuccess(String status) {

                }

                @Override
                public void onFailure(String errorCode) {

                }
            } );

        } else {
            statusCode=504;
            if(type.equals("login")){
                if (listener != null)
                    listener.onFailure(String.valueOf(statusCode)); // <---- fire listener here
            }

            if(type.equals("paymentopt") &&  paymentOptscope.equals("Direct")){
               // Toast.makeText(context,"paymentOptscope inside errro function=="+paymentOptscope, Toast.LENGTH_LONG).show();
                if (listener != null)
                    listener.onFailure(String.valueOf(statusCode)); // <---- fire listener here
            }

        }
    }

    private void getPaymentOptions(final String mid,final Context context){
       // Toast.makeText(context, "In Payment options..", Toast.LENGTH_LONG).show();
        JSONObject jsonObjectHeader = new JSONObject();
        JSONObject jsonObjectBody = new JSONObject();
        JSONObject jsonP = new JSONObject();
        JSONObject jsonReq = new JSONObject();
        ByteArrayEntity entity;
       String token = sharedpreferences.getString("jwtTokenNew", null);
        try {
            jsonObjectHeader.put("userID", userId);
            jsonObjectHeader.put("parentMID", mid);
            jsonObjectHeader.put("requestedAt", new Date());
            jsonObjectHeader.put("jwtToken", token);
            // jsonObjectHeader.put("deviceID",deviceId);
            jsonObjectBody.put("userType","APP");
            jsonP.put("authHeader", jsonObjectHeader);
            jsonP.put("payload", jsonObjectBody);

            jsonReq.put("requestData", jsonP);

            entity = new ByteArrayEntity(jsonReq.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            AsyncHttpClient client = new AsyncHttpClient();
            client.setConnectTimeout(12000);
            client.setMaxRetriesAndTimeout(1, 12000);
            //Toast.makeText(getApplicationContext(),Settings.getSettings().getLogInIp(), Toast.LENGTH_LONG).show();
           // System.out.println("Payment Url="+APISettings.getApiSettings().getPaymentOptUrl());

            client.post(context, APISettings.getApiSettings().getPaymentOptUrl(), entity, "application/json", new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    try {
                        //dialog.dismiss();
                        int responseStatus = response.getJSONObject("respHeader").getInt("responseCode");
                     //   Toast.makeText(context, "responseStatus=="+responseStatus, Toast.LENGTH_LONG).show();
                        if (responseStatus == 200) {
                            for (int i = 0; i < headers.length; i++) {
                                Log.d("", "header (" + headers[i].getName() + ") -> " + headers[i].getValue());
                                if (headers[i].getName().equals("Authorization")) {
                                    editor.putString("jwtTokenNew", headers[i].getValue()).commit();
                                }
                            }


                            String data = response.getJSONObject("respBody").getString("PaymentOption");







                           if(data!=null || !data.equals("")) {

                               parseOptionData(data, context);
                            if(paymentOptscope.equals("Direct")){
                                  // Toast.makeText(context,"paymentOptscope inside errro function=="+paymentOptscope, Toast.LENGTH_LONG).show();
                                   if (listener != null)
                                       listener.onSuccess(String.valueOf("0000")); // <---- fire listener here
                               }

                           }else{
                               editor.putString("paymentopt",null).commit();
                           }




                            Log.d("response.....: ", "> " + response);

                        } else {
                            String errorText = "Server communication error (" + responseStatus + ")";

                            switch (responseStatus) {
                                case 101: {
                                    errorText = "Internal Error";
                                    break;
                                }
                                case 205: {
                                    errorText = "Payment Options not configured for this merchant";
                                    break;
                                }
                                default: {
                                }
                            }
                            if(paymentOptscope.equals("Direct")){
                                if (listener != null) {
                                    listener.onFailure(String.valueOf(responseStatus)); // <---- fire listener here
                                }
                            }
                            //    Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
                        }
                       // System.out.println("responseStatus=1111==" + responseStatus);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //  dialog.dismiss();
                   // Toast.makeText(context, "Unable to connect to server in payment options !!!", Toast.LENGTH_LONG).show();
                    handleHttpError(statusCode,context,"paymentopt");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //dialog.dismiss();
                   // Toast.makeText(context, "Unable to connect to server in payment options !!!", Toast.LENGTH_LONG).show();
                    handleHttpError(statusCode,context,"paymentopt");
                }
            });

            //return -1;


        } catch (Exception e) {
                e.printStackTrace();
        }

        System.out.println("Outside catch .....!!");


    }
        private void parseOptionData(String data,Context context){
            //Toast.makeText(context,data,Toast.LENGTH_SHORT).show();
            String opt[]=data.split(",");
            String merchantPayOpt="";
            String optval;
            HashMap<String,String> map = new HashMap<String, String>();
            sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            Gson gson=new Gson() ;
            for(int i=0;i<opt.length;i++){

                if(opt[i].contains(":")){
                    optval =  opt[i].split(":")[0];
                    map.put(optval,opt[i].split(":")[1]);
                    String json = gson.toJson(map);
                    //Toast.makeText(context,json,Toast.LENGTH_SHORT).show();
                    editor.putString("subpayopt", json);
                    editor.commit();

                }else{
                    optval = opt[i];
                }
                merchantPayOpt = merchantPayOpt + optval+",";


            }

            merchantPayOpt =  merchantPayOpt.replaceAll("]","");
            merchantPayOpt =  merchantPayOpt.replace("\"", "");
            merchantPayOpt =  merchantPayOpt.substring(1);

            merchantPayOpt = merchantPayOpt.substring(0, merchantPayOpt.length()-1);

            editor.putString("paymentopt",merchantPayOpt).commit();
            //Toast.makeText(context,merchantPayOpt,Toast.LENGTH_SHORT).show();




        }


        public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x:", b));
        }
        return builder.toString();
    }

    private String getSdkUserId(Context context) {
        String userId = "";
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;

        PackageInfo packageInfo = null;
       // System.out.println("packageName===" + packageName);
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;

        byte[] cert = signatures[0].toByteArray();

        InputStream input = new ByteArrayInputStream(cert);

        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");


        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
            //  System.out.println("cert issuerDN>>" + c.getIssuerDN());
            //System.out.println("cert signature>>" + bytesToHex(c.getSignature()));
        } catch (CertificateException e) {
            e.printStackTrace();
        }


        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());


            String hexval = bytesToHex(publicKey);
            hexval = hexval.substring(0, hexval.length() - 1);


            Log.d("Example", "Cer: " + hexval);
            userId = hexval + packageName;
            userId = userId.toUpperCase();
           // System.out.println("User Id===" + userId);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return userId;
    }
    public void setEnv (String type) throws InvalidParameterException {
        if(Application.QA.equals(type)){
            APISettings.getApiSettings().setBaseUrl("https://qa.phicommerce.com/");
            APISettings.getApiSettings().setSaleBaseUrl("https://qa.phicommerce.com/");
          /* APISettings.getApiSettings().setBaseUrl("http://192.168.1.85:9191/");
            APISettings.getApiSettings().setSaleBaseUrl("http://192.168.1.85:9292/");*/

        }else if(Application.PROD.equals(type)){
            APISettings.getApiSettings().setBaseUrl("https://secure.payphi.com/");
            APISettings.getApiSettings().setSaleBaseUrl("https://secure-ptg.payphi.com/");
        }else{
            throw new InvalidParameterException("Invalid Parameter!!");
        }

    }
    public void setMerchantName(String name,Context context){
        sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString("merchantName",name).commit();
    }
}
