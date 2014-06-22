package com.mike.scanevrythng.scanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.mike.scanevrythng.scanner.R;


public class WelcomeActivity extends ActionBarActivity implements OnClickListener {
	// Additional object cast due to a bug in IntelliJ (http://youtrack.jetbrains.com/issue/IDEA-79680)
	private String TAG = ((Object) this).getClass().getSimpleName();

	// Activity Properties
	private Button startScanningBtn;
	private Button showListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

	    startScanningBtn = (Button) findViewById(R.id.start_scanning_button);
	    showListBtn = (Button) findViewById(R.id.show_list_button);

	    startScanningBtn.setOnClickListener(this);
	    showListBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
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

	@Override
	public void onClick(View view) {
		Log.i(TAG, "In onClick Listsner on Welcome Page");

		Intent intent;

		switch(view.getId()) {
			case R.id.start_scanning_button:
				Log.i(TAG, "Selected Start Scanning Button");
				intent = new Intent(this, ScanActivity.class);
				break;
			case R.id.show_list_button:
				Log.i(TAG, "Selected Show List button");
				intent = new Intent(this, ThngListActivity.class);
				break;
			default:
				throw new RuntimeException("Unrecognised event triggered in onClickListener");
		}
		startActivity(intent);
	}
}
