package com.payphi.customersdk.util;

import android.content.Context;
import android.text.TextUtils;


import com.payphi.customersdk.APISettings;
import com.payphi.customersdk.Utility;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Ramkumar on 19-05-2017.
 */

public class PayForm {

    private static final String FORM_INPUT_START = "<input type=\"hidden\" name=\"";
    private static final String FORM_INPUT_END = "\"/>";
    public static final String VALUE = "\" value=\"";
    private static  final String payFormFooter="</form>\n" +
            "\t<script>\n" +
            "        function submitForm() {\n" +
            "\t      document.forms['myForm'].submit();\n" +
            "        }\n" +
            "\t\twindow.onload = function () {\n" +
            "\t\t\tsetTimeout(function() {\n" +
            "\t\t\t\tsubmitForm();\t\t\t\t\n" +
            "\t\t\t}, 500);\n" +
            "\t\t};\n" +
            "\t</script>\n" +
            "</body>\n" +
            "</html>";
    private static  final String payFormHeader="<html style=\"height:100%; width: 100%;\"><head><title>Processing, Please Wait...</title><style>#top{text-align:left;border-bottom:1px solid #ddd;padding-bottom:16px;margin-bottom:-50px}.spin{width:60px;height:60px;margin:0 auto;margin-bottom:-60px;position:relative;top:-30px}.spin div{width:100%;height:100%;vertical-align:middle;display:inline-block;opacity:0;border-radius:50%;border:4px solid #333;-webkit-animation:spin 1.3s linear infinite;animation:spin 1.3s linear infinite;-webkit-box-sizing:border-box;box-sizing:border-box}#spin2 div{-webkit-animation-delay:0.65s;animation-delay:0.65s}@-webkit-keyframes spin{0%{-webkit-transform:scale(0.5);opacity:0;border-width:8px}20%{-webkit-transform:scale(0.6);opacity:0.8;border-width:4px}90%{-webkit-transform:scale(1);opacity:0}}@keyframes spin{0%{transform:scale(0.5);opacity:0;border-width:8px}20%{transform:scale(0.6);opacity:0.8;border-width:4px}90%{transform:scale(1);opacity:0}}@media(max-height:400px){#top{border:none}}</style></head>\n" +
            "\n" +
            "<body onload=\"\" style=\"overflow:hidden;text-align:center;height:100%;white-space:nowrap;margin:0;padding:0;font-family:ubuntu,verdana,helvetica,sans-serif\"> \n" +
            "\n" +
            "<div style=\"display:inline-block;vertical-align:middle;width:90%;height:60%;max-height:440px;position:relative;padding-bottom:60px\"> \n" +
            "<div id=\"top\"> <span style=\"font-size:44px;float:right;line-height:64px;color:#666\">   </span> \n" +
            "</div> \n" +
            "<div style=\"margin-top:100px;\"> \n" +
            "<div style=\"text-align:center;white-space:initial;margin-bottom:75px;font-size:20px;color:#666\">\n" +
            "<br/>\n" +
            "<br/>\n" +
            "Redirecting to Gateway... <br/>\n" +
            "</div> \n" +
            "<div class=\"spin\"><div></div></div> \n" +
            "<div class=\"spin\" id=\"spin2\"><div></div></div>\n" +
            "</div>\n" +
            "</div> \n" +
            "<div style=\"display:inline-block;vertical-align:middle;height:90%;width:0\"></div>";

    public static String getPayFormHtml(Context context, String secureToken, TreeMap<String, String> paramsMap) {
        try {
            /*InputStream in = context.getAssets().open("payFormHeader.txt");
            byte[] header = new byte[in.available()];
            in.read(header);
            in.close();

            in = context.getAssets().open("payFormFooter.txt");
            byte[] footer = new byte[in.available()];
            in.read(footer);
            in.close();*/

            String headerStr = new String(payFormHeader);
            String footerStr = new String(payFormFooter);

            String formStart = "<form id=\"myForm\" name=\"myForm\" action=\"" + APISettings.getApiSettings().getFormActionUrl() +"\"  method=\"post\">";
            //System.out.println("formStart="+formStart);

   /*         <form id="myForm" name="myForm" action="http://192.168.1.88:9292/pg/api/sale" method="post">*/

                    String inputStringList = "";
         //   paramsMap.put("returnURL", "https://qa.phicommerce.com/pg/api/responseToApp");
            paramsMap.put("returnURL", APISettings.getApiSettings().getReturnUrl());
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getValue())) {
                    String formInput = FORM_INPUT_START + entry.getKey() + VALUE + entry.getValue() + FORM_INPUT_END;
                    inputStringList += formInput;
                }
            }

            String secureTokenHash = Utility.prepareSecureHash(secureToken, paramsMap);
            String formInputHash = FORM_INPUT_START + "secureHash" + VALUE + secureTokenHash + FORM_INPUT_END;
            inputStringList += formInputHash;

            return headerStr + formStart + inputStringList + footerStr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
