package com.android.xs.view.setting;

import com.android.xs.view.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

public class DonationsActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.donations__activity);

		/* PayPal */

		ViewStub paypalViewStub = (ViewStub) findViewById(R.id.donations__paypal_stub);
		paypalViewStub.inflate();

	}

	/**
	 * Donate button with PayPal by opening browser with defined URL For
	 * possible parameters see:
	 * https://cms.paypal.com/us/cgi-bin/?cmd=_render-content
	 * &content_ID=developer/ e_howto_html_Appx_websitestandard_htmlvariables
	 * 
	 * @param view
	 */
	public void donatePayPalOnClick(View view) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme("https").authority("www.paypal.com")
				.path("cgi-bin/webscr");
		uriBuilder.appendQueryParameter("cmd", "_donations");

		uriBuilder.appendQueryParameter("business", "mayer_v@web.de");
		uriBuilder.appendQueryParameter("lc", "DE");
		uriBuilder.appendQueryParameter("item_name", "XS1 App Developer");
		uriBuilder.appendQueryParameter("no_note", "1");
		// uriBuilder.appendQueryParameter("no_note", "0");
		// uriBuilder.appendQueryParameter("cn", "Note to the developer");
		uriBuilder.appendQueryParameter("no_shipping", "1");
		uriBuilder.appendQueryParameter("currency_code", "EUR");
		// uriBuilder.appendQueryParameter("bn",
		// "PP-DonationsBF:btn_donate_LG.gif:NonHosted");
		Uri payPalUri = uriBuilder.build();

		// Start your favorite browser
		Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
		startActivity(viewIntent);
	}

	/**
	 * Called when this activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * Called when this activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

}
