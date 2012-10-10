package com.android.xs.view.add;

import java.util.ArrayList;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.Translator;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.RF_System;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class Add_Sens extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	// Dialog für Ladevorgang
	private Dialog dialog;
	// Liste der Systeme (wird aus dem XS1 ausgelesen)
	private ArrayList<String> sys = new ArrayList<String>();

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Maske für IP und Passwort anzeigen
		setContentView(R.layout.add_sens_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// Die Spinner befüllen
		// Typ Spinner
		Spinner typ_sp = (Spinner) findViewById(R.id.spinner_sens_typ);
		ArrayAdapter<String> adapter_typ = new ArrayAdapter<String>(
				Add_Sens.this, android.R.layout.simple_spinner_item,
				RuntimeStorage.getSens_types());
		typ_sp.setAdapter(adapter_typ);
		// System Spinner
		Spinner sys_sp = (Spinner) findViewById(R.id.spinner_sens_sys);
		for (RF_System s : RuntimeStorage.getSystems()) {
			sys.add(s.toString());
		}
		ArrayAdapter<String> adapter_sys = new ArrayAdapter<String>(
				Add_Sens.this, android.R.layout.simple_spinner_item, sys);
		sys_sp.setAdapter(adapter_sys);

		// den Button holen
		Button button = (Button) findViewById(R.id.button_sens);
		// dem Button die Click Action hinzu fügen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedrückt
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Add_Sens.this, "",
						"Sensor anlegen...", true, false);
				dialog.show();

				// Daten holen und zu String konvertieren
				String[] sensData = new String[8];
				Spinner typ = (Spinner) findViewById(R.id.spinner_sens_typ);
				sensData[0] = Translator.translate(typ.getSelectedItem()
						.toString());
				Spinner sys = (Spinner) findViewById(R.id.spinner_sens_sys);
				sensData[1] = sys.getSelectedItem().toString();
				EditText fak = (EditText) findViewById(R.id.text_sens_faktor);
				sensData[2] = fak.getText().toString();
				EditText off = (EditText) findViewById(R.id.text_sens_offset);
				sensData[3] = off.getText().toString();
				EditText name = (EditText) findViewById(R.id.text_sens_name);
				sensData[4] = name.getText().toString();
				EditText hc1 = (EditText) findViewById(R.id.text_sens_hc1);
				sensData[5] = hc1.getText().toString();
				EditText hc2 = (EditText) findViewById(R.id.text_sens_hc2);
				sensData[6] = hc2.getText().toString();
				EditText add = (EditText) findViewById(R.id.text_sens_addr);
				sensData[7] = add.getText().toString();

				// Starten
				new addSens().execute(sensData);
			}
		});
	}

	/**
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class addSens extends AsyncTask<String, Boolean, Boolean> {

		/**
		 * 
		 */
		@Override
		protected Boolean doInBackground(String... data) {
			// Der Befehl wird weiter gereicht zur XSone Objekt
			return myXsone.add_Sensor(data);
		}

		/**
		 * 
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(Add_Sens.this, "Sensor erfolgreich angelegt..",
						Toast.LENGTH_LONG).show();
				finish();
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(getBaseContext());
				return;
			}
		}

	}
}
