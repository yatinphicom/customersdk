package com.payphi.customersdk;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utility extends Activity {

	public static boolean isConectionAvailable(Context context) {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return activeNetwork != null &&
				activeNetwork.isConnected();
	}

	public static String generateSHA(String message) {
		byte[] hashedBytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			hashedBytes = md.digest(message.getBytes("UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
		return bytesToHex(hashedBytes);
	}

	public static String prepareSecureHash(String secureToken, TreeMap<String, String> paramsMap) {
		String test = "";
		TextUtils.isEmpty(test);

		String hashInput = "";
        String hashInputkey = "";

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey())) {
                hashInputkey += entry.getKey();
            }
        }
      //  System.out.println("hashInputkey > " + hashInputkey);

		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			if (!TextUtils.isEmpty(entry.getValue())) {
				hashInput += entry.getValue();
			}
		}
	//	System.out.println("hashInput > " + hashInput);
		String secureHash = Utility.generateHMAC(hashInput, secureToken);
		return secureHash;
	}


	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if(value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}
	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if(value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

	public static String getTokenData(String token){
		String tokendata="";
		try {
			byte[] data = Base64.decode(token, Base64.DEFAULT);
			 tokendata = new String(data, "UTF-8");

		}catch (Exception e){
				//e.printStackTrace();
		}
		return tokendata;
	}


	private String millisToDate(long millis){

		return DateFormat.getDateInstance(DateFormat.SHORT).format(millis);
		//You can use DateFormat.LONG instead of SHORT

	}
	public static boolean IsTokenExpired(String expDate) throws Exception
	{
		long unixSeconds = Long.parseLong(expDate);
		Date dte = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // give a timezone reference for formating (see comment at the bottom
		String expformattedDate = sdf.format(dte);
		String todaydate = sdf.format(new Date());

	//	System.out.println("formattedDate----"+expformattedDate);
	//	System.out.println("todaydate----"+todaydate);

		Date expformatteddate = sdf.parse(expformattedDate);
		Date  todayDate = sdf.parse(todaydate);


		if (todayDate.compareTo(expformatteddate) > 0) {
	//		System.out.println("Expird");
			return true;
		} else if (todayDate.compareTo(expformatteddate) < 0) {
	//		System.out.println("Not Expird");
			return false;
		}


		/*if(todayDate.getTime() < expformatteddate.getTime()){
			System.out.println("Not expired");
		}else{
			System.out.println("Not expired");
		}

		if(expformatteddate.compareTo(todayDate) <0){// not expired
			System.out.println("Not expired..<0");
			 return false;

		}else if(todayDate.compareTo(expformatteddate)==0){// both date are same
			if(todayDate.getTime() < expformatteddate.getTime()){// not expired
				System.out.println("Not expired..>0");
				 return false;
			}else if(todayDate.getTime() == expformatteddate.getTime()){//expired
				System.out.println(" expired..1");
				return true;


			}else{//expired
				System.out.println(" expired..2");
				return true;

			}
		}else{//expired
			System.out.println("Not expired..3");
			return false;

		}*/
		return true;
	}

	public static String  parseJWT(String jwt) {

		String authToken =jwt;
		String text="";
		String[] segments = authToken.split("\\.");
		String base64String = segments[1];

		int requiredLength = (int)(4 * Math.ceil(base64String.length() / 4.0));
		int nbrPaddings = requiredLength - base64String.length();

		if (nbrPaddings > 0) {
			base64String = base64String + "====".substring(0, nbrPaddings);
		}

		base64String = base64String.replace("-", "+");
		base64String = base64String.replace("_", "/");

		try {
			byte[] data = Base64.decode(base64String, Base64.DEFAULT);


			text = new String(data, "UTF-8");


		} catch (Exception e) {
	//		e.printStackTrace();
		}

		return text;

	}

	public static String generateHMAC(String message, String secretKey) {
		Mac sha256_HMAC;
		byte[] hashedBytes = null;
		try {
			sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
		    sha256_HMAC.init(secret_key);
		    
		    hashedBytes = sha256_HMAC.doFinal(message.getBytes());		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
		}
		
		//Check here 
		return bytesToHex(hashedBytes);
	}
	
	public static String bytesToHex(byte[] message) {
		StringBuffer stringBuffer = new StringBuffer();
		try{
			for (int i = 0; i < message.length; i++) {
			    stringBuffer.append(Integer.toString((message[i] & 0xff) + 0x100, 16).substring(1));
			}
		} catch (Exception e) {
			// TODO: handle exception
	//		e.printStackTrace();
		}
		return stringBuffer.toString();
	}
	
	public static String generateSalt() {
		String salt = null;
		try {
			salt = UUID.randomUUID().toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return salt;
	}
	public static String GetUTCdatetimeAsString() {

		String utcTime = null;
		String utcTime2 = null;
		String strTimeStamp = null;
		StringBuilder s = new StringBuilder();

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat f1 = new SimpleDateFormat("HH:mm:ss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		f1.setTimeZone(TimeZone.getTimeZone("UTC"));
		//System.out.println();
		utcTime = f.format(new Date());
		utcTime2 = f1.format(new Date());
		s.append(utcTime);
		s.append("T");
		s.append(utcTime2);
		strTimeStamp = s.toString();

		return strTimeStamp;
	}
//Method to write data in given filename with given storage location



	/*public static JsonObject responseMessage(int returnCode,String description,JsonObject resBody) {
		JsonObject response = new JsonObject();
		JsonObject resHeader = new JsonObject();
		
		resHeader.put("returnCode", returnCode);
		resHeader.put("description", description);

		response.put("resHeader", resHeader);
		response.put("resBody", resBody);
		logger.debug(response.toString());
		return response;
	}*/
	
/*	public static JsonObject generateRegExp(Map<String, String> patternMap) {
		logger.debug("request for generateRegExp");
		String patternstring =null;
		JsonObject resObj = new JsonObject();

		try {
			String[] str = patternMap.get("minLength").split(":");
			int minLength = Integer.parseInt(str[0]);
			resObj.put("minLength", str[1]);
			str = patternMap.get("digit").split(":");
			int digit = Integer.parseInt(str[0]);
			resObj.put("digit", str[1]);
	
			str = patternMap.get("specialChar").split(":");
			int specialChar = Integer.parseInt(str[0]);
			resObj.put("specialChar", str[1]);
			
			str = patternMap.get("upperCaseChar").split(":");
			int upperCaseChar = Integer.parseInt(str[0]);
			resObj.put("upperCaseChar", str[1]);

			patternstring = "^"
			+ ((digit == 1) ? ("(?=.*[0-9])") : "")
			+ ((upperCaseChar == 1) ? ("(?=.*[A-Z])") : "")
			+ ((specialChar == 1) ? ("(?=.*[@#$%^&+=])") : "")
			+ "([a-zA-Z0-9@#$%^&+=]+)"
			+ ((minLength > 0) ? ("{"+ minLength +",}") : "")
			+ "$";
			resObj.put(Constants.RESET_PWD_PATTERN, patternstring);
		}catch (Exception e) {
			logger.error("Exception in generateRegExp :" + e);
		}
		return resObj;
	}*/
	
/*	public static boolean isValidPassword(String newPassword, SysParams2 sysParams) {
		Map<String, String> patternMap = sysParams.getAllKeyValuesofType(Constants.RESET_PWD_PATTERN);
		JsonObject jsonObject = Utility.generateRegExp(patternMap);
		Pattern pattern = Pattern.compile(jsonObject.getString(Constants.RESET_PWD_PATTERN));
		Matcher matcher = pattern.matcher(newPassword);
		return matcher.matches();
	}
	
	public static ApplicationConfig getAppConfig(Vertx vertx){
		LocalMap<String, Object> appContext = vertx.sharedData().getLocalMap(FrameWorkConstants.FRAMEWORK_GLOBAL_CONTEXT);
		ApplicationConfig applicationConfig	 = (ApplicationConfig) appContext.get(Constants.APP_CONFIG);
		return applicationConfig; 

	}
	
	public static SysParams2 getSysParams(Vertx vertx){
		LocalMap<String, Object> appContext = vertx.sharedData().getLocalMap(FrameWorkConstants.FRAMEWORK_GLOBAL_CONTEXT);
		return (SysParams2) appContext.get(Constants.SYSPARAMS); 
	}*/
	
	/*public static String generateToken(byte[] secret, JsonObject payload){
		long now = System.currentTimeMillis();
		JwtBuilder builder = Jwts.builder()
				.setIssuedAt(new Date(now))
				.setSubject("User Auth")
				.setIssuer(SyncStateContract.Constants.TOKEN_ISSUER)
				.setAudience(payload.encode())
				.signWith(SignatureAlgorithm.HS256, secret);
		long expMillis = now + 1200000; //20 minutes by default
		Date exp = new Date(expMillis);
		builder.setExpiration(exp);
		//Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}*/
	
	
/*	public static String generateBatchID() {

		String requestID = "B"+UUID.randomUUID().toString() ;
		return requestID;
	}
	
	public static String stringSubstitutor(Map<String, Object> valuesMap,String templateString) {
		 StrSubstitutor sub = new StrSubstitutor(valuesMap);
		 return sub.replace(templateString);		
	}*/

	public static String getMonthDate(String iso8601string)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.US);
		Date date = new Date();

		String[]  arr = iso8601string.split("T");
		String dateInString = arr[0];
		try {
			 date = formatter.parse(dateInString);
			//System.out.println("date test.......................... " + dateFormat.format(date));
		}catch (Exception e){

		}
		return dateFormat.format(date);

	}
}