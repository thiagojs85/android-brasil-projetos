package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Classe inicial do app.
 * 
 * @author Neto
 * 
 */
public class EscolarMobile extends Activity {
	private DbAdapter mDbAdapter = null;
	private Spinner turmas;
	private Spinner materias;
	private AutoCompleteTextView alunos;
	private Button btVisualizar;
	private Button btChamada;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		alunos = (AutoCompleteTextView) findViewById(R.id.AC_alunos);
		turmas = (Spinner) findViewById(R.id.sp_turmas);
		materias = (Spinner) findViewById(R.id.sp_materias);

		btVisualizar = (Button) findViewById(R.id.visualizar);
		btChamada = (Button) findViewById(R.id.chamada);

		mDbAdapter = new DbAdapter(this).open();

		/*
		 * A parte abaixo não é mais funcional. Ela simplesmente insere valores
		 * hard-coded no Spinner. Na versão atual, os valores são recuperados do
		 * banco de dados. ArrayAdapter<CharSequence> adapterTurmas =
		 * ArrayAdapter.createFromResource( this, R.array.turmas,
		 * android.R.layout.simple_spinner_item);
		 * adapterTurmas.setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * turmas.setAdapter(adapterTurmas); turmas.setSelection(9);
		 * 
		 * ArrayAdapter<CharSequence> adapterMaterias =
		 * ArrayAdapter.createFromResource( this, R.array.materias,
		 * android.R.layout.simple_spinner_item);
		 * adapterMaterias.setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * materias.setAdapter(adapterMaterias); materias.setSelection(6);
		 */

		// Função para inserir os valores possíveis dentro dos spinners na tela.
		populateFields();

		btVisualizar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
				long idTurma = turmas.getSelectedItemId();
				long idMateria = materias.getSelectedItemId();
				// Esse é o ID do DB?
				long idAluno = alunos.getId();
				/*
				  // Tem que criar a classe NotaAluno
				  Intent i = new Intent(EscolarMobile.this, NotaAluno.class);
				  i.putExtra(DbAdapter.COLUMN_ID_ALUNO, idAluno);
				  i.putExtra(DbAdapter.COLUMN_ID_TURMA, idTurma);
				  i.putExtra(DbAdapter.COLUMN_ID_MATERIA, idMateria);
				  startActivity(i);
				  */
				 Toast.makeText(EscolarMobile.this,
						"Botão de Visualizar Aluno não implementado!",
						Toast.LENGTH_LONG).show();
			}
		});

		btChamada.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
				long idTurma = turmas.getSelectedItemId();
				long idMateria = materias.getSelectedItemId();
				Intent i = new Intent(EscolarMobile.this, ListaAlunos.class);
				i.putExtra("chamada", true);
				i.putExtra(DbAdapter.COLUMN_ID_TURMA, idTurma);
				i.putExtra(DbAdapter.COLUMN_ID_MATERIA, idMateria);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		populateFields();
	}

	/**
	 * Este método busca os dados na tabela para inserir os valores aceitáveis
	 * dentro dos spinners. No caso de uma atualização (um insert ou update) na
	 * tabela de turmas ou materias, essa atualização será exibida.
	 */
	private void populateFields() {

		Cursor c = mDbAdapter.consultarTodos(DbAdapter.TABLE_TURMA,
				new String[] { DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME });

		if (c != null) {
			startManagingCursor(c);
			turmas.setEnabled(true);
			materias.setEnabled(true);
			alunos.setEnabled(true);
			btVisualizar.setEnabled(true);
			btChamada.setEnabled(true);
			materias.setVisibility(View.VISIBLE);
		} else {
			turmas.setEnabled(false);
			materias.setEnabled(false);
			alunos.setEnabled(false);
			btVisualizar.setEnabled(false);
			btChamada.setEnabled(false);
		}
		// TODO [Otavio] Segundo a documentação, a classe
		// SimpleCursorAdapter
		// está deprecated.
		// Aconselha-se atualizar segundo a documentação.
		turmas.setAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_dropdown_item, c,
				new String[] { DbAdapter.COLUMN_NOME },
				new int[] { android.R.id.text1 }));
		turmas.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mDbAdapter = new DbAdapter(EscolarMobile.this).open();

				Cursor c = mDbAdapter.acessarMateriasPorTurma(id);

				if (c != null) {
					startManagingCursor(c);
					materias.setEnabled(true);
					alunos.setEnabled(true);
					btVisualizar.setEnabled(true);
					btChamada.setEnabled(true);
					materias.setVisibility(View.VISIBLE);
				} else {
					materias.setEnabled(false);
					alunos.setEnabled(false);
					btVisualizar.setEnabled(false);
					btChamada.setEnabled(false);
				}
				// TODO [Otavio] Segundo a documentação, a classe
				// SimpleCursorAdapter está deprecated.
				// Aconselha-se atualizar segundo a documentação.
				materias.setAdapter(new SimpleCursorAdapter(EscolarMobile.this,
						android.R.layout.simple_spinner_item, c,
						new String[] { DbAdapter.COLUMN_NOME },
						new int[] { android.R.id.text1 }));

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				materias.setVisibility(View.INVISIBLE);
			}
		});
		if(turmas.isEnabled() && materias.isEnabled()){
			alunos.setEnabled(true);
		}else{
			alunos.setEnabled(false);
		}
		//TODO Buscar no DB! Isso aqui está estatico!
		// Tem que usar o ID da turma selecionada para buscar os alunos daquela turma!
		String[] arrayAlunos = getResources().getStringArray(R.array.alunos);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(EscolarMobile.this,
				R.layout.list_item, arrayAlunos);
		alunos.setAdapter(adapter3);
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.turmas, menu);
		inflater.inflate(R.menu.professores, menu);
		inflater.inflate(R.menu.sobre, menu);
	
		// [Otavio] As opções de cadastrar/visualizar materias e alunos estarão
		// disponíveis dentro de turma.
		// inflater.inflate(R.menu.materias, menu);
		// inflater.inflate(R.menu.alunos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;

		switch (item.getItemId()) {
		case R.id.menu_turma:
			i = new Intent(this, ListaTurmas.class);
			startActivityForResult(i, 0);
			return true;
		case R.id.menu_professores:// seleciona o menu de acordo com o nome do
									// xml
			i = new Intent(this, ListaProfessores.class);
			startActivityForResult(i, 0);
			return true;
		case R.id.menu_materias:
			i = new Intent(this, ListaMaterias.class);
			startActivityForResult(i, 0);
			return true;
		case R.id.menu_alunos:
			i = new Intent(this, ListaAlunos.class);
			// i.putExtra("isMultiItensSelectable",true);
			startActivityForResult(i, 0);
			return true;

		case R.id.menu_sobre:
			i = new Intent(this, Sobre.class);
			startActivityForResult(i, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}