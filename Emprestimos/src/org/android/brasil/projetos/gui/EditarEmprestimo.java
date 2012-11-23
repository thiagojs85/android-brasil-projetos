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

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.ContatosController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Categoria;
import org.android.brasil.projetos.model.Emprestimo;
import org.android.brasil.projetos.model.TipoCategoria;
import org.android.brasil.projetos.model.TipoStatus;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

@SuppressLint("ValidFragment")
public class EditarEmprestimo extends FragmentActivity {

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

	private Calendar dataDevolucao;
	private EditText etDataDevolucao;
	private EditText etHoraDevolucao;
	private EditText etContato;
	private CategoriaController cc;
	private EmprestimoController ec;
	private ContatosController ctc;
	private int status;

	/*
	 * private static final int DATE_DIALOG_ID_DATE = 0; private static final
	 * int DATE_DIALOG_ID_TIME = 1;
	 */

	@Override
	protected void onStop() {
		super.onStop();
		cc.close();
		ec.close();
		ctc.close();
	}

	private class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), this,
					dataDevolucao.get(Calendar.YEAR),
					dataDevolucao.get(Calendar.MONTH),
					dataDevolucao.get(Calendar.DAY_OF_MONTH));
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Log.w("",year+"  "+monthOfYear+"  "+dayOfMonth);
			dataDevolucao.set(Calendar.YEAR, year);
			dataDevolucao.set(Calendar.MONTH, monthOfYear);
			dataDevolucao.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			atualizarData();
		}
	}

	private class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int hour = dataDevolucao.get(Calendar.HOUR_OF_DAY);
			int minute = dataDevolucao.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hour, minute,
					true);
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dataDevolucao.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dataDevolucao.set(Calendar.MINUTE, minute);
			atualizarData();
		}
	}

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
		if (cc == null || cc.isClosed()) {
			cc = new CategoriaController(this);
		}
		if (ec == null || ec.isClosed()) {
			ec = new EmprestimoController(this);
		}
		if (ctc == null || ctc.isClosed()) {
			ctc = new ContatosController(this);
		}

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.string.app_name);

		spNomes.setAdapter(ctc.getContatoAdapter());

		dataDevolucao = Calendar.getInstance();
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
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getSupportFragmentManager(), "DatePicker");
			}
		});

		etHoraDevolucao.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getSupportFragmentManager(), "TimePicker");
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

		}

		// populateFields();

	}

	private void carregarCategoria() {

		if (cc == null) {
			cc = new CategoriaController(this);
		}

		SimpleCursorAdapter adapterCategorias = cc.getCategoriaAdapter(
				CategoriaController.TODOS, false);

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
					R.string.nome_do_item_deve_ser_informado, Toast.LENGTH_LONG)
					.show();
			return false;
		}

		if (dataDevolucao.getTime().getTime() < Calendar.getInstance()
				.getTime().getTime()) {
			Toast.makeText(EditarEmprestimo.this,
					R.string.data_hora_devem_ser_informados, Toast.LENGTH_SHORT)
					.show();

			return false;
		}

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
				}
				dataDevolucao.setTime(emprestimo.getData());
				status = emprestimo.getStatus();

				atualizarData();

				ad = spCategoria.getAdapter();
				long idCat = emprestimo.getIdCategoria();

				for (int i = 0; i < ad.getCount(); ++i) {

					if (ad.getItemId(i) == idCat) {
						spCategoria.setSelection(i);
						break;
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
			dataDevolucao.set(Calendar.SECOND, 0);
			Date data = dataDevolucao.getTime();

			int status = 0;
			if (rbEmprestar.isChecked()) {
				status = Emprestimo.STATUS_EMPRESTAR;
			} else if (rbPegarEmprestado.isChecked()) {
				status = Emprestimo.STATUS_PEGAR_EMPRESTADO;
			}

			long idContato = spNomes.getSelectedItemId();

			int alarme = Emprestimo.DESATIVAR_ALARME;
			if (cbAlarme.isChecked()) {
				alarme = Emprestimo.ATIVAR_ALARME;

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

			Emprestimo emp = new Emprestimo();
			emp.setItem(item);
			emp.setDescricao(descricao);
			emp.setData(data);
			emp.setStatus(status);
			emp.setAtivarAlarme(alarme);
			emp.setIdContato(idContato);
			emp.setIdCategoria(idCategoria);

			if (cbContato.isChecked()) {
				emp.setContato(etContato.getText().toString());
			} else {
				emp.setContato(null);
			}

			if (idEmprestimo == null) {
				emp.setIdEmprestimo(0);
			} else {
				emp.setIdEmprestimo(idEmprestimo);

			}

			ec.inserirOuAtualizar(emp);
		}
	}

	private void atualizarData() {

		SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
		etDataDevolucao.setText(simpleFormat.format(dataDevolucao.getTime()));

		simpleFormat = new SimpleDateFormat("HH:mm");
		etHoraDevolucao.setText(simpleFormat.format(dataDevolucao.getTime()));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		devolverReceber();
		item.setEnabled(false);

		return super.onMenuItemSelected(featureId, item);
	}

	private void devolverReceber() {
		ec.devolverOuReceber(idEmprestimo);
		// Toast.makeText(EditarEmprestimo.this, "Sucesso",
		// Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if (status == TipoStatus.EMPRESTADO.getId()) {
			menu.add(0, Menu.FIRST, 0, R.string.menu_devolver).setIcon(
					R.drawable.devolver);
			status = TipoStatus.DEVOLVIDO.getId();
			menu.add(0, Menu.FIRST + 1, 0, "Estornar")
					.setIcon(R.drawable.devolver).setEnabled(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		item.setEnabled(true);
		return super.onOptionsItemSelected(item);
	}

}
