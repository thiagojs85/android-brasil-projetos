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
import java.util.Locale;

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.ContatosController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Categoria;
import org.android.brasil.projetos.model.Emprestimo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

public class EditarEmprestimoFragment extends SherlockFragment {

	private EditText etItem;
	private EditText etDescricao;
	private TextView tvContato;

	boolean notificacao = false;

	private long idEmprestimo;
	private long idCategoria;

	private boolean firstload;

	private Spinner spNomes;
	private Spinner spCategoria;

	private CheckBox cbAlarme;
	private CheckBox cbContato;

	private RadioButton rbEmprestar;
	private RadioButton rbPegarEmprestado;

	private Button btnConfirm;

	private Calendar dataDevolucao;
	private EditText etDataDevolucao;
	private EditText etHoraDevolucao;
	private EditText etContato;
	private CategoriaController cc;
	private EmprestimoController ec;
	private ContatosController ctc;

	@Override
	public void onStop() {
		super.onStop();
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
			return new TimePickerDialog(getActivity(), this, hour, minute, true);
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dataDevolucao.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dataDevolucao.set(Calendar.MINUTE, minute);
			atualizarData();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editar_emprestimo, container,
				false);
		etItem = (EditText) view.findViewById(R.id.et_item);
		etDescricao = (EditText) view.findViewById(R.id.et_descricao);
		etDataDevolucao = (EditText) view.findViewById(R.id.et_data);
		etHoraDevolucao = (EditText) view.findViewById(R.id.et_hora);
		etContato = (EditText) view.findViewById(R.id.et_contato);

		spNomes = (Spinner) view.findViewById(R.id.sp_auto_nome);
		spCategoria = (Spinner) view.findViewById(R.id.sp_categoria);

		cbAlarme = (CheckBox) view.findViewById(R.id.cb_alarme);
		cbContato = (CheckBox) view.findViewById(R.id.cb_contato);

		rbEmprestar = (RadioButton) view.findViewById(R.id.rb_emprestar);
		rbPegarEmprestado = (RadioButton) view
				.findViewById(R.id.rb_pegar_emprestado);

		tvContato = (TextView) view.findViewById(R.id.tv_contato);

		btnConfirm = (Button) view.findViewById(R.id.btn_confirmar);

		etContato.setEnabled(false);
		etContato.setVisibility(View.GONE);

		if (cc == null) {
			cc = new CategoriaController(this.getActivity());
		}
		if (ec == null) {
			ec = new EmprestimoController(this.getActivity());
		}
		if (ctc == null || ctc.isClosed()) {
			ctc = new ContatosController(this.getActivity());
		}
		firstload = true;
		spNomes.setAdapter(ctc.getContatoAdapter());

