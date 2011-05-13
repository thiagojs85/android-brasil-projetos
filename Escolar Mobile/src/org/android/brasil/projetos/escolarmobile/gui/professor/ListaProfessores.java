package org.android.brasil.projetos.escolarmobile.gui.professor;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.base.TelaListaBasica;
import org.android.brasil.projetos.escolarmobile.dao.ProfessorVO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;

/**
 * Classe responsavel por listar os professores cadastrados e adicionar mais se
 * necessário
 * 
 * @author Neto
 */

public class ListaProfessores extends TelaListaBasica {

	private static final int DIALOG_DELETAR = 0;
	private long idProfessor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button ibt = (Button) findViewById(R.id.add);
		ibt.setText(R.string.adicionar_professor);// sobrescreve a string
													// original do xml

	}

	@Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar
		// true
		return false;
	}

	@Override
	public void onClick(View v) {

		Intent i = new Intent(this, CadastroProfessor.class);
		startActivityForResult(i, ADD_ID);
	}

	@Override
	public Cursor getItensCursor() {
		ProfessorVO.open(this);
		Cursor c = ProfessorVO
				.consultarTodos(new String[] {
						ProfessorVO.COLUMN_ID, ProfessorVO.COLUMN_NOME });
		ProfessorVO.close();
		return c;
	}

	@Override
	public void setActionOnEditItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		Intent i = new Intent(this, CadastroProfessor.class);
		// Repassa o id da linha selecionada.
		i.putExtra(ProfessorVO.TABLE_PROFESSOR, info.id);
		startActivityForResult(i, EDIT_ID);
	}

	@Override
	public void setActionOnDeleteItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		idProfessor = (new Long(info.id).longValue());
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
							ProfessorVO.open(ListaProfessores.this);
							ProfessorVO.removerProfessor(idProfessor);
							ProfessorVO.close();
							ListaProfessores.super.updateItens();
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
		return R.string.titulo_lista_professores;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
