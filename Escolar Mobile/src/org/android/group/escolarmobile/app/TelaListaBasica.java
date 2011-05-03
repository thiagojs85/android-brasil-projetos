package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public abstract class TelaListaBasica extends ListActivity implements OnClickListener {

	protected static final int ADD_ID = Menu.FIRST;
	protected static final int EDIT_ID = ADD_ID + 1;
    protected static final int DELETE_ID = EDIT_ID + 1;
	
	protected Button btAdd;   
	protected String[] itens;
	
	protected DbAdapter mDbAdapter;
	protected Cursor c;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbAdapter = new DbAdapter(this).open();
        
        setContentView(R.layout.base_list_window);
        btAdd = (Button) findViewById(R.id.add);
		registerForContextMenu(getListView());
		
		updateItens();
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
            break;
        case DELETE_ID:
        	setActionOnDeleteItem(item);
            break;
        }
        return super.onContextItemSelected(item);

	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(mDbAdapter != null) {
    		mDbAdapter.close();
    	}
    }
    
    /**
     * Este método procura os ítens que farão parte da lista exibida pela atividade.
     * Ele é chamado automaticamente na criação da tela, mas para se atualizar a lista
     * de ítens posteriormente, deve-se chamar este método.
     */
    public void updateItens() {
    	c = getItensCursor();
		if(c != null) {
			startManagingCursor(c);
		}
		
		if(isMultiItensSelectable()){

			setListAdapter(new SimpleCursorAdapter(this, 
					android.R.layout.simple_list_item_multiple_choice, 
					c, 
					new String[]{DbAdapter.COLUMN_NOME}, //tem que adicionar a coluna DbAdapter.COLUMN_REGISTRO aqui
					new int[]{android.R.id.text1}));
			
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

			    for(int i = 0; i< getListAdapter().getCount();++i){
			                getListView().setItemChecked(i, true);
			      }

		}else{
			// A criação deste ListAdapter é praticamente a mesma utilizada no tutorial do Notepad.
			// Mais informações no Step 12 em http://developer.android.com/resources/tutorials/notepad/notepad-ex1.html

			if(getItensOfLine().length <2){
			setListAdapter(new SimpleCursorAdapter(this, 
					R.layout.base_list_item, 
					c, 
					getItensOfLine(), 
					new int[]{R.id.n_prontuario}));
			}else{
				setListAdapter(new SimpleCursorAdapter(this, 
						R.layout.base_list_item, 
						c, 
						getItensOfLine(), 
						new int[]{R.id.n_prontuario,R.id.n_sub}));				
			}
		}
    }

    protected String[] getItensOfLine() {
    	return new String[]{DbAdapter.COLUMN_NOME};

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
