package com.payphi.customersdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jayesh on 24-05-2017.
 */
public class WebViewPaymentClient extends Activity {

    final Activity activity = this;
    String formUrl;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.paywebview);

        if(getIntent().getSerializableExtra("formurl") != null) {
            formUrl= getIntent().getSerializableExtra("formurl").toString();
            //Toast.makeText(this,"Web view url=="+formUrl,Toast.LENGTH_LONG).show();
        }


        WebView webView = (WebView) this.findViewById(R.id.paywebviewId);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(true);


        // settings.setPluginsEnabled(true);



        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);


        // ---Setting for local cache---
        webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);

        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by
        // default



       /* webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);

                if(progress == 100)
                    activity.setTitle(R.string.app_name);
            }
        });*/
       try {

           webView.setWebViewClient(new WebViewClient() {

               @Override
               public void onPageStarted(WebView view, String url, Bitmap favicon) {
                   if(url.startsWith("upi://")){
                       view.stopLoading();
                   }
               }

               @Override
               public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                   // Handle the error
                   //Toast.makeText(getApplicationContext(), "Your Internet Connection May not be active Or " + errorCode , Toast.LENGTH_LONG).show();
                   System.out.println("Error in loading=="+description);
                   if(failingUrl.startsWith("upi://")){
                       HandleIntentCall(failingUrl);
                   }else {
                       Intent intent = new Intent();
                       intent.putExtra("ResultType", "web");
                       setResult(3, intent);
                       finish();
                   }
               }

               @Override
               public void onPageFinished(WebView view, String url) {
                   // System.out.println("Return url==" + url);
                   Intent intent = getIntent();
                   Uri uri = Uri.parse(url);

                   if (url.startsWith(APISettings.getApiSettings().getReturnUrl())) {
                       int noOfParams = uri.getQueryParameterNames().size();
                       if (noOfParams <= 2) {
                           intent.putExtra("ResultType","web");
                           setResult(3, intent);
                           finish();
                           return;
                       }

                       for (String paramName : uri.getQueryParameterNames()) {
                           intent.putExtra(paramName, uri.getQueryParameter(paramName));
                       }
                       intent.putExtra("ResultType","web");
                       setResult(Activity.RESULT_OK, intent);
                       finish();
                   }
               }


               @Override
               public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                   //Your code to do
                   // Toast.makeText(getApplicationContext(), "Your Internet Connection May not be active Or " + error , Toast.LENGTH_LONG).show();
                   System.out.println("Error in loading=="+error.toString());
               }


           });

           // webView.loadUrl("javascript:changeLocation("+url+")");

          // webView.loadData(url, "text/html", "UTF-8");
         //  formUrl="https://stackoverflow.com/";
          // formUrl="http://www.google.com";
           String base64version = Base64.encodeToString(formUrl.getBytes(), Base64.DEFAULT);
           webView.loadData(base64version, "text/html; charset=UTF-8", "base64");
          // formUrl ="<html><body><b>hello</b></body></html>";
          // webView.loadDataWithBaseURL(null, formUrl, "text/html", "UTF-8",null);
           //webView.loadUrl("https://www.google.com");
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void HandleIntentCall(String url) {
        PackageManager manager = getPackageManager();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            //Then there is an Application(s) can handle your intent
            // Toast.makeText(getContext(), "Acivity found", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent1);
        } else {
            Toast.makeText(this, "No Acivity found", Toast.LENGTH_SHORT).show();
            //No Application can handle your intent
        }

    }

    @Override
    public void onBackPressed() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent();
                        intent.putExtra("ResultType","web");
                        setResult(RESULT_CANCELED,intent);
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

