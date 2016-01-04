package com.example.hosin.bikesummon;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Http����Ĺ�����
 * 
 * @author zym
 * 
 */
public class HttpUtil
{
	private static final String urlHeader = "http://hellobike.sinaapp.com";
	private static final int TIMEOUT_IN_MILLIONS = 5000;

	public interface CallBack
	{
		void onRequestComplete(String result);
	}


	/**
	 * �첽��Get����
	 * 
	 * @param urlStr
	 * @param callBack
	 */
	public static void doGetAsyn(final String urlStr, final CallBack callBack)
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					String result = doGet(urlStr);
					if (callBack != null)
					{
						callBack.onRequestComplete(result);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			};
		}.start();
	}

	/**
	 * �첽��Post����
	 * @param urlStr
	 * @param params
	 * @param callBack
	 * @throws Exception
	 */
	public static void doPostAsyn(final String urlStr, final JSONObject params,
			final CallBack callBack) throws Exception
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					String result = doPost(urlStr, params).toString();
					if (callBack != null)
					{
						callBack.onRequestComplete(result);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			};
		}.start();

	}

	/**
	 * Get���󣬻�÷�������
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) 
	{
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try
		{
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else
			{
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
			}
			conn.disconnect();
		}
		
		return null ;

	}

	/**
	 * ��ָ�� URL ����POST����������
	 * 
	 * @param url
	 *            ��������� URL
	 * @param param
	 *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
	 * @return ������Զ����Դ����Ӧ���
	 * @throws JSONException 
	 * @throws Exception
	 */
	public static JSONObject doPost(String urlFlag, JSONObject param) throws ParseException,
	IOException, JSONException 
	{
		
		String url = urlHeader + urlFlag;
		
		Log.d("para", url+" "+param.toString());
		
		HttpPost post = new HttpPost(url);
		post.addHeader("Content-Type", "application/json");
		HttpEntity entity = null;
		if (param != null) {
			try {
				/*JSONObject param = new JSONObject();
				param = JSONObject.fromObject(list);*/
				StringEntity se = new StringEntity(param.toString(),HTTP.UTF_8);
				post.setEntity(se);
				Log.d("json", "send finished");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				Log.d("test", response.toString());

				String result = EntityUtils.toString(response.getEntity());

				//result = new String(result.getBytes(""));

				JSONObject result1 = new JSONObject(result);

				Log.d("json", result.toString());
				return result1;
			} else if (response.getStatusLine().getStatusCode() == 500) {
				Log.d("json", "wrong return");
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("json", "exception");
		}
		
		return null;
		
		
}}


