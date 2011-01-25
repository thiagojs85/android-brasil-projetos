package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public abstract class BasicListWindow extends ListActivity implements OnClickListener {

	protected static final int ADD_ID = Menu.FIRST;
	protected static final int EDIT_ID = ADD_ID + 1;
    protected static final int DELETE_ID = EDIT_ID + 1;
	
	protected Button btAdd;   
	protected String[] itens;
	
	protected DbAdapter mDbAdapter;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbAdapter = new DbAdapter(this).open();
        
        setContentView(R.layout.base_list_window);
        btAdd = (Button) findViewById(R.id.add);
		registerForContextMenu(getListView());
		if(isMultiItensSelectable()){
			// TODO [Otavio] Esta parte ainda não foi testada, pois nenhuma activity usou esta funcionalidade!
			setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,itens));
	        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		}else{
			// A criação deste ListAdapter é praticamente a mesma utilizada no tutorial do Notepad.
			// Mais informações no Step 12 em http://developer.android.com/resources/tutorials/notepad/notepad-ex1.html
			Cursor c = getItensCursor();
			startManagingCursor(c);
			setListAdapter(new SimpleCursorAdapter(this, 
					R.layout.base_list_item, 
					c, 
					new String[]{DbAdapter.COLUMN_NOME}, 
					new int[]{R.id.n_prontuario}));
		}
        btAdd.setOnClickListener(this);
    }

	protected boolean isMultiItensSelectable() {
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
	
		menu.add(Menu.NONE, EDIT_ID, 0, R.string.base_menu_edit);
		menu.add(Menu.NONE, DELETE_ID, 0, R.string.base_menu_delete);

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
        }
        return super.onContextItemSelected(item);

	}



    public abstract void onClick(View v);
	public Button getBtAdd() {
		return btAdd;
	}

	public void setBtAdd(Button btAdd) {
		this.btAdd = btAdd;
	}

	public String[] getItens() {
		return itens;
	}

	public void setItens(String[] itens) {
		this.itens = itens;
	} 
	
	/**
	 * Recupera o cursor apontando para os itens que deverão ser exibidos na lista da Activity.
	 * 
	 * @return Cursor resultante da consulta à tabela para recuperar os itens a serem exibidos.
	 */
	public abstract Cursor getItensCursor();
	
	/**
	 *  Metodos que devem ser implementados ao se extender uma classe
	 * para as ações de  Editar um item da lista e para Deletar um item
	 */
    public abstract void setActionOnEditItem(MenuItem item);
    
	public abstract void setActionOnDeleteItem(MenuItem item);

}
