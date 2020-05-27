package com.payphi.customersdk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payphi.customersdk.util.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QRCodeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QRCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRCodeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String testUPiString =  "upi://pay?pa=nadeem@npci&pn=nadeem%20chinna&mc=0000&tid=cxnkjcnkjdfdvjndkjfvn&tr=4894\n" +
            "398cndhcd23&tn=Pay%20to%20mystar%20store&am=10&mam=null&cu=INR&url=https://mystar.co\n" +
            "m/orderid=9298yw89e8973e87389e78923ue892";
    String serviceChargeAmt="";
    String selection;
    String requestFrom = null;
    JSONObject serchargeResponse;
    private String mParam1;
    private String mParam2;
    private String amount,currencyCode,customerEmailID,mobileNo,addlParam1,addlParam2,qrType;
    String invoiceList="";
    private String tranRefNo;
    private String merchantId;
    private String secureToken;
private String aggregatorID;
    SharedPreferences sharedpreferences;
    TextView amtText;
    TextView servicetxt;
    TextView totalamttext;
    Handler handler;
    String totalamt="";
    String serviceCharge="";
    TableRow qrserviceRow;
    TableRow qrtotamountRow;
    TextView time;
    View view ;
    String click = "system";
    String msg = "Transaction rejected please try again later";
    Map<Integer,String> map;
    public static ProgressDialog paydialog; // this = YourActivity
    String invoiceNo,customerID;
    private OnFragmentInteractionListener mListener;
    String qrstring="";
    Button proeedTopay;
    private boolean flag=true;
    TextView usermsg;
    Button statButton;
    CountDownTimer countDownTimer;
    boolean enableflag= true;
    boolean clickedflag=true;
    Button clicker;
    ProgressBar progressBar;
    public QRCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QRCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QRCodeFragment newInstance(String param1, String param2) {
        QRCodeFragment fragment = new QRCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = this.getArguments();
        //Toast.makeText(getContext(),bundle.getString("Amount"),Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        paydialog = new ProgressDialog(this.getActivity());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);


        view = inflater.inflate(R.layout.activity_qrcode, container, false);
        sharedpreferences = this.getActivity().getSharedPreferences("AppSdk", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedpreferences.edit();
        SetMessageMap();
        if (bundle!=null) {
            if (bundle.getString("qrType") != null) {
                String qrtype = bundle.getString("qrType");

                ImageView qrtypeImage = (ImageView)view.findViewById(R.id.qrtype);
                if (qrtype.equals("U")) {
                    //qrtypeImage.setImageResource(R.drawable.upi1);
                    //   setContentView(R.layout.upi_activity);
                    view = inflater.inflate(R.layout.upi_activity, container, false);

                    amtText = (TextView) view.findViewById(R.id.amtId);
                    servicetxt =(TextView) view.findViewById(R.id.qrservicechanrgeId);
                    totalamttext =(TextView) view.findViewById(R.id.qrtotalamtId);

                    qrserviceRow = (TableRow) view.findViewById(R.id.qrserviceRowId);
                    qrtotamountRow =(TableRow) view.findViewById(R.id.qrtotamountRowId);
                    proeedTopay =  (Button) view.findViewById(R.id.proceedtoPayId);
                    proeedTopay.setVisibility(View.GONE);
                    usermsg = (TextView) view.findViewById(R.id.msgId);
                    statButton = (Button) view.findViewById(R.id.checkStatusUpiQr);
                    qrserviceRow.setVisibility(View.INVISIBLE);
                    qrtotamountRow.setVisibility(View.INVISIBLE);
                    usermsg.setVisibility(View.INVISIBLE);
                    statButton.setVisibility(View.INVISIBLE);
                    progressBar = (ProgressBar)view.findViewById(R.id.pb);
                    progressBar.setVisibility(View.GONE);
                    time = (TextView)view.findViewById(R.id.time);
                    time.setVisibility(View.GONE);

                proeedTopay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    final Dialog dialog;
                    dialog = new Dialog(getContext());
                    dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimaryDark));
                    dialog.requestWindowFeature(getActivity().getWindow().FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.vpainput);
                    dialog.setCanceledOnTouchOutside(false);
                    TextView txtTitle = (TextView) dialog.findViewById(R.id.textDialog);
                    final EditText input = (EditText) dialog.findViewById(R.id.vpaText);
                    input.setEnabled(false);
                    input.setVisibility(View.GONE);
                    RadioButton rb1 = (RadioButton)dialog.findViewById(R.id.cash);
                    RadioButton rb2 = (RadioButton)dialog.findViewById(R.id.card);
                    clicker = (Button) dialog.findViewById(R.id.confButton);
                    clicker.setEnabled(false);
                    if(appsCount() == 0){
                        txtTitle.setText("VPA Payment");
                        clicker.setEnabled(true);
                        rb1.setVisibility(View.GONE);
                        //rb2.setVisibility(View.GONE);
                        input.setEnabled(true);
                        //input.setVisibility(View.VISIBLE);
                        selection = "VPA";
                    }
                    dialog.show();
                    rb1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            input.setEnabled(false);
                            clicker.setEnabled(true);
                            selection = "APP";
                            getUpiString("U");
                            dialog.dismiss();
                        }
                    });
                    rb2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clicker.setEnabled(true);
                            input.setEnabled(true);
                            input.setVisibility(View.VISIBLE);
                            input.requestFocus();
                            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                            selection = "VPA";
                        }
                    });
                    clicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(selection == "APP"){
                                dialog.dismiss();
                                getUpiString("U");
                            }else {
                                if(input.getText().toString().trim().length() == 0){
                                    Toast.makeText(getContext(),"Enter valid VPA",Toast.LENGTH_LONG).show();
                                }else {
                                    if(!input.getText().toString().equals("") &&  input.getText().toString().contains("@")){
                                        dialog.dismiss();
                                        vpaPayment("UPI",input.getText().toString());
                                    }else {
                                        Toast.makeText(getContext(),"Please enter valid VPA",Toast.LENGTH_LONG).show();
                                    }

                                    //Toast.makeText(getContext(),"VPA method",Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                    });
//                         if(clickedflag){
//                             StartCounter(view);
//                             SceduleTask();
//                         }
//
//
//
//                            OpenAvailableIntents();
//                            clickedflag =false;
//                            statButton.setVisibility(View.VISIBLE);

                        }
                    });

                } else if (qrtype.equals("B")) {
                   // setContentView(R.layout.activity_qrcode);
                    view = inflater.inflate(R.layout.activity_qrcode, container, false);
                    //qrtypeImage.setImageResource(R.drawable.bharatheader);
                    amtText = (TextView) view.findViewById(R.id.amtId);
                    servicetxt =(TextView) view.findViewById(R.id.qrservicechanrgeId);
                    totalamttext =(TextView) view.findViewById(R.id.qrtotalamtId);

                    qrserviceRow = (TableRow) view.findViewById(R.id.qrserviceRowId);
                    qrtotamountRow =(TableRow) view.findViewById(R.id.qrtotamountRowId);

                    qrserviceRow.setVisibility(View.INVISIBLE);
                    qrtotamountRow.setVisibility(View.INVISIBLE);


                }
            }


          //  this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            ((CardView) view.findViewById(R.id.logosCardView)).setCardElevation(0);
  //          ((CardView) view.findViewById(R.id.logosCardView)).setContentPadding(0, 0, 0, 0);

            if (bundle.getString("Amount") != null) {
                amount = bundle.getString("Amount");
                DecimalFormat df = new DecimalFormat();
                df.setMinimumFractionDigits(2);
                Float f = Float.parseFloat(amount);
                df.format(f);
                amount = String.format("%.2f", f);

                amtText.setText(amount);

            }
            if (bundle.getString("MerchantTxnNo") != null) {
                    tranRefNo =  bundle.getString("MerchantTxnNo");
            }
            if (bundle.getString("MerchantID") != null) {
                merchantId =  bundle.getString("MerchantID");
            }
            if (bundle.getString("SecureToken") != null) {
              secureToken=  bundle.getString("SecureToken");
            }
            if (bundle.getString("aggregatorID") != null) {
                aggregatorID=  bundle.getString("aggregatorID");
            }
            if (bundle.getString("invoiceNo") != null) {
                invoiceNo=  bundle.getString("invoiceNo");
            }
            if (bundle.getString("customerID") != null) {
                customerID=  bundle.getString("customerID");
            }

            if (bundle.getString("currency") != null) {
                currencyCode=  bundle.getString("currency");
            }
            if (bundle.getString("emailID") != null) {
                customerEmailID=  bundle.getString("emailID");
            }
            if (bundle.getString("mobileNo") != null) {
                mobileNo=  bundle.getString("mobileNo");
            }
            if (bundle.getString("addlParam1") != null) {
                addlParam1=  bundle.getString("addlParam1");
            }
            if (bundle.getString("addlParam2") != null) {
                addlParam2=  bundle.getString("addlParam2");
            }
            if (bundle.getString("invoiceList") != null) {
                invoiceList=  bundle.getString("invoiceList");
            }
            GetSerCharge();

