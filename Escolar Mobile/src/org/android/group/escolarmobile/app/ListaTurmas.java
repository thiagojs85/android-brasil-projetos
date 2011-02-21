package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

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

public class ListaTurmas extends TelaListaBasica {
	
	private long idDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);
        ibt.setText(R.string.adicionar_turma);
    }
    
    @Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar true
		return false;
	}
    
	@Override
	public void onClick(View v) {
		/* Inicia a tela de cadastro de turma
		 * 
		 */
		 
    	Intent i = new Intent(this, CadastroTurmas.class);
    	startActivityForResult(i, ADD_ID);
	}
	
	@Override
	public Cursor getItensCursor() {
		/**
		 * Preenche a lista de turmas com base nos dados do BD
		 */
		return mDbAdapter.consultarTodos(DbAdapter.TABLE_TURMA, new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
	}
	
	@Override
	public void setActionOnEditItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
				
    	Intent i = new Intent(this, CadastroTurmas.class);

    	// Repassa o id da linha selecionada para a tela de edição
		Bundle b = new Bundle();
		b.putLong(DbAdapter.COLUMN_ID, (new Long(info.id).longValue()));
		i.putExtras(b);
    	startActivityForResult(i, EDIT_ID);
	}
	
	@Override
	public void setActionOnDeleteItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		idDelete = (new Long(info.id).longValue());
		showDialog(DELETE_ID);
	}
	
	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DELETE_ID:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.dialog_delete).setCancelable(false);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mDbAdapter.removerTurma(idDelete);
					}
				});
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				return builder.create();
			default:
				return null;
		}
	}
	
}