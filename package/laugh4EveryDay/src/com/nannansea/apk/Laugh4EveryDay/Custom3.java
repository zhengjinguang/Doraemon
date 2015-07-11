package com.nannansea.apk.Laugh4EveryDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Custom3{
	Context mContext;
	View mView;
	ProgressDialog mProgressDialog;
	public Custom3(Context context){
		this.mContext = context;
		mView = ((Activity)mContext).findViewById(R.id.custom3);
	}
	public void Custom3Init(){
		Custom3UserInit();
		Custom3LogonInit();
		Custom3MsgInit();
	}
	public void Custom3MsgInit(){
		Button btn = (Button)mView.findViewById(R.id.cst3_btnMsg);
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(Data.mUserName==null){showToast("请先登录");return;}
				mProgressDialog = ProgressDialog.show(mContext, "","正在加载，请稍后......", false);
				new ProgressThread(handlerMsg,"getmsg",Data.mUserName,null).start();
			}
		});
	}
	public void Custom3UserInit(){
		TextView tv = (TextView)mView.findViewById(R.id.cst3_logonstatus);
		if(Data.mUserName!=null)tv.setText("登录状态： 已登录");
		else tv.setText("登录状态： 未登录");
		tv = (TextView)mView.findViewById(R.id.cst3_logonname);
		if(Data.mUserName!=null)tv.setText("登录账号： "+Data.mUserName);
		else tv.setText("登录账号： 无名氏");
		/*tv = (TextView)mView.findViewById(R.id.cst3_logonscore);
		if(Data.mUserName!=null)
			Data.mUserScore = new NetworkData().getUserScore(Data.mUserName);
		tv.setText("用户积分：" + Data.mUserScore);
		Button btn = (Button)mView.findViewById(R.id.cst3_getscore);
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View view){

			}
		});
		btn = (Button)mView.findViewById(R.id.cst3_refreshscore);
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				TextView tv = (TextView)mView.findViewById(R.id.cst3_logonscore);
				if(Data.mUserName!=null){
					Data.mUserScore = new NetworkData().getUserScore(Data.mUserName);
					tv.setText("用户积分：" + Data.mUserScore);
				}
			}
		});
		*/
		if(Data.mUserName!=null){
			EditText ev = (EditText)mView.findViewById(R.id.cst3_username);
			ev.setText(Data.mUserName);
			ev = (EditText)mView.findViewById(R.id.cst3_userpwd);
			ev.setText(Data.mUserPwd);
			CheckBox cb = (CheckBox)mView.findViewById(R.id.cst3_checkBox);
			cb.setChecked(Data.mAutoLogon);
		}
	}
	public void Custom3LogonInit(){
		Button btn = (Button)mView.findViewById(R.id.cst3_logon);
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				String UserName = getUserName();
				String UserPwd = getUserPwd();
				if(UserName.length()==0||UserPwd.length()==0){
					showDialog("用户名和密码不能为空");
					return;
				}
				clearEditFocus();
				mProgressDialog = ProgressDialog.show(mContext, "","正在登录，请稍后......", false);
				new ProgressThread(handler,"logon",UserName,UserPwd).start();
			}
		});
		btn = (Button)mView.findViewById(R.id.cst3_regist);
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
				final View register = inflater.inflate(R.layout.register,null);
				new Builder(mContext).setTitle("用户注册").setView(register)
					.setPositiveButton("确定",new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int id) {
							String UserName = ((EditText)register.findViewById(R.id.reg_UserName))
								.getText().toString();
							String UserPwd = ((EditText)register.findViewById(R.id.reg_UserPwd))
							.getText().toString();
							String DoPwd = ((EditText)register.findViewById(R.id.reg_DoPwd))
							.getText().toString();
							if(UserName.length()==0||UserPwd.length()==0){
								showDialog("用户名和密码不能为空");
								return;
							}
							if(!UserPwd.equals(DoPwd)){
								showDialog("密码不一致");
								return;
							}
							mProgressDialog = ProgressDialog.show(mContext, "","正在注册，请稍后......", false);
							new ProgressThread(handler,"register",UserName,UserPwd).start();
						}
				    }).setNegativeButton("取消", null).show();
			}
		});
		CheckBox cb = (CheckBox)mView.findViewById(R.id.cst3_checkBox);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				Data.mAutoLogon = isChecked;
				new Data(mContext).UserInfoWrite();
			}
		});
	}
	private String getUserName(){
		EditText ev = (EditText)mView.findViewById(R.id.cst3_username);
		String UserName = ev.getText().toString();
		return UserName;
	}
	private String getUserPwd(){
		EditText ev = (EditText)mView.findViewById(R.id.cst3_userpwd);
		String UserPwd = ev.getText().toString();
		return UserPwd;
	}
	private void clearEditFocus(){
		EditText ev = (EditText)mView.findViewById(R.id.cst3_username);
	    ev.clearFocus();
	    ev.setSelected(false);
	    ev = (EditText)mView.findViewById(R.id.cst3_userpwd);
	    ev.clearFocus();
	    ev.setSelected(false);
	}
	private void showDialog(String str){
		Builder builder = new Builder(mContext);
    	builder.setMessage(str) 
    		.setCancelable(false) 
    		.setPositiveButton("确定",null) 
    		.setNegativeButton("取消",null);
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	List<Map<String, Object>> mList = null;
	private List<Map<String, Object>> getData(String name) {
		int head[] = {R.drawable.head1,R.drawable.head2,R.drawable.head3,R.drawable.head4,R.drawable.head5,
				R.drawable.head6,R.drawable.head7,R.drawable.head8,R.drawable.head9,R.drawable.head10};
		mList = new ArrayList<Map<String, Object>>();
		String str[][] = new NetworkData().getMessage(name);
        if(str==null)return mList;
        for(int i =0;i<str.length;i++){
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("UserImage", head[i%10]);
        	map.put("UserText", str[i][2]);
        	map.put("UserName", str[i][0]);
        	map.put("UserDate", str[i][1].subSequence(5, 16));
        	mList.add(map);
        }
        return mList;
    }
	private class ProgressThread extends Thread {
		Handler mHandler;
		String cmd;
		String UserName;
		String UserPwd;
		ProgressThread(Handler h,String cmd,String UserName,String UserPwd) {
            mHandler = h;
            this.cmd = cmd;
            this.UserName = UserName;
            this.UserPwd = UserPwd;
        }
		public void run() {
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			if(cmd.equals("register")){
				String str = new NetworkData().UserRegister(UserName,UserPwd);
				if(str != null){
					b.putString("rst", str);
				}else b.putString("rst", "注册成功");
			}else if(cmd.equals("logon")){
				String rst = new NetworkData().UserLogon(UserName,UserPwd);
				if(rst != null){
					Data.mUserName = null;
					Data.mUserPwd = null;
					b.putString("rst", rst);
				}
				else{
					Data.mUserName = UserName;
					Data.mUserPwd = UserPwd;
					b.putString("rst", "登录成功");
					new Data(mContext).UserInfoWrite();
				}
			}else if(cmd.equals("getmsg")){
				getData(Data.mUserName);
			}
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
			String str = msg.getData().getString("rst");
			showToast(str);
	        Custom3UserInit();
		}
	};
	final Handler handlerMsg = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
			ListView lv = (ListView)mView.findViewById(R.id.cst3_listview);
			MyListAdapter adapter = new MyListAdapter(mContext,mList,R.layout.cst2_lv_item);
			lv.setAdapter(adapter);
			((Laugh4EveryDayActivity)mContext).setListViewHeightBasedOnChildren(lv);
			showToast("加载成功");
		}
	};
    public void showToast(String str){
		Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}