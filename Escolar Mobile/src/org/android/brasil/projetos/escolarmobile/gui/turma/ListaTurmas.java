package org.android.brasil.projetos.escolarmobile.gui.turma;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.base.TelaListaBasica;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;
import org.android.brasil.projetos.escolarmobile.gui.aluno.ListaAlunos;
import org.android.brasil.projetos.escolarmobile.gui.materia.ListaMaterias;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class ListaTurmas extends TelaListaBasica {

	protected static final int VISUALIZAR_ALUNOS_ID = DELETE_ID + 1;

	private long idTurma;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button ibt = (Button) findViewById(R.id.add);
		ibt.setText(R.string.adicionar_turma);

		// Quando o usuário clica em uma das turmas da lista, exibe a lista de
		// matérias daquela turma.
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> context, View view,
					int position, long id) {
				Intent i = new Intent(ListaTurmas.this, ListaMaterias.class);
				i.putExtra(TurmaVO.TABLE_TURMA, id);
				startActivity(i);
			}
		});
	}

	@Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar
		// true
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		menu.add(Menu.NONE, VISUALIZAR_ALUNOS_ID, 0, R.string.visualizar_alunos);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case VISUALIZAR_ALUNOS_ID:
			setActionOnViewAlunos(item);
			break;
		}
		return super.onContextItemSelected(item);

	}

	@Override
	public void onClick(View v) {
		/*
		 * Inicia a tela de cadastro de turma
		 */
		Intent i = new Intent(this, CadastroTurmas.class);
		startActivityForResult(i, ADD_ID);
	}

	@Override
	public Cursor getItensCursor() {
		/**
		 * Preenche a lista de turmas com base nos dados do BD
		 */
		// TODO Preencher com base no professor logado..mas para isso tem que
		// ser implementado o login.
		TurmaVO.open(this);
		Cursor c = TurmaVO.consultarTodos( new String[] {
				TurmaVO.COLUMN_ID, TurmaVO.COLUMN_NOME,
				TurmaVO.COLUMN_DESCRICAO });
		TurmaVO.close();
		return c;
	}

	@Override
	protected String[] getItensOfLine() {
		return new String[] { TurmaVO.COLUMN_NOME, TurmaVO.COLUMN_DESCRICAO };

	}

	@Override
	public void setActionOnEditItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		// Repassa o id da linha selecionada para a tela de edição
		Intent i = new Intent(this, CadastroTurmas.class);
		i.putExtra(TurmaVO.TABLE_TURMA, info.id);
		startActivityForResult(i, EDIT_ID);
	}

	@Override
	public void setActionOnDeleteItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		idTurma = (new Long(info.id).longValue());
		showDialog(DELETE_ID);
	}

	public void setActionOnViewAlunos(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		// Repassa o id da linha selecionada para a tela de edição
		Intent i = new Intent(this, ListaAlunos.class);
		i.putExtra(TurmaVO.TABLE_TURMA, new Long(info.id));
		startActivityForResult(i, VISUALIZAR_ALUNOS_ID);

	}

	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id
	 *            identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DELETE_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_delete).setCancelable(false);
			builder.setPositiveButton(R.string.sim,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

							// TODO Remover as notas de todas as matérias de
							// todos alunos
							// TODO Remover alunos desta turma
							// TODO Remover relacionamentos TurmaMateria e
							// materias que não utilizadas em outras turmas
							TurmaVO.open(ListaTurmas.this);
							TurmaVO.removerTurma(idTurma);
							TurmaVO.close();
							ListaTurmas.super.updateItens();
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

	@Override
	public int setTitle() {
		return R.string.titulo_lista_turmas;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
