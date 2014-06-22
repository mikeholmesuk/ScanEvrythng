package com.mike.scanevrythng.scanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.*;
import com.mike.scanevrythng.scanner.R;
import com.mike.scanevrythng.scanner.dto.Thng;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ThngListActivity extends Activity {
	private String TAG = this.getClass().getSimpleName();

	private ListView thngListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thng_list);

	    thngListView = (ListView) findViewById(R.id.show_list_listview);

	    // Make the callout
	    new ThngListBackgroundProvider().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.thng_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private class ThngListBackgroundProvider extends AsyncTask<String, String, String> {
		// Dialog to show while waiting for resiults
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Setup the dialog
			dialog = new ProgressDialog(ThngListActivity.this);
			dialog.setMessage("Fetching your thngs");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			// Build the URL
			String url = getApplicationContext().getString(R.string.evrythng_protocol) +
					getApplicationContext().getString(R.string.evrythng_base_url);

			url = url + "/thngs?access_token=" + getApplicationContext().getString(R.string.evrythng_access_token);

			Log.i(TAG, "URL: " + url);

			// Using Apache HTTP libraries
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			try {
				HttpResponse response = client.execute(httpGet);
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

			// Parse the results from the input stream here

			return builder.toString();
		}

		@Override
		public void onPostExecute(String result) {
			Log.i(TAG, "On Post Execute");

			// Get rid of the dialog box
			dialog.cancel();

			List<Thng> thngList = parseThngs(result);

			thngListView.setAdapter(new ArrayAdapter<Thng>(
					ThngListActivity.this,
					android.R.layout.simple_list_item_activated_1,
					thngList));

			thngListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.i(TAG, "Clicked on list entry");

					String item = (String) parent.getItemAtPosition(position);
					Log.i(TAG, "String: " + item);
				}
			});
		}

		private List<Thng> parseThngs(String result) {
			List<Thng> thngs = new ArrayList<Thng>();
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();
				// Needed so GSON can map the dates
				gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
					public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
						return new Date(json.getAsJsonPrimitive().getAsLong());
					}
				});

				Gson gson = gsonBuilder.create();
				thngs = Arrays.asList(gson.fromJson(result, Thng[].class));

				for (Thng thng : thngs) {
					Log.i(TAG, "Name: " + thng.getName() + " [" + thng.getId() + "]");
				}
			}
			catch (Exception e) {
				Log.e(TAG, "Exception caught when retrieving list of thngs: " + e.getMessage());
				e.printStackTrace();
			}

			return thngs;
		}
	}
}
