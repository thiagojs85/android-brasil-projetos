/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.android.brasil.projetos.gui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.android.brasil.projetos.dao.EmprestimoDbAdapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditarEmprestimo extends Activity {

	private EditText etItem;
	private EditText etDescricao;
	private Long mRowId;
	private EmprestimoDbAdapter mDbHelper;
	private TextView tvContato;

	private Spinner txtAutoNome;

	private CheckBox cbAlarme;

	private RadioButton rbEmprestar;
	private RadioButton rbPegarEmprestado;

	private Date dataDevolucao;
	private EditText etDataDevolucao;
	private EditText etHoraDevolucao;

	private static final int DATE_DIALOG_ID_DATE = 0;
	private static final int DATE_DIALOG_ID_TIME = 1;

	private DatePickerDialog.OnDateSetListener dataListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dataDevolucao.setYear(year - 1900);
			dataDevolucao.setMonth(monthOfYear);
			dataDevolucao.setDate(dayOfMonth);
			atualizarData();
		}
	};
	private OnTimeSetListener horaListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0, int hora, int minuto) {
			dataDevolucao.setHours(hora);
			dataDevolucao.setMinutes(minuto);
			atualizarData();

		}
	};;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editar_emprestimo);
		setTitle(R.string.editar_emprestimo);

		etItem = (EditText) findViewById(R.id.item);
		etDescricao = (EditText) findViewById(R.id.descricao);
		etDataDevolucao = (EditText) findViewById(R.id.data);
		etHoraDevolucao = (EditText) findViewById(R.id.hora);
		txtAutoNome = (Spinner) findViewById(R.id.txt_auto_nome);
		cbAlarme = (CheckBox) findViewById(R.id.cb_alarme);
		rbEmprestar = (RadioButton) findViewById(R.id.rb_emprestar);
		rbPegarEmprestado = (RadioButton) findViewById(R.id.rb_pegar_emprestado);
		tvContato = (TextView) findViewById(R.id.tv_contato);
		Button confirmButton = (Button) findViewById(R.id.confirmar);

		ContentResolver cr = getContentResolver();
		Cursor c = null;
		c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		String[] from = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
		int[] to = new int[] { android.R.id.text1 };
		startManagingCursor(c);

		txtAutoNome.setAdapter(new SimpleCursorAdapter(EditarEmprestimo.this,
				android.R.layout.simple_spinner_dropdown_item, c, from, to));

		dataDevolucao = Calendar.getInstance().getTime();
		atualizarData();

		rbEmprestar.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					tvContato.setText(R.string.contato);
				}

			}
		});

		rbPegarEmprestado.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					tvContato.setText(R.string.pegar_emprestado_de);
				}

			}
		});

		etDataDevolucao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID_DATE);
			}
		});

		etHoraDevolucao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID_TIME);
			}
		});


		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (validarCampos()) {
					saveState();
					if (mDbHelper != null) {
						mDbHelper.close();
						mDbHelper = null;
					}
					setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(EditarEmprestimo.this, "O nome do item deve ser informado!",
							Toast.LENGTH_LONG).show();
				}
			}

		});

		mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState
				.getSerializable(EmprestimoDbAdapter.COLUNA_ID);

		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(EmprestimoDbAdapter.COLUNA_ID) : null;
		}

		populateFields();
	}

	private boolean validarCampos() {
		String item = etItem.getText().toString();

		if (item.trim().equals("")) {
			return false;
		}

		return true;
	}

	private void populateFields() {
		if (mRowId != null) {
			if (mDbHelper == null) {
				mDbHelper = new EmprestimoDbAdapter(this);
			}

			mDbHelper.open();
			Cursor c = mDbHelper.consultarEmprestimo(mRowId);
			startManagingCursor(c);
			long status = c.getLong(c.getColumnIndexOrThrow(EmprestimoDbAdapter.COLUNA_STATUS));
			if (status == EmprestimoDbAdapter.STAUTS_EMPRESTAR) {
				rbPegarEmprestado.setChecked(false);
				rbEmprestar.setChecked(true);

			}

			if (status == EmprestimoDbAdapter.STAUTS_PEGAR_EMPRESTADO) {
				rbEmprestar.setChecked(false);
				rbPegarEmprestado.setChecked(true);
			}

			long alarme = c.getLong(c.getColumnIndexOrThrow(EmprestimoDbAdapter.COLUNA_ATIVAR_ALARME));
			if (alarme == EmprestimoDbAdapter.ATIVAR_ALARME) {
				cbAlarme.setChecked(true);
			}

			if (alarme == EmprestimoDbAdapter.DESATIVAR_ALARME) {
				cbAlarme.setChecked(false);
			}

			etItem.setText(c.getString(c.getColumnIndexOrThrow(EmprestimoDbAdapter.COLUNA_ITEM)));

			etDescricao.setText(c.getString(c
					.getColumnIndexOrThrow(EmprestimoDbAdapter.COLUNA_DESCRICAO)));

			Adapter ad = txtAutoNome.getAdapter();
			long id = c.getLong(c.getColumnIndexOrThrow(EmprestimoDbAdapter.COLUNA_ID_CONTATO));
			for (int i = 0; i < ad.getCount(); ++i) {

				if (ad.getItemId(i) == id) {
					txtAutoNome.setSelection(i);
					break;
				}
			}

			dataDevolucao = new Date(c.getLong(c
					.getColumnIndexOrThrow(EmprestimoDbAdapter.COLUNA_DATA_DEVOLUCAO)));

			atualizarData();
			mDbHelper.close();

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(EmprestimoDbAdapter.COLUNA_ID, mRowId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		if (validarCampos()) {
			String item = etItem.getText().toString();
			String descricao = etDescricao.getText().toString();
			Date data = dataDevolucao;
			data.setSeconds(0);

			int status = 0;
			if (rbEmprestar.isChecked()) {
				status = EmprestimoDbAdapter.STAUTS_EMPRESTAR;
			} else if (rbPegarEmprestado.isChecked()) {
				status = EmprestimoDbAdapter.STAUTS_PEGAR_EMPRESTADO;
			}

			long idContato = txtAutoNome.getSelectedItemId();

			if (mDbHelper == null) {
				mDbHelper = new EmprestimoDbAdapter(this);
			}
			mDbHelper.open();

			int alarme = EmprestimoDbAdapter.DESATIVAR_ALARME;
			if (cbAlarme.isChecked()) {
				alarme = EmprestimoDbAdapter.ATIVAR_ALARME;
			}
			if (mRowId == null) {
				long id = mDbHelper.inserirEmprestimo(item, descricao, data, status, alarme,
						idContato);
				if (id > 0) {
					mRowId = id;
				}
			} else {
				mDbHelper.atualizarEmprestimo(mRowId, item, descricao, data, status, alarme,
						idContato);
				;
			}

			Intent intent = new Intent(EditarEmprestimo.this, Alarme.class);
			intent.putExtra(EmprestimoDbAdapter.COLUNA_ID, mRowId);
			PendingIntent sender = PendingIntent.getBroadcast(EditarEmprestimo.this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, data.getTime(), sender);


			mDbHelper.close();
		}
	}

	private void atualizarData() {

		SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
		etDataDevolucao.setText(simpleFormat.format(dataDevolucao));
		simpleFormat = new SimpleDateFormat("HH:mm");
		etHoraDevolucao.setText(simpleFormat.format(dataDevolucao));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID_DATE:

			return new DatePickerDialog(this, dataListener, dataDevolucao.getYear() + 1900,
					dataDevolucao.getMonth(), dataDevolucao.getDate());
		case DATE_DIALOG_ID_TIME:
			return new TimePickerDialog(EditarEmprestimo.this, horaListener,
					dataDevolucao.getHours(), dataDevolucao.getMinutes(), true);
		}
		return null;
	}

}
