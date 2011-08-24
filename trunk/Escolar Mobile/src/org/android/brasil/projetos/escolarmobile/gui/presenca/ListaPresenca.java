package org.android.brasil.projetos.escolarmobile.gui.presenca;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.base.TelaListaBasica;
import org.android.brasil.projetos.escolarmobile.dao.AlunoVO;
import org.android.brasil.projetos.escolarmobile.dao.PresencaVO;
import org.android.brasil.projetos.escolarmobile.gui.aluno.ListaAlunos;

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
 * Classe responsavel por listar as chamadas feitas
 * 
 * @author Júlio
 */

public class ListaPresenca extends TelaListaBasica {

	private static final int DIALOG_DELETAR = 0;
	private long idTurmaMateria;
	private long periodo;
	private long idChamada;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button ibt = (Button) findViewById(R.id.add);
		ibt.setText(R.string.fazer_chamada);

	}

	@Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar
		// true
		return false;
	}

	@Override
	public void onClick(View v) {

		Intent i = new Intent(this, ListaAlunos.class);
		i.putExtra("chamada", true);
		startActivityForResult(i, ADD_ID);
	}

	@Override
	public Cursor getItensCursor() {
		PresencaVO.open(this);
		Cursor c = PresencaVO.consultarPresenca(idTurmaMateria, periodo);
		PresencaVO.close();
		return c;
	}
	protected String[] getItensOfLine() {
		return new String[] { PresencaVO.COLUMN_DATA };

	}
	@Override
	public void setActionOnEditItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		Intent i = new Intent(this, ListaAlunos.class);
		i.putExtra(PresencaVO.COLUMN_ID, info.id);
		startActivityForResult(i, EDIT_ID);
	}

	@Override
	public void setActionOnDeleteItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		idChamada = (new Long(info.id).longValue());
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
			builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					PresencaVO.open(ListaPresenca.this);
					PresencaVO.removerPresenca(idChamada);
					PresencaVO.close();
					ListaPresenca.super.updateItens();
				}
			});
			builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
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
