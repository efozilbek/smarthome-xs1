package com.android.xs.controller.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class PersistantStorage {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	// Der Festspeicher f�r die App
	private static PersistantStorage pers = null;
	// Der Context f�r den Speichervorgang
	private static Context con = null;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	private PersistantStorage() {
	};

	/**
	 * Singleton getInstance Funktion
	 * 
	 * @param context
	 *            - der Kontext der App, n�tig f�r den Speichervorgang
	 * @return - Instanz von PersistantStorage
	 */
	public static PersistantStorage getInstance(Context context) {
		if (pers == null) {
			pers = new PersistantStorage();
			con = context;
		}
		return pers;
	}

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Speichert Daten auf dem Smartphone im Context der Activity nach dem
	 * �bergebenen Namen
	 * 
	 * @param data
	 *            - das zu speichernde Objekt
	 * @param filename
	 *            - Der Dateiname
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean saveData(Object data, String filename) {
		try {
			FileOutputStream fos = con.openFileOutput(filename,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.flush();
			oos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			Log.e("PersistantStorage", "Dateifehler");
			return false;
		} catch (Exception e) {
			Log.e("PersistantStorage", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * l�dt Daten aus dem Telefon
	 * 
	 * @param - filename. Der Dateiname
	 * @return - gibts das geladene Objekt zur�ck. Null bei Fehler
	 */
	public Object getData(String filename) {

		try {
			FileInputStream fos = con.openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(fos);
			Object data = ois.readObject();
			ois.close();
			fos.close();
			return data;
		} catch (Exception e) {
			Log.e("PersistantStorage", e.getMessage());
			return null;
		}
	}
}