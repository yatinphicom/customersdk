package com.payphi.customersdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payphi.customersdk.util.Message;
import com.payphi.customersdk.util.PayForm;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class PaymentOptions extends FragmentActivity {
    private Context mContext;

    LinearLayout mLinearLayout;
    private Button mButton;
    int i = 0;
    String vpa="";
    String amount="";
    String tranRefNo="";
    String merchantId="";
    String currencyCode="";
    String secureToken="";
    String customerID="";
    String customerEmailID="";
    String invoiceNo="";
    String payType="";
    CardView cardView1;
    CardView cardView2;
    CardView cardView3;
    CardView cardView4;
    CardView cardView5;
    CardView cardView6;
    CardView cardView7;
    CardView cardView8;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ProgressDialog dialog; // this = YourActivity
    private FragmentTransaction mFragmentTransaction;
    Map<Integer,String> map = new HashMap();
    public static ProgressDialog paydialog; // this = YourActivity
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor qreditor;
    private FragmentManager mFragmentManager;
    String testUPiString =  "upi://pay?pa=nadeem@npci&pn=nadeem%20chinna&mc=0000&tid=cxnkjcnkjdfdvjndkjfvn&tr=4894\n" +
            "398cndhcd23&tn=Pay%20to%20mystar%20store&am=10&mam=null&cu=INR&url=https://mystar.co\n" +
            "m/orderid=9298yw89e8973e87389e78923ue892";
    private String qrType;
    private String mobileNo;
   private String addlParam1,addlParam2;
    String qrString = "";
    String invoiceList;
    String msg = "Transaction rejected please try again later.";
    FrameLayout frameLayout;
    LinearLayout linearLayout;
     String serviceCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.paymentopt);


        cardView1 = (CardView) findViewById(R.id.c1);
        cardView2 = (CardView) findViewById(R.id.c2);
        cardView3 = (CardView) findViewById(R.id.c3);
        cardView4 = (CardView) findViewById(R.id.c4);
        cardView5 = (CardView) findViewById(R.id.c5);
        cardView6 = (CardView) findViewById(R.id.c6);
        cardView7 = (CardView) findViewById(R.id.c7);
        cardView8 = (CardView) findViewById(R.id.c8);

        imageView1 = (ImageView) findViewById(R.id.i1);
        imageView2 = (ImageView) findViewById(R.id.i2);
        imageView3 = (ImageView) findViewById(R.id.i3);
        imageView4 = (ImageView) findViewById(R.id.i4);
        imageView5 = (ImageView) findViewById(R.id.i5);
        imageView6 = (ImageView) findViewById(R.id.i6);
        imageView7 = (ImageView) findViewById(R.id.i7);
        imageView8 = (ImageView) findViewById(R.id.i8);




        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
        cardView4.setVisibility(View.GONE);
        cardView5.setVisibility(View.GONE);
        cardView6.setVisibility(View.GONE);
        cardView7.setVisibility(View.GONE);
        cardView8.setVisibility(View.GONE);



        CheckforConnection();
        SetMessageMap();
        paydialog = new ProgressDialog(this);
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        frameLayout  = (FrameLayout) findViewById(R.id.fragId);
        frameLayout.setVisibility(View.INVISIBLE);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayoutid);

         sharedpreferences = this.getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedpreferences.edit();
      //  Toast.makeText(this,sharedpreferences.getString("paymentopt", null).toString(),Toast.LENGTH_SHORT).show();
        if(sharedpreferences.getString("jwtTokenNew", null)==null || sharedpreferences.getString("paymentopt", null)==null) {
            if (sharedpreferences.getString("paymentopt", null) != null){
                if (sharedpreferences.getString("paymentopt", null).equals("")) {
                    Intent retrn = new Intent();
                    setResult(4, retrn);
                    finish();
                    return;
                }
        }


            dialog = new ProgressDialog(this);

            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Initializing..Please wait!");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            String mid= sharedpreferences.getString("mid",null).toString();
            String appId= sharedpreferences.getString("appId",null).toString();

            Application app = new Application();

            app.setAppInfo(mid, appId, getApplicationContext(), new Application.IAppInitializationListener() {
                @Override
                public void onSuccess(String status) {
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }

                @Override
                public void onFailure(String errorCode) {
                    dialog.dismiss();
                    int code=3;
                    // Toast.makeText(getApplicationContext(),"errorCode="+errorCode,Toast.LENGTH_LONG).show();
                    if(errorCode.equals("201") || errorCode.equals("205")){
                        code=4;
                    }
                    Intent retrn = new Intent();
                    setResult(code,retrn);
                    finish();
                    return;
                }
                      } );
        } else {
            String paymentoption = sharedpreferences.getString("paymentopt", null).toString();
           // CreatePaymentoptions(paymentoption);

            if(getIntent().getSerializableExtra("Amount") != null) {
                amount = getIntent().getSerializableExtra("Amount").toString();

                DecimalFormat df = new DecimalFormat();
                df.setMinimumFractionDigits(2);
                Float f= Float.parseFloat(amount);
                df.format(f);

                TextView amt = (TextView) findViewById(R.id.amt);
                amt.setText("Rs. "+String.format("%.2f", f));

            }
            if(getIntent().getSerializableExtra("MerchantTxnNo") != null) {
                tranRefNo= getIntent().getSerializableExtra("MerchantTxnNo").toString();
              //  TextView oId = (TextView) findViewById(R.id.orderId);
              //  oId.setText(tranRefNo);

            }
            if(getIntent().getSerializableExtra("CurrencyCode") != null) {
                currencyCode= getIntent().getSerializableExtra("CurrencyCode").toString();

            }
            if(getIntent().getSerializableExtra("MerchantID") != null) {
                merchantId= getIntent().getSerializableExtra("MerchantID").toString();

            }
            if(getIntent().getSerializableExtra("SecureToken") != null) {
                secureToken= getIntent().getSerializableExtra("SecureToken").toString();
            }
            if(getIntent().getSerializableExtra("CustomerEmailID") != null) {
                customerEmailID= getIntent().getSerializableExtra("CustomerEmailID").toString();
            }

            if(getIntent().getSerializableExtra("addlParam1") != null) {
                addlParam1= getIntent().getSerializableExtra("addlParam1").toString();
            }
            if(getIntent().getSerializableExtra("addlParam2") != null) {
                addlParam2= getIntent().getSerializableExtra("addlParam2").toString();
            }


            TextView merName = (TextView) findViewById(R.id.merchntName);
            if(sharedpreferences.getString("merchantName",null)!=null){
                merName.setText(sharedpreferences.getString("merchantName",null));
            }else{
                merName.setText("-");
            }
            if(getIntent().getSerializableExtra("PaymentType") != null) {
                payType= getIntent().getSerializableExtra("PaymentType").toString();
            }
            if(getIntent().getSerializableExtra("vpa") != null) {
                vpa= getIntent().getSerializableExtra("vpa").toString();
            }

            if(getIntent().getSerializableExtra("CustomerID") != null) {
                customerID= getIntent().getSerializableExtra("CustomerID").toString();
            }
            if(getIntent().getSerializableExtra("invoiceNo") != null) {
                invoiceNo= getIntent().getSerializableExtra("invoiceNo").toString();
            }
            //
            if(payType!=null && !payType.equals("")){
                {
                   // setContentView(R.layout.blank);
                    System.out.println("paymentoptions1=="+paymentoption);
                    if(!paymentoption.contains(payType)){
                        Intent retrn = new Intent();
                        setResult(4,retrn);
                        finish();
                    }else {
                        PerformForPayment(payType);
                    }
                }
            }else {
               // setContentView(R.layout.paymentopt);
                System.out.println("paymentoptions2=="+paymentoption);
                CreatePaymentoptions(paymentoption);
            }



        }
    }

    private void SetMessageMap() {

        map =  new HashMap<>();
        map.put(10," Transaction yet not recieved to server please try again");
        map.put(11 ,"Transaction in Request state try after Some time");
        map.put(12 ,"Error in processing Transaction status");
        map.put(13, "unable to generate QR");
        map.put(14 ,"Select Your Bank");
        map.put(15, "Enter or Scan Aadhaar Number");
        map.put(16, "Enter valid Aadhaar No.");
        map.put(17, "Last Transaction is unknown please try after 15 mins after verifing Consumer account detials.");
        map.put(18, "Last Delivery is already paid");
    }



    private void CheckforConnection(){
        if (!Utility.isConectionAvailable(getApplicationContext())) {
            Intent retrn = new Intent();
            setResult(3,retrn);
            finish();
        }
    }
    private void CreatePaymentoptions(String paymentoption ){
      //simulated
             // paymentoption = "upi";
                String payopt[] = paymentoption.split(",");
              int size = payopt.length;
        int imageView1resID ;
        int imageView2resID;
        int imageView3resID;
        int imageView4resID;
        int imageView5resID;
        int imageView6resID;
        int imageView7resID;
        int imageView8resID;

        // images should come from baseurl + /images/paymentopt_<option>.jpg
        Object cardViewArr[] = new Object[8] ;
        cardViewArr[0] = cardView1;
        cardViewArr[1] = cardView2;
        cardViewArr[2] = cardView3;
        cardViewArr[3] = cardView4;
        cardViewArr[4] = cardView5;
        cardViewArr[5] = cardView6;
        cardViewArr[6] = cardView7;
        cardViewArr[7] = cardView8;



        Object imageViewArr[] = new Object[8];
        imageViewArr[0] = imageView1;
        imageViewArr[1] = imageView2;
        imageViewArr[2] = imageView3;
        imageViewArr[3] = imageView4;
        imageViewArr[4] = imageView5;
        imageViewArr[5] = imageView6;
        imageViewArr[6] = imageView7;
        imageViewArr[7] = imageView8;


        int index = 0;
        for (String paymentOpt : payopt) {
            CardView cardview = (CardView)cardViewArr[index];
            cardview.setVisibility(View.VISIBLE);

            ImageView imageView = (ImageView)imageViewArr[index];

            Glide.with(this)
                    .load("https://qa.phicommerce.com" + "/images/paymentopt/"+paymentOpt+".jpg")
                    .diskCacheStrategy( DiskCacheStrategy.DATA )
                    .into(imageView);
            imageView.setTag(R.string.paymentOptTagID, paymentOpt);

            index++;
        }



    }

    public void Onclickcard1(View view){
        String popt =  imageView1.getTag(R.string.paymentOptTagID).toString();
//        Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }
    public void Onclickcard2(View view){
        String popt =  imageView2.getTag(R.string.paymentOptTagID).toString();
  //      Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);

    }
    public void Onclickcard3(View view){
        String popt =  imageView3.getTag(R.string.paymentOptTagID).toString();
    //    Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }
    public void Onclickcard4(View view){
        String popt =  imageView4.getTag(R.string.paymentOptTagID).toString();
      //  Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }
    public void Onclickcard5(View view){
        String popt =  imageView5.getTag(R.string.paymentOptTagID).toString();
        //Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }
    public void Onclickcard6(View view){
        String popt =  imageView6.getTag(R.string.paymentOptTagID).toString();
        //Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }
    public void Onclickcard7(View view){
        String popt =  imageView7.getTag(R.string.paymentOptTagID).toString();
        //Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }
    public void Onclickcard8(View view){
        String popt =  imageView8.getTag(R.string.paymentOptTagID).toString();
        //Toast.makeText(this,popt,Toast.LENGTH_LONG).show();
        PerformForPayment(popt);
    }

    private void ProceedForUpiWeb(final String opt){
        final Dialog dialog;
        dialog = new Dialog(PaymentOptions.this);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimaryDark));
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.vpainput);
        dialog.setCanceledOnTouchOutside(false);
        final EditText input = (EditText) dialog.findViewById(R.id.vpaText);
        Button clicker = (Button) dialog.findViewById(R.id.confButton);
        if(vpa!=null && !vpa.equals("") ) {
            if(vpa.contains("@")){
                ProceedToUpi(opt.toUpperCase());
            }else{
                Toast.makeText(getApplicationContext(), "Not Valid VPA!", Toast.LENGTH_LONG).show();
                return;
            }
        }else{
            dialog.show();
        }

        clicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vpatext = input.getText().toString();

                if (vpatext!=null && !vpatext.equals("")){
                    if(vpatext.contains("@")){}
                    vpa=vpatext;
                    ProceedToUpi(opt.toUpperCase());
                }else{
                    Toast.makeText(getApplicationContext(), "Enter Valid VPA!", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(payType!=null && !payType.equals("")){
                    finish();
                }else{

                }
            }
        });
    }


    private void PerformForPayment(final String opt){

        if (!Utility.isConectionAvailable(getApplicationContext())) {
            Intent retrn = new Intent();
            setResult(3,retrn);
            finish();
        }else {
            if (opt.equals("upi")) {
                qrType = "U";
                AttachFragment();
//                PackageManager manager = getPackageManager();
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(testUPiString));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
//                if (infos.size() > 0) {
//                    //Then there is an Application(s) can handle your intent
//                   // Toast.makeText(MainActivity.this, "Acivity found", Toast.LENGTH_SHORT).show();
//                    getUpiString("U");
//                 //   ProceedForUpiWeb(opt);
//                } else {
//                    ProceedForUpiWeb(opt);
//                    //No Application can handle your intent
//                }
            } else if (opt.equals("card")) {
                ProceedToCard(opt.toUpperCase());
            } else if (opt.equals("nb")) {
                ProceedToNet(opt.toUpperCase());
            } else if (opt.equals("bharatqr")) {
                ProceedToBharat();
            } else if (opt.equals("other")) {
                ProceedToOther();
            } else if (opt.equals("aadhaar")) {
                ProceedToaddhaar();
            } else if (opt.equals("sms")) {
                ProceedTosms();
            } else if (opt.equals("wallet")) {
                ProceedToWallet(opt.toUpperCase());
            }
        }
    }

        private String getUpiString (String type){
            qrType = type;
            //Toast.makeText(getContext(),"In PaymentOptions",Toast.LENGTH_SHORT).show();
            TreeMap<String, String> paramsMap = new TreeMap<String, String>();

            paramsMap.put("merchantID", merchantId);
            paramsMap.put("amount", amount);
            paramsMap.put("currency", currencyCode);
            paramsMap.put("merchantRefNo", tranRefNo);
            paramsMap.put("emailID", customerEmailID);

            if (type.equals("U")) {
                paramsMap.put("requestType", "UPIQR");
            } else {
                paramsMap.put("requestType", "BharatQR");
            }

            paramsMap.put("secureTokenHash", "Y");

            if (invoiceNo != null && !invoiceNo.equals("")) {
                paramsMap.put("invoiceNo", invoiceNo);
            }
            if (mobileNo!= null && !mobileNo.equals("")) {
                paramsMap.put("mobileNo", mobileNo);
            }
            if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                paramsMap.put("aggregatorID", sharedpreferences.getString("mid", null));
            }

            if (addlParam1 != null && !addlParam1.equals("")) {
                paramsMap.put("addlParam1", addlParam1);
            }
            if (addlParam2 != null && !addlParam2.equals("")) {
                paramsMap.put("addlParam2", addlParam2);
            }

            if (invoiceList != null && !invoiceList.equals("")) {
                paramsMap.put("invoiceList", invoiceList);
            }

            if (customerID != null && !customerID.equals("")) {
                paramsMap.put("customerID", customerID);
            }

            System.out.println("paramsMap===" + paramsMap);
            System.out.println("secureToken===" + secureToken);

            String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);
