package com.omni.dew.coresocial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.omni.dew.basemodule.BaseLogin;
import com.omni.dew.basemodule.LoginResponse;
import com.omni.dew.socialfacebook.FaceBookLogin;
import com.omni.dew.socialgoogle.GoogleLogin;

public class SocialLogin {
    BaseLogin baseLogin;
    BaseLoginResponse baseLoginResponse;
    private int RESPONSE_CODE;
    private SocialLogin(){};
    Activity activity;
    LoginType loginType;
    public SocialLogin(LoginType loginType, Activity activity, BaseLoginResponse response) {
        this.baseLoginResponse = response;
        this.activity = activity;
        this.loginType = loginType;
        String className = "com.omni.dew.socialgoogle.GoogleLogin";// really passed in from config
        if (loginType == LoginType.GOOGLE) {
            className = "com.omni.dew.socialgoogle.GoogleLogin";
        } else if (loginType == LoginType.FACEBOOK) {
            className = "com.omni.dew.socialfacebook.FaceBookLogin";
        }
        try {
            Class c = Class.forName(className);
            baseLogin = (BaseLogin) c.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if(baseLogin!=null){
            baseLogin.init(activity, loginResponse);
        }
    }

    LoginResponse loginResponse = new LoginResponse() {
        @Override
        public void sendResponse(String json) {
            // send to main Activity
            baseLoginResponse.sendResponse(json);
            saveLoginData(loginType.name(), json);
        }
    };

    public int getResponseCode() {
        return RESPONSE_CODE;
    }

    public void login(){
        RESPONSE_CODE = baseLogin.login();
    }

    public void sendIntentResponse(Intent data){
        baseLogin.onResponse(data);
    }

    public boolean isLogin() {
        return baseLogin.isLogin();
    }

    static final String LOGIN_PREFERENCES = "login_pref";
    private void saveLoginData(String loginType, String data){
        SharedPreferences sharedpreferences = activity.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("loginType", loginType);
        editor.putString("loginData", data);
        editor.commit();
    }

    public static String getLoginType(Context cn){
        SharedPreferences sharedpreferences = cn.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString("loginType", null);
    }

    public static String getLoginData(Context cn){
        SharedPreferences sharedpreferences = cn.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString("loginData", null);
    }
}
