package com.payphi.customersdk;

/**
 * Created by jayesh on 25-05-2017.
 */
public class APISettings {
    private static APISettings apiSettings = new APISettings();
    private String baseUrl;
    private String loginUrl = baseUrl + "/merapp/login/";
    private String paymentOptUrl = baseUrl + "/merapp/secure/getPaymentOpts/";
    private String returnUrl = baseUrl + "/pg/api/responseToApp";
    private String saleBaseUrl;
    private String formActionUrl = saleBaseUrl +  "/pg/api/sale";
    private String getGenerateQr =  saleBaseUrl +  "pg/api/generateQR";
    private String checkPaymentStatus = saleBaseUrl + "pg/api/command";
    private String serviceChargesContextPath = "pg/api/getSerCharges";

    public String getSaleBaseUrl() {
        return saleBaseUrl;
    }

    public void setSaleBaseUrl(String saleBaseUrl) {
        this.saleBaseUrl = saleBaseUrl;
    }

    public static APISettings getApiSettings() {
        return apiSettings;
    }

    public String getCheckPaymentStatus() {
        return saleBaseUrl + "pg/api/command";
    }

    public String getGetSerCharges() {
        return saleBaseUrl +  serviceChargesContextPath;
    }
    public String getGenerateQr() {
        return saleBaseUrl +  "pg/api/generateQR";
    }

    public static void setApiSettings(APISettings apiSettings) {
        APISettings.apiSettings = apiSettings;
    }


    public String getBaseUrl() {
        return baseUrl;
    }


    public String getLoginUrl() {
        return baseUrl + "/merapp/login/";
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getPaymentOptUrl() {
        return baseUrl + "merapp/secure/getPaymentOpts/";
    }

    public void setPaymentOptUrl(String paymentOptUrl) {
        this.paymentOptUrl = paymentOptUrl;
    }

    public String getReturnUrl() {
        return saleBaseUrl + "pg/api/appResponse";
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getFormActionUrl() {
        return saleBaseUrl +  "pg/api/sale?v=2";
    }

    public void setFormActionUrl(String formActionUrl) {
        this.formActionUrl = formActionUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
