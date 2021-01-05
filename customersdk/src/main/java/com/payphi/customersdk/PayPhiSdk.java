package com.payphi.customersdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by swapnil.g on 6/6/2017.
 */
 public  class PayPhiSdk {
    public static final String QA = "QA";
    public static final String PROD = "PROD";

    public static final String YES = "YES";
    public static final String NO = "NO";
   // public static final String FRAGMENT = "FRAGMENT";
    public static final String DIALOG = "DIALOG";
    public static final String POGO="POGO";
    public static final String BIJLIPAY="BIJLIPAY";
    private static Application application;
    private static IAppPaymentResponseListener paymentlistener;
    private static IAppInitializationListener loginListener;
    private static IAppMessageListener messageListener;
    static     Map<Integer,String> sdkMap;
    public interface IAppPaymentResponseListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered

        public void onPaymentResponse(int resultCode, Intent data);
    }

    public interface IAppPaymentResponseListenerEx extends IAppPaymentResponseListener {
        public void onPaymentResponse(int resultCode,Intent data, Map<String, String> additionalInfo);
    }

    public interface IAppMessageListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered

        public String onMessageRequest(int resultCode);

    }


    public interface IAppInitializationListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onSuccess(String status);

        // or when data has been loaded
        public void onFailure(String errorCode);


    }
    public PayPhiSdk() {
        // set null or default listener or accept as argument to constructor
        this.paymentlistener = null;
        this.messageListener = null;

    }
    public static android.support.v4.app.Fragment makePayment(Context context, Intent intent, String display,final IAppPaymentResponseListener listener) throws InvalidParameterException {
        paymentlistener = listener;
//        Toast.makeText(context,"make payment called",Toast.LENGTH_LONG).show();
//        Log.d("In make payment","-------");
        switch (display) {
            case DIALOG:
                Activity con = new Activity();
               // System.out.print("In Dialog...");
                //Intent intent = new Intent(context, PaymentOptions.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // con.setIntent(intent);
                context.startActivity(intent.setFlags(FLAG_ACTIVITY_NEW_TASK));

                break;
            case "FRAGMENT":

                break;
            default:
                Intent nullintent = new Intent();
                onPaymentResponse(0,4,nullintent);

        }

        return null;
    }

   /* protected void startActivityForResult(Intent data, int code) {
        {

            //Log.e("", "insede method startActivityForR
            // esult()");
            System.out.println("data="+data.toString());
            System.out.println("code="+code);


           // Toast.makeText(this,""+data.toString(),Toast.LENGTH_LONG).1ow();
        }


    }*/

    public static void onPaymentResponse(int requestCode, int resultCode, Intent data, Map<String, String> additionalInfo) {

        if (paymentlistener != null) {
            if (paymentlistener instanceof IAppPaymentResponseListenerEx) {
                ((IAppPaymentResponseListenerEx)(paymentlistener)).onPaymentResponse(resultCode, data, additionalInfo);
            } else {
                paymentlistener.onPaymentResponse(resultCode, data);
            }
        }
    }

    public static void onPaymentResponse(int requestCode, int resultCode, Intent data) {
        onPaymentResponse(requestCode, resultCode, data, new HashMap<String, String>());
    }

    public static String onMessageRequest(int resultCode){
        String msg="";

        if(messageListener!=null){
       msg =  messageListener.onMessageRequest(resultCode);
        }
        return msg;
    }


        public   static Map getMessage(){

            if(sdkMap!=null){
                    return sdkMap;
            }else{
                sdkMap =  new HashMap<>();
                sdkMap.put(10," Transaction yet not recieved to server please try again");
                sdkMap.put(11 ,"We have still not received your payment confirmation.");
                sdkMap.put(12 ,"Error in processing Transaction status");
                sdkMap.put(13, "unable to generate QR");
                sdkMap.put(14 ,"Select Your Bank");
                sdkMap.put(15, "Enter or Scan Aadhaar Number");
                sdkMap.put(16, "Enter valid Aadhaar No.");
                sdkMap.put(17, "Last Transaction is unknown please try after 15 mins after verifing Consumer account detials.");
                sdkMap.put(18, "Last Delivery is already paid");
            }
          return sdkMap;
        }
      public static void setLocalizedMessageMap(Map<Integer, String> localizedMap){

        sdkMap = localizedMap;

      }

}
