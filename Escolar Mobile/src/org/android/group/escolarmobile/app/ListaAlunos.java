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
import android.widget.Button;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListaAlunos extends TelaListaBasica {

	private static final int DIALOG_DELETAR = 0;
	private long idDelete;
	private long idTurma;
	//private boolean isMultiItensSelectable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//isMultiItensSelectable tem que ser setado antes de chamar o super.onCreate(savedInstanceState); 
    	//isMultiItensSelectable = this.getIntent().getBooleanExtra("isMultiItensSelectable", false);
    	
    	super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);
        ibt.setText(R.string.adicionar_aluno);//sobrescreve a string original do xml
        
        idTurma = this.getIntent().getLongExtra(DbAdapter.COLUMN_ID_TURMA, 0);
    }
    
    
    @Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar true
		//return isMultiItensSelectable;
    	return false;
	}
    
    
	@Override
	public void onClick(View v) {
		
    	Intent i = new Intent(this, CadastroAluno.class);
    	i.putExtra(DbAdapter.COLUMN_ID_TURMA, idTurma);
    	startActivityForResult(i, ADD_ID);
	}


	@Override
	public Cursor getItensCursor() {
		
		long idMateria = this.getIntent().getLongExtra("id", 0);
		
		// Se não houver id nos Extras, mostre todas as matérias existentes.
		if(idMateria < 1) {
			return mDbAdapter.consultarTodos(DbAdapter.TABLE_ALUNO, 
					new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		
		} else {
			return mDbAdapter.acessarAlunosPorMaterias(idMateria);
		}
	}
	
	@Override
	public void setActionOnEditItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
    	Intent i = new Intent(this, CadastroAluno.class);

    	// Repassa o id da linha selecionada.
		i.putExtra(DbAdapter.COLUMN_ID, info.id);
    	startActivityForResult(i, EDIT_ID);
	}
	
	@Override
	public void setActionOnDeleteItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		idDelete = (new Long(info.id).longValue());
		showDialog(DIALOG_DELETAR);
	}
	
	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DELETAR:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.dialog_delete).setCancelable(false);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mDbAdapter.removerAluno(idDelete);
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
