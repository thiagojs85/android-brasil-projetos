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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;

/**
* Classe responsavel por listar as materias cadastradas e adicionar mais se necessário
*
* @author Neto
*/
public class ListaMaterias extends TelaListaBasica{
	
	protected static final int LARCAR_NOTAS_ID = DELETE_ID + 1;
	protected static final int FAZER_CHAMADA_ID = LARCAR_NOTAS_ID + 1;
	private static final int DIALOG_DELETAR = 0;
	private long idDelete;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);
        ibt.setText(R.string.cadastrar_materias);//sobrescreve a string original do xml
        
        // TODO FIXME ListaAlunos será a view para realizar chamadas?
        // Quando o usuário clica em uma das matérias da lista, exibe a lista de alunos para chamada.
        this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> context, View view, int position, long id) {
				Intent i = new Intent(ListaMaterias.this, ListaAlunos.class).putExtra(DbAdapter.COLUMN_ID_TURMA, id);
		
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
	
		menu.add(Menu.NONE, LARCAR_NOTAS_ID, 0, R.string.lancar_notas);
		menu.add(Menu.NONE, FAZER_CHAMADA_ID, 0, R.string.fazer_chamada);

	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
              
		case LARCAR_NOTAS_ID:
			setActionOnNotaItem(item);            
		return true;
		case FAZER_CHAMADA_ID:
			setActionOnChamadaItem(item);
        return true;
    }
        return super.onContextItemSelected(item);

	}
    
	@Override
	public void onClick(View v) {
		
    	Intent i = new Intent(this, CadastroMateria.class);
    	i.putExtra(DbAdapter.COLUMN_ID_TURMA, 
    			this.getIntent().getLongExtra(DbAdapter.COLUMN_ID, 0));
    	startActivityForResult(i, ADD_ID);
	}


	@Override
	public Cursor getItensCursor() {
		
		long idTurma = this.getIntent().getLongExtra("id", 0);
		
		// Se não houver id nos Extras, mostre todas as matérias existentes.
		if(idTurma < 1) {
			return mDbAdapter.consultarTodos(DbAdapter.TABLE_MATERIA, 
					new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		} else {
			return mDbAdapter.acessarMateriasPorTurma(idTurma);
		}
	}
	
	@Override
	public void setActionOnEditItem(MenuItem item){
		// TODO
		//AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
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
	
	public void setActionOnNotaItem(MenuItem item){
		//TODO: acao do longpress lancar nota
	}
	
	public void setActionOnChamadaItem(MenuItem item){
		Intent i = new Intent(ListaMaterias.this, ListaAlunos.class).putExtra("chamada", true);
		startActivity(i);
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
						mDbAdapter.removerMateria(idDelete);
						ListaMaterias.super.updateItens();
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
