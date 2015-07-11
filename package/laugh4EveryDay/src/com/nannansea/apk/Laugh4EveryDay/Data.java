package com.nannansea.apk.Laugh4EveryDay;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;

public class Data{
	static boolean showAds = false;
	static int width;
	static int height;
	static float density;
	static String mUserName=null,mUserPwd=null;
	static int mUserScore = 1000;
	static boolean mAutoLogon;
	static Context mContext;
	public  Data(){
	}
	public  Data(Context context){
		mContext = context;
	}
	public void UserInfoRead(){
        try{
        	FileInputStream is =(mContext).openFileInput("UserInfo.txt");
        	int a = is.read();
        	if(a==1){
        		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
    			BufferedReader br = new BufferedReader(isr);
    			mUserName = br.readLine();
    			mUserPwd = br.readLine();
    			mAutoLogon = true;
        	}else{
        		mUserName = null;
    			mUserPwd = null;
    			mAutoLogon = false;
        	}
        	is.close();
        }catch(IOException e){
        	mUserName = null;
			mUserPwd = null;
			mAutoLogon = false;
        }
	}
	public void UserInfoWrite(){
		try{
			FileOutputStream os = (mContext).openFileOutput("UserInfo.txt", Activity.MODE_PRIVATE);
			if(mAutoLogon&&mUserName!=null){
				os.write(1);
				os.write(mUserName.getBytes());
				os.write('\n');
				os.write(mUserPwd.getBytes());
			}
			else os.write(0);
			os.close();
		}catch(IOException e){
		}	
	}
}