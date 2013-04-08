package com.android.xs.view.setting;

import com.android.xs.view.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

public class About extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

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

		setContentView(R.layout.tab_table_layout);

		TableLayout table = (TableLayout) findViewById(R.id.tab_table);

		TextView tv = new TextView(this);
		tv.setText("Autor: Viktor Mayer\n" + "Version: "
				+ this.getString(R.string.version)
				+ "\n"
				+ "App zur Steuerung der XS1\n"
				+ "Vollversion\n"
				+ "\n"
				+ "\n"
				+ "Anwendungshinweise:\n"
				+ "\n"
				+ "Die Anwendung fragt beim ersten Start nach einer Verbindung zur XS1. Dies ist �ber das lokale WLAN (IP) oder aber �ber einen DynDns"
				+ "Anbieter (InetAdresse) m�glich. Zur Nutzung ist eine Verbindung jedoch zwingend erforderlich.\n"
				+ "\n"
				+ "Die Tabs:\n"
				+ "\n"
				+ "Aktuatoren: Hier werden alle eingerichteten Aktuatoren gezeigt."
				+ "Die mei�ten Aktuatoren werden �ber einen Schalter gesteuert. Dimmer werden �ber einen Schieberegler dargestellt."
				+ "Temperatursteuerungen werden �ber einen Auswahlspinner eingestellt."
				+ "Durch langes klicken auf den Namen des aktuatoren k�nnen auch die definierten Funktionen gew�hlt werden."
				+ "Beim erneuten Aufruf des Tabs wird dieses nur aktualisiert, soweit man �ber WLAN verbunden ist um die Netzlast im 3G Modus gering zu halten. "
				+ "\n\n Makros: Durch dr�cken "
				+ "von Men� gelangt man zu den Makros. Hier k�nnen neue Makros aufgezeichnet, die Aufzeichnung beendet, oder die Liste der Makros betrachtet werden. "
				+ "Nach dem Bet�tigen der Aufzeichnung werden alle Aktionen (Buttons oder Funktionsaufrufe) im System protokolliert. Nach dem Stoppen der Aufzeichnung "
				+ "Kann das Makro benannt werden und ist dann im Ger�t gespeichert."
				+ "Makros werden durch einfaches klick auf den Namen wiedergegeben. Durch langes klicken auf den Namen des Makros kann dieses gel�scht werden.\n"
				+ "\n"
				+ "Sensoren: Hier werden alle Sensorwerte dar gestellt. Die Aktualisierung erfolgt manuell.\n"
				+ "\n"
				+ "Timer: Hier werden alle Timer dar gestellt. Die Aktualisierung erfolgt manuell.\n"
				+ "\n"
				+ "Skripte: Hier werden alle Skripte dar gestellt. Die Aktualisierung erfolgt manuell.\n"
				+ "\n"
				+ "Optionen: Hier werden mehere Einstellm�glichkeiten geboten.\n\n"
				+ "	Ger�teinfo: Zeigt die Informationen, welche aus der XS1 ausgelesen wurden.\n\n"
				+ "	Konfiguration: Hier�ber kann eine Verbindung neu eingerichtet werden.\n\n"
				+ "	Hinzuf�gen: Hier k�nnen neue Objekte in die XS1 geschrieben werden."
				+ " Diese k�nnen Aktuatoren, Sensoren, Timer oder Skripte sein. Ggf. m�ssen hier Herstellerspezifische Daten eingegeben werden."
				+ " Ab der Firmwareversion 3 des XS1 wird hier zus�tzlich die M�glichkeit geboten die Codes anhand der Sender anzulernen.\n\n"
				+ "	Entfernen: Hier k�nnen bestehende Objekte aus der XS1 gel�scht werden.\n\n"
				+ "	Abonnement: Ein Dienst wird gestartet, welcher alle Aktivit�ten der XS1 aufzeichnet und in dem Fenster ausgibt. Dieser l�uft nach Wunsch auch nach"
				+ "	Beendigung der Anwendung weiter. Beim Bet�tigen von 'Zur�ck' erfolgt eine Abfrage, ob der Dienst im Hintergrund fortgef�hrt werden soll.\n\n"
				+ "   Positions-Tool: !EXPERIMENTEL! Hier kann f�r die aktuelle Position ein bestimmtes Makro beim Verlassen oder Betreten automatisch ausgef�hrt werden." 
				+ " N�hert sich der Nutzer der gesetzten Position auf 1KM oder verl�sst den Radius von 1KM (je nach Einstellung) wird das gew�hle Makro getriggert." 
				+ " Die Anwendung muss dabei nicht laufen.\n\n" 
				+ "   Alarm-Action: Hier k�nnen Makros zu bestimmten Uhrzeiten getriggert werden. So k�nnen z.B. jeden Morgen bestimmte Lichter und Ger�te automatisch angeschalten werden." 
				+ " Auch nach einem Neustart des Ger�tes bleiben die Alarme erhalten.\n\n"
				+ " �ber: Zeigt diese Infoseite an.\n" 
				+ "\n" 
				+ "Widget: Das Widget erm�glicht eine Sprachsteuerung der Anwendung. Um Kommandos optimal erkennen zu k�nnen m�ssen diese in einer bestimmten Form gesprochen werden\n" 
				+ "  F�r Makros gilt das Schema ['Makro'] + [Makroname]\n" 
				+ "  F�r Aktuatoren gilt das Schema [Aktuatorname] + ['an'|'anschalten'|'aus'|'ausschalten'|Wert] + ['Prozent'|'Grad' -> Optional]\n" 
				+ "  F�r Sensoren gilt das Schema [Sensorname] + ['abfragen']\n");
		table.addView(tv);

	}

}
