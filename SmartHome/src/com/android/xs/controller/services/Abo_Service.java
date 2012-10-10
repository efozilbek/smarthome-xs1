package com.android.xs.controller.services;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class Abo_Service extends Service {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	private static Xsone myXsone = null;
	// Abo Thread
	private Thread t;
	// Wird zur Prüfung genutzt, ob der Service läuft
	private static Abo_Service instance = null;
	// Timer zur Verbindungsprüfung
	private Timer check_timer;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Gibt zurück, ob der Service läuft
	 * 
	 * @return - true, wenn eine Instanz vorliegt, also der Service läuft, sonst
	 *         false
	 */
	public static boolean isInstanceCreated() {
		return instance != null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * Hier wird die Instanz angelegt sowie das Xsone Objekt geholt
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		myXsone = RuntimeStorage.getMyXsone();

	}

	/**
	 * Beim beenden wird die Instanz gelöscht. Zudem der thread gestoppt und die
	 * Liste geleert
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		instance = null;

		myXsone.unsubscribe();

		// Den Verbindungschecker beenden
		check_timer.cancel();

		// Die Liste leeren
		RuntimeStorage.getAbo_data_list().clear();
		Toast.makeText(this, "XS Abo Service beendet...", Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * Beim erneuten starten wird der Thread gestartet, falls er nicht schon
	 * läuft. Zudem wir die Liste geleert
	 */
	@Override
	public void onStart(Intent intent, int startid) {

		/**
		 * Der Thread startet die abfrage beim XS1. Der Aufruf subscribe ist
		 * blockierend und muss deshalb in einen eigenen Thread
		 */
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (myXsone != null)
						myXsone.subscribe(RuntimeStorage.getAbo_data_list());
				} catch (IOException e) {
					// Thread gestoppt. Die Exception kommt vom subscribe
					RuntimeStorage.getAbo_data_list().add(
							"Verbindung abgebrochen!\n");
					return;
				} catch (Exception e) {
					// bei den übrigen Exceptions ist nichts zu tun
				}
			}
		});
		t.start();

		ConnectionCheck checker = new ConnectionCheck();
		check_timer = new Timer(true);
		check_timer.schedule(checker, 5000, 30000);

		Toast.makeText(this, "XS Abo Service gestartet...", Toast.LENGTH_LONG)
				.show();

		// Die Liste leeren
		RuntimeStorage.getAbo_data_list().clear();
	}

	private class ConnectionCheck extends TimerTask {

		@Override
		public void run() {
			if (!t.isAlive()) {
				// alle 30 Sek Verbindung versuchen neu aufzubauen
				RuntimeStorage.getAbo_data_list().add(
						"Verbindung wird versucht herzustellen...\n");
				try {
					myXsone.subscribe(RuntimeStorage.getAbo_data_list());
				} catch (IOException e) {
					// Thread gestoppt. Die Exception kommt vom subscribe
					RuntimeStorage.getAbo_data_list().add(
							"Verbindung abgebrochen!\n");
				} catch (Exception e) {
					// bei den übrigen Exceptions ist nichts zu tun
				}
			}
		}
	}

}
