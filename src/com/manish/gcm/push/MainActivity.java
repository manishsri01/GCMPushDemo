package com.manish.gcm.push;

import static com.manish.gcm.push.CommonUtilities.SENDER_ID;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

	private String TAG = "** GCMPushDEMOAndroid**";
	private TextView mDisplay;
	String regId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkNotNull(SENDER_ID, "SENDER_ID");
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		mDisplay = (TextView) findViewById(R.id.textView1);

		regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.v(TAG, "Already registered");
		}
		/**
		 * call asYnc Task
		 */
		new sendIdOnOverServer().execute();
		mDisplay.setText("RegId=" + regId);
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		GCMRegistrar.unregister(this);
	}

	public class sendIdOnOverServer extends AsyncTask<String, Void, String> {

		ProgressDialog pd = null;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(MainActivity.this, "Please wait",
					"Loading please wait..", true);
			pd.setCancelable(true);

		}

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpResponse response = null;
				HttpParams httpParameters = new BasicHttpParams();
				HttpClient client = new DefaultHttpClient(httpParameters);
				String url="http://10.0.0.30//parsing/GCM.php?" + "&regID="+ regId;
				Log.i("Send URL:", url);
				HttpGet request = new HttpGet(url);
				
				response = client.execute(request);

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				String webServiceInfo = "";
				while ((webServiceInfo = rd.readLine()) != null) {
					Log.d("****Status Log***", "Webservice: " + webServiceInfo);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();

		}

	}

}