		spCategoria
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (firstload) {
							if (idCategoria > 0) {
								carregarSpinnerCategoria(idCategoria);
							}
						}
						firstload = false;
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
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
				DialogFragment dialogFragment = new DatePickerFragment();
				dialogFragment.show(getActivity().getSupportFragmentManager(),
						"DatePicker");
			}
		});

		etHoraDevolucao.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				DialogFragment dialogFragment = new TimePickerFragment();
				dialogFragment.show(getActivity().getSupportFragmentManager(),
						"TimePicker");
			}
		});

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (validarCampos()) {
					saveData();
					EmprestimoFragment emprestimoFragment = (EmprestimoFragment) getActivity()
							.getSupportFragmentManager().findFragmentById(
									R.id.ListItensFragment);
					if (emprestimoFragment == null) {
						getActivity().getSupportFragmentManager()
								.popBackStack();
					} else {
						limparCamposTela();
					}
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

		dataDevolucao = Calendar.getInstance();
		atualizarData();

		idEmprestimo = -1L;
		if (savedInstanceState != null) {
			idEmprestimo = savedInstanceState.getLong(
					EmprestimoDAO.TABELA_EMPRESTIMOS, -1);
			Emprestimo emp = (Emprestimo) savedInstanceState
					.getSerializable("OBJ");
			if (emp != null) {
				populateFields(emp);
			}

		} else {
			Bundle args = getArguments();
			if (args != null) {
				idEmprestimo = args.getLong(EmprestimoDAO.TABELA_EMPRESTIMOS,
						-1);
			}
			updateItemView(idEmprestimo);
		}
		// Inflate the layout for this fragment
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void updateItemView(long id) {

		idEmprestimo = id;
		if (id > -1) {
			populateFields(ec.getEmprestimo(id));
		} else {
			populateFields(null);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(EmprestimoDAO.TABELA_EMPRESTIMOS, idEmprestimo);
		outState.putSerializable("OBJ", getEmprestimoFromInterface());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null && args.getBoolean(Alarme.CANCEL_NOTIFICATION, false)) {
			int idEmp = (int) args
					.getLong(EmprestimoDAO.TABELA_EMPRESTIMOS, -1);
			if (idEmp > -1) {
				NotificationManager nm = (NotificationManager) getActivity()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancel(getActivity().getText(R.string.app_name) + "", idEmp);
			} else {
				Toast.makeText(getActivity(),
						"ID invalido para cancelar notificação",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		populateFields(null);
	}

	private void carregarSpinnerCategoria(long idSelected) {
		SpinnerAdapter adapterCategorias = spCategoria.getAdapter();

		for (int i = 0; i < adapterCategorias.getCount(); ++i) {
			if (adapterCategorias.getItemId(i) == idSelected) {
				spCategoria.setSelection(i, true);
				break;
			}
		}
		Log.w("count", adapterCategorias.getCount() + "");

	}

	private boolean validarCampos() {
		String item = etItem.getText().toString();
		Categoria cat = CategoriaDAO.deCursorParaCategoria((Cursor) spCategoria
				.getSelectedItem());
		if (cat.getId() == CategoriaDAO.TODAS_ID) {
			Toast.makeText(getActivity(), "Escolha uma categoria!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		if (item.trim().equals("")) {
			Toast.makeText(EditarEmprestimoFragment.this.getActivity(),
					R.string.nome_do_item_deve_ser_informado, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		if (cbAlarme.isChecked()) {
			if (dataDevolucao.getTime().getTime() < Calendar.getInstance()
					.getTime().getTime()) {
				Toast.makeText(EditarEmprestimoFragment.this.getActivity(),
						R.string.data_hora_devem_ser_informados,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	private void populateFields(Emprestimo emprestimo) {
		if (emprestimo != null) {
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

			String contato = emprestimo.getNomeContato();
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
			idCategoria = emprestimo.getIdCategoria();
			firstload = true;
			if (spCategoria.getAdapter() == null
					|| spCategoria.getAdapter().getCount() == 0) {
				spCategoria.setAdapter(cc.getAdapter(CategoriaDAO.TODAS_ID));
			}
			atualizarData();
		} else {
			idCategoria = -1;
			if (spCategoria.getAdapter() == null
					|| spCategoria.getAdapter().getCount() == 0) {
				spCategoria.setAdapter(cc.getAdapter(CategoriaDAO.TODAS_ID));
			}
			limparCamposTela();
		}
	}

	private void limparCamposTela() {
		etItem.setText("");
		etDescricao.setText("");
		spNomes.setSelection(0);
		spCategoria.setSelection(0, true);
		cbAlarme.setChecked(false);
		cbContato.setChecked(false);
		etContato.setVisibility(View.GONE);
		dataDevolucao = Calendar.getInstance();
		idEmprestimo = -1L;
		atualizarData();
		etContato.setText("");
		rbEmprestar.setSelected(true);

	}

	/*
	 * @Override public void onRestoreInstanceState(Bundle savedInstanceState) {
	 * super.onRestoreInstanceState(savedInstanceState); Emprestimo emp =
	 * (Emprestimo) savedInstanceState
	 * .getSerializable(EmprestimoDAO.TABELA_EMPRESTIMOS); idEmprestimo = (Long)
	 * savedInstanceState .getSerializable(EmprestimoDAO.TABELA_EMPRESTIMOS);
	 * populateFields(emp); }
	 */

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private Emprestimo getEmprestimoFromInterface() {
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
			emp.setNomeContato(etContato.getText().toString());
		} else {
			emp.setNomeContato(null);
		}

		if (idEmprestimo < 0) {
			emp.setIdEmprestimo(-1);
		} else {
			emp.setIdEmprestimo(idEmprestimo);

		}
		return emp;
	}

	private void saveData() {
		if (validarCampos()) {
			Emprestimo emp = getEmprestimoFromInterface();
			idEmprestimo = ec.inserirOuAtualizar(emp);
			if (cbAlarme.isChecked()) {
				Intent intent = new Intent(
						EditarEmprestimoFragment.this.getActivity(),
						Alarme.class);
				intent.putExtra(EmprestimoDAO.TABELA_EMPRESTIMOS, idEmprestimo);
				PendingIntent sender = PendingIntent.getBroadcast(
						EditarEmprestimoFragment.this.getActivity(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				getActivity();
				AlarmManager am = (AlarmManager) getActivity()
						.getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, emp.getData().getTime(), sender);

			}

		}
	}

	private void atualizarData() {

		SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy",
				Locale.getDefault());
		etDataDevolucao.setText(simpleFormat.format(dataDevolucao.getTime()));

		simpleFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
		etHoraDevolucao.setText(simpleFormat.format(dataDevolucao.getTime()));
	}

	/*
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { ec.devolverOuReceber(idEmprestimo); item.setEnabled(false); return
	 * super.onMenuItemSelected(featureId, item); }
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu);
	 * 
	 * if (status == TipoStatus.EMPRESTADO.getId()) { menu.add(0, Menu.FIRST, 0,
	 * R.string.menu_devolver).setIcon( R.drawable.devolver); status =
	 * TipoStatus.DEVOLVIDO.getId(); menu.add(0, Menu.FIRST + 1, 0, "Estornar")
	 * .setIcon(R.drawable.devolver).setEnabled(false); } return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		item.setEnabled(true);
		return super.onOptionsItemSelected(item);
	}

}
