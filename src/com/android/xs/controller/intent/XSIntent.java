package com.android.xs.controller.intent;

import java.util.ArrayList;
import java.util.Locale;

import com.android.xs.controller.storage.PersistantStorage;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.MakroController;
import com.android.xs.model.device.Actuator;
import com.android.xs.model.device.Sensor;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.AorS_Object;
import com.android.xs.model.device.components.Makro;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.ConnectionException;
import com.android.xs.view.R;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

public class XSIntent extends Activity {
	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/
	Intent result_data = new Intent();
	boolean check = true;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * wird beim Ausf�hren zuerst aufgerufen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		ArrayList<String> names = getIntent().getStringArrayListExtra("names");
		ArrayList<Integer> vals = getIntent().getIntegerArrayListExtra("vals");
		ArrayList<String> makros = getIntent().getStringArrayListExtra("makros");
		boolean makrosactiononentry = getIntent().getBooleanExtra("makrosactiononentry", true);

		boolean invalid_data = false;
		boolean do_makro = false;

		// pr�fen auf Daten f�r einzelne Schalter
		// Makos oder einzelen Aktuatoren?
		if (vals == null || names == null) {
			do_makro = true;
		}

		// Pr�fung f�r Makro
		if (do_makro) {
			if (makros == null)
				invalid_data = true;
			else {
				if (makros.size() < 1)
					invalid_data = true;
			}
		}
		// Pr�fung f�r Aktuatorenliste
		else {
			if ((vals.size() != names.size()) && names.size() > 0)
				invalid_data = true;
			else {
				for (int val : vals) {
					if (val < 0 || val > 100)
						invalid_data = true;
				}
			}
		}

		if (invalid_data) {
			result_data.putExtra("Status", "�bergebene Daten Fehlerhaft!");
			finish();
			return;

		}

		// XSOne Objekt
		// holen----------------------------------------------------
		// Die RuntimeStorage mit Persistant Storage setzen
		if (RuntimeStorage.getMyPers() == null)
			RuntimeStorage.setMyPers(PersistantStorage.getInstance(getBaseContext()));

		// Die Runtime Storage mit Http setzen
		if (RuntimeStorage.getMyHttp() == null)
			RuntimeStorage.setMyHttp();

		// es wird versucht die gespeicherte IP anzulegen..
		String[] xsdata = RuntimeStorage.getXsdata();
		if (xsdata == null) {
			result_data.putExtra("Status", "XSone App noch nicht konfiguriert!");
			finish();
			return;
		}
		try {
			// beim anlegen entsteht netzlast, da alles ausgelesen wird
			RuntimeStorage.setMyXsone(new Xsone(xsdata[0], xsdata[1], xsdata[2]));
		} catch (ConnectionException e) {
			result_data.putExtra("Status", "Verbindungsfehler!");
			finish();
			return;
		}

		ArrayList<String> data = new ArrayList<String>();

		// Makro
		// ausf�hren-------------------------------------------------------------------

		// beim verlassen oder betreten holen
		String k = LocationManager.KEY_PROXIMITY_ENTERING;
		// Key for determining whether user is leaving or entering.. default,
		// also wenn nicht erfolgreich bestimmt werden konnte dann wird es nicht
		// ausgef�hrt
		boolean state = getIntent().getBooleanExtra(k, makrosactiononentry);
		// DEBUG
		// boolean state = makrosactiononentry;

		if (do_makro && makrosactiononentry == state) {
			// nummer in dr Liste rausfinden
			ArrayList<Makro> mlist = MakroController.loadMakros();
			int id = 0;
			for (String mname : makros) {
				for (Makro m : mlist) {
					if (m.getName().toLowerCase(Locale.GERMAN).equals(mname.toLowerCase())) {
						new ExecuteMakro().execute((int) id);
						// eine liste aller ausgef�hrten makros wird zur�ck
						// gegeben
						data.add(mname);
					}
					id++;
				}
				id = 0;
			}
		}

		// Einzelne Komponenten
		// ansprechen-------------------------------------------------------------------
		else if (do_makro == false) {
			// Daten senden an das
			// XS1-----------------------------------------------
			// im Fall einer leeren Liste werden alle Namen zur�ckgegeben
			if (names.size() == 0) {
				data.add("-from here actuators-");
				for (XS_Object a : RuntimeStorage.getMyXsone().getMyActuatorList()) {
					data.add(a.getName());
				}
				data.add("-from here sensors-");
				for (XS_Object s : RuntimeStorage.getMyXsone().getMySensorList()) {
					data.add(s.getName());
				}
			} else {
				// sonst wird je nach Aktuator oder Sensor Objekt ein Wert
				// gesendet oder zur�ckgegeben
				for (int x = 0; x < names.size(); x++) {
					AorS_Object obj = (AorS_Object) RuntimeStorage.getMyXsone().getObject(names.get(x));
					if (obj != null) {
						if (obj.getClass().equals(Actuator.class)) {
							if (!obj.setValue(vals.get(x), true)) {
								getIntent().putExtra("Status", "Verbindungsfehler!");
								return;
							}
						} else if (obj.getClass().equals(Sensor.class)) {
							data.add(obj.getName() + ";" + obj.getValue());
						}
					}
				}
			}
		}

		result_data.putStringArrayListExtra("Values", data);
		if (check = true)
			result_data.putExtra("Status", "Erfolgreich!");
		else
			result_data.putExtra("Status", "Fehler bei Ausf�hrung!");
		finish();
		return;
	}

	@Override
	public void finish() {
		setResult(RESULT_OK, result_data);
		super.finish();
	}

	/**
	 * Die Klasse f�hrt das makro als eigenen Task aus.
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class ExecuteMakro extends AsyncTask<Integer, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			return RuntimeStorage.getMyXsone().getMyMakroList().get((int) params[0]).replay();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			check = result;
		}
	}
}
