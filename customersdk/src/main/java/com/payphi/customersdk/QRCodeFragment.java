package com.payphi.customersdk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TimeUtils;
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

import com.payphi.customersdk.util.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    TextView merchantName;
    Button statButton;
    TextView cancelText;
    TextView chooseUpi;
    CountDownTimer countDownTimer;
    boolean enableflag= true;
    boolean clickedflag=true;
    Button clicker;
    ProgressBar progressBar;
    RecyclerView imageList;
    Button showMoreApps;
    private RecyclerView.LayoutManager layoutManager;
    UpiAdapter upiAdapter;
    ArrayList<UpiModel> imageModelArrays;
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
                    showMoreApps = (Button)view.findViewById(R.id.showMoreApps);
                    chooseUpi = (TextView)view.findViewById(R.id.chooseUpi);
                    cancelText = (TextView) view.findViewById(R.id.cancelText);
                    amtText = (TextView) view.findViewById(R.id.amtId);
                    servicetxt =(TextView) view.findViewById(R.id.qrservicechanrgeId);
                    totalamttext =(TextView) view.findViewById(R.id.qrtotalamtId);
                    merchantName = (TextView) view.findViewById(R.id.merchantName);
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
                    cancelText.setVisibility(View.INVISIBLE);
                    showMoreApps.setVisibility(View.GONE);
                    chooseUpi.setVisibility(View.GONE);
                    progressBar = (ProgressBar)view.findViewById(R.id.pb);
                    progressBar.setVisibility(View.GONE);
                    time = (TextView)view.findViewById(R.id.time);
                    time.setVisibility(View.GONE);
                    if(sharedpreferences.getString("merchantName",null)!=null){
                        merchantName.setText(sharedpreferences.getString("merchantName",null));
                    }else{
                        merchantName.setText("");
                    }

                    imageList = (RecyclerView)view.findViewById(R.id.imageList);
                    layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
                    imageList.setLayoutManager(layoutManager);

                    imageModelArrays = new ArrayList<>();

                    upiAdapter = new UpiAdapter(getActivity(), imageModelArrays, getActivity(),QRCodeFragment.this);
                    imageList.setAdapter(upiAdapter);
                    showMoreApps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            imageList.setVisibility(View.GONE);
                            chooseUpi.setVisibility(View.GONE);
                            showMoreApps.setVisibility(View.GONE);
                            OpenAvailableIntents();
                            if(clickedflag){
                                StartCounter(view);
                                SceduleTask();
                            }
                            clickedflag =false;
                            usermsg.setVisibility(View.VISIBLE);
                            usermsg.setText("Waiting for payment confirmation.\n Please authorize the payment from your BHIM UPI App.");
                        }
                    });
                    proeedTopay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    proeedTopay.setBackgroundColor(getResources().getColor(R.color.button));
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
                    input.setFilters(EmojiFilter.getFilter());
                    RadioButton rb1 = (RadioButton)dialog.findViewById(R.id.cash);
                    RadioButton rb2 = (RadioButton)dialog.findViewById(R.id.card);
                    clicker = (Button) dialog.findViewById(R.id.confButton);
                    clicker.setEnabled(false);
                        proeedTopay.setBackgroundColor(getResources().getColor(R.color.buttonReset));
                    if(appsCount() == 0){
                        txtTitle.setText("VPA Payment");
                        clicker.setEnabled(true);
                        rb1.setVisibility(View.GONE);
                        input.setEnabled(true);
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
                                        proeedTopay.setVisibility(View.GONE);
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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("sample.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    private void getUpiOptions(){
        OkHttpClient client = new OkHttpClient();
        paydialog.show();
        Request request = new Request.Builder()
                .url("https://api.pexels.com/v1/search?query=work+place&per_page=50&page=1")
                .header("Authorization", "YOUR_API")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                paydialog.dismiss();
                try {
                    JSONObject parent = new JSONObject(loadJSONFromAsset());
                    JSONArray photos = parent.getJSONArray("photos");

                    Log.d("resp", "data"+photos);
                    for (int i = 0; i < photos.length(); i++) {

                        JSONObject data = photos.getJSONObject(i);
                        UpiModel upiModel = new UpiModel();
                        upiModel.setAction(data.getString("action"));
                        upiModel.setName(data.getString("name"));
                        upiModel.setImageUrl(data.getString("url"));
                        upiModel.setUpiUrl(qrstring);
                        imageModelArrays.add(upiModel);
                    }

                    upiAdapter.notifyDataSetChanged();

                } catch (JSONException f) {
                    f.printStackTrace();
                }
                //Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String mMessage = response.body().string();
                paydialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject parent = new JSONObject(loadJSONFromAsset());
                            JSONArray photos = parent.getJSONArray("photos");
                            showMoreApps.setVisibility(View.VISIBLE);
                            Log.d("resp", "data"+photos);
                            for (int i = 0; i < photos.length(); i++) {

                                JSONObject data = photos.getJSONObject(i);
                                UpiModel upiModel = new UpiModel();
                                upiModel.setAction(data.getString("action"));
                                upiModel.setName(data.getString("name"));
                                upiModel.setImageUrl(data.getString("url"));

                                imageModelArrays.add(upiModel);
                            }

                            upiAdapter.notifyDataSetChanged();

                        } catch (JSONException f) {
                            f.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void vpaPayment(String type,String vpa) {
        final int DEFAULT_TIMEOUT = 90;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

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
        //AsyncHttpClient client = new AsyncHttpClient();


        FormBody.Builder builder = new FormBody.Builder();
        builder.add("amount",amount);
        builder.add("currencyCode",currencyCode);
        builder.add("merchantID",merchantId);
        builder.add("merchantTxnNo",tranRefNo);
        builder.add("invoiceNo",invoiceNo);
        builder.add("allowDisablePaymentMode",type);
        builder.add("secureHash", secureHash);
        builder.add("paymentMode", type);
        builder.add("payType", "0");
        if(!vpa.equals("") &&  vpa.contains("@")){
            builder.add("customerUPIAlias", vpa);
        }
        if(!merchantId.equals(sharedpreferences.getString("mid",null).toString())){
            builder.add("aggregatorID",sharedpreferences.getString("mid",null));
        }

        if(customerID!=null && !customerID.equals("")){
            builder.add("customerID", customerID);
        }
        builder.add("transactionType","SALE");
        builder.add("secureTokenHash","Y");
        builder.add("customerEmailID",customerEmailID);
        builder.add("returnURL", APISettings.getApiSettings().getReturnUrl());
        RequestBody requestBody = builder
                .build();
        String callType = "&callType=s2s";
        Request request = new Request.Builder()
                .url(APISettings.getApiSettings().getFormActionUrl()+callType)
                .post(requestBody)
                .build();
        paydialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Handler mainHandler = new Handler(getContext().getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        paydialog.dismiss();
                        flag = false;
                       // Log.d("error",e.getMessage());
                        Intent intent = new Intent();
                        PayPhiSdk.onPaymentResponse(0, RESULT_CANCELED, intent);
                        getActivity().finish();
                    }
                };
                mainHandler.post(runnable);
            }

            @Override
            public void onResponse(Call call, final Response data) throws IOException {
                Handler mainHandler = new Handler(getContext().getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        paydialog.dismiss();
                        //Log.d("vpa response",response.toString());
                        //System.out.println("response===..............."+response);
                        try {
                            ResponseBody responseBody = data.body();
                            JSONObject response = new JSONObject(responseBody.string());
                           // Log.d("vpa response",response.toString());
                            int statusCode = data.code();
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
                                                    flag = false;
                                                    getActivity().finish();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Intent intent = new Intent();
                                            PayPhiSdk.onPaymentResponse(0, 4, intent);
                                            if (getActivity() != null) {
                                                flag = false;
                                                getActivity().finish();
                                            }
                                        }
                                    } else {
                                        Intent intent = new Intent();
                                        PayPhiSdk.onPaymentResponse(0, 4, intent);
                                        if (getActivity() != null) {
                                            flag = false;
                                            getActivity().finish();
                                        }
//
                                    }

                                } catch (Exception e) {
                                    //Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                    paydialog.dismiss();
                                 //   e.printStackTrace();
                                    Intent intent = new Intent();
                                    PayPhiSdk.onPaymentResponse(0, 2, intent);
                                    if (getActivity() != null) {
                                        flag = false;
                                        getActivity().finish();
                                    }
                                }
                            }else {
                                Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);
                                if (getActivity() != null) {
                                    flag = false;
                                    getActivity().finish();
                                }
                            }
                        }catch (Exception e){
                           // Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                            paydialog.dismiss();
                           // e.printStackTrace();
                            Intent intent = new Intent();
                            PayPhiSdk.onPaymentResponse(0, 2, intent);
                            if (getActivity() != null) {
                                flag = false;
                                getActivity().finish();
                            }
                        }

                    }
                };
                mainHandler.post(runnable);
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
                    cancelText.setVisibility(View.VISIBLE);
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
                cancelText.setVisibility(View.VISIBLE);
                statButton.setEnabled(true);
                statButton.setText("Verify Payment");
                proeedTopay.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                usermsg.setText("Payment confirmation not yet received.Please click on verify in case you have approved the request.");
               // mTextField.setText("done!");
                flag = false;
            }

        }.start();

        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statButton.setBackgroundColor(getResources().getColor(R.color.button));
                click = "user";
                CheckStatusTransaction();
                statButton.setBackgroundColor(getResources().getColor(R.color.buttonReset));
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
    protected void stopService(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void checkStatus(){
        //System.out.println("Inside check stauts......");
        enableflag = false;
        usermsg.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        usermsg.setText("Payment confirmation not yet received.Please click on verify in case  you have approved the request.");
        statButton.setVisibility(View.VISIBLE);
        cancelText.setVisibility(View.VISIBLE);
        statButton.setEnabled(true);
        statButton.setText("Verify Payment");
        CheckStatusTransaction();
    }

    public void CheckStatusTransaction(){
        OkHttpClient client = new OkHttpClient();
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
            FormBody.Builder builder = new FormBody.Builder();
//            paydialog.show();
            
            builder.add("amount", amount);
            builder.add("merchantTxnNo", tranRefNo);
            builder.add("transactionType", "STATUS");
            builder.add("originalTxnNo", tranRefNo);
            builder.add("paymentMode", "UPIQR");

            if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                builder.add("merchantID", sharedpreferences.getString("mid", null));
            }else{
                builder.add("merchantID", merchantId);
            }
            final RequestBody requestBody = builder
                    .build();
            Request request = new Request.Builder()
                    .url(APISettings.getApiSettings().getCheckPaymentStatus())
                    .post(requestBody)
                    .build();
            if(!flag) {
                paydialog.show();
            }
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    try {
                        if(getActivity() == null){
                            Log.d("null", "context");
                        }else {
                            if(!flag) {
                                paydialog.dismiss();
                            }
                            Handler mainHandler = new Handler(getContext().getMainLooper());
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    // Log.d("in error","status check error");
                                    Toast.makeText(getContext(),"Error in processing Transaction status ",Toast.LENGTH_LONG).show();
                                }
                            };
                            mainHandler.post(runnable);
                        }
                    }catch (Exception p){

                    }
                }

                @Override
                public void onResponse(Call call, final Response data) throws IOException {
                    try{
                        if (getActivity() == null) {
                            Log.d("null", "context");
                        } else {
                            Handler mainHandler = new Handler(getContext().getMainLooper());
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (!flag) {
                                        paydialog.dismiss();
                                    }
//                            System.out.println("response===..............."+response);
                                    try {
                                        ResponseBody responseBody = data.body();
                                        JSONObject response = new JSONObject(responseBody.string());
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
                                                    // e.printStackTrace();
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
                                                    // e.printStackTrace();
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


                                        if (txnStatus.equals("SUC")) {
                                            StoreTransaction(responseCode, respDescription, merchantId, merchantTxnNo, txnStatus, txnId, txnAuthId, paymentDateTime, invoiceNo, customerID);
                                            Map respMap = new HashMap();
                                            respMap = Utility.toMap(response);
                                            Intent intent = new Intent();
                                            for (Object key : respMap.keySet()) {
                                                intent.putExtra(String.valueOf(key), String.valueOf(respMap.get(key)));
                                            }
                                            countDownTimer.cancel();
                                            flag = false;
                                            intent.putExtra("respType", "checkstatus");
                                            //PayPhiSdk.onPaymentResponse(0, -1, intent);


                                            if (getActivity() != null) {

                                                //   getActivity().setResult(getActivity().RESULT_OK, intent);
                                                PayPhiSdk.onPaymentResponse(0, RESULT_OK, intent);
                                                getActivity().finish();
                                            }

                                            //  Utility.updateAccessToken(getContext().getSharedPreferences("AppSdk", Context.MODE_PRIVATE), headers);
                                            //  getActivity().finish();


                                        }

                                        if (txnStatus.equals("REJ")) {

                                            StoreTransaction(responseCode, respDescription, merchantId, merchantTxnNo, txnStatus, txnId, txnAuthId, paymentDateTime, invoiceNo, customerID);
                                            Map respMap = new HashMap();
                                            respMap = Utility.toMap(response);
                                            Intent intent = new Intent();
                                            for (Object key : respMap.keySet()) {

                                                intent.putExtra(String.valueOf(key), String.valueOf(respMap.get(key)));


                                            }
                                            intent.putExtra("respType", "checkstatus");
                                            countDownTimer.cancel();
                                            flag = false;
                                            if (getActivity() != null) {
                                                // getActivity().setResult(getActivity().RESULT_OK, intent);
                                                PayPhiSdk.onPaymentResponse(0, RESULT_OK, intent);
                                                getActivity().finish();
                                            }

                                        } else if (txnStatus.equals("REQ")) {
                                            int errorcode = Message.VERIFYERR11.getMessage();
//                         //   map = PayPhiSdk.getMessage();
                                            if (!flag || click == "user") {
                                                click = "system";
                                                if (map.containsKey(errorcode)) {
                                                    msg = map.get(errorcode);
                                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Transaction in Request state please click verify after payment", Toast.LENGTH_LONG).show();
                                                }
                                            }

//                            if(!flag){
//                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
//                            }


                                        } else if (txnStatus.equals("ERR")) {
                                            int errorcode = Message.VERIFYERR12.getMessage();
                                            countDownTimer.cancel();
                                            flag = false;
                                            // map = PayPhiSdk.getMessage();
                                            if (map.containsKey(errorcode)) {
                                                msg = map.get(errorcode);
                                            }

                                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        paydialog.dismiss();
                                        // e.printStackTrace();
                                        flag = false;
                                        //   Toast.makeText(getContext(),"Execption..1 ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent();
                                        countDownTimer.cancel();
                                        //  getActivity().setResult(2, intent);
                                        PayPhiSdk.onPaymentResponse(0, 2, intent);
                                        getActivity().finish();
                                        //Toast.makeText(getContext(), "Internal error", Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        // e.printStackTrace();
                                    }
                                }
                            };
                            mainHandler.post(runnable);
                        }
                    }catch (Exception e){

                    }
                }
            });
        } catch (Exception e) {

//            Toast.makeText(getContext(),"Execption..4 ",Toast.LENGTH_LONG).show();
           // e.printStackTrace();
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
    public boolean doesUpiAppExists(String packageName){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", String.format("failed to get package info for package name = {%s}, exception message = {%s}",
                    packageName, e.getMessage()));
        }

        if (packageInfo == null) {
            return false;
        }else {
            return true;
        }
    }
    public void onClickApp() {
        imageList.setVisibility(View.GONE);
        chooseUpi.setVisibility(View.GONE);
        showMoreApps.setVisibility(View.GONE);
        if(clickedflag){
            StartCounter(view);
            SceduleTask();
        }
        clickedflag =false;
        usermsg.setVisibility(View.VISIBLE);
        usermsg.setText("Waiting for payment confirmation.\n Please authorize the payment from your BHIM UPI App.");
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
        OkHttpClient client = new OkHttpClient();
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

       // System.out.println("paramsMap===" + paramsMap);
        //System.out.println("secureToken===" + secureToken);

        String secureHash = Utility.prepareSecureHash(secureToken, paramsMap);
//            paramsMap.put("secureHash", secureHash);
          /*  int errorcode = Message.VERIFYERR13.getMessage();

            map = PayPhiSdk.getMessage();
            if (map.containsKey(errorcode)) {
                msg = map.get(errorcode);
            }*/
        try {
            Context context = getActivity().getApplicationContext();
            
//            paydialog.show();

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("merchantID", merchantId);
            builder.add("amount", amount);
            builder.add("currency", currencyCode);
            builder.add("merchantRefNo", tranRefNo);
            builder.add("emailID", customerEmailID);
            builder.add("secureHash", secureHash);
            if (type.equals("U")) {
                builder.add("requestType", "UPIQR");
            } else {
                builder.add("requestType", "BharatQR");
            }

            builder.add("secureTokenHash", "Y");

            if (invoiceNo != null && !invoiceNo.equals("")) {
                builder.add("invoiceNo", invoiceNo);
            }
            if (mobileNo!= null && !mobileNo.equals("")) {
                builder.add("mobileNo", mobileNo);
            }
            if (!merchantId.equals(sharedpreferences.getString("mid", null).toString())) {
                builder.add("aggregatorID", sharedpreferences.getString("mid", null));
            }

            if (addlParam1 != null && !addlParam1.equals("")) {
                builder.add("addlParam1", addlParam1);
            }
            if (addlParam2 != null && !addlParam2.equals("")) {
                builder.add("addlParam2", addlParam2);
            }

            if (invoiceList != null && !invoiceList.equals("")) {
                builder.add("invoiceList", invoiceList);
            }

            if (customerID != null && !customerID.equals("")) {
                builder.add("customerID", customerID);
            }
            //client.addHeader("deviceID", "AND:" + deviceID);
            paydialog.show();
            RequestBody requestBody = builder
                    .build();
            Request request = new Request.Builder()
                    .url(APISettings.getApiSettings().getGenerateQr())
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Handler mainHandler = new Handler(getContext().getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            PayPhiSdk.onPaymentResponse(0, 1, intent);
                            if(getActivity()!=null){
                                getActivity().finish();
                                flag=false;
                            }
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    };
                    mainHandler.post(runnable);
                }

                @Override
                public void onResponse(Call call, final Response data) throws IOException {
                    Handler mainHandler = new Handler(getContext().getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                paydialog.dismiss();
                                ResponseBody responseBody = data.body();
                                JSONObject response = new JSONObject(responseBody.string());
                                Log.d("stringresp",response.toString());
                                int status = response.getJSONObject("respHeader").getInt("returnCode");
                                // Toast.makeText(getContext(),"status="+status, Toast.LENGTH_LONG).show();
                                if (status == 200) {
                                    chooseUpi.setVisibility(View.VISIBLE);
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
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("upiString",qrstring).commit();
                                        getUpiOptions();
                                       // System.out.println("upiQR===" + qrstring.toString());
//                                        OpenAvailableIntents();
//                                        if(clickedflag){
//                                            StartCounter(view);
//                                            SceduleTask();
//                                        }
//                                        clickedflag =false;
//                                        usermsg.setVisibility(View.VISIBLE);
//                                        usermsg.setText("Waiting for payment confirmation.\n Please authorize the payment from your BHIM UPI App.");
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
                               // e.printStackTrace();
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
                            } catch (IOException e1) {
                               // e1.printStackTrace();
                            }
                        }
                    };
                    mainHandler.post(runnable);
                }
            });
        } catch (Exception e) {
          //  e.printStackTrace();
        }
        return null;
    }

    private void GetSerCharge() {
        OkHttpClient client = new OkHttpClient();
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
          
//            paydialog.show();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("merchantID", merchantId);
            builder.add("amount", amount);
            builder.add("merchantTxnNo", tranRefNo);
            builder.add("paymentMode", "UPI");
            builder.add("paymentOptionCodes", "");
            builder.add("secureTokenHash", "Y");
            builder.add("currencyCode",currencyCode);
            builder.add("secureHash", secureHash);
            if(sharedpreferences.getBoolean("useAggregatorMID", false)){
                builder.add("addlParam1", addlParam1);
            }
            if (invoiceList != null || !invoiceList.equals("")) {
                builder.add("invoiceList",invoiceList);
            }
            if (merchantId == null || (!merchantId.equals(sharedpreferences.getString("mid", null).toString()))) {
                builder.add("aggregatorID", sharedpreferences.getString("mid", null));
            }
            if(getActivity()!=null) {
                paydialog.show();
            }
            RequestBody requestBody = builder
                    .build();
            Headers.Builder hbuilder = new Headers.Builder();
            if (requestFrom != null && requestFrom.equals("collectionApp")) {

                hbuilder.add("token", sharedpreferences.getString("jwtTokenNew", null));
                hbuilder.add("userID", sharedpreferences.getString("sdkuserId", null));
                hbuilder.add("merchantID", merchantId);
                hbuilder.add("versionID", "2.0");

            }
            Headers header = hbuilder.build();
            final Request request = new Request.Builder()
                    .url(APISettings.getApiSettings().getGetSerCharges())
                    .headers(header)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(getActivity()!=null) {
                        paydialog.dismiss();
                    }
                    Handler mainHandler = new Handler(getContext().getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            PayPhiSdk.onPaymentResponse(0, 3, intent);
                            if(getActivity()!=null){
                                getActivity().finish();
                                flag=false;
                            }
                        }
                    };
                    mainHandler.post(runnable);
                }

                @Override
                public void onResponse(Call call, final Response data) throws IOException {
                    Handler mainHandler = new Handler(getContext().getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity()!=null) {
                                paydialog.dismiss();
                            }
                            //System.out.println("response===..............."+response);
                            try {
                                ResponseBody responseBody = data.body();
                                JSONObject response = new JSONObject(responseBody.string());
                                //System.out.println("response come from bank api-");
                                int responseStatus = response.getJSONObject("respHeader").getInt("responseCode");
                                //System.out.println("responseCode : " + responseStatus);
                                if (responseStatus == 200) {
                                  //  proeedTopay.setVisibility(View.VISIBLE);
                                    getUpiString("U");
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


                                         //   System.out.println("amnt"+amount);
                                           // System.out.println("serviceChargeAmt"+serviceChargeAmt);
                                            //System.out.println("Totoal"+tot);


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
                              //  e.printStackTrace();
                                //   Toast.makeText(getContext(),"Execption..1 ",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                PayPhiSdk.onPaymentResponse(0, 2, intent);
                                if(getActivity()!=null){
                                    getActivity().finish();
                                    flag=false;
                                }
                                //Toast.makeText(getContext(), "Internal error", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                               // e.printStackTrace();
                            }
                        }
                    };
                    mainHandler.post(runnable);
                }
            });
        }catch (Exception e){
          //  e.printStackTrace();
            Intent intent = new Intent();
            PayPhiSdk.onPaymentResponse(0, 2, intent);
            if(getActivity()!=null){
                getActivity().finish();
                flag=false;
            }

        }
    }
}