//            if (bundle.getString("ServiceCharge") != null) {
//                serviceCharge=  bundle.getString("ServiceCharge");
//                DecimalFormat df = new DecimalFormat();
//                df.setMinimumFractionDigits(2);
//                Float f = Float.parseFloat(serviceCharge);
//                df.format(f);
//                serviceCharge = String.format("%.2f", f);
//
//
//
//                servicetxt.setText(serviceCharge);
//                qrserviceRow.setVisibility(View.VISIBLE);
//                qrtotamountRow.setVisibility(View.VISIBLE);
//            }
//            if (bundle.getString("TotalAmount") != null) {
//                totalamt=  bundle.getString("TotalAmount");
//                DecimalFormat df = new DecimalFormat();
//                df.setMinimumFractionDigits(2);
//                Float f = Float.parseFloat(totalamt);
//                df.format(f);
//                totalamt = String.format("%.2f", f);
//
//                totalamttext.setText(totalamt);
//                qrserviceRow.setVisibility(View.VISIBLE);
//                qrtotamountRow.setVisibility(View.VISIBLE);
//            }
//            if (bundle.getString("qrstring") != null) {
//                qrstring = bundle.getString("qrstring");
//
//
//                //generateBharatQR(qrstring);
//
//            }
        }

        //OpenAvailableIntents();
        return view;
    }

    private void vpaPayment(String type,String vpa) {
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("amount", amount);
        paramsMap.put("currency", currencyCode);

        paramsMap.put("merchantID",merchantId);
        paramsMap.put("merchantTxnNo",tranRefNo);

        paramsMap.put("invoiceNo",invoiceNo);
        paramsMap.put("allowDisablePaymentMode",type);
        paramsMap.put("paymentMode", type);
        paramsMap.put("payType", "0");

        if(!vpa.equals("") &&  vpa.contains("@")){
            paramsMap.put("customerUPIAlias", vpa);
        }
        if(!merchantId.equals(sharedpreferences.getString("mid",null).toString())){
            paramsMap.put("aggregatorID",sharedpreferences.getString("mid",null));
        }

        if(customerID!=null && !customerID.equals("")){
            paramsMap.put("customerID", customerID);
        }
        paramsMap.put("transactionType","SALE");
        paramsMap.put("secureTokenHash","Y");
        paramsMap.put("customerEmailID",customerEmailID);
        paramsMap.put("returnURL", APISettings.getApiSettings().getReturnUrl());

        String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);
        final Context context = this.getActivity().getApplicationContext();
        AsyncHttpClient client = new AsyncHttpClient();


        final int DEFAULT_TIMEOUT = 30 * 1000;
        client.setConnectTimeout(DEFAULT_TIMEOUT);
        client.setMaxRetriesAndTimeout(1, DEFAULT_TIMEOUT);
        client.setTimeout(DEFAULT_TIMEOUT);

        RequestParams params = new RequestParams();
        params.add("amount",amount);
        params.add("currencyCode",currencyCode);
        params.add("merchantID",merchantId);
        params.add("merchantTxnNo",tranRefNo);
        params.add("invoiceNo",invoiceNo);
        params.add("allowDisablePaymentMode",type);
        params.put("secureHash", secureHash);
        params.add("paymentMode", type);
        params.add("payType", "0");
        if(!vpa.equals("") &&  vpa.contains("@")){
            params.add("customerUPIAlias", vpa);
        }
        if(!merchantId.equals(sharedpreferences.getString("mid",null).toString())){
            params.add("aggregatorID",sharedpreferences.getString("mid",null));
        }

        if(customerID!=null && !customerID.equals("")){
            params.add("customerID", customerID);
        }
        params.add("transactionType","SALE");
        params.add("secureTokenHash","Y");
        params.add("customerEmailID",customerEmailID);
        params.add("returnURL", APISettings.getApiSettings().getReturnUrl());

        StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        paydialog.show();
        String callType = "&callType=s2s";
        proeedTopay.setVisibility(View.GONE);
        client.post(context, APISettings.getApiSettings().getFormActionUrl()+callType, stringEntity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                paydialog.dismiss();
                //Log.d("vpa response",response.toString());
                //System.out.println("response===..............."+response);
                try {
                    if (response.has("responseCode")) {
                        try {
                            if (statusCode == 200) {
                                try {
                                    String responseCode = response.getString("responseCode");
                                    if (responseCode.equals("000") || responseCode.equals("0000") || responseCode.equals("R1000")) {
                                        if (clickedflag) {
                                            StartCounter(view);
                                            SceduleTask();
                                        }
                                        clickedflag = false;
                                        usermsg.setVisibility(View.VISIBLE);
                                        usermsg.setText("Payment approval request has been sent to your BHIM UPI App. Please authorize the payment from your BHIM UPI App.");
                                        //statButton.setVisibility(View.VISIBLE);
                                    } else {
                                        Intent intent = new Intent();
                                        PayPhiSdk.onPaymentResponse(0, 4, intent);
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                            flag = false;
                                        }
                                    }
                                } catch (JSONException e) {
                                    Intent intent = new Intent();
                                    PayPhiSdk.onPaymentResponse(0, 4, intent);
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                        flag = false;
                                    }
                                }
                            } else {
                                Intent intent = new Intent();

                                PayPhiSdk.onPaymentResponse(0, 4, intent);
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    flag = false;
                                }
//
                            }

                        } catch (Exception e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                            paydialog.dismiss();
                            e.printStackTrace();
                            Intent intent = new Intent();
                            PayPhiSdk.onPaymentResponse(0, 2, intent);
                            if (getActivity() != null) {
                                getActivity().finish();
                                flag = false;
                            }
                        }
                    }else {
                        Intent intent = new Intent();
                        PayPhiSdk.onPaymentResponse(0, 2, intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                            flag = false;
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    paydialog.dismiss();
                    e.printStackTrace();
                    Intent intent = new Intent();
                    PayPhiSdk.onPaymentResponse(0, 2, intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                        flag = false;
                    }
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                paydialog.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Intent intent = new Intent();
                // getActivity().setResult(3, intent);
                PayPhiSdk.onPaymentResponse(0, RESULT_CANCELED, intent);
                getActivity().finish();
                handleHttpError(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                paydialog.dismiss();
                super.onFailure(statusCode, headers, responseString, throwable);
                Intent intent = new Intent();
                // getActivity().setResult(3, intent);
                PayPhiSdk.onPaymentResponse(0, RESULT_CANCELED, intent);
                getActivity().finish();
                handleHttpError(statusCode);
            }
        });

    }

    private int appsCount(){
        PackageManager manager = getContext().getPackageManager();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse(testUPiString));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return infos.size();
    }

    private void SceduleTask() {
      /*  Timer timer = new Timer();
        int time = 0;
        time=10;
        long callingtime =  time * 1000;
        timer.schedule(new UpdateTimeTask(),1, callingtime);*/
        handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
      /* do what you need to do */
                //CallMehod();
                // CheckStatusTransaction(view);
                if(flag){
                    CheckStatusTransaction();
                }


      /* and here comes the "trick" */
                handler.postDelayed(this, 30000);
            }
        };
        handler.postDelayed(runnable, 30000);
        // submit task to threadpool:


// At some point in the future, if you want to kill the task:

    }

    private void StartCounter(View view) {

           countDownTimer = new CountDownTimer(180000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(enableflag){
                    //statButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    progressBar.setMax(180);
                    progressBar.setProgress((int) (millisUntilFinished / 1000));
                    time.setText(""+millisUntilFinished / 1000);
                }else{
                    statButton.setVisibility(View.VISIBLE);
                    statButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    statButton.setText("Verify Payment");
                }

             //   mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                statButton.setVisibility(View.VISIBLE);
                statButton.setEnabled(true);
                statButton.setText("Verify Payment");
                progressBar.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                usermsg.setText("Payment confirmation not yet received. Please click on verify in case you have approved the request.");
               // mTextField.setText("done!");
                flag = false;
            }

        }.start();

        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = "user";
                CheckStatusTransaction();
            }
        });

    }
    private void StoreTransaction(String responseCode,String txnRespDescription,String merchantId,String merchantTxnNo,String txnStatus,String txnID,String txnAuthID,String paymentDateTime,String invoiceNo,String customerID){
        Map map= new HashMap();

        map.put("responseCode",responseCode);
        map.put("respDescription",txnRespDescription);
        map.put("merchantId", merchantId);
        map.put("merchantTxnNo", merchantTxnNo);
        map.put("txnStatus", txnStatus);
        map.put("txnID", txnID);
        map.put("authID",txnAuthID);
        //map.put("txnResponseCode", "");
        //	map.put("token", token);
        map.put("paymentDateTime", paymentDateTime);
        map.put("paymentID", txnID);
        map.put("addlParam1",customerID);
        map.put("invoiceNo",invoiceNo);
        map.put("insertDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
       String amnt =  sharedpreferences.getString("Qr_"+merchantTxnNo,null);
        map.put("amount",amnt);


        //PaymentsOption.getInstace().StoreTxnForPogo(map);
    }



    public void OpenAvailableIntents(){
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrstring));
       // startActivityForResult(intent,100);


        PackageManager manager = getActivity().getPackageManager();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse(qrstring));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            //Then there is an Application(s) can handle your intent
           // Toast.makeText(getContext(), "Acivity found", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(qrstring));
            startActivityForResult(intent1,100);
        } else {
            Toast.makeText(getContext(), "No Acivity found", Toast.LENGTH_SHORT).show();
            //No Application can handle your intent
        }
    }

    public void checkStatus(){
        System.out.println("Inside check stauts......");
        enableflag = false;
        usermsg.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        usermsg.setText("Payment confirmation not yet received. Please click on verify in case of you have approved the request.");
        statButton.setVisibility(View.VISIBLE);
        statButton.setEnabled(true);
        statButton.setText("Verify Payment");
        CheckStatusTransaction();
    }

    public void CheckStatusTransaction(){

        TreeMap<String, String> paramsMap = new TreeMap<String, String>();


        paramsMap.put("amount", amount);
        paramsMap.put("merchantTxnNo", tranRefNo);
        paramsMap.put("transactionType", "STATUS");
        paramsMap.put("paymentMode", "UPIQR");
        if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
            paramsMap.put("merchantID", sharedpreferences.getString("mid", null));
        }else{
            paramsMap.put("merchantID", merchantId);
        }

      //  String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);


        try {
            Context context = this.getActivity().getApplicationContext();
            final AsyncHttpClient client = new AsyncHttpClient();


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
            params.put("paymentMode", "UPIQR");

            if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                params.put("merchantID", sharedpreferences.getString("mid", null));
            }else{
                params.put("merchantID", merchantId);
            }

            StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
            if(!flag) {
                paydialog.show();
            }
            //qreditor=sharedpreferences.edit();
            //client.post(context, "", );
            // client.post(context,APISettings.getApiSettings().getGenerateQr() ,  "application/json",new JsonHttpResponseHandler() {
            //           client.post
            client.post(context, APISettings.getApiSettings().getCheckPaymentStatus(), stringEntity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //Log.d("json rsponse",response.toString());
                    if(!flag) {
                        paydialog.dismiss();
                    }
                    //System.out.println("response===..............."+response);
                    try {
                        //JSONObject jsonObject = response.getJSONObject("resBody");
                        String txnId ="";
                        String responseCode = "";
                        String merchantId=   "";
                        String merchantTxnNo ="";
                        String txnStatus="" ;
                        String paymentDateTime="";
                        String paymentID ="";
                        String txnAuthId="";
                        String respDescription ="";
                        //	String txnStatus = response.getString("txnStatus");



                        if(response.has("responseCode") && response.getString("responseCode")!=null) {
                            responseCode = response.getString("responseCode");

                        }


                        if(response.has("merchantTxnNo") && response.getString("merchantTxnNo")!=null) {
                            merchantTxnNo = response.getString("merchantTxnNo");

                        }

                        if(response.has("merchantId") && response.getString("merchantId")!=null) {
                            merchantId = response.getString("merchantId");
                        }

                        if(response.has("txnID") && response.getString("txnID")!=null) {
                            txnId = response.getString("txnID");
                        }

                        if(response.has("txnAuthId") && response.getString("txnAuthId")!=null) {
                            txnAuthId = response.getString("txnAuthID");
                        }

                        if(response.has("responseCode") && (responseCode.equals("000") || responseCode.equals("0000")) ){
                            //Toast.makeText(context, respDescription, Toast.LENGTH_SHORT).show();
                            if(response.has("txnStatus") && response.getString("txnStatus")!=null){
                                txnStatus=response.getString("txnStatus");
                            }




                            if(response.has("txnRespDescription")&& response.getString("txnRespDescription")!=null){
                                respDescription = response.getString("txnRespDescription");
                            }
                            if(response.has("respDescription")&& response.getString("respDescription")!=null){
                                respDescription = response.getString("respDescription");
                            }


                            if(response.has("paymentDateTime") && response.getString("paymentDateTime")!=null){

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

                            if(response.has("txnStatus") && response.getString("txnStatus")!=null){
                                txnStatus=response.getString("txnStatus");
                            }
                            //txnStatus="Fail";

                            if(response.has("txnRespDescription")&& response.getString("txnRespDescription")!=null){
                                respDescription = response.getString("txnRespDescription");
                            }
                            if(response.has("respDescription") && response.getString("respDescription")!=null){
                                respDescription = response.getString("respDescription");
                            }


                            if(response.has("paymentDateTime") && response.getString("paymentDateTime")!=null){

                                paymentDateTime = response.getString("paymentDateTime");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {


                                  //  paymentDateTime= dateFormat.format(paymentDateTime);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                    //        msg=PayPhiSdk.onMessageRequest(10);
                            /*int errorcode = Message.VERIFYERR10.getMessage();

                            //map = PayPhiSdk.getMessage();
                            if(map.containsKey(errorcode)){
                                msg = map.get(errorcode);
                            }
                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();*/
                            //showNotification(txnStatus,respDescription);
                        }


                            if(txnStatus.equals("SUC")){
                                StoreTransaction(responseCode,respDescription,merchantId,merchantTxnNo,txnStatus,txnId,txnAuthId,paymentDateTime,invoiceNo,customerID);
                                Map respMap = new HashMap();
                                respMap =  Utility.toMap(response);
                                Intent intent = new Intent();
                                for ( Object key : respMap.keySet() ) {
                                    intent.putExtra(String.valueOf(key),String.valueOf(respMap.get(key)));
                                }
                                countDownTimer.cancel();
                                flag = false;
                                intent.putExtra("respType","checkstatus");
                                //PayPhiSdk.onPaymentResponse(0, -1, intent);


                                if(getActivity()!=null){
                                 //   getActivity().setResult(getActivity().RESULT_OK, intent);
                                    PayPhiSdk.onPaymentResponse(0, RESULT_OK, intent);
                                    getActivity().finish();
                                }

                              //  Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                              //  getActivity().finish();


                            }

                        if(txnStatus.equals("REJ") ) {

                            StoreTransaction(responseCode,respDescription,merchantId,merchantTxnNo,txnStatus,txnId,txnAuthId,paymentDateTime,invoiceNo,customerID);
                            Map respMap = new HashMap();
                            respMap =  Utility.toMap(response);
                            Intent intent = new Intent();
                            for ( Object key : respMap.keySet() ) {

                                intent.putExtra(String.valueOf(key),String.valueOf(respMap.get(key)));


                            }
                            intent.putExtra("respType","checkstatus");
                            countDownTimer.cancel();
                            flag = false;
                            if(getActivity()!=null){
                               // getActivity().setResult(getActivity().RESULT_OK, intent);
                                PayPhiSdk.onPaymentResponse(0, RESULT_OK, intent);
                                getActivity().finish();
                            }

                        }else if(txnStatus.equals("REQ")){
                            int errorcode = Message.VERIFYERR11.getMessage();
//                         //   map = PayPhiSdk.getMessage();
                            if(!flag || click == "user"){
                                click = "system";
                                if(map.containsKey(errorcode)){
                                    msg = map.get(errorcode);
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getContext(),"Transaction in Request state please click verify after payment",Toast.LENGTH_LONG).show();
                                }
                            }

//                            if(!flag){
//                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
//                            }


                        }else if(txnStatus.equals("ERR")){
                            int errorcode = Message.VERIFYERR12.getMessage();
                            countDownTimer.cancel();
                            flag = false;
                           // map = PayPhiSdk.getMessage();
                            if(map.containsKey(errorcode)){
                                msg = map.get(errorcode);
                            }

                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        }





                    } catch (JSONException e) {
                        paydialog.dismiss();
                        e.printStackTrace();
                     //   Toast.makeText(getContext(),"Execption..1 ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        countDownTimer.cancel();
                      //  getActivity().setResult(2, intent);
                        PayPhiSdk.onPaymentResponse(0, 2, intent);
                        getActivity().finish();
                        //Toast.makeText(getContext(), "Internal error", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    paydialog.dismiss();
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Toast.makeText(getContext(),"Error in processing Transaction status ",Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent();
//                   // getActivity().setResult(3, intent);
//                    PayPhiSdk.onPaymentResponse(0, 3, intent);
//                    getActivity().finish();
//                    handleHttpError(statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    paydialog.dismiss();
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(getContext(),"Error in processing Transaction status ",Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent();
//                   // getActivity().setResult(3, intent);
//                    PayPhiSdk.onPaymentResponse(0, 3, intent);
//                    getActivity().finish();
//                    handleHttpError(statusCode);
                }
            });

        } catch (Exception e) {
//            Toast.makeText(getContext(),"Execption..4 ",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


//end call
    }

    private void SetMessageMap() {

        map =  new HashMap<>();
        map.put(10," Transaction yet not recieved to server please try again");
        map.put(11 ,"We have still not received your payment confirmation, click on verify after payment done.");
        map.put(12 ,"Error in processing Transaction status");
        map.put(13, "unable to generate QR");
        map.put(14 ,"Select Your Bank");
        map.put(15, "Enter or Scan Aadhaar Number");
        map.put(16, "Enter valid Aadhaar No.");
        map.put(17, "Last Transaction is unknown please try after 15 mins after verifing Consumer account detials.");
        map.put(18, "Last Delivery is already paid");
    }

    private void generateBharatQR(String qrstring){

        // String qrInput = generateQRInput(amount,mobileNumber, txnNumber);
        //System.out.println("QRInput : "+ qrstring);
        //generateQRCode(qrstring);

    }

        // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




    private void handleHttpError(int statusCode) {
        //paydialog.dismiss();
        String errorText = "Server communication error - #" + statusCode;
        if (statusCode == 403) {
            sessionError();
        } else {
            // Toast.makeText(getContext(), errorText, Toast.LENGTH_LONG).show();
        }
    }
    private void sessionError() {
        //  Toast.makeText(getContext(), "Your session has expired. Please relogin.", Toast.LENGTH_LONG).show();
       /* Intent intent = new Intent(PaymentOptions.this, LoginActivity.class);
        startActivity(intent);
        finish();*/
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
            Context context = getActivity().getApplicationContext();
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

           // qreditor = sharedpreferences.edit();
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
                            proeedTopay.setVisibility(View.GONE);
                            JSONObject jsonObject = response.getJSONObject("respBody");
                            if (jsonObject.has("upiQR")) {
                                Gson gson = new Gson();
                                String json = gson.toJson(jsonObject);
//                                qreditor.putString(tranRefNo, json);
//                                qreditor.putString("Qr_" + tranRefNo, amount);
//                                qreditor.commit();
                            }

                            if (qrType.equals("U")) {
                                qrstring = jsonObject.getString("upiQR");
                                System.out.println("upiQR===" + qrstring.toString());
                                OpenAvailableIntents();
                                if(clickedflag){
                                     StartCounter(view);
                                     SceduleTask();
                                 }
                                clickedflag =false;
                                usermsg.setVisibility(View.VISIBLE);
                                usermsg.setText("Waiting for payment confirmation.\n Please authorize the payment from your BHIM UPI App.");
                                //statButton.setVisibility(View.VISIBLE);
//                                if (jsonObject.has("serviceChargeUPI") && !jsonObject.getString("serviceChargeUPI").equals("0")) {
//                                    serviceCharge = jsonObject.get("serviceChargeUPI").toString();
//
//                                }

                                //simulated
                                //  AttachFragment();
                                if (!qrstring.equals("") && !qrstring.equals("null")) {
                                    //bahratqrString = jsonObject.get("bharatQR").toString();
                                    //           genrateFuncionIntent(qrString);
                                    //AttachFragment();

                                } else {
                                    if (qrstring != null && !qrstring.equals("") && !qrstring.equals("null")) {
                                        //  genrateFuncionIntent(qrString);
                                       // AttachFragment();
                                    } else {
                                        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                       // ProceedForUpiWeb("upi");
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
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                /*Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);*/

                        } else if (status == 201) {
                            paydialog.dismiss();
                            //Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                            // sessionError();
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                               /* Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);*/
                            //Simulated
                            // AttachFragment();

                        } else {
                            paydialog.dismiss();
                            //Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                            // sessionError();
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent();
                        PayPhiSdk.onPaymentResponse(0, 2, intent);
                        if(getActivity()!=null){
                            getActivity().finish();
                            flag=false;
                        }
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent();
                    PayPhiSdk.onPaymentResponse(0, 1, intent);
                    if(getActivity()!=null){
                        getActivity().finish();
                        flag=false;
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent();
                    PayPhiSdk.onPaymentResponse(0, 1, intent);
                    if(getActivity()!=null){
                        getActivity().finish();
                        flag=false;
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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

    private void GetSerCharge() {
       /* String amount = amt.getText().toString();
        amount = amount.replace("Rs. ","");
        amount = amount.trim();*/
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("merchantID", merchantId);
        paramsMap.put("amount", amount);
        paramsMap.put("currencyCode",currencyCode);
        paramsMap.put("merchantTxnNo", tranRefNo);
        paramsMap.put("paymentMode", "UPI");
        paramsMap.put("paymentOptionCodes", "");
        paramsMap.put("secureTokenHash", "Y");
        if(sharedpreferences.getBoolean("useAggregatorMID", false)){
            paramsMap.put("addlParam1", addlParam1);
        }
        if (invoiceList != null || !invoiceList.equals("")) {
            paramsMap.put("invoiceList",invoiceList);
        }

        if (merchantId == null || (!merchantId.equals(sharedpreferences.getString("mid", null).toString()))) {
            paramsMap.put("aggregatorID", sharedpreferences.getString("mid", null));
        }

        String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);

        try {
            Context context = this.getActivity().getApplicationContext();
            AsyncHttpClient client = new AsyncHttpClient();


            final int DEFAULT_TIMEOUT = 30 * 1000;
            client.setConnectTimeout(DEFAULT_TIMEOUT);
            client.setMaxRetriesAndTimeout(1, DEFAULT_TIMEOUT);
            client.setTimeout(DEFAULT_TIMEOUT);
//            paydialog.show();

            RequestParams params = new RequestParams();
            params.put("merchantID", merchantId);
            params.put("amount", amount);
            params.put("merchantTxnNo", tranRefNo);
            params.put("paymentMode", "UPI");
            params.put("paymentOptionCodes", "");
            params.put("secureTokenHash", "Y");
            params.put("currencyCode",currencyCode);
            params.put("secureHash", secureHash);
            if(sharedpreferences.getBoolean("useAggregatorMID", false)){
                params.put("addlParam1", addlParam1);
            }
            if (invoiceList != null || !invoiceList.equals("")) {
                params.put("invoiceList",invoiceList);
            }
            if (merchantId == null || (!merchantId.equals(sharedpreferences.getString("mid", null).toString()))) {
                params.put("aggregatorID", sharedpreferences.getString("mid", null));
            }

            if(requestFrom!=null && requestFrom.equals("collectionApp") ){

                client.addHeader("token", sharedpreferences.getString("jwtTokenNew", null));
                client.addHeader("userID", sharedpreferences.getString("sdkuserId", null));
                client.addHeader("merchantID", merchantId);
                client.addHeader("versionID", "2.0");


            }


            StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
           // Log.d("params",params.toString());
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
            if(getActivity()!=null) {
                paydialog.show();
            }
            //qreditor=sharedpreferences.edit();
            //client.post(context, "", );
            // client.post(context,APISettings.getApiSettings().getGenerateQr() ,  "application/json",new JsonHttpResponseHandler() {
            //           client.post



            client.post(context, APISettings.getApiSettings().getGetSerCharges(), stringEntity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                   // Log.d("service charge",response.toString());
                    if(getActivity()!=null) {
                        paydialog.dismiss();
                    }
                    //System.out.println("response===..............."+response);
                    try {

                        //System.out.println("response come from bank api-");
                        int responseStatus = response.getJSONObject("respHeader").getInt("responseCode");
                        //System.out.println("responseCode : " + responseStatus);
                        if (responseStatus == 200) {
                            proeedTopay.setVisibility(View.VISIBLE);
                            serchargeResponse = response.getJSONObject("respBody");
                            if(serchargeResponse.has("serviceCharge")){

                                if(Float.parseFloat(serchargeResponse.getString("serviceCharge"))!=0){
                                        /*String amount = amt.getText().toString();
                                        amount = amount.replace("Rs. ","");
                                        amount = amount.trim();*/


                                    serviceChargeAmt = serchargeResponse.getString("serviceCharge");


                                    Float tot = Float.parseFloat(amount)+Float.parseFloat(serviceChargeAmt);

                                    DecimalFormat df = new DecimalFormat();
                                    df.setMinimumFractionDigits(2);
                                    df.format(tot);
                                    String formattot = String.format("%.2f", tot);


                                    System.out.println("amnt"+amount);
                                    System.out.println("serviceChargeAmt"+serviceChargeAmt);
                                    System.out.println("Totoal"+tot);


                                    servicetxt.setText(serviceChargeAmt);
                                    totalamttext.setText(formattot);
                                    qrserviceRow.setVisibility(View.VISIBLE);
                                    qrtotamountRow.setVisibility(View.VISIBLE);
                                }
                            }
                        }else{
                            if(getActivity()!=null) {
                                paydialog.dismiss();
                            }
                            Intent intent = new Intent();
                            PayPhiSdk.onPaymentResponse(0, 4, intent);
                            if(getActivity()!=null){
                                getActivity().finish();
                                flag=false;
                            }
                        }


                    } catch (JSONException e) {
                        if(getActivity()!=null) {
                            paydialog.dismiss();
                        }
                        e.printStackTrace();
                        //   Toast.makeText(getContext(),"Execption..1 ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        PayPhiSdk.onPaymentResponse(0, 2, intent);
                        if(getActivity()!=null){
                            getActivity().finish();
                            flag=false;
                        }
                        //Toast.makeText(getContext(), "Internal error", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if(getActivity()!=null) {
                        paydialog.dismiss();
                    }
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    // Toast.makeText(getContext(),"Execption..2 ",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    PayPhiSdk.onPaymentResponse(0, 3, intent);
                    handleHttpError(statusCode);
                    if(getActivity()!=null){
                        getActivity().finish();
                        flag=false;
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if(getActivity()!=null) {
                        paydialog.dismiss();
                    }
                    super.onFailure(statusCode, headers, responseString, throwable);
                    //   Toast.makeText(getContext(),"Execption.. 3",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    PayPhiSdk.onPaymentResponse(0, 3, intent);
                    handleHttpError(statusCode);
                    if(getActivity()!=null){
                        getActivity().finish();
                        flag=false;
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Intent intent = new Intent();
            PayPhiSdk.onPaymentResponse(0, 2, intent);
            if(getActivity()!=null){
                getActivity().finish();
                flag=false;
            }

        }
    }
}
