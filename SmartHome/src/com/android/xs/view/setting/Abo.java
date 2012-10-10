package com.android.xs.view.setting;

import com.android.xs.controller.services.Abo_Service;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.view.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Die Activity Abo baut in Verbindung zur XS1 auf, welche durch chunked tranfer
 * mode offen gehalten wird. Bi eintreffnden Events wird eine Nachricht von der
 * XS1 gesendet, wlche ausgegeben wird. Hierzu werden zwei Threads gstartet.
 * einer, welcher das request sendet und einen Input Stram bereit hält sowie
 * einer, welcher periodisch den TextView aktualisiert.
 * 
 * @author Viktor Mayer
 * 
 */
public class Abo extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private TextView tv;
	private boolean go = true;
	private Handler mHandler = new Handler();
	/**
	 * Das Runnable Objekt kann die Textview aktualisieren.
	 */
	private Runnable updateTextView = new Runnable() {
		int old_length = 0;

		@Override
		public void run() {
			if (RuntimeStorage.getAbo_data_list().size() > old_length) {
				while (old_length < RuntimeStorage.getAbo_data_list().size()) {
					tv.append(RuntimeStorage.getAbo_data_list().get(
							old_length++));
				}
			}
			if (go)
				mHandler.postDelayed(updateTextView, 250);
		}
	};

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Die Startfunktion der Activity holt das XSone Objekt und initialisiert
	 * den Textview für die Ausgabe. Daraufhin wird der Thread zum anmelden an
	 * der XS1 gestartet sowie der Handler zum aktualisieren der TextView.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// beim Abo soll der Bildschirm nicht abgestellt werden
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.abo);

		tv = (TextView) findViewById(R.id.abo_text);
		tv.setText("Abonnement:\n");

		if (!Abo_Service.isInstanceCreated()) {
			// Der Service wird gestartet. Dieser ruft in einem Thread die
			// subscribe Funktion auf des Http Objekts
			startService(new Intent(this, Abo_Service.class));
		}

		// regelmäßige Aktualisierung der Ausgabe
		mHandler.postDelayed(updateTextView, 500);

		tv.append("Verbindung wird hergestellt...");
		return;
	}

	/**
	 * bei Pause muss das Warten auf HTTP antwort beendet werden. Hierzu wird
	 * der Thread sowie der Handler beendet
	 */
	@Override
	public void onPause() {
		super.onPause();

		
	}

	/**
	 * bei erneuter Ausführung müssen nur der Request sowie der Handler zum
	 * Updaten des Textes neu gestartet werden
	 */
	@Override
	public void onRestart() {
		super.onRestart();

		// prüfen ob service läuft
		if (Abo_Service.isInstanceCreated())
			Toast.makeText(this, "XS Abo Service läuft bereits...",
					Toast.LENGTH_LONG).show();
		else {
			startService(new Intent(this, Abo_Service.class));
			mHandler.postDelayed(updateTextView, 500);
		}
	}

	/**
	 * für die Tabs wird der KeyDown für Back sowie Menü überschrieben
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Beim Back Button wird in für die Tabs ein Hinweis ausgegeben, ob die
		// Anwendung beendet werden soll
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// Bei Pause muss gefragt werden, ob der Service gestoppt werden soll

			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setCancelable(false); // This blocks the 'BACK' button
			ad.setTitle("Hinweis");
			ad.setMessage("Möchten sie den XS Abo Service aktiv lassen?");
			// Die beiden Auswahlbuttons setzen
			ad.setButton("Nein", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					stopService(new Intent(getBaseContext(), Abo_Service.class));
					go = false;
					dialog.dismiss();
					finish();
				}
			});
			ad.setButton2("Ja", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Dialog schließen
					dialog.dismiss();
					finish();
				}
			});
			ad.show();
		}
		return true;
	}

}
