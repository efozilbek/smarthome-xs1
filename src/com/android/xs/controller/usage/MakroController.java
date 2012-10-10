package com.android.xs.controller.usage;

import java.util.ArrayList;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.components.Makro;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class MakroController {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static MakroController mc = null;
	private final static String XS_MAKROS = "SmartHomeXS.makros";
	private Makro makro;
	private boolean enabled = false;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	private MakroController() {
	}

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * gibt eine Instanz zur�ck
	 */
	public static MakroController getInstance() {
		if (mc == null)
			mc = new MakroController();
		return mc;
	}

	/**
	 * startet ein neues Makro. es wird ein neues Makro Objekt angelegt.
	 */
	public void newMakro() {
		makro = new Makro();
		enabled = true;
	}

	/**
	 * Gibt Das aktuelle Makro zur�ck, falls es zuvor gestartet wurde, sonst
	 * null
	 * 
	 * @return - Das Makro, sonst null
	 */
	public Makro MakroEnabled() {
		if (enabled)
			return makro;
		else
			return null;
	}

	/**
	 * stopt das aktuelle Makro. Diesem wird der Name �bergeben und das Objekt
	 * dann in die Liste sowie persistent gespeichert. Das aktuelle makro wird
	 * dann auf null gesetzt.
	 * 
	 * @param name
	 *            - String: der Name des Makros
	 */
	public void stopMakro(String name) {
		// nur speichern falls nicht durch Appende gestoppt
		if (name != null) {
			makro.setName(name);
			RuntimeStorage.getMyXsone().add_Makro(makro);
			RuntimeStorage.getMyPers().saveData(
					RuntimeStorage.getMyXsone().getMyMakroList(), XS_MAKROS);
		}
		enabled = false;
	}

	/**
	 * speichert die aktuelle Makro Liste im Speicher
	 */
	public void saveMakros() {
		RuntimeStorage.getMyPers().saveData(
				RuntimeStorage.getMyXsone().getMyMakroList(), XS_MAKROS);
	}

	/**
	 * l�dt die Makroliste aus dem Speicher und gibt diese zur�ck, falls
	 * vorhanden, bei Fehler wird eine leere Liste zur�ck gegeben.
	 * 
	 * @return - Die Liste aller Makros, leere Liste bei Fehler
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Makro> loadMakros() {
		ArrayList<Makro> makro_list = (ArrayList<Makro>) RuntimeStorage
				.getMyPers().getData(XS_MAKROS);
		if (makro_list == null)
			return new ArrayList<Makro>();
		return makro_list;
	}

}
