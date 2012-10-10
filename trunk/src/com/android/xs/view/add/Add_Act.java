package com.android.xs.view.add;

import java.util.ArrayList;
import java.util.Arrays;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow;
import android.widget.TextView;

public class Add_Act extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private ArrayList<String> system = new ArrayList<String>();
	String[] fn_names = { "", "AN", "AUS", "AUF", "AB", "OEFFNEN",
			"SCHLIESSEN", "HOCH", "RUNTER", "ZU", "LINKS", "RECHTS", "OBEN",
			"UNTEN", "VOR", "ZURUECK", "MAKRO", "LERNEN", "AN/AUS", "DIMMEN",
			"HELLER", "DUNKLER" };
	String[] percent = new String[100];
	String[] degree = new String[31];
	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	// Dialog f�r Ladevorgang
	private Dialog dialog;
	// Zum anzeigen variabel ben�tigter Felder
	private OnItemSelectedListener inputUnhider;
	// Liste mit allen Funktionsnamen, da Temp und Prozent dynamisch erzeugt
	// werden
	ArrayList<String> fn_names_all = new ArrayList<String>();
	// Die Elemente der Gui
	private ArrayAdapter<String> adapter_typ_typ;
	private Spinner typ_sp_typ1;
	private Spinner typ_sp_typ2;
	private Spinner typ_sp_typ3;
	private Spinner typ_sp_typ4;
	// Die Elemente werden je nach Wert des Spinner aus bzw ein geblendet
	private LinearLayout[] layouts_val = new LinearLayout[4];
	private LinearLayout[] layouts_tim = new LinearLayout[4];
	private TableRow[] rows = new TableRow[4];

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

		// Maske f�r IP und Passwort anzeigen
		setContentView(R.layout.add_act_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// Prozentangaben bef�llen f�r den Spinner
		int i;
		for (i = 1; i < 101; i++) {
			percent[i - 1] = i + "%";
		}
		// Grad bef�llen fr� den Spinner
		for (i = 5; i < 36; i++) {
			degree[i - 5] = i + "�C";
		}
		// Die Gesamtliste f�r Funktionsnamen erstellen
		fn_names_all.addAll(Arrays.asList(fn_names));
		fn_names_all.addAll(Arrays.asList(percent));
		fn_names_all.addAll(Arrays.asList(degree));

		// Die Eingabefelder f�r Wert, die dynamisch ein bzw ausgeblendet werden
		layouts_val[0] = (LinearLayout) findViewById(R.id.linearLayout_val1);
		layouts_val[1] = (LinearLayout) findViewById(R.id.linearLayout_val2);
		layouts_val[2] = (LinearLayout) findViewById(R.id.linearLayout_val3);
		layouts_val[3] = (LinearLayout) findViewById(R.id.linearLayout_val4);

		layouts_tim[0] = (LinearLayout) findViewById(R.id.linearLayout_tim1);
		layouts_tim[1] = (LinearLayout) findViewById(R.id.linearLayout_tim2);
		layouts_tim[2] = (LinearLayout) findViewById(R.id.linearLayout_tim3);
		layouts_tim[3] = (LinearLayout) findViewById(R.id.linearLayout_tim4);

		// Die Rows die dynamisch ein bzw ausgeblendet werden
		rows[0] = (TableRow) findViewById(R.id.row_fnct1);
		rows[1] = (TableRow) findViewById(R.id.row_fnct2);
		rows[2] = (TableRow) findViewById(R.id.row_fnct3);
		rows[3] = (TableRow) findViewById(R.id.row_fnct4);

		// Der Event Listener f�r den Spinner typ blendet bei Auswahl von
		// "an,warten,aus" sowie "absolut" die n�tigen Felder ein
		inputUnhider = new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int num = (Integer) ((Spinner) arg0).getTag();
				String chosen = "";
				if (arg1 != null)
					chosen = ((TextView) arg1).getText().toString();
				if (num >= 0 && num < 10) {
					// Hier wird Wert und Zeit eingeblendet
					if (chosen.equals("absolut")) {
						layouts_val[num].setVisibility(LinearLayout.VISIBLE);
						layouts_tim[num].setVisibility(LinearLayout.VISIBLE);
						rows[num].setVisibility(TableRow.VISIBLE);
					}
					// Hier wird Zeit eingeblendet
					else if (chosen.equals("on_wait_off")) {
						rows[num].setVisibility(TableRow.VISIBLE);
						layouts_tim[num].setVisibility(LinearLayout.VISIBLE);
						layouts_val[num].setVisibility(LinearLayout.GONE);
					}
					// Hier wird Wert eingeblendet
					else if (chosen.equals("auto") || chosen.equals("manual")) {
						rows[num].setVisibility(TableRow.VISIBLE);
						layouts_tim[num].setVisibility(LinearLayout.GONE);
						layouts_val[num].setVisibility(LinearLayout.VISIBLE);
					}
					// Sonst wird alles aus geblendet
					else {
						rows[num].setVisibility(TableRow.GONE);
					}
				} else {
					// F�r das FHT sytem werden gesonerte Typen ben�tigt
					// (Temperatur)
					for (RF_System s : RuntimeStorage.getSystems()) {
						if (s.getName().equals(chosen)) {
							adapter_typ_typ = new ArrayAdapter<String>(
									Add_Act.this,
									android.R.layout.simple_spinner_item,
									s.getFunction());
							typ_sp_typ1.setAdapter(adapter_typ_typ);
							typ_sp_typ2.setAdapter(adapter_typ_typ);
							typ_sp_typ3.setAdapter(adapter_typ_typ);
							typ_sp_typ4.setAdapter(adapter_typ_typ);
						}
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		};

		// Die Spinner bef�llen f�r Typ und System
		final Spinner typ_sp = (Spinner) findViewById(R.id.spinner_act_typ);
		ArrayAdapter<String> adapter_typ = new ArrayAdapter<String>(
				Add_Act.this, android.R.layout.simple_spinner_item,
				RuntimeStorage.getAct_types());
		typ_sp.setAdapter(adapter_typ);

		// System Spinner
		for (RF_System s : RuntimeStorage.getSystems()) {
			system.add(s.toString());
		}
		final Spinner sys_sp = (Spinner) findViewById(R.id.spinner_act_sys);
		ArrayAdapter<String> adapter_sys = new ArrayAdapter<String>(
				Add_Act.this, android.R.layout.simple_spinner_item, system);
		sys_sp.setAdapter(adapter_sys);
		sys_sp.setOnItemSelectedListener(inputUnhider);
		sys_sp.setTag(10);

		// Funktionsnamen Spinner bef�llen
		final Spinner typ_sp_fn1 = (Spinner) findViewById(R.id.spinner_act_fn_one);
		ArrayAdapter<String> adapter_typ_fn = new ArrayAdapter<String>(
				Add_Act.this, android.R.layout.simple_spinner_item,
				fn_names_all);
		typ_sp_fn1.setAdapter(adapter_typ_fn);
		// Funktionsspinner zwei
		final Spinner typ_sp_fn2 = (Spinner) findViewById(R.id.spinner_act_fn_two);
		typ_sp_fn2.setAdapter(adapter_typ_fn);
		// Funktionsspinner drei
		final Spinner typ_sp_fn3 = (Spinner) findViewById(R.id.spinner_act_fn_three);
		typ_sp_fn3.setAdapter(adapter_typ_fn);
		// Funktionsspinner vier
		final Spinner typ_sp_fn4 = (Spinner) findViewById(R.id.spinner_act_fn_four);
		typ_sp_fn4.setAdapter(adapter_typ_fn);

		// Funktionstyp Spinner bef�llen und den Listener hinzu f�gen
		typ_sp_typ1 = (Spinner) findViewById(R.id.spinner_act_typ_one);
		typ_sp_typ1.setTag(0);
		typ_sp_typ1.setOnItemSelectedListener(inputUnhider);
		// Typ Spinner zwei
		typ_sp_typ2 = (Spinner) findViewById(R.id.spinner_act_typ_two);
		typ_sp_typ2.setTag(1);
		typ_sp_typ2.setOnItemSelectedListener(inputUnhider);
		// Typ Spinner drei
		typ_sp_typ3 = (Spinner) findViewById(R.id.spinner_act_typ_three);
		typ_sp_typ3.setTag(2);
		typ_sp_typ3.setOnItemSelectedListener(inputUnhider);
		// Typ Spinner vier
		typ_sp_typ4 = (Spinner) findViewById(R.id.spinner_act_typ_four);
		typ_sp_typ4.setTag(3);
		typ_sp_typ4.setOnItemSelectedListener(inputUnhider);

		// den Button holen
		Button button = (Button) findViewById(R.id.button_act_ok);
		// dem Button die Click Action hinzu f�gen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedr�ckt
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Add_Act.this, "",
						"Aktuator anlegen...", true, false);
				dialog.show();

				// Das Daten array f�r die Eingabewerte
				String[] data = new String[22];
				// Das Array wird mit den Werten der Felder bef�llt
				data[0] = Translator.translate(typ_sp.getSelectedItem()
						.toString());
				data[1] = sys_sp.getSelectedItem().toString();
				EditText name = (EditText) findViewById(R.id.text_act_name);
				data[2] = name.getText().toString();
				EditText hc1 = (EditText) findViewById(R.id.text_act_hc1);
				data[3] = hc1.getText().toString();
				EditText hc2 = (EditText) findViewById(R.id.text_act_hc2);
				data[4] = hc2.getText().toString();
				EditText address = (EditText) findViewById(R.id.text_act_address);
				data[5] = address.getText().toString();
				// ab hier Funktionen
				data[6] = typ_sp_fn1.getSelectedItem().toString();
				data[7] = Translator.translate(typ_sp_typ1
						.getSelectedItem().toString());
				EditText val1 = (EditText) findViewById(R.id.text_act_val1);
				data[8] = val1.getText().toString();
				EditText tim1 = (EditText) findViewById(R.id.text_act_tim1);
				data[9] = tim1.getText().toString();
				// Funktion zwei
				data[10] = typ_sp_fn2.getSelectedItem().toString();
				data[11] = Translator.translate(typ_sp_typ2
						.getSelectedItem().toString());
				EditText val2 = (EditText) findViewById(R.id.text_act_val2);
				data[12] = val2.getText().toString();
				EditText tim2 = (EditText) findViewById(R.id.text_act_tim2);
				data[13] = tim2.getText().toString();
				// Funktion drei
				data[14] = typ_sp_fn3.getSelectedItem().toString();
				data[15] = Translator.translate(typ_sp_typ3
						.getSelectedItem().toString());
				EditText val3 = (EditText) findViewById(R.id.text_act_val3);
				data[16] = val3.getText().toString();
				EditText tim3 = (EditText) findViewById(R.id.text_act_tim3);
				data[17] = tim3.getText().toString();
				// Funktion vier
				data[18] = typ_sp_fn4.getSelectedItem().toString();
				data[19] = Translator.translate(typ_sp_typ4
						.getSelectedItem().toString());
				EditText val4 = (EditText) findViewById(R.id.text_act_val4);
				data[20] = val4.getText().toString();
				EditText tim4 = (EditText) findViewById(R.id.text_act_tim4);
				data[21] = tim4.getText().toString();

				// Starten
				new addAct().execute(data);
			}
		});

	}

	/**
	 * Ausgelagerter Task um Dialog anzeigen zu k�nnen. Der neue Aktuator wird
	 * an das XS1 gesendet
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class addAct extends AsyncTask<String, Boolean, Boolean> {

		/**
		 * 
		 */
		@Override
		protected Boolean doInBackground(String... data) {
			// myXsone.add_RemObj(rem_obj);
			return myXsone.add_Actuator(data);
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
				Toast.makeText(Add_Act.this, "Aktuator erfolgreich angelegt..",
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