package org.android.brasil.projetos.escolarmobile.base;

import java.util.ArrayList;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.AlunoVO;
import org.android.brasil.projetos.escolarmobile.dao.MateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaMateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;
import org.android.brasil.projetos.escolarmobile.gui.aluno.ListaAlunos;
import org.android.brasil.projetos.escolarmobile.gui.professor.ListaProfessores;
import org.android.brasil.projetos.escolarmobile.gui.turma.ListaTurmas;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Classe inicial do app.
 * 
 * @author Neto
 * 
 */
public class EscolarMobile extends Activity {
	private Spinner turmas;
	private Spinner materias;
	private AutoCompleteTextView alunos;
	private Button btVisualizar;
	private Button btChamada;
	private Cursor c;

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
				long idTurma = turmas.getSelectedItemId();
				long idMateria = materias.getSelectedItemId();
				AlunoVO.open(EscolarMobile.this);
				long idAluno = AlunoVO.getIdAlunoPorNomeETurma(alunos.getText().toString(),turmas.getSelectedItemId());
				AlunoVO.close();
				if(idAluno > 0){
					Toast.makeText(EscolarMobile.this,
							"Que tal você pegar essa bagaça para fazer heim?",
							Toast.LENGTH_LONG).show();
					/*Intent i = new Intent(EscolarMobile.this, NotasAluno.class);
					i.putExtra(AlunoVO.TABLE_ALUNO, idAluno);
					i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
					i.putExtra(MateriaVO.TABLE_MATERIA, idMateria);
					startActivity(i);*/
				}else{
					Toast.makeText(EscolarMobile.this,
							R.string.erro_nome_invalido,
							Toast.LENGTH_LONG).show();
				}
			}
		});

		btChamada.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				long idTurma = turmas.getSelectedItemId();
				long idMateria = materias.getSelectedItemId();
				Intent i = new Intent(EscolarMobile.this, ListaAlunos.class);
				i.putExtra("chamada", true);
				i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
				i.putExtra(MateriaVO.TABLE_MATERIA, idMateria);
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

		TurmaVO.open(this);
		c = TurmaVO.consultarTodos(new String[] { TurmaVO.COLUMN_ID,
				TurmaVO.COLUMN_NOME });
		TurmaVO.close();

		if (c != null && c.getCount() > 0) {
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
				new String[] { TurmaVO.COLUMN_NOME },
				new int[] { android.R.id.text1 }));
		stopManagingCursor(c);

		turmas.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TurmaMateriaVO.open(EscolarMobile.this);
				Cursor c = TurmaMateriaVO.acessarMateriasPorTurma(id);
				TurmaMateriaVO.close();

				alunos.setText("");
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
						new String[] { MateriaVO.COLUMN_NOME },
						new int[] { android.R.id.text1 }));
				stopManagingCursor(c);

				//TODO Está funcionando, mas será que não tem como fazer do mesmo modo que foi feito para os spinners
				// usando Cursor? Assim evitaria buscar o ID do nome selecionado depois..
				AlunoVO.open(EscolarMobile.this);
				ArrayList<String> nomesAlunos = AlunoVO.getNomeDeAlunosPorTurma(turmas.getSelectedItemId());
				AlunoVO.close();
				
				if(nomesAlunos != null && !nomesAlunos.isEmpty()){
					alunos.setEnabled(true);
					btVisualizar.setEnabled(true);
					btChamada.setEnabled(true);
				}else{
					alunos.setEnabled(false);
					btVisualizar.setEnabled(false);
					btChamada.setEnabled(false);
					
				}
				ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
						EscolarMobile.this, R.layout.list_item, nomesAlunos);
				alunos.setAdapter(adapter3);

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				materias.setVisibility(View.INVISIBLE);
			}
		});
		if (turmas.isEnabled() && materias.isEnabled()) {
			alunos.setEnabled(true);
		} else {
			alunos.setEnabled(false);
		}

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
			startActivity(i);
			return true;
		case R.id.menu_professores:// seleciona o menu de acordo com o nome do
									// xml
			i = new Intent(this, ListaProfessores.class);
			startActivity(i);
			return true;
		case R.id.menu_sobre:
			i = new Intent(this, Sobre.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (c != null) {
			c.close();
		}
	}

}