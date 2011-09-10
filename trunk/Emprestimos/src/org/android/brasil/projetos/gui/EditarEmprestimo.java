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

import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Emprestimo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
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
					Log.w("EditarEmprestimo","Fim da edição");
					setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(EditarEmprestimo.this, "O nome do item deve ser informado!",
							Toast.LENGTH_LONG).show();
				}
			}

		});

		mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState
				.getSerializable(EmprestimoDAO.COLUNA_ID_EMPRESTIMO);

		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(EmprestimoDAO.COLUNA_ID_EMPRESTIMO) : null;
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

			EmprestimoDAO.open(getApplicationContext());
			Cursor c = EmprestimoDAO.consultarEmprestimo(mRowId);
			startManagingCursor(c);
			long status = c.getLong(c.getColumnIndexOrThrow(EmprestimoDAO.COLUNA_STATUS));
			if (status == Emprestimo.STAUTS_EMPRESTAR) {
				rbPegarEmprestado.setChecked(false);
				rbEmprestar.setChecked(true);

			}

			if (status == Emprestimo.STAUTS_PEGAR_EMPRESTADO) {
				rbEmprestar.setChecked(false);
				rbPegarEmprestado.setChecked(true);
			}

			long alarme = c.getLong(c
					.getColumnIndexOrThrow(EmprestimoDAO.COLUNA_ATIVAR_ALARME));
			if (alarme == Emprestimo.ATIVAR_ALARME) {
				cbAlarme.setChecked(true);
			}

			if (alarme == Emprestimo.DESATIVAR_ALARME) {
				cbAlarme.setChecked(false);
			}

			etItem.setText(c.getString(c.getColumnIndexOrThrow(EmprestimoDAO.COLUNA_ITEM)));

			etDescricao.setText(c.getString(c
					.getColumnIndexOrThrow(EmprestimoDAO.COLUNA_DESCRICAO)));

			Adapter ad = txtAutoNome.getAdapter();
			long id = c.getLong(c.getColumnIndexOrThrow(EmprestimoDAO.COLUNA_ID_CONTATO));
			for (int i = 0; i < ad.getCount(); ++i) {

				if (ad.getItemId(i) == id) {
					txtAutoNome.setSelection(i);
					break;
				}
			}

			dataDevolucao = new Date(c.getLong(c
					.getColumnIndexOrThrow(EmprestimoDAO.COLUNA_DATA_DEVOLUCAO)));

			atualizarData();

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(EmprestimoDAO.COLUNA_ID_EMPRESTIMO, mRowId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
				status = Emprestimo.STAUTS_EMPRESTAR;
			} else if (rbPegarEmprestado.isChecked()) {
				status = Emprestimo.STAUTS_PEGAR_EMPRESTADO;
			}

			long idContato = txtAutoNome.getSelectedItemId();

			
			EmprestimoDAO.open(getApplicationContext());

			int alarme = Emprestimo.DESATIVAR_ALARME;
			if (cbAlarme.isChecked()) {
				alarme = Emprestimo.ATIVAR_ALARME;
			}

			Emprestimo emp = new Emprestimo();
			emp.setItem(item);
			emp.setDescricao(descricao);
			emp.setData(data);
			emp.setStatus(status);
			emp.setAtivarAlarme(alarme);
			emp.setIdContato(idContato);
			//TODO: Remover valor fixo
			emp.setIdCategoria(0);
			if (mRowId == null) {

				long id = EmprestimoDAO.inserirEmprestimo(emp);
				if (id > 0) {
					mRowId = id;
				}
			} else {
				emp.setIdEmprestimo(mRowId);
				EmprestimoDAO.atualizarEmprestimo(emp);
				;
			}
			EmprestimoDAO.close();
			
			Intent intent = new Intent(EditarEmprestimo.this, Alarme.class);
			intent.putExtra(EmprestimoDAO.COLUNA_ID_EMPRESTIMO, mRowId);
			PendingIntent sender = PendingIntent.getBroadcast(EditarEmprestimo.this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, data.getTime(), sender);

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
