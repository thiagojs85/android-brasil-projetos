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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.ContatosController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.dao.util.Util;
import org.android.brasil.projetos.model.Categoria;
import org.android.brasil.projetos.model.Emprestimo;
import org.android.brasil.projetos.model.TipoCategoria;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
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
	private Long idEmprestimo;
	private TextView tvContato;
	boolean notificacao = false;

	private Spinner spNomes;
	private Spinner spCategoria;

	private CheckBox cbAlarme;
	private CheckBox cbContato;

	private RadioButton rbEmprestar;
	private RadioButton rbPegarEmprestado;

	private Date dataDevolucao;
	private EditText etDataDevolucao;
	private EditText etHoraDevolucao;
	private EditText etContato;
	private CategoriaController cc;
	private EmprestimoController ec;
	private ContatosController ctc;

	private static final int DATE_DIALOG_ID_DATE = 0;
	private static final int DATE_DIALOG_ID_TIME = 1;

	@Override
	protected void onPause() {
		super.onPause();
		cc.close();
		ec.close();
		// TODO: Tem que fechar os cursores..cade o método close desse cara?
		// ctc.close();
	}

	private DatePickerDialog.OnDateSetListener dataListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dataDevolucao.setYear(year - 1900);
			dataDevolucao.setMonth(monthOfYear);
			dataDevolucao.setDate(dayOfMonth);
			atualizarData();
		}
	};
	private OnTimeSetListener horaListener = new OnTimeSetListener() {

		public void onTimeSet(TimePicker arg0, int hora, int minuto) {
			dataDevolucao.setHours(hora);
			dataDevolucao.setMinutes(minuto);
			atualizarData();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editar_emprestimo);
		setTitle(R.string.editar_emprestimo);

		etItem = (EditText) findViewById(R.id.item);
		etDescricao = (EditText) findViewById(R.id.descricao);
		etDataDevolucao = (EditText) findViewById(R.id.data);
		etHoraDevolucao = (EditText) findViewById(R.id.hora);
		spNomes = (Spinner) findViewById(R.id.txt_auto_nome);
		cbAlarme = (CheckBox) findViewById(R.id.cb_alarme);
		rbEmprestar = (RadioButton) findViewById(R.id.rb_emprestar);
		rbPegarEmprestado = (RadioButton) findViewById(R.id.rb_pegar_emprestado);
		tvContato = (TextView) findViewById(R.id.tv_contato);
		Button confirmButton = (Button) findViewById(R.id.confirmar);
		spCategoria = (Spinner) findViewById(R.id.sp_categoria);
		cbContato = (CheckBox) findViewById(R.id.cbContato);
		etContato = (EditText) findViewById(R.id.etContato);

		etContato.setEnabled(false);
		etContato.setVisibility(View.GONE);

		cc = new CategoriaController(this);
		ec = new EmprestimoController(this);
		ctc = new ContatosController(this);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.string.app_name);

		spNomes.setAdapter(ctc.getContatoAdapter());

		dataDevolucao = Calendar.getInstance().getTime();
		atualizarData();

		rbEmprestar.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					tvContato.setText(R.string.contato);
				}

			}
		});

		rbPegarEmprestado
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						if (arg1) {
							tvContato.setText(R.string.pegar_emprestado_de);
						}

					}
				});

		etDataDevolucao.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID_DATE);
			}
		});

		etHoraDevolucao.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID_TIME);
			}
		});

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (validarCampos()) {
					saveState();
					// Log.w("EditarEmprestimo", "Fim da edição");
					setResult(RESULT_OK);
					finish();
				}
			}

		});

		cbContato.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					spNomes.setEnabled(false);
					etContato.setEnabled(true);
					etContato.setVisibility(View.VISIBLE);
					spNomes.setEnabled(false);
					spNomes.setVisibility(View.GONE);
				} else {
					spNomes.setEnabled(true);
					etContato.setEnabled(false);
					etContato.setVisibility(View.GONE);
					spNomes.setEnabled(true);
					spNomes.setVisibility(View.VISIBLE);
				}

			}
		});

		idEmprestimo = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(EmprestimoDAO.COLUNA_ID_EMPRESTIMO);

		if (idEmprestimo == null) {
			Bundle extras = getIntent().getExtras();
			idEmprestimo = extras != null ? extras
					.getLong(EmprestimoDAO.COLUNA_ID_EMPRESTIMO) : null;

			// TODO: Desativar o alarme em Alarme.java, crie um
			// EmprestimosController lá.
			// Assim tentamos misturar menos a logica..
			notificacao = extras != null ? extras
					.getBoolean(EmprestimoDAO.COLUNA_ATIVAR_ALARME) : null;

			if (notificacao) {
				ec.atualizaNotificacao(idEmprestimo);
			}

			if (idEmprestimo == 0) {
				idEmprestimo = null;
			}
		}

		populateFields();

	}

	private void carregarCategoria() {

		if (cc == null) {
			cc = new CategoriaController(this);
		}

		SimpleCursorAdapter adapterCategorias = cc
				.getCategoriaAdapter(CategoriaController.TODOS);

		if (adapterCategorias != null && adapterCategorias.getCount() > 0) {
			spCategoria.setEnabled(true);
		} else {
			spCategoria.setEnabled(false);
		}

		spCategoria.setAdapter(adapterCategorias);

		spCategoria.setSelection(1, true);

		spCategoria
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						if (id == TipoCategoria.OUTRA.getId()) {
							Intent i = new Intent(EditarEmprestimo.this,
									CategoriaUI.class);
							startActivityForResult(i, 0);
						}
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	private boolean validarCampos() {
		String item = etItem.getText().toString();

		Categoria cat = cc.getCategoria(spCategoria.getSelectedItemId());

		if (cat.getNomeCategoria().equals(CategoriaDAO.OUTRA)) {
			return false;
		}

		if (item.trim().equals("")) {
			Toast.makeText(EditarEmprestimo.this,
					"O nome do item deve ser informado!", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		// TODO: O que o trecho abaixo está fazendo? Não entendi o motivo
		// disso...
		// Temos um método que atualiza o EditText de Data e Hora..acho que isso
		// não deveria estar aqui..
		// INICIO
		SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
		etDataDevolucao.setText(simpleFormat.format(dataDevolucao));

		String data = etDataDevolucao.getText().toString();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date d = null;
		Date d1 = Calendar.getInstance().getTime();
		d1.setHours(0);
		d1.setMinutes(0);
		d1.setSeconds(0);

		try {
			d = formatter.parse(data);// catch exception
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO: Para verificar qual Date é mais novo você pode usar o método
		// Date.getTime()
		// ..que o maior será o objeto Date com data mais "no futuro"
		// Ou seja, para verificar se a data de devolução não está setada para
		// um valor no passado:
		if (dataDevolucao.getTime() < Calendar.getInstance().getTime()
				.getTime()) {
			// Valor inválido! Mudar a data!
		}

		if (Util.dataDiff(d, d1) > 0) {

			Calendar c = Calendar.getInstance();

			int ano = c.get(Calendar.YEAR);
			int mes = c.get(Calendar.MONTH) + 1;
			int dia = c.get(Calendar.DAY_OF_MONTH);

			Toast.makeText(
					EditarEmprestimo.this,
					"Data deve ser maior ou igual a " + dia + "/" + mes + "/"
							+ ano, Toast.LENGTH_SHORT).show();
			return false;

		}
		// FIM

		return true;
	}

	private void populateFields() {

		carregarCategoria();

		if (idEmprestimo != null) {

			if (ec.existe(idEmprestimo)) {
				Emprestimo emprestimo = ec.getEmprestimo(idEmprestimo);

				long status = emprestimo.getStatus();
				if (status == Emprestimo.STATUS_EMPRESTAR) {
					rbPegarEmprestado.setChecked(false);
					rbEmprestar.setChecked(true);

				}

				if (status == Emprestimo.STATUS_PEGAR_EMPRESTADO) {
					rbEmprestar.setChecked(false);
					rbPegarEmprestado.setChecked(true);
				}

				long alarme = emprestimo.getAtivarAlarme();
				if (alarme == Emprestimo.ATIVAR_ALARME) {
					cbAlarme.setChecked(true);
				}

				if (alarme == Emprestimo.DESATIVAR_ALARME) {
					cbAlarme.setChecked(false);
				}

				etItem.setText(emprestimo.getItem());

				etDescricao.setText(emprestimo.getDescricao());

				String contato = emprestimo.getContato();
				Adapter ad = spNomes.getAdapter();

				if (contato != null) {

					etContato.setEnabled(true);
					etContato.setVisibility(View.VISIBLE);
					spNomes.setEnabled(false);

					etContato.setText(contato);
					cbContato.setChecked(true);

				} else {

					etContato.setEnabled(false);
					etContato.setVisibility(View.GONE);
					spNomes.setEnabled(true);
					cbContato.setChecked(false);

					long id = emprestimo.getIdContato();

					for (int i = 0; i < ad.getCount(); ++i) {

						if (ad.getItemId(i) == id) {
							spNomes.setSelection(i);
							break;
						}
					}

					dataDevolucao = emprestimo.getData();

					atualizarData();

					ad = spCategoria.getAdapter();
					long idCat = emprestimo.getIdCategoria();

					for (int i = 0; i < ad.getCount(); ++i) {

						if (ad.getItemId(i) == idCat) {
							spCategoria.setSelection(i);
							break;
						}
					}
				}
			} else {
				limparCamposTela();
			}
		}
	}

	private void limparCamposTela() {
		etItem.setText("");
		etDescricao.setText("");
		spNomes.setSelection(0);
		spCategoria.setSelection(1, true);
		cbAlarme.setChecked(false);
		cbContato.setChecked(false);
		etContato.setVisibility(View.GONE);
		atualizarData();
		etContato.setText("");
		rbEmprestar.setSelected(true);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(EmprestimoDAO.COLUNA_ID_EMPRESTIMO,
				idEmprestimo);
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
				status = Emprestimo.STATUS_EMPRESTAR;
			} else if (rbPegarEmprestado.isChecked()) {
				status = Emprestimo.STATUS_PEGAR_EMPRESTADO;
			}

			int alarme = Emprestimo.DESATIVAR_ALARME;
			if (cbAlarme.isChecked()) {
				alarme = Emprestimo.ATIVAR_ALARME;

				//TODO: Que tal criar um controller para Alarme e passar esse trecho para ele?				
				//TODO: POSSIVEL BUG: Se vc estiver inserindo um emprestimo, idEmprestimo vai estar com que valor?
				//acho que null neh? Isso tem que ser feito depois que o emprestimo for inserido no banco.
				Intent intent = new Intent(EditarEmprestimo.this, Alarme.class);
				intent.putExtra(EmprestimoDAO.COLUNA_ID_EMPRESTIMO,
						idEmprestimo);
				PendingIntent sender = PendingIntent.getBroadcast(
						EditarEmprestimo.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, data.getTime(), sender);

			}

			long idCategoria = spCategoria.getSelectedItemId();
			long idContato = 0L;

			Emprestimo emp = new Emprestimo();
			emp.setItem(item);
			emp.setDescricao(descricao);
			emp.setData(data);
			emp.setStatus(status);
			emp.setAtivarAlarme(alarme);

			emp.setIdCategoria(idCategoria);

			if (cbContato.isChecked()) {
				emp.setContato(etContato.getText().toString());
				emp.setIdContato(0);
			} else {
				emp.setContato(null);
				idContato = spNomes.getSelectedItemId();
				emp.setIdContato(idContato);
			}

			if (idEmprestimo == null) {

				long id = ec.inserirEmprestimo(emp);
				if (id > 0) {
					idEmprestimo = id;
				}
			} else {
				emp.setIdEmprestimo(idEmprestimo);
				ec.atualizarEmprestimo(emp);

			}
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

			return new DatePickerDialog(this, dataListener,
					dataDevolucao.getYear() + 1900, dataDevolucao.getMonth(),
					dataDevolucao.getDate());
		case DATE_DIALOG_ID_TIME:
			return new TimePickerDialog(EditarEmprestimo.this, horaListener,
					dataDevolucao.getHours(), dataDevolucao.getMinutes(), true);
		}
		return null;
	}

}
