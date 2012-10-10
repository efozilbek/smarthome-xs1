package com.android.xs.view.setting;

import java.util.ArrayList;
import java.util.LinkedList;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.XS_Object;
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
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Die Activity l�scht ein Objekt aus der Liste aller Objekte. Die Liste wird
 * ausgelesen und ausgegeben als Spinner. bei auswahl und best�tigen wird das
 * Objekt aus der Liste aller XS_Objekte des Xsone geholt und in einem neuen
 * Prozess der l�schbefehl aufgerufen
 * 
 * @author Viktor Mayer
 * 
 */
public class Remove extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	private Xsone myXsone;
	// Dialog f�r ladevorgang
	private Dialog dialog;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Die Funktion bef�llt den Spinner und f�gt dem Button eine Aktion hinzu.
	 * beim Bet�tigen wird der l�schvorgang eingeleitet
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Maske f�r IP und Passwort anzeigen
		setContentView(R.layout.remove_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// Alle Elemente holen
		LinkedList<XS_Object> all_El = new LinkedList<XS_Object>();
		all_El.addAll(myXsone.getMyActuatorList());
		all_El.addAll(myXsone.getMySensorList());
		all_El.addAll(myXsone.getMyTimerList());
		all_El.addAll(myXsone.getMyScriptList());
		// Das String Array f�r den Spinner anlegen
		ArrayList<String> names = new ArrayList<String>();
		for (XS_Object rem : all_El) {
			if (!rem.getType().equals("disabled"))
				names.add(rem.getName());
		}
		// Die Spinner bef�llen
		Spinner obj_sp = (Spinner) findViewById(R.id.spinner_remove);
		String[] names_ar = names.toArray(new String[names.size()]);
		ArrayAdapter<String> adapter_typ = new ArrayAdapter<String>(
				Remove.this, android.R.layout.simple_spinner_item, names_ar);
		obj_sp.setAdapter(adapter_typ);

		// den Button holen
		Button button = (Button) findViewById(R.id.button_remove);
		// dem Button die Click Action hinzu f�gen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedr�ckt
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Remove.this, "", "l�schen...",
						true, false);
				dialog.show();

				// Den Names des gew�hlten Objekts holen
				Spinner obj_sp = (Spinner) findViewById(R.id.spinner_remove);
				String obj_name = obj_sp.getSelectedItem().toString();

				// Das gew�hlte Objekt holen
				XS_Object tobedel = myXsone.getObject(obj_name);

				// Starten
				new removeObj().execute(tobedel);
			}
		});
	}

	/**
	 * Die Klasse stellt den l�schprozess dar. Der Befehl wird dabei an das
	 * Xsone Objekt weiter geleitet und je nach Erfolg erfolgt eine Infomeldung
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class removeObj extends AsyncTask<XS_Object, Boolean, Boolean> {

		/**
		 * leitet das zu l�schende Objekt weiter
		 */
		@Override
		protected Boolean doInBackground(XS_Object... object) {
			// Der Befehl wird ans XSone Objekt weiter geleitet, welcher dies an
			// das HTTP Objekt weiter leitet
			return myXsone.remove(object[0], true);
		}

		/**
		 * nimmt das Ergebnis des L�schvorgangsentgegen un dgeneriert eine
		 * Infomeldung
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(Remove.this, "Element wurde gel�scht..",
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