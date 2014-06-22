package com.mike.scanevrythng.scanner.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.mike.scanevrythng.scanner.R;
import com.mike.scanevrythng.scanner.service.EvrythngService;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScanActivity extends ActionBarActivity implements ScanditSDKListener {
	private String TAG = ((Object) this).getClass().getSimpleName();

	private ScanditSDKAutoAdjustingBarcodePicker barcodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

	    Log.i(TAG, "Setting up Scandit Scanning");

	    barcodePicker = new
			    ScanditSDKAutoAdjustingBarcodePicker(this,
			    getApplicationContext().getString(R.string.scandit_application_key),
			    ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);

	    barcodePicker.getOverlayView().addListener(this);

	    setContentView(barcodePicker);
    }

	@Override
	public void onResume() {
		Log.i(TAG, "In onResume method");
		barcodePicker.startScanning();
		super.onResume();
	}

	@Override
	public void onPause() {
		barcodePicker.stopScanning();
		super.onPause();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void didScanBarcode(String barcode, String barcodeType) {
		new ScanditCaller().execute(barcode, barcodeType);
	}

	@Override
	public void didManualSearch(String s) {
		// Called when using the Scandit Search bar
		Log.i(TAG, "Manual Search String: " + s);
	}

	@Override
	public void didCancel() {}

	// Private Class to call the Scandit Service
	private class ScanditCaller extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String barcode = params[0];
			String barcodeType = params[1];

			// Build the URL
			String url = getApplicationContext().getString(R.string.scandit_protocol) +
					getApplicationContext().getString(R.string.scandit_base_url);
			url = url + "/" + barcode;
			url = url + "?key=" + getApplication().getString(R.string.scandit_product_api_key);

			Log.i(TAG, "URL: " + url);

			// Using Apache HTTP libraries
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			try {
				HttpResponse response = client.execute(httpGet);

				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream content = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}

					Intent thngPostIntent = new Intent(ScanActivity.this, EvrythngService.class);
					if (builder.toString().length() > 2) {
						thngPostIntent.putExtra("product", builder.toString());
						Log.i(TAG, "Starting IntentService");
						startService(thngPostIntent);
					}
					else {
						Log.e(TAG, "Respoonse form Scandit is empty product");
					}
				}
				else {
					Log.e(TAG, "Non 200 HTTP response caught when calling Scandit API");
					Log.e(TAG, "HTTP RESPONSE CODE: " + response.getStatusLine().getStatusCode());
					Log.e(TAG, "HTTP REASON PHRASE: " + response.getStatusLine().getReasonPhrase());
				}

				Log.i(TAG, "Builder response: " + builder.toString());
			}
			catch (IOException ioe) {
				Log.e(TAG, "Error handled while calling Scandit API");
				ioe.printStackTrace();
			}

			// Success or Failure -> display toast to user
			return builder.toString();
		}
	}
}
