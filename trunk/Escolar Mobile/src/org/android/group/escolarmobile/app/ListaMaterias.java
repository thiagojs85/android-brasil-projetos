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
import android.widget.Button;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
* Classe responsavel por listar as materias cadastradas e adicionar mais se necessário
*
* @author Neto
*/
public class ListaMaterias extends BasicListWindow{
	
	protected static final int LARCAR_NOTAS_ID = DELETE_ID + 1;
	protected static final int FAZER_CHAMADA_ID = LARCAR_NOTAS_ID + 1;
	private static final int DIALOG_DELETAR = 0;
	private long idDelete;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);
        ibt.setText(R.string.adicionar_materia);//sobrescreve a string original do xml
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
	
		menu.add(Menu.NONE, EDIT_ID, 0, R.string.base_menu_edit);
		menu.add(Menu.NONE, DELETE_ID, 0, R.string.base_menu_delete);
		menu.add(Menu.NONE, LARCAR_NOTAS_ID, 0, R.string.lancar_notas);
		menu.add(Menu.NONE, FAZER_CHAMADA_ID, 0, R.string.fazer_chamada);

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
		case LARCAR_NOTAS_ID:
			setActionOnEditItem(item);            
			return true;
		case FAZER_CHAMADA_ID:
			setActionOnDeleteItem(item);
        return true;
    }
        return super.onContextItemSelected(item);

	}
    
	@Override
	public void onClick(View v) {
		
    	Intent i = new Intent(this, CadastroMateria.class);
    	startActivityForResult(i, ADD_ID);
	}


	@Override
	public Cursor getItensCursor() {
		return mDbAdapter.consultarTodos(DbAdapter.TABLE_MATERIA, 
				new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		
	}
	
	@Override
	public void setActionOnEditItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
    	//Intent i = new Intent(this, CadastroMateria.class);

    	// Repassa o id da linha selecionada.
		/*Bundle b = new Bundle();
		b.putLong(DbAdapter.COLUMN_ID, info.id);
		i.putExtras(b);
    	startActivityForResult(i, EDIT_ID);*/
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
						mDbAdapter.removerProfessor(idDelete);
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
