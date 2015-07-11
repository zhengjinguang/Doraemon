package com.nannansea.apk.Laugh4EveryDay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkData{
	String url = "http://www.nannansea.com/apk/dboperate.php?";
	public boolean netconn;
	public NetworkData(Context context){
		ConnectivityManager connMgr = (ConnectivityManager)
        	context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		boolean isWifiConn = networkInfo.isConnected();
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = networkInfo.isConnected();
		netconn = isWifiConn|isMobileConn;
	}
	public NetworkData(){
	}
	public boolean getNetStatus(){
		return netconn;
	}
	public String getNetData(String param){
		String ERR[] = {
				"成功",
				"注册用户失败  已经存在此用户名",
				"注册用户失败  未知原因",
				"查找失败",
				"登陆失败  用户不存在",
				"登陆失败  密码错误",
				"上传失败  未知原因",
				"发送失败  未知原因",
				"读取失败  未知原因"};
		String s = null;
		try {
			InputStream is = getNetInputStream(url+param);
			if(is.read()!='E')return "Error";
			if(is.read()!='R')return "Error";
			if(is.read()!='R')return "Error";
			if(is.read()!='=')return "Error";
			int num = is.read();
			if(num != '0')return ERR[num-'0'];
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line;
			StringBuilder sb = new StringBuilder();
			line = br.readLine();
			if(line==null)return null;
			sb.append(line);
			while ((line = br.readLine()) != null) {  
                sb.append("\n"+line);  
            }
			s = sb.toString();
			is.close();
		} catch (Exception e){
				return "Error";
			}
		return s;
	}
	public String[][] getMessage(String name){
		String rst[][]=null;
		String param = "cmd=getmessage&UserName="+name;
		String s = getNetData(param);
		if(s==null)return null;
    	String str[] = s.split("<br/>");
    	rst = new String[str.length][];
    	for(int i=0;i<str.length;i++)
    		rst[i]=str[i].split("&nbsp");
    	return rst;
	}
	public void addPoint(int point){
		if(Data.mUserName==null)return;
		String param = "cmd=addscore&point="+point+"&name="+Data.mUserName;
		getNetData(param);
	}
	public String sendMessage(String UserFrom,String UserTo,String message){
		String param = "cmd=sendmessage&UserFrom="+UserFrom+"&UserTo="+UserTo+"&UserMessage="+message;
		String rst = getNetData(param);
		System.out.println("rst="+rst);
		if(rst == null)rst = "发送成功";
		return rst;
	}
	public String putLaughText(String name,String laughtext){
		if(StringUtils.isBlank(name)){
			name = "无名氏";
		}
		String param = "cmd=putlaugh&name="+name+"&laughtext="+laughtext;
		return getNetData(param);
	}
	public String[][] getLaughText(int from){
		String rst[][]=null;
		String param = "cmd=getlaugh&from="+from;
		String s = getNetData(param);
		if(s==null)return null;
    	String str[] = s.split("<br/>");
    	rst = new String[str.length][];
    	for(int i=0;i<str.length;i++)
    		rst[i]=str[i].split("&nbsp");
    	return rst;
	}
	public String getScrollTitle(){
		String param = "cmd=gettitle"; 
		return getNetData(param);
	}
	public int getUserScore(String name){
		String param = "cmd=getscore&name="+name; 
		return Integer.parseInt(getNetData(param));
	}
	public String UserLogon(String name,String pwd){
		String param = "cmd=logon&name="+name+"&pwd="+pwd;
		return getNetData(param);
	}
	public String UserRegister(String name,String pwd){
		String param = "cmd=register&name="+name+"&pwd="+pwd;
		return getNetData(param);
	}
	public String[] getLaughText(String date){
        String str[]={"no date"};
        String param = "cmd=select&date="+date;
        String s = getNetData(param);
    	if(s==null)return str;
    	str = s.split("<br/>");
        return str;
	}
    public InputStream getNetInputStream(String urlStr) {
        try {
        	String s1 = new URLtoUTF8().toUtf8String(urlStr);
        	String s2 = s1.replaceAll(" ","%20");
        	String s3 = s2.replaceAll("\n","\\\\n");
            URL url = new URL(s3);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            return is;
        } catch (Exception e) {
        }
        return null;
    }
    public class URLtoUTF8 {
        //转换为%E4%BD%A0形式
        public String toUtf8String(String s) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 0 && c <= 255) {
                    sb.append(c);
                } else {
                    byte[] b;
                    try {
                        b = String.valueOf(c).getBytes("utf-8");
                    } catch (Exception ex) {
                        System.out.println(ex);
                        b = new byte[0];
                    }
                    for (int j = 0; j < b.length; j++) {
                        int k = b[j];
                        if (k < 0)
                            k += 256;
                        sb.append("%" + Integer.toHexString(k).toUpperCase());
                    }
                }
            }
            return sb.toString();
        }
    }
}