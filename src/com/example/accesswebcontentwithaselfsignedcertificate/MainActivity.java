package com.example.accesswebcontentwithaselfsignedcertificate;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	Button testConnectionButton;
	
	InputStream hostCertificate;
	String hostURL;
	ConnectToHostWithSelfSignedSSL connectToHostWithSelfSignedSSL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		testConnectionButton = (Button) findViewById(R.id.testConnectionButton);
		
		hostURL = "https://YOURHOST";	// Change this line 
		
		hostCertificate = getResources().openRawResource(R.raw.host);
		
		testConnectionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Do the magic ;)
				connectToHostWithSelfSignedSSL = new ConnectToHostWithSelfSignedSSL(hostCertificate, hostURL);
				String result = connectToHostWithSelfSignedSSL.executeUsingHTTPS();
				Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
			}
		});
	}

}
