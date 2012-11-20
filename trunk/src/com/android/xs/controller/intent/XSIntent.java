package com.android.xs.controller.intent;

import java.util.ArrayList;
import com.android.xs.controller.storage.PersistantStorage;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Actuator;
import com.android.xs.model.device.Sensor;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.AorS_Object;
import com.android.xs.model.error.ConnectionException;
import com.android.xs.view.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class XSIntent extends Activity {
	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/
	Intent result_data = new Intent();

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

		boolean invalid_data = false;
		for (int val : vals) {
			if (val < 0 || val > 100)
				invalid_data = true;
		}
		if (vals == null || names == null || (vals.size() != names.size()) || invalid_data) {
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

		// Daten senden an das
		// XS1-----------------------------------------------
		ArrayList<String> Sens_vals = new ArrayList<String>();
		for (int x = 0; x < names.size(); x++) {
			AorS_Object obj = (AorS_Object) RuntimeStorage.getMyXsone().getObject(names.get(x));
			if (obj != null) {
				if (obj.getClass().equals(Actuator.class)) {
					if (!obj.setValue(vals.get(x), true)) {
						getIntent().putExtra("Status", "Verbindungsfehler!");
					}else
						result_data.putExtra("Status", "Erfolgreich!");
				} else if (obj.getClass().equals(Sensor.class)) {
					Sens_vals.add(obj.getName()+ ";" + obj.getValue());
					result_data.putExtra("Status", "Erfolgreich!");
				}
			}
		}
		result_data.putStringArrayListExtra("Values", Sens_vals);
		result_data.putExtra("Status", "Erfolgreich!");
		finish();
		return;

	}

	@Override
	public void finish() {
		setResult(RESULT_OK, result_data);
		super.finish();
	}
}