//            paramsMap.put("secureHash", secureHash);
          /*  int errorcode = Message.VERIFYERR13.getMessage();

            map = PayPhiSdk.getMessage();
            if (map.containsKey(errorcode)) {
                msg = map.get(errorcode);
            }*/
            try {
                Context context = getApplicationContext();
                AsyncHttpClient client = new AsyncHttpClient();


                final int DEFAULT_TIMEOUT = 30 * 1000;
                client.setConnectTimeout(DEFAULT_TIMEOUT);
                client.setMaxRetriesAndTimeout(1, DEFAULT_TIMEOUT);
                client.setTimeout(DEFAULT_TIMEOUT);
//            paydialog.show();

                RequestParams params = new RequestParams();
                params.put("merchantID", merchantId);
                params.put("amount", amount);
                params.put("currency", currencyCode);
                params.put("merchantRefNo", tranRefNo);
                params.put("emailID", customerEmailID);
                params.put("secureHash", secureHash);
                if (type.equals("U")) {
                    params.put("requestType", "UPIQR");
                } else {
                    params.put("requestType", "BharatQR");
                }

                params.put("secureTokenHash", "Y");

                if (invoiceNo != null && !invoiceNo.equals("")) {
                    params.put("invoiceNo", invoiceNo);
                }
                if (mobileNo!= null && !mobileNo.equals("")) {
                    params.put("mobileNo", mobileNo);
                }
                if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                    params.put("aggregatorID", sharedpreferences.getString("mid", null));
                }

                if (addlParam1 != null && !addlParam1.equals("")) {
                    params.put("addlParam1", addlParam1);
                }
                if (addlParam2 != null && !addlParam2.equals("")) {
                    params.put("addlParam2", addlParam2);
                }

                if (invoiceList != null && !invoiceList.equals("")) {
                    params.put("invoiceList", invoiceList);
                }

                if (customerID != null && !customerID.equals("")) {
                    params.put("customerID", customerID);
                }
                //client.addHeader("deviceID", "AND:" + deviceID);


                StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
                stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
                paydialog.show();

                qreditor = sharedpreferences.edit();
                //client.post(context, "", );
                // client.post(context,APISettings.getApiSettings().getGenerateQr() ,  "application/json",new JsonHttpResponseHandler() {
                //           client.post

                System.out.println("Qr Url=====" + APISettings.getApiSettings().getGenerateQr());
                client.post(context, APISettings.getApiSettings().getGenerateQr(), stringEntity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        //System.out.println("response===..............."+response);
                        try {
                            paydialog.dismiss();
                            int status = response.getJSONObject("respHeader").getInt("returnCode");
                            // Toast.makeText(getContext(),"status="+status, Toast.LENGTH_LONG).show();
                            if (status == 200) {
                                JSONObject jsonObject = response.getJSONObject("respBody");
                                if (jsonObject.has("upiQR")) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(jsonObject);
                                    qreditor.putString(tranRefNo, json);
                                    qreditor.putString("Qr_" + tranRefNo, amount);
                                    qreditor.commit();
                                }

                                if (qrType.equals("U")) {
                                    qrString = jsonObject.getString("upiQR");
                                    System.out.println("upiQR===" + qrString.toString());

                                    if (jsonObject.has("serviceChargeUPI") && !jsonObject.getString("serviceChargeUPI").equals("0")) {
                                        serviceCharge = jsonObject.get("serviceChargeUPI").toString();

                                    }

                                   //simulated
                                  //  AttachFragment();
                                    if (!qrString.equals("") && !qrString.equals("null")) {
                                        //bahratqrString = jsonObject.get("bharatQR").toString();
                             //           genrateFuncionIntent(qrString);
                                        AttachFragment();

                                    } else {
                                        if (qrString != null && !qrString.equals("") && !qrString.equals("null")) {
                                          //  genrateFuncionIntent(qrString);
                                            AttachFragment();
                                        } else {
                                            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                            ProceedForUpiWeb("upi");
                                          //  Intent intent = new Intent();
                                           // PayPhiSdk.onPaymentResponse(0, 2, intent);
                                        }
                                    }

                                }
                                //Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                            } else if (status == 101) {
                                paydialog.dismiss();
                               // Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                                // sessionError();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                /*Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);*/

                            } else if (status == 201) {
                                paydialog.dismiss();
                                //Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                                // sessionError();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                               /* Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);*/
                                //Simulated
                               // AttachFragment();

                            } else {
                                paydialog.dismiss();
                                //Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                                // sessionError();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                /*Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);*/
                                //Simulated
                                //AttachFragment();
                            }

                        } catch (JSONException e) {
                            paydialog.dismiss();
                            e.printStackTrace();
                            /*Intent intent = new Intent();
                            PayPhiSdk.onPaymentResponse(0, 2, intent);*/
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            //Simulated
                            //AttachFragment();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        paydialog.dismiss();
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                  /*  if (listener != null) {
                        listener.onFailure(String.valueOf("504")); // <---- fire listener here
                    }*/
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                   /*     Intent intent = new Intent();
                        PayPhiSdk.onPaymentResponse(0, 3, intent);*/
                        handleHttpError(statusCode);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        paydialog.dismiss();
                        super.onFailure(statusCode, headers, responseString, throwable);
                   /* if (listener != null) {
                        listener.onFailure(String.valueOf("504")); // <---- fire listener here
                    }*/
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        /*Intent intent = new Intent();
                        PayPhiSdk.onPaymentResponse(0, 3, intent);*/
                        handleHttpError(statusCode);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    private void AttachFragment() {
        QRCodeFragment fragment = new QRCodeFragment();
        Bundle bundle = new Bundle();
        //  Intent intent = new Intent(this.getActivity(), QRCodeFragment.class);

        //intent.putExtra("qrstring", qrstring);
       /* intent.putExtra("amnt",model.getAmount());
        intent.putExtra("awbNo",model.getAwbNumber());*/
        //intent.putExtra("qrType",qrType);
        //simulated
        //
        //qrString="upi://pay?pa=Payphi.ecomm@icici&pn=nadeem%20chinna&mc=0000&tid=cxnkjcnkjdfdvjndkjfvn&tr=4894\n" +
          //    "398cndhcd23&tn=Pay%20to%20mystar%20store&am=1.00&mam=null&cu=INR&url=https://mystar.co\n" +
            //   "m/orderid=9298yw89e8973e87389e78923ue892";

      /*  qrString="upi://pay?pa=nadeem@npci&pn=nadeem%20chinna&mc=0000&tid=cxnkjcnkjdfdvjndkjfvn&tr=4894\n" +
          "398cndhcd23&tn=Pay%20to%20mystar%20store&am=10&mam=null&cu=INR&url=https://mystar.co\n" +
         "m/orderid=9298yw89e8973e87389e78923ue892";*/

        bundle.putSerializable("Amount", amount);
        bundle.putSerializable("MerchantTxnNo", tranRefNo);
        bundle.putSerializable("MerchantID", merchantId);
        bundle.putSerializable("SecureToken", secureToken);
        bundle.putSerializable("currency", currencyCode);
        bundle.putSerializable("merchantRefNo", tranRefNo);
        bundle.putSerializable("emailID", customerEmailID);
        if (qrType.equals("U")) {
            bundle.putSerializable("requestType", "UPIQR");
        } else {
            bundle.putSerializable("requestType", "BharatQR");
        }
        bundle.putSerializable("secureTokenHash", "Y");
        if (invoiceNo != null && !invoiceNo.equals("")) {
            bundle.putSerializable("invoiceNo", invoiceNo);
        }
        if (mobileNo!= null && !mobileNo.equals("")) {
            bundle.putSerializable("mobileNo", mobileNo);
        }
        if (addlParam1 != null && !addlParam1.equals("")) {
            bundle.putSerializable("addlParam1", addlParam1);
        }
        if (addlParam2 != null && !addlParam2.equals("")) {
            bundle.putSerializable("addlParam2", addlParam2);
        }
        if (invoiceList != null && !invoiceList.equals("")) {
            bundle.putSerializable("invoiceList", invoiceList);
        }
       // String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);

       // bundle.putSerializable("qrstring", qrString);
        bundle.putSerializable("qrType", qrType);
        bundle.putSerializable("invoiceNo", invoiceNo);
        bundle.putSerializable("customerID", customerID);

        if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
            bundle.putSerializable("aggregatorID", sharedpreferences.getString("mid", null));
        }else{
            bundle.putSerializable("aggregatorID", merchantId);
        }

        if(serviceCharge!=null && !serviceCharge.equals("") && Float.parseFloat(serviceCharge)!=0 ){

            Float tot = Float.parseFloat(amount)+Float.parseFloat(serviceCharge);

            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(2);
            df.format(tot);
            tot = Float.parseFloat(String.format("%.2f", tot));


            Float servicechrg=Float.parseFloat(serviceCharge);
            df.format(servicechrg);
            servicechrg = Float.parseFloat(String.format("%.2f", servicechrg));

            bundle.putSerializable("ServiceCharge",String.valueOf(servicechrg));
            bundle.putSerializable("TotalAmount", String.valueOf(tot));

        }

        fragment.setArguments(bundle);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragId, fragment);
        mFragmentTransaction.commit();
        frameLayout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);

    }

    private void genrateFuncionIntent(String qrString) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrString));
        startActivityForResult(intent,100);


    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent();
                            intent.putExtra("ResultType","CANCELED");
                            PayPhiSdk.onPaymentResponse(0,RESULT_CANCELED,intent);
                            finish();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            //do nothing
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to cancel the ongoing payment?").setPositiveButton("Cancel", dialogClickListener)
                    .setNegativeButton("Do not cancel", dialogClickListener).show();
        }
    }

    private void handleHttpError(int statusCode) {
        String errorText = "Server communication error -^^&&&*** #" + statusCode;
        if (statusCode == 403) {
            // sessionError();
        } else {
            //   Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
        }
    }
    private  void   ProceedToUpi(String type){
        proceedForPayment(type);

    }
    private  void   ProceedToWallet(String type){
        proceedForPayment(type);

    }
    private  void   ProceedToCard(String type){
        proceedForPayment(type);

    }
    private  void   ProceedToNet(String type){

        proceedForPayment(type);
    }
    private  void   ProceedToBharat(){
        Toast.makeText(this,"bharat",Toast.LENGTH_LONG).show();
    }
    private  void   ProceedToOther(){
        Toast.makeText(this,"other",Toast.LENGTH_LONG).show();
    }
    private  void   ProceedToaddhaar(){
        Toast.makeText(this,"aadhar",Toast.LENGTH_LONG).show();
    }
    private  void   ProceedTosms(){
        Toast.makeText(this,"SMS",Toast.LENGTH_LONG).show();
    }

   private void proceedForPayment(String type){
       // Toast.make90Text(this,"Card",Toast.LENGTH_LONG).show();
       String url = getFormUrl(type);
       //Toast.makeText(this,url,Toast.LENGTH_LONG).show();
       System.out.println("formUrl===="+url);
       Log.d("formurl==",url);
       Intent intent = new Intent(PaymentOptions.this, WebViewPaymentClient.class);
       intent.putExtra("formurl",url);
       startActivityForResult(intent,3);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String message="";
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle=null ;

           if (data != null) {
               bundle = data.getExtras();
           }
         /*  for (String key : bundle.keySet()) {
               Object value = bundle.get(key);
               System.out.println("Data from sdk key=" + key + "Data from sdk value=" + value);
           }*/

           if (bundle != null && bundle.containsKey("ResultType") && bundle.get("ResultType").equals("web")) {
               //super.setResult(resultCode, data);
               PayPhiSdk.onPaymentResponse(requestCode,resultCode,data);
               finish();
           } else {
               intentResponse(requestCode,resultCode,data);
           }


    }
    private void intentResponse(int requestCode, int resultCode, Intent intent){

        Bundle bundle=null;

            if (intent != null) {
                bundle = intent.getExtras();
            }
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    System.out.println("Data from psp key=" + key + "Data from psp value=" + value);
                }

                if (bundle.containsKey("Status")) {
                    if (bundle.get("Status").equals("FAILURE")) {

                        //   setResult(RESULT_CANCELED, intent);
                        PayPhiSdk.onPaymentResponse(requestCode, resultCode, intent);
                        finish();
                    } else {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragId);
                        if (fragment instanceof QRCodeFragment) {
                            // do something with f
                            ((QRCodeFragment) fragment).checkStatus();
                        }
                    }
                } else {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragId);
                    if (fragment instanceof QRCodeFragment) {
                        // do something with f
                        ((QRCodeFragment) fragment).checkStatus();
                    }
                }

                // check if the request code is same as what is passed  here it is 2


            } else {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragId);
                if (fragment instanceof QRCodeFragment) {
                    // do something with f
                    ((QRCodeFragment) fragment).checkStatus();
                }
            }
        }


    public void CheckStatusTransaction() {

            TreeMap<String, String> paramsMap = new TreeMap<String, String>();


            paramsMap.put("amount", amount);
            paramsMap.put("merchantTxnNo", tranRefNo);
            paramsMap.put("transactionType", "STATUS");

            if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                paramsMap.put("merchantID", sharedpreferences.getString("mid", null));
            } else {
                paramsMap.put("merchantID", merchantId);
            }

            //  String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);


            try {
                Context context = getApplicationContext();
                AsyncHttpClient client = new AsyncHttpClient();


                final int DEFAULT_TIMEOUT = 30 * 1000;
                client.setConnectTimeout(DEFAULT_TIMEOUT);
                client.setMaxRetriesAndTimeout(1, DEFAULT_TIMEOUT);
                client.setTimeout(DEFAULT_TIMEOUT);
//            paydialog.show();

                RequestParams params = new RequestParams();

                params.put("amount", amount);
                params.put("merchantTxnNo", tranRefNo);
                params.put("transactionType", "STATUS");
                params.put("originalTxnNo", tranRefNo);

                if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                    params.put("merchantID", sharedpreferences.getString("mid", null));
                } else {
                    params.put("merchantID", merchantId);
                }

                StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
                stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
                paydialog.show();

                //qreditor=sharedpreferences.edit();
                //client.post(context, "", );
                // client.post(context,APISettings.getApiSettings().getGenerateQr() ,  "application/json",new JsonHttpResponseHandler() {
                //           client.post
                client.post(context, APISettings.getApiSettings().getCheckPaymentStatus(), stringEntity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        paydialog.dismiss();
                        //System.out.println("response===..............."+response);
                        try {
                            //JSONObject jsonObject = response.getJSONObject("resBody");
                            String txnId = "";
                            String responseCode = "";
                            String merchantId = "";
                            String merchantTxnNo = "";
                            String txnStatus = "";
                            String paymentDateTime = "";
                            String paymentID = "";
                            String txnAuthId = "";
                            String respDescription = "";
                            //	String txnStatus = response.getString("txnStatus");


                            if (response.has("responseCode") && response.getString("responseCode") != null) {
                                responseCode = response.getString("responseCode");

                            }


                            if (response.has("merchantTxnNo") && response.getString("merchantTxnNo") != null) {
                                merchantTxnNo = response.getString("merchantTxnNo");

                            }

                            if (response.has("merchantId") && response.getString("merchantId") != null) {
                                merchantId = response.getString("merchantId");
                            }

                            if (response.has("txnID") && response.getString("txnID") != null) {
                                txnId = response.getString("txnID");
                            }

                            if (response.has("txnAuthId") && response.getString("txnAuthId") != null) {
                                txnAuthId = response.getString("txnAuthID");
                            }

                            if (response.has("responseCode") && (responseCode.equals("000") || responseCode.equals("0000"))) {
                                //Toast.makeText(context, respDescription, Toast.LENGTH_SHORT).show();
                                if (response.has("txnStatus") && response.getString("txnStatus") != null) {
                                    txnStatus = response.getString("txnStatus");
                                }


                                if (response.has("txnRespDescription") && response.getString("txnRespDescription") != null) {
                                    respDescription = response.getString("txnRespDescription");
                                }
                                if (response.has("respDescription") && response.getString("respDescription") != null) {
                                    respDescription = response.getString("respDescription");
                                }


                                if (response.has("paymentDateTime") && response.getString("paymentDateTime") != null) {

                                    paymentDateTime = response.getString("paymentDateTime");

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {


                                        //paymentDateTime= dateFormat.format(paymentDateTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                //showNotification(txnStatus,respDescription);
                            } else {
                                //Toast.makeText(context, respDescription, Toast.LENGTH_SHORT).show();

                                if (response.has("txnStatus") && response.getString("txnStatus") != null) {
                                    txnStatus = response.getString("txnStatus");
                                }
                                //txnStatus="Fail";

                                if (response.has("txnRespDescription") && response.getString("txnRespDescription") != null) {
                                    respDescription = response.getString("txnRespDescription");
                                }
                                if (response.has("respDescription") && response.getString("respDescription") != null) {
                                    respDescription = response.getString("respDescription");
                                }


                                if (response.has("paymentDateTime") && response.getString("paymentDateTime") != null) {

                                    paymentDateTime = response.getString("paymentDateTime");

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {


                                        //  paymentDateTime= dateFormat.format(paymentDateTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                //        msg=PayPhiSdk.onMessageRequest(10);
                                int errorcode = Message.VERIFYERR10.getMessage();


                                if (map.containsKey(errorcode)) {
                                    msg = map.get(errorcode);
                                }
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                //showNotification(txnStatus,respDescription);
                            }


                            if (txnStatus.equals("SUC")) {
                                //StoreTransaction(responseCode,respDescription,merchantId,merchantTxnNo,txnStatus,txnId,txnAuthId,paymentDateTime,invoiceNo,customerID);
                                Map respMap = new HashMap();
                                respMap = Utility.toMap(response);
                                Intent intent = new Intent();
                                for (Object key : respMap.keySet()) {
                                    intent.putExtra(String.valueOf(key), String.valueOf(respMap.get(key)));
                                }

                                intent.putExtra("respType", "checkstatus");
                                setResult(RESULT_OK, intent);
                                finish();
                                // Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                                finish();


                            }

                            if (txnStatus.equals("REJ")) {

                                //StoreTransaction(responseCode,respDescription,merchantId,merchantTxnNo,txnStatus,txnId,txnAuthId,paymentDateTime,invoiceNo,customerID);
                                Map respMap = new HashMap();
                                respMap = Utility.toMap(response);
                                Intent intent = new Intent();
                                for (Object key : respMap.keySet()) {

                                    intent.putExtra(String.valueOf(key), String.valueOf(respMap.get(key)));


                                }
                                intent.putExtra("respType", "checkstatus");
                                setResult(RESULT_OK, intent);
                                finish();
                                //Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                                finish();


                            } else if (txnStatus.equals("REQ")) {
                                int errorcode = Message.VERIFYERR11.getMessage();


                                if (map.containsKey(errorcode)) {
                                    msg = map.get(errorcode);
                                }
                                //Toast.makeText(getContext(),"Transaction in Request state try after Some time",Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            } else if (txnStatus.equals("ERR")) {
                                int errorcode = Message.VERIFYERR12.getMessage();


                                if (map.containsKey(errorcode)) {
                                    msg = map.get(errorcode);
                                }

                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            paydialog.dismiss();
                            e.printStackTrace();
                            //   Toast.makeText(getContext(),"Execption..1 ",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            // PayPhiSdk.onPaymentResponse(0, 2, intent);
                            //getActivity().finish();
                            //Toast.makeText(getContext(), "Internal error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        paydialog.dismiss();
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        // Toast.makeText(getContext(),"Execption..2 ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        //PayPhiSdk.onPaymentResponse(0, 3, intent);
                        handleHttpError(statusCode);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        paydialog.dismiss();
                        super.onFailure(statusCode, headers, responseString, throwable);
                        //   Toast.makeText(getContext(),"Execption.. 3",Toast.LENGTH_LONG).show();
                 /*   Intent intent = new Intent();
                    PayPhiSdk.onPaymentResponse(0, 3, intent);*/
                        handleHttpError(statusCode);
                    }
                });

            } catch (Exception e) {
                // Toast.makeText(getContext(),"Execption..4 ",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

//end call



    private String getFormUrl(String type){
        String formurl="";
      SharedPreferences  sharedpreferences = getApplicationContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
        TreeMap<String,String> map = new TreeMap<String,String>();
        map.put("amount",amount);
        map.put("currencyCode",currencyCode);
        map.put("merchantID",merchantId);
        map.put("merchantTxnNo",tranRefNo);
        map.put("invoiceNo",invoiceNo);
        map.put("allowDisablePaymentMode",type);
        if(type.equalsIgnoreCase("CARD") || type.equalsIgnoreCase("NB") ) {
            map.put("paymentMode", type);
            map.put("payType", "1");



        }

        if(type.equalsIgnoreCase("UPI")){
            map.put("paymentMode", type);
            map.put("payType", "0");

            if(!vpa.equals("") &&  vpa.contains("@")){
                map.put("customerUPIAlias", vpa);
            }
        }


        if(!merchantId.equals(sharedpreferences.getString("mid",null).toString())){
            map.put("aggregatorID",sharedpreferences.getString("mid",null));
        }

        if(customerID!=null && !customerID.equals("")){
            map.put("customerID", customerID);
        }
        map.put("transactionType","SALE");
        map.put("secureTokenHash","Y");
        map.put("customerEmailID",customerEmailID);



        formurl = PayForm.getPayFormHtml(getApplicationContext(),secureToken,map);

        return formurl;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tranRefNo = null;
    }
}