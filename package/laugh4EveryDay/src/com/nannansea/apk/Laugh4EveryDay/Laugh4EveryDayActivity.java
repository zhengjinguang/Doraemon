package com.nannansea.apk.Laugh4EveryDay;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

import org.json.JSONArray;

public class Laugh4EveryDayActivity extends Activity {
	Context mContext;
	OnCreateContextMenuListener cmlistener;
	final Handler mStartHandle = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==1){
				initView();
				View _splashView = findViewById(R.id.splash);
				mProgressDialog.dismiss();
				_splashView.setVisibility(View.GONE);
			}else if(msg.what==0){
				mProgressDialog.dismiss();
				mProgressDialog = ProgressDialog.show(mContext, "","正在初始化，请稍后......", false);
			}else if(msg.what==2){
				mProgressDialog.dismiss();
				showDialog(1);
			}
		}
	};
	private class StartThread extends Thread {
		public void run() {
			SystemClock.sleep(1000);
			if(new NetworkData(mContext).getNetStatus()){
				mStartHandle.sendEmptyMessage(0);
			}else{
				mStartHandle.sendEmptyMessage(2);
				return;
			}
//			try {
//				JSONArray object = JSON.parseArray(HttpUtils.doHttpGet("http://app.nannansea.com/api/app/version?app=com.nannansea.apk.Laugh4EveryDay&version=1.1"));
//				Data.showAds = object.getJSONObject(0).getInteger("app_ads") == 1;
//			}catch (Exception e){
//				Data.showAds = false;
//			}
			SystemClock.sleep(1000);
			mStartHandle.sendEmptyMessage(1);
		}
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
				WindowManager.LayoutParams. FLAG_FULLSCREEN);
	    DisplayMetrics dm=new DisplayMetrics();  
	    getWindowManager().getDefaultDisplay().getMetrics(dm); 
	    Data.width = dm.widthPixels;
	    Data.height = dm.heightPixels;
	    Data.density = dm.density;
	    setContentView(R.layout.splash);
	    mProgressDialog = ProgressDialog.show(mContext, "","正在检测网络，请稍后......", false);
	    new StartThread().start();
 /*	    final View _splashView = findViewById(R.id.splash);		
	    CountDownTimer timer = new CountDownTimer(5000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {}
			@Override
			public void onFinish() {
				_splashView.setVisibility(View.GONE);
			}
		};
		timer.start();
*/    }
    public void initView(){
    	findViewById(R.id.vs_main).setVisibility(View.VISIBLE);

		new Data(this).UserInfoRead();
        AutoScrollTextView sv = (AutoScrollTextView)findViewById(R.id.scrolltext);
        sv.setText(new NetworkData().getScrollTitle());
        sv.init(getWindowManager());
        sv.startScroll();
        cmlistener = new OnCreateContextMenuListener(){
            @Override  
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
            	longLv = (ListView)v;    
                menu.add(0, 0, 0, "复制文本");
            }
        };
        launchTabHost();
    }
    public void showtoast(String str){
    	Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    ListView longLv;
    public boolean onContextItemSelected(MenuItem item) {
    	ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    	
    	AdapterView.AdapterContextMenuInfo menuInfo;
    	menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	int index = menuInfo.position;
        ListAdapter listAdapter = longLv.getAdapter();   
        if (listAdapter == null) {
            return true;  
        }
        TextView listItem = (TextView)listAdapter.getView(index, null, longLv);  
    	clip.setText(listItem.getText());   
        return super.onContextItemSelected(item);   
    }
    private void launchTabHost() {
    	custom1Init();
    	new Custom2(this).Custom2Init();
    	new Custom3(this).Custom3Init();
        //final TabHost tabHost = this.getTabHost();
    	final TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
    	tabHost.setup();
       tabHost.addTab(tabHost.newTabSpec("speak")
    		   .setIndicator(getIndicatorView(R.drawable.smile1,"笑口常开"))
               .setContent(R.id.custom1));
       tabHost.addTab(tabHost.newTabSpec("upload")
    		   .setIndicator(getIndicatorView(R.drawable.smile2,"快乐分享"))
               .setContent(R.id.custom2));  
       tabHost.addTab(tabHost.newTabSpec("app")
    		   .setIndicator(getIndicatorView(R.drawable.smile3,"我的地盘"))
               .setContent(R.id.custom3));  
       final FrameLayout fl = tabHost.getTabContentView ();
	   fl.getChildAt(1).setVisibility(View.GONE);
	   fl.getChildAt(2).setVisibility(View.GONE);
       fl.getChildAt(0).setVisibility(View.VISIBLE);
       tabHost.setOnTabChangedListener(new OnTabChangeListener(){
    	   public void onTabChanged(String tabId){
           		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
           			View v = tabHost.getTabWidget().getChildAt(i);
           			GifView gv = (GifView)v.findViewById(R.id.gif1);
           			if (tabHost.getCurrentTab() == i) {
           				gv.showAnimation();
           			}else{
           				gv.showCover();
           			}
           		}
    		   if(tabId.equals("speak")){
    			   fl.getChildAt(1).setVisibility(View.GONE);
    			   fl.getChildAt(2).setVisibility(View.GONE);
    			   fl.getChildAt(0).setVisibility(View.VISIBLE);
    		   }else if(tabId.equals("upload")){
    			   fl.getChildAt(0).setVisibility(View.GONE);
    			   fl.getChildAt(2).setVisibility(View.GONE);
    			   fl.getChildAt(1).setVisibility(View.VISIBLE);
    		   }else if(tabId.equals("app")){
    			   fl.getChildAt(0).setVisibility(View.GONE);
    			   fl.getChildAt(1).setVisibility(View.GONE);
    			   fl.getChildAt(2).setVisibility(View.VISIBLE);
    		   }
    	   }
       });
       tabHost.setCurrentTab(0);
    }
    protected View getIndicatorView(int ID,String str) {
    	LayoutInflater mInflater = LayoutInflater.from(this); 
    	View _View = mInflater.inflate(R.layout.gif, null);
    	TextView tv = (TextView)_View.findViewById(R.id.text1);
    	tv.setText(str);
    	GifView gv = (GifView)_View.findViewById(R.id.gif1);
    	gv.setGifImage(ID);
    	gv.setGifImageType(GifView.GifImageType.COVER);
    	gv.showCover();
    	return _View;
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {
            return;  
        } 
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(View.MeasureSpec.makeMeasureSpec(
            		Data.width,View.MeasureSpec.EXACTLY), 0);
            totalHeight += listItem.getMeasuredHeight();  
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.width = Data.width;
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);
    } 
    public void custom1Init(){
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        View v = findViewById(R.id.custom1);
        Button bt;
        ListView lv;
        Date date;
        String str[];
        int i = 0;
        for(i=0;i<5;i++){
        	date =new Date(c.getTimeInMillis());
    		bt = (Button)v.findViewById(btn[i]);
    		bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, Data.width/15);
    		bt.setText(format.format(date)+" "+LauarUtil.getLauar(c));
        	str = new NetworkData().getLaughText(format.format(date));
        	lv = (ListView) v.findViewById(listv[i]);
        	lv.setAdapter(new ArrayAdapter<String>(this,R.layout.list_items,str));
        	setListViewHeightBasedOnChildren(lv);
        	lv.setOnCreateContextMenuListener(cmlistener);
        	lv.setVisibility(View.GONE);
        	c.add(Calendar.DATE, -1);
        }
        v.findViewById(listv[0]).setVisibility(View.VISIBLE);
        ((Button)v.findViewById(R.id.text1)).setText(String.valueOf(c.get(Calendar.YEAR)));
        ((Button)v.findViewById(R.id.text2)).setText(String.valueOf(1+c.get(Calendar.MONTH)));
        ((Button)v.findViewById(R.id.text3)).setText(String.valueOf(c.get(Calendar.DATE)));
        findViewById(R.id.text3).setBackgroundColor(0x88FF8888);
    	selectViewId = R.id.text3;
    	
        lv = (ListView) v.findViewById(R.id.listview6);
        date =new Date(c.getTimeInMillis());
        str = new NetworkData().getLaughText(format.format(date));
    	lv.setAdapter(new ArrayAdapter<String>(this,R.layout.list_items,str));
    	setListViewHeightBasedOnChildren(lv);
    	lv.setOnCreateContextMenuListener(cmlistener);
        lv.setVisibility(View.GONE);
    }
    int btn[]={R.id.cst1_button1,R.id.cst1_button2,R.id.cst1_button3,R.id.cst1_button4,R.id.cst1_button5};
    int listv[]={R.id.listview1,R.id.listview2,R.id.listview3,R.id.listview4,R.id.listview5};
    public void buttonOnClick(View view)
    {
    	int i=0;
    	while(btn[i]!= view.getId())i++;
    	ListView v = (ListView)findViewById(listv[i]);
       	if(v.getVisibility()!=View.VISIBLE)
    		v.setVisibility(View.VISIBLE);
    	else
    		v.setVisibility(View.GONE);
    }
    public void textOnClick(View view){
    	findViewById(R.id.text1).setBackgroundColor(0xFF606060);
    	findViewById(R.id.text2).setBackgroundColor(0xFF808080);
    	findViewById(R.id.text3).setBackgroundColor(0xFFa0a0a0);
    	view.setBackgroundColor(0x88FF8888);
    	selectViewId = view.getId();
    	if(findViewById(R.id.listview6).getVisibility()==View.GONE){
        	findViewById(R.id.listview6).setVisibility(View.VISIBLE);
    	}
    }
    int selectViewId;
    public void btnOnClick(View view){
    	Calendar c = Calendar.getInstance();
    	int year,month,day;
    	try{
    		year = Integer.parseInt(((Button)findViewById(R.id.text1)).getText().toString());
    		month = Integer.parseInt(((Button)findViewById(R.id.text2)).getText().toString())-1;
    		day = Integer.parseInt(((Button)findViewById(R.id.text3)).getText().toString());
    	}catch(Exception e){
    		return;
    	}
    	c.set(year, month, day);
    	if(view.getId()==R.id.btn1){
    		switch(selectViewId){
    			case R.id.text1:
    				c.add(Calendar.YEAR, 1);
    				break;
    			case R.id.text2:
    				c.add(Calendar.MONTH, 1);
    				break;
    			case R.id.text3:
    				c.add(Calendar.DATE, 1);
    				break;
    			default:
    				break;
    		}
    	}else if(view.getId()==R.id.btn2){
    		switch(selectViewId){
				case R.id.text1:
					c.add(Calendar.YEAR, -1);
					break;
				case R.id.text2:
					c.add(Calendar.MONTH, -1);
					break;
				case R.id.text3:
					c.add(Calendar.DATE, -1);
					break;
				default:
					break;
    		}
    	}
    	Calendar c1 = Calendar.getInstance();
    	if(c.after(c1))return;
    	c1.add(Calendar.DATE,  -Data.mUserScore);
    	if(c.before(c1)){
    		showtoast("积分不够");
    		return;
    	}
    	
        ((Button)findViewById(R.id.text1)).setText(String.valueOf(c.get(Calendar.YEAR)));
        ((Button)findViewById(R.id.text2)).setText(String.valueOf(1+c.get(Calendar.MONTH)));
        ((Button)findViewById(R.id.text3)).setText(String.valueOf(c.get(Calendar.DATE)));
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date =new Date(c.getTimeInMillis());
        String mdate = format.format(date);
        mProgressDialog = ProgressDialog.show(mContext, "","正在刷新，请稍后......", false);
		new ProgressThread(handler,mdate).start();
    }
    ProgressDialog mProgressDialog;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
			ListView lv = (ListView) findViewById(R.id.listview6);
	        lv.invalidate();
	        String str[] = msg.getData().getStringArray("rst");
	    	lv.setAdapter(new ArrayAdapter<String>(mContext,R.layout.list_items,str));
	    	setListViewHeightBasedOnChildren(lv);
	    	lv.setVisibility(View.VISIBLE);
	    	lv.setOnCreateContextMenuListener(cmlistener);
		}
	};
	private class ProgressThread extends Thread {
		Handler mHandler;
		String mDate;
		ProgressThread(Handler h,String date) {
            mHandler = h;
            mDate = date;
        }
		public void run() {
			Bundle b = new Bundle();
			Message msg = mHandler.obtainMessage();
			String str[] = new NetworkData().getLaughText(mDate);
			b.putStringArray("rst", str);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.showDialog(0);
			return true;
		}else{		
			return super.onKeyDown(keyCode, event);
		}
	}
	 protected Dialog onCreateDialog (int id){
			Builder builder = new Builder(this);
	    	String str = null;
	    	if(id==0)str="确定退出程序";
	    	else if(id==1)str="网络错误，退出程序";
			switch(id){
	    		case 0:
	    		case 1:
	    			builder.setMessage(str) 
	    			.setCancelable(false) 
	    			.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
	    				public void onClick(DialogInterface dialog, int id) {
	    					onDestroy();
	    	        		System.exit(0);
	    				} 
	    			}) 
	    			.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
	    				public void onClick(DialogInterface dialog, int id) { 
	    				} 
	    			});
	    			break;
	    		default:break;
	    	}
	    	AlertDialog alert = builder.create();
	    	return alert;
	 }
}