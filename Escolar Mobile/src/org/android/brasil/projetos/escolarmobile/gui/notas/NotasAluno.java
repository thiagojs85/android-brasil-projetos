package org.android.brasil.projetos.escolarmobile.gui.notas;

import java.util.List;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.AlunoVO;
import org.android.brasil.projetos.escolarmobile.dao.MateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.NotaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaMateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class NotasAluno extends ListActivity {

	private Cursor c;
	private TextView nomeAluno, matriculaTurma;
	private Spinner periodos;
	private long idTurma;
	private long idMateria;
	private long idAluno;
	
	private TurmaVO turmaVO;
	private AlunoVO alunoVO;
	private List<NotaVO> notasVO;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notas_aluno);

		this.setTitle("Notas Auno");

		nomeAluno = (TextView) findViewById(R.id.tv_nome_aluno);
		matriculaTurma = (TextView) findViewById(R.id.tv_matricula_turma);
		periodos = (Spinner) findViewById(R.id.sp_periodos);

		idMateria = this.getIntent().getLongExtra(MateriaVO.TABLE_MATERIA, 0);
		idTurma = this.getIntent().getLongExtra(TurmaVO.TABLE_TURMA, 0);
		idAluno = this.getIntent().getLongExtra(AlunoVO.TABLE_ALUNO, 0);
		updateItens();
	}

	private void updateItens() {
		AlunoVO.open(NotasAluno.this);
		alunoVO = AlunoVO.consultarAluno(idAluno);
		AlunoVO.close();
		nomeAluno.setText(alunoVO.getNome());
		
		TurmaVO.open(NotasAluno.this);
		turmaVO = TurmaVO.consultarTurmaPorId(idTurma);
		TurmaVO.close();
		
		matriculaTurma.setText("RM: " + alunoVO.getRegistro() + ", Turma: "
				+ turmaVO.getNome());
		
		TurmaMateriaVO.open(NotasAluno.this);
		long idTurmaMateria = TurmaMateriaVO.getId(idTurma, idMateria);
		TurmaMateriaVO.close();
		
		NotaVO.open(NotasAluno.this);
		notasVO = NotaVO.consultarNotas(idTurmaMateria);
		NotaVO.close();
		
		// TODO Buscar pelas notas de um aluno de uma matéria de uma dada turma
		// em um dado período.
		// Para relacionar uma matéria com uma turma utilizar a turma_materia
		// Consultar todos os periodos, se o número de perídos igual 0,
		// desabilitar spinner
		// se períodos > 0 colocar o número de cada periodo como itens do
		// spinner

		/*
		 * c = mDbAdapter.consultarTodos(DbAdapter.TABLE_NOTA, new String[] {
		 * DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME });
		 * periodos.setAdapter(new SimpleCursorAdapter(this,
		 * android.R.layout.simple_spinner_item, c, new String[]
		 * {DbAdapter.COLUMN_PERIODO}, new int[] { android.R.id.text1 }));
		 * stopManagingCursor(c); mDbAdapter.close();
		 */

		// TODO apos conseguir buscar do DB, remover o preenchimento estatico
		// utilizado abaixo
		ArrayAdapter<CharSequence> adapterPeriodos = ArrayAdapter
				.createFromResource(this, R.array.periodos,
						android.R.layout.simple_spinner_item);
		adapterPeriodos
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		periodos.setAdapter(adapterPeriodos);
		periodos.setSelection(3);

		setListAdapter(new SimpleCursorAdapter(this, R.layout.notas_aluno_item,
				c, new String[] { AlunoVO.COLUMN_NOME }, new int[] {
						R.id.item_materias, R.id.item_notas, R.id.item_faltas }));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (c != null) {
			c.close();
		}
	}

}
