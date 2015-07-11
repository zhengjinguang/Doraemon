package com.nannansea.apk.Laugh4EveryDay;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

public class Custom2{
	Context mContext;
	ProgressDialog mProgressDialog;
	View mView;
	public Custom2(Context context){
		this.mContext = context;
		mView = ((Activity)mContext).findViewById(R.id.custom2);
	}
	OnClickListener mListenerRefesh = new OnClickListener(){
		public void onClick(View v){
			mProgressDialog = ProgressDialog.show(mContext, "","正在刷新，请稍后......", false);
			new ProgressThread(handler,"refesh",null,null).start();
		}
	};
	OnClickListener mListenerMore = new OnClickListener(){
		public void onClick(View v){
			mProgressDialog = ProgressDialog.show(mContext, "","正在加载，请稍后......", false);
			new ProgressThread(handler,"more",null,null).start();
		}
	};
	public void Custom2Init(){
		View v = ((Activity)mContext).findViewById(R.id.custom2);
		ListView lv = (ListView)v.findViewById(R.id.listview2_1);
		mFrom = 0;
		mList = new ArrayList<Map<String, Object>>();
		MyListAdapter adapter = new MyListAdapter(mContext,getData(0),R.layout.cst2_lv_item);
		lv.setAdapter(adapter);
		((Laugh4EveryDayActivity)mContext).setListViewHeightBasedOnChildren(lv);
		
		Button btn = (Button)v.findViewById(R.id.cst2_btnMore);
		btn.setOnClickListener(mListenerMore);
		btn = (Button)mView.findViewById(R.id.cst2_btnrefresh);
		btn.setOnClickListener(mListenerRefesh);
		
		btn = (Button)v.findViewById(R.id.cst2_btnUpLoad);
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				View upload_popunwindwow = getLayoutView(R.layout.upload);
				TextView tv = (TextView)upload_popunwindwow.findViewById(R.id.Upload_tvtext);
				tv.setText("分享我的快乐...");
				final PopupWindow mPopupWindow = new PopupWindow(upload_popunwindwow,
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				mPopupWindow.setFocusable(true);
				mPopupWindow.showAtLocation(((Activity)mContext).findViewById(R.id.custom2),
	                    Gravity.CENTER, 0, 0);
				final EditText ev = (EditText)upload_popunwindwow.findViewById(R.id.Upload_Edtext);
				ev.setOnLongClickListener(new OnLongClickListener(){
					public boolean onLongClick(View v){
						View context_popunwindwow = getLayoutView(R.layout.context);
						final PopupWindow pWindow = new PopupWindow(context_popunwindwow,
								LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
						String str = ev.getText().toString();
						if(StringUtils.isBlank(str)){
							showToast("内容不能为空......");
							return;
						}
						mProgressDialog = ProgressDialog.show(mContext, "","正在上传，请稍后......", false);
						new ProgressThread(handler,"putlaugh",Data.mUserName,str).start();
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
	public View getLayoutView(int resource){
		LayoutInflater mLayoutInflater = (LayoutInflater) ((Activity)mContext).getApplicationContext()
        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View upload_popunwindwow = mLayoutInflater.inflate(resource, null);
		return upload_popunwindwow;
	}
	int head[] = {R.drawable.head1,R.drawable.head2,R.drawable.head3,R.drawable.head4,R.drawable.head5,
			R.drawable.head6,R.drawable.head7,R.drawable.head8,R.drawable.head9,R.drawable.head10};
    int mFrom = 0;
	List<Map<String, Object>> mList = null;
	private List<Map<String, Object>> getData(int from) {
        String str[][] = new NetworkData().getLaughText(from);
        if(str==null)return mList;
        for(int i =0;i<str.length;i++){
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("UserImage", head[i%10]);
        	map.put("UserText", str[i][0]);
//        	map.put("UserName", str[i][1]);
//        	map.put("UserDate", str[i][2].subSequence(0, 16));
        	mList.add(map);
        }
        mFrom += str.length;
        return mList;
    }
    public void showToast(String str){
		Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String str = null;
			String cmd = msg.getData().getString("cmd");
			if(cmd!=null&&cmd.equals("refresh")){
				MyListAdapter adapter = new MyListAdapter(mContext,mList,R.layout.cst2_lv_item);
				ListView lv = (ListView)mView.findViewById(R.id.listview2_1);
				lv.setAdapter(adapter);
				((Laugh4EveryDayActivity)mContext).setListViewHeightBasedOnChildren(lv);
				str = "刷新成功";
			}else if(cmd!=null&&cmd.equals("more")){
				MyListAdapter adapter = new MyListAdapter(mContext,mList,R.layout.cst2_lv_item);
				ListView lv = (ListView)mView.findViewById(R.id.listview2_1);
				lv.setAdapter(adapter);
				((Laugh4EveryDayActivity)mContext).setListViewHeightBasedOnChildren(lv);
				str = "加载成功";
			}else{
				str = msg.getData().getString("rst");
			}
			mProgressDialog.dismiss();
			showToast(str);
		}
	};
	private class ProgressThread extends Thread {
		Handler mHandler;
		String cmd;
		String UserName;
		String text;
		ProgressThread(Handler h,String cmd,String UserName,String str) {
            mHandler = h;
            this.cmd = cmd;
            this.UserName = UserName;
            this.text = str;
        }
		public void run() {
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			if(cmd.equals("putlaugh")){
				String str = new NetworkData().putLaughText(Data.mUserName, text);		
				if(str != null){
					b.putString("rst", str);
				}else b.putString("rst", "上传成功");
			}else if(cmd.equals("refesh")){
				mList = new ArrayList<Map<String, Object>>();
				mFrom = 0;
				getData(mFrom);
				b.putString("cmd","refresh");
			}else if(cmd.equals("more")){
				getData(mFrom);
				b.putString("cmd", "more");
			}
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
}