package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ListaTurmas extends TelaListaBasica {
	
	protected static final int VISUALIZAR_ALUNO_ID = DELETE_ID + 1;
	
	private long idDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);
        ibt.setText(R.string.adicionar_turma);
        
        // Quando o usuário clica em uma das turmas da lista, exibe a lista de matérias daquela turma.
        this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> context, View view, int position, long id) {
				Intent i = new Intent(ListaTurmas.this, ListaMaterias.class).putExtra("id", id);
		
				startActivity(i);				
			}
		});
    }
    
    @Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar true
		return false;
	}
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, VISUALIZAR_ALUNO_ID, 0, R.string.visualizar_aluno);

	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case EDIT_ID:
            setActionOnEditItem(item);            
            return true;
        case DELETE_ID:
        	setActionOnDeleteItem(item);
            return true;
        case VISUALIZAR_ALUNO_ID:
        	setActionOnViewItem(item);
        }
        return super.onContextItemSelected(item);

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
	
	public void setActionOnViewItem(MenuItem item){
		Intent i = new Intent(this, ListaAlunos.class);
		
		startActivityForResult(i, VISUALIZAR_ALUNO_ID);
		
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
