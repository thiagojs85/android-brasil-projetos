package org.android.brasil.projetos.escolarmobile.gui.aluno;

import java.sql.Date;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.base.TelaListaBasica;
import org.android.brasil.projetos.escolarmobile.dao.AlunoVO;
import org.android.brasil.projetos.escolarmobile.dao.MateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.PresencaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;
import org.android.brasil.projetos.escolarmobile.gui.notas.NotasAluno;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class ListaAlunos extends TelaListaBasica {

	private static final int DIALOG_DELETAR = 0;
	private long idAluno;
	private long idTurma;
	private long idMateria;
	private DatePicker datapicker;
	private boolean isMultiItensSelectable;
	private PresencaVO presencaVO;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//Os ids precisam ser setados antes de chamar o super
		isMultiItensSelectable = this.getIntent().getBooleanExtra("chamada",
				false);
		idMateria = this.getIntent().getLongExtra(MateriaVO.TABLE_MATERIA, 0);
		idTurma = this.getIntent().getLongExtra(TurmaVO.TABLE_TURMA, 0);

		super.onCreate(savedInstanceState);
		Button ibt = (Button) findViewById(R.id.add);

		if (isMultiItensSelectable()) {
			ibt.setText(R.string.lancar_chamada);
			Toast.makeText(ListaAlunos.this, R.string.desmarcar_os_faltosos,
					Toast.LENGTH_LONG).show();

		} else {
			ibt.setText(R.string.adicionar_aluno);
			// Quando o usuário clica em um aluno da lista, exibe as notas do aluno.
			this.getListView().setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> context, View view,
						int position, long id) {
					Toast.makeText(ListaAlunos.this, "Ei seu preguiçoso, bora fazer isso aqui funcionar!",
							Toast.LENGTH_LONG).show();		
						Intent i = new Intent(ListaAlunos.this, NotasAluno.class);
						i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
						i.putExtra(MateriaVO.TABLE_MATERIA, idMateria);
						i.putExtra(AlunoVO.TABLE_ALUNO, id);
						startActivity(i);
					
				}
			});

		}


	}

	@Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar
		// true
		return isMultiItensSelectable;
	}

	@Override
	protected String[] getItensOfLine() {
		return new String[] { AlunoVO.COLUMN_NOME, AlunoVO.COLUMN_REGISTRO };

	}

	@Override
	public void onClick(View v) {

		if (isMultiItensSelectable()) {
			dialogFaltas();
		} else {
			Intent i = new Intent(this, CadastroAluno.class);
			i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
			startActivityForResult(i, ADD_ID);
		}

	}

	@Override
	public Cursor getItensCursor() {

		/*
		 * [Júlio]Comentado porque não leva em conta que uma mesma matéria pode
		 * ser ministrada para mais de uma turma se for uma matéria padrão!
		 * Assim, um aluno está fazendo portugues em 1A e em 2B, por exemplo. //
		 * Se não houver id nos Extras, mostre todas as matérias existentes.
		 * if(idMateria < 1) { return
		 * mDbAdapter.consultarTodos(DbAdapter.TABLE_ALUNO, new
		 * String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		 * 
		 * } else { return mDbAdapter.acessarAlunosPorMaterias(idMateria); }
		 */
		if (idTurma <= 0) {
			return null;

		} else {
			// TODO Melhorar a seleção de alunos utilizando também as matérias
			// que ele faz..
			// usar idMateria no select, isso vai dar mais liberdade no futuro
			AlunoVO.open(ListaAlunos.this);
			Cursor c = AlunoVO.acessarAlunosPorTurma(idTurma);
			AlunoVO.close();
			return c;
		}
	}

	@Override
	public void setActionOnEditItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		// Repassa o id do aluno selecionado para edição.
		Intent i = new Intent(this, CadastroAluno.class);
		i.putExtra(AlunoVO.TABLE_ALUNO, new Long(info.id));
		startActivityForResult(i, EDIT_ID);
	}

	@Override
	public void setActionOnDeleteItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		idAluno = (new Long(info.id).longValue());
		showDialog(DIALOG_DELETAR);
	}

	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id
	 *            identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DELETAR:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_delete).setCancelable(false);
			builder.setPositiveButton(R.string.sim,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							AlunoVO.open(ListaAlunos.this);
							AlunoVO.removerAluno(idAluno);
							AlunoVO.close();
							ListaAlunos.super.updateItens();
						}
					});
			builder.setNegativeButton(R.string.nao,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			return builder.create();
		default:
			return null;
		}
	}

	private void dialogFaltas() {

		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.dialog_lancar_faltas);
		dialog.setTitle(R.string.lancar_faltas);

		datapicker = (DatePicker) dialog.findViewById(R.id.datePicker);

		final EditText numeroFaltas = (EditText) dialog
				.findViewById(R.id.eNumeroFaltas);

		final Button lancarFaltas = (Button) dialog
				.findViewById(R.id.blancarFaltas);
		final Button cancelar = (Button) dialog.findViewById(R.id.bCancelar);

		lancarFaltas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Date date = new Date(datapicker.getYear() - 1900, datapicker
						.getMonth(), datapicker.getDayOfMonth());
				
				long[] ids = ListaAlunos.this.getListView().getCheckItemIds();
				
				presencaVO = new PresencaVO(ListaAlunos.this);
				presencaVO.setFaltas(Integer.parseInt(numeroFaltas.getText()
						.toString()));
				presencaVO.setData(date);
				PresencaVO.open(ListaAlunos.this);
				for (long id : ids) {
					presencaVO.setIdAluno(id);
					PresencaVO.inserirPresenca(presencaVO);
				}
				PresencaVO.close();

				dialog.dismiss();
				Toast.makeText(ListaAlunos.this, R.string.lcto_faltas_sucesso,
						Toast.LENGTH_LONG).show();
			}
		});

		cancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	@Override
	public int setTitle() {
		if (isMultiItensSelectable()) {
			return R.string.titulo_chamada;
		} else {
			return R.string.titulo_lista_alunos;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (presencaVO != null) {
			PresencaVO.close();
		}

	}

}
