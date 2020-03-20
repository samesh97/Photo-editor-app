package com.sba.sinhalaphotoeditor.firebase;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

import static com.sba.sinhalaphotoeditor.Config.Constants.SHARED_PREF_NAME;
import static com.sba.sinhalaphotoeditor.Config.Constants.USER_ID_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.USER_NAME_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.USER_PHONE_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.USER_PROFILE_LINK_KEY;

public class SihalaUser implements Serializable
{
    private String userName;
    private String userProfilePic;
    private String userId;
    private String phoneNumber;

    public SihalaUser()
    {

    }
    public SihalaUser(String userName,String userProfilePic,String userId,String phoneNumber)
    {
        this.userName = userName;
        this.userProfilePic = userProfilePic;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }


    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserProfilePic()
    {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic)
    {
        this.userProfilePic = userProfilePic;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
    public void saveUser(Context context,SihalaUser user)
    {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(USER_NAME_KEY,user.getUserName());
        editor.putString(USER_PROFILE_LINK_KEY,user.getUserProfilePic());
        editor.putString(USER_PHONE_KEY,user.getPhoneNumber());
        editor.putString(USER_ID_KEY,user.getUserId());

        editor.apply();
    }
    public static SihalaUser getUser(Context context)
    {
        SihalaUser user = null;

        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        String name = pref.getString(USER_NAME_KEY,null);
        String dp = pref.getString(USER_PROFILE_LINK_KEY,null);
        String number = pref.getString(USER_PHONE_KEY,null);
        String uid = pref.getString(USER_ID_KEY,null);

        if(name != null && number != null && uid != null)
        {
            user = new SihalaUser(name,dp,uid,number);
        }

        return user;

    }


}
