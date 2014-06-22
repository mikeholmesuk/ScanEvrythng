package com.mike.scanevrythng.scanner.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mike.scanevrythng.scanner.R;
import com.mike.scanevrythng.scanner.dto.Basic;
import com.mike.scanevrythng.scanner.dto.Geo;
import com.mike.scanevrythng.scanner.dto.Thng;
import com.mike.scanevrythng.scanner.dto.ThngLocation;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EvrythngService extends IntentService {
	private String TAG = this.getClass().getSimpleName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.mike.scanevrythng.scanner.service.action.FOO";
    private static final String ACTION_BAZ = "com.mike.scanevrythng.scanner.service.action.BAZ";

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, EvrythngService.class);
        intent.setAction(ACTION_FOO);
        context.startService(intent);
    }

    public EvrythngService() {
        super("EvrythngService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

	    Log.i(TAG, "Intent object: " + intent.getStringExtra("product"));

	    Gson gson = new GsonBuilder().create();

	    Basic basic = gson.fromJson(intent.getStringExtra("product"), Basic.class);

	    Log.e(TAG, "BASIC OBJECT: " + gson.toJson(basic));

	    Thng thng = new Thng();
	    thng.setName(basic.getBasic().getName());
	    ArrayMap<String, String> property = new ArrayMap<String, String>();
	    property.put("category", basic.getBasic().getCategory());
	    thng.setProperties(property);

	    // Setup geo location details
	    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	    Criteria criteria = new Criteria();

	    Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

	    if (location != null) {
		    Log.i(TAG, "Location is not null (" + location.getLongitude() + ", " + location.getLatitude() + ")");
		    Geo geo = new Geo();
		    geo.setType("Point");
		    geo.setCoordinates(new Double[] {location.getLongitude(), location.getLatitude()});
		    ThngLocation thngLocation = new ThngLocation(geo);
		    thng.setLocation(thngLocation);
	    }

	    Log.e(TAG, "THNG BEFORE SENDING: " + gson.toJson(thng));
	    createNewThng(thng);
    }

	private void createNewThng(Thng thng) {
		// Build the URL
		String url = getApplicationContext().getString(R.string.evrythng_protocol) +
				getApplicationContext().getString(R.string.evrythng_base_url);

		url = url + "/thngs";

		Log.i(TAG, "URL: " + url);

		// Setup Gson
		Gson gson = new GsonBuilder().create();

		Log.i(TAG, "THNG: " + gson.toJson(thng));

		// Using Apache HTTP libraries
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Authorization", getApplicationContext().getString(R.string.evrythng_access_token));

		// Add the Thng to the body
		try {
			Log.i(TAG, "Adding " + gson.toJson(thng) + " to request");
			ByteArrayEntity entity = new ByteArrayEntity(gson.toJson(thng).getBytes("UTF8"));
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httpPost.setEntity(entity);
		}
		catch (UnsupportedEncodingException uce) {
			Log.e(TAG, "Exception caught when converting Thng to JSON");
		}

		try {
			HttpResponse response = client.execute(httpPost);
			Log.i(TAG, "Status: " + response.getStatusLine());
			Log.i(TAG, "Status Code: " + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = response.getEntity();
				InputStream content = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}

				Log.i(TAG, "Response received");
				Log.i(TAG, "Response: " + builder.toString());

			}
		}
		catch (IOException ioe) {
			Log.e(TAG, "IO Exception caught when makign call to Evrythng");
			ioe.printStackTrace();
		}
	}
}
