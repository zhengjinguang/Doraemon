package com.nannansea.apk.Laugh4EveryDay;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

public class MyListAdapter extends BaseAdapter{
    private List<? extends Map<String, ?>> mData;
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;
    ProgressDialog mProgressDialog;

    public MyListAdapter(Context context, List<? extends Map<String, ?>> data,
            int resource) {
    	this.mContext = context;
        mData = data;
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
   	public int getCount() {  
        return mData.size();  
    }
   	public Object getItem(int position) {
        return mData.get(position);
    } 
    public long getItemId(int position) {  
        return position;  
    } 
    public View getView(int position, View convertView, ViewGroup parent){ 
    	return createViewFromResource(position, convertView, parent, mResource);
    }
    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }
    private void bindView(int position, View view) {
        final Map<String,?> dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }
        ImageView im = (ImageView)view.findViewById(R.id.UserImage);
        Object data = dataSet.get("UserImage");
        String text = data == null ? "" : data.toString();
        if (text == null) {
            text = "";
        }
        im.setImageResource(Integer.parseInt(text));
        
        TextView tv = (TextView)view.findViewById(R.id.UserName);
        data = dataSet.get("UserName");
        text = data == null ? "" : data.toString();
        if (text == null) {
            text = "";
        }
        tv.setText(text);
        final String UserT = text;
        
        tv = (TextView)view.findViewById(R.id.UserText);
        data = dataSet.get("UserText");
        text = data == null ? "" : data.toString();
        if (text == null) {
            text = "";
        }
        tv.setText(text);
        
        tv = (TextView)view.findViewById(R.id.UserDate);
        data = dataSet.get("UserDate");
        text = data == null ? "" : data.toString();
        if (text == null) {
            text = "";
        }
        tv.setText(text);
        
        View v = (View)view.findViewById(R.id.SendMesssage);
        v.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		if(Data.mUserName == null){
        			Toast toast = Toast.makeText(mContext, "~请先登录~", Toast.LENGTH_LONG);
        	        toast.setGravity(Gravity.CENTER, 0, 0);
        	        toast.show();
					return;
				}
        		View upload_popunwindwow = mInflater.inflate(R.layout.upload, null);
				TextView tv = (TextView)upload_popunwindwow.findViewById(R.id.Upload_tvtext);
				tv.setText("To: "+UserT+"  发送信息...");
				final PopupWindow mPopupWindow = new PopupWindow(upload_popunwindwow,
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				mPopupWindow.setFocusable(true);
				mPopupWindow.showAtLocation(view,//Data.mainActivity.findViewById(R.id.custom2),
	                    Gravity.CENTER, 0, 0);
				final EditText ev = (EditText)upload_popunwindwow.findViewById(R.id.Upload_Edtext);
				ev.setOnLongClickListener(new OnLongClickListener(){
					public boolean onLongClick(View v){
						View context_popunwindwow = mInflater.inflate(R.layout.context,null);
						final PopupWindow pWindow = new PopupWindow(context_popunwindwow,
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						pWindow.setFocusable(true);
						pWindow.showAtLocation(((Activity)mContext).findViewById(R.id.custom2),
			                    Gravity.CENTER, 0, 0);
						Button btn = (Button)context_popunwindwow.findViewById(R.id.Context_Paste);
						btn.setOnClickListener(new OnClickListener(){
							public void onClick(View v){
								ClipboardManager clip = (ClipboardManager)((Activity)mContext).getSystemService(Context.CLIPBOARD_SERVICE);
						    	ev.setText(clip.getText());
						    	pWindow.dismiss();
							}
						});
						btn = (Button)context_popunwindwow.findViewById(R.id.Context_Cancel);
						btn.setOnClickListener(new OnClickListener(){
							public void onClick(View v){
								pWindow.dismiss();
							}
						});
						return true;
					}
				});
				Button btn = (Button)upload_popunwindwow.findViewById(R.id.UpLoad_OK);
				btn.setOnClickListener(new OnClickListener(){
					public void onClick(View v){
						String message = ev.getEditableText().toString();
						String UserFrom = Data.mUserName;
						String UserTo = UserT;
						mProgressDialog = ProgressDialog.show(mContext, "","正在发送，请稍后......", false);
						new ProgressThread(handler,UserFrom,UserTo,message).start();
						mPopupWindow.dismiss();
					}
				});
				btn = (Button)upload_popunwindwow.findViewById(R.id.UpLoad_Cancel);
				btn.setOnClickListener(new OnClickListener(){
					public void onClick(View v){
						mPopupWindow.dismiss();
					}
				});
        	}
        });
    }
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
			String str = msg.getData().getString("rst");
			Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_LONG);
	        toast.setGravity(Gravity.CENTER, 0, 0);
	        toast.show();
		}
	};
    private class ProgressThread extends Thread {
		Handler mHandler;
		String message;
		String UserFrom;
		String UserTo;
		ProgressThread(Handler h,String UserFrom,String UserTo, String message) {
            mHandler = h;
            this.message = message;
            this.UserFrom = UserFrom;
            this.UserTo = UserTo;
        }
		public void run() {
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			String str = new NetworkData().sendMessage(UserFrom, UserTo, message);
			b.putString("rst", str);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
}