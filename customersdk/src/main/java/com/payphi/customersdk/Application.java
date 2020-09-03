package com.payphi.customersdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
            final Handler mainHandler = new Handler(context.getMainLooper());
            userId = getSdkUserId(context);
            JSONObject jsonObjectHeader = new JSONObject();
            JSONObject jsonObjectBody = new JSONObject();
            JSONObject jsonP = new JSONObject();
            JSONObject jsonReq = new JSONObject();

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

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, jsonReq.toString());
                final Request request = new Request.Builder()
                        .url(APISettings.getApiSettings().getLoginUrl())
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handleHttpError(504, context, "login");
                    }

                    @Override
                    public void onResponse(Call call, Response data) throws IOException {
                        if(data.isSuccessful()){
                            sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
                            editor = sharedpreferences.edit();
                            try {
                                //dialog.dismiss();
                                Headers headers = data.headers();
                                String jsonData = data.body().string();
                                JSONObject response = new JSONObject(jsonData);
                                int responseStatus = response.getJSONObject("respHeader").getInt("responseCode");

                                if (responseStatus == 200) {
                                    for (int i = 0; i < headers.size(); i++) {
                                       // System.out.println(headers.name(i) + ": " + headers.value(i));
                                        if (headers.name(i).equalsIgnoreCase("Authorization")) {
                                            if (headers.value(i) != null) {
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putString("jwtTokenNew", headers.value(i)).commit();
                                            }
                                        }
                                    }


                                    getPaymentOptions(mid, context);
                                    if (listener != null) {
                                        Runnable myRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess("0000"); // <---- fire listener here
                                            } // This is your code
                                        };
                                        mainHandler.post(myRunnable);
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
                                        final int finalResponseStatus = responseStatus;
                                        Runnable myRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onFailure(String.valueOf(finalResponseStatus)); // <---- fire listener here
                                            } // This is your code
                                        };
                                        mainHandler.post(myRunnable);
                                    }
                                    //    Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
                                }
                                // System.out.println("responseStatus=1111==" + responseStatus);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            handleHttpError(data.code(), context, "login");
                        }
                    }
                });
            } catch (Exception e) {

            }
        }
    }
    private void handleHttpError(int statusCode,Context context,String type) {
        Handler mainHandler = new Handler(context.getMainLooper());
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
                if (listener != null) {
                    final int finalStatusCode = statusCode;
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailure(String.valueOf(finalStatusCode)); // <---- fire listener here
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }

            }

            if(type.equals("paymentopt") &&  paymentOptscope.equals("Direct")){
                // Toast.makeText(context,"paymentOptscope inside errro function=="+paymentOptscope, Toast.LENGTH_LONG).show();
                if (listener != null) {
                    final int finalStatusCode1 = statusCode;
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailure(String.valueOf(finalStatusCode1)); // <---- fire listener here
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }
            }

        }
    }

    private void getPaymentOptions(final String mid,final Context context){
        final Handler mainHandler = new Handler(context.getMainLooper());
        // Toast.makeText(context, "In Payment options..", Toast.LENGTH_LONG).show();
        JSONObject jsonObjectHeader = new JSONObject();
        JSONObject jsonObjectBody = new JSONObject();
        JSONObject jsonP = new JSONObject();
        JSONObject jsonReq = new JSONObject();

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


            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, jsonReq.toString());
            final Request request = new Request.Builder()
                    .url(APISettings.getApiSettings().getPaymentOptUrl())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response rdata) throws IOException {
                    if(rdata.isSuccessful()){
                        sharedpreferences = context.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();
                        try {
                            //dialog.dismiss();
                            Headers headers = rdata.headers();
                            String jsonData = rdata.body().string();
                            JSONObject response = new JSONObject(jsonData);
                            final int responseStatus = response.getJSONObject("respHeader").getInt("responseCode");
                            //   Toast.makeText(context, "responseStatus=="+responseStatus, Toast.LENGTH_LONG).show();
                            if (responseStatus == 200) {
                                for (int i = 0; i < headers.size(); i++) {
                                 //   System.out.println(headers.name(i) + ": " + headers.value(i));
                                    if (headers.name(i).equalsIgnoreCase("Authorization")) {
                                        if (headers.value(i) != null) {
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString("jwtTokenNew", headers.value(i)).commit();
                                        }
                                    }
                                }

                                String data = response.getJSONObject("respBody").getString("PaymentOption");

                                if(data!=null || !data.equals("")) {

                                    parseOptionData(data, context);
                                    if(paymentOptscope.equals("Direct")){
                                        // Toast.makeText(context,"paymentOptscope inside errro function=="+paymentOptscope, Toast.LENGTH_LONG).show();
                                        if (listener != null){
                                            Runnable myRunnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onSuccess(String.valueOf("0000")); // <---- fire listener here
                                                } // This is your code
                                            };
                                            mainHandler.post(myRunnable);
                                        }
                                    }

                                }else{
                                    editor.putString("paymentopt",null).commit();
                                }




                                // Log.d("response.....: ", "> " + response);

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
                                        Runnable myRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onFailure(String.valueOf(responseStatus)); // <---- fire listener here
                                            } // This is your code
                                        };
                                        mainHandler.post(myRunnable);
                                    }
                                }
                                //    Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
                            }
                            // System.out.println("responseStatus=1111==" + responseStatus);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        handleHttpError(rdata.code(),context,"paymentopt");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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


            // Log.d("Example", "Cer: " + hexval);
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
