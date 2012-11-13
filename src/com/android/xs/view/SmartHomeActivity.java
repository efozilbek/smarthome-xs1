package com.android.xs.view;

import com.android.xs.controller.storage.PersistantStorage;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.error.ConnectionException;
import com.android.xs.view.R;
import com.android.xs.view.setting.Config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * stellt die Schnittstelle zur View Komponente dar und enth�lt das Xsone Objekt
 * mit allen darzustellenden Daten wird beim Systemstart aufgerufen und ruft die
 * weiteren Activities auf.
 * 
 * @author Viktor Mayer
 * 
 */
public class SmartHomeActivity extends Activity {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * wird beim Ausf�hren zuerst aufgerufen. Pr�ft ob bereits Daten gespeichert
	 * wurden, falls nicht wird die FirstRun Activity aufgerufen
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		// Die RuntimeStorage mit Persistant Storage setzen
		if (RuntimeStorage.getMyPers() == null)
			RuntimeStorage.setMyPers(PersistantStorage
					.getInstance(getBaseContext()));

		// Die Runtime Stoarge mit Http setzen
		if (RuntimeStorage.getMyHttp() == null)
			RuntimeStorage.setMyHttp();

		// es wird versucht die gespeicherte IP anzulegen.. bei Fehler wird
		// firstrun aufgerufen, welcher die IP abfragt und setmyXsone aufruft um
		// diese das XSOne Objekt im RuntimeStorage zu speichern
		if (RuntimeStorage.getXsdata() == null) {
			// in diesem Fall ist noch keine IP gespeichert
			Intent intent = new Intent(this, Config.class);
			startActivity(intent);
		}

		// falls die IP erfolgreich geladen wurde wird das Xsone Objekt
		// aktualisiert. Das Objekt wird neu geladen vom
		// XS1 und es kann dann zum Mainframe gewechselt werden nach einem
		// Update des XS1
		else {
			new UpdateXSData().execute();
		}
	}

	/**
	 * beim Fortsetzen wurde die IP gesetzt vom Benutzer. Ist nur der Fall nach
	 * Aufruf von Firstrun, da die Activity mit dem Mainframe endet
	 */
	@Override
	public void onRestart() {
		super.onRestart();

		// Das Objekt sollte nun fertig angelegt sein und kann verwendet werden
		// pr�fen (Benutzer k�nnte zur�ck gedr�ckt haben!!)
		if (Config.isConn_validated()) {
			// Die XS_Daten m�ssen nun persistent gespeichert werden
			String[] data = {
					RuntimeStorage.getMyXsone().getMyIpSetting().getIp(),
					RuntimeStorage.getMyXsone().getUsername(),
					RuntimeStorage.getMyXsone().getPassword() };
			RuntimeStorage.setXsdata(data);
			// den Content f�r Spinner holen
			RuntimeStorage.get_content();
			// Ist die Verbindung gelungen kann zum Mainframe gewechselt werden
			Intent intent = new Intent(this, MainFrame.class);
			startActivity(intent);
			finish();
		} else {
			// Andernfalls wird das Programm beendet
			alert(getBaseContext(), "Keine Verbindung definiert!");
		}
	}

	/**
	 * Die Alert Funktion benachrichtigt den User und gibt Option zum beenden
	 * oder neu konfigurieren
	 * 
	 * @param con
	 *            - Der Context der Activity
	 * @param txt
	 *            - Die auszugebende Warnmeldung
	 */
	private void alert(Context con, String txt) {
		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setTitle("Warnung");
		ad.setMessage(txt);
		// Die beiden Auswahlbuttons setzen
		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Beenden", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// Dialog schlie�en und Activity beenden
				dialog.dismiss();
				finish();
			}
		});
		ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Konfigurieren", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// Dialog schlie�en und Konfiguration starten
				dialog.dismiss();
				Intent intent = new Intent(SmartHomeActivity.this,
						Config.class);
				startActivity(intent);
			}
		});
		ad.show();
	}

	/**
	 * Ausgelagerter Thread, da je nach Verbindung zeitintensiv. UpdateXSData
	 * legt ein neues XSone Objekt an. Stimmen die Daten nicht mehr wird eine
	 * Excpetion geworfen. In diesem Fall wird in PostExecute der Alert
	 * aufgerufen , in dem Das Programm beendet, oder die Verbindung neu
	 * konfiguriert werden kann. Bei Erfolg startet das Mainframe
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class UpdateXSData extends AsyncTask<Context, Boolean, Boolean> {

		/**
		 * Im Hintergrundprozess wird das XSone angelegt. hier Kann es wegen
		 * Netzverkehr zu verz�gerung kommen. Bei Fehler wird false zur�ck
		 * gegeben
		 * 
		 * @return - False, falls keine Verbindung hergestellt werden konnte
		 * 
		 */
		@Override
		protected Boolean doInBackground(Context... arg0) {
			String[] xsdata = RuntimeStorage.getXsdata();
			try {
				// beim anlegen entsteht netzlast, da alles ausgelesen wird
				RuntimeStorage.setMyXsone(new Xsone(xsdata[0], xsdata[1],
						xsdata[2]));
			} catch (ConnectionException e) {
				return false;
			}
			// den Content f�r die Spinner holen
			RuntimeStorage.get_content();

			// nun kann zum Hauptprozess gewechselt werden
			Intent intent = new Intent(SmartHomeActivity.this, MainFrame.class);
			startActivity(intent);
			// Die Activity kann beendet werden
			SmartHomeActivity.this.finish();
			return true;

		}

		/**
		 * Konnte keine Verbindung aufgebaut werden muss der Alert Dialog mit
		 * den Auswahlm�glichkeiten aufgerufen werden
		 */
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (!result) {
				alert(getBaseContext(),
						"Verbindung konnte nicht hergestellt werden!");
			}
		}
	}

}