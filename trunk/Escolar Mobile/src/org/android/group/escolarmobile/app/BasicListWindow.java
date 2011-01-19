package org.android.group.escolarmobile.app;

import org.group.dev.R;

import android.app.ListActivity;
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

public class BasicListWindow extends ListActivity implements OnClickListener,BasicListInterface{

	protected static final int ADD_ID = Menu.FIRST;
	protected static final int EDIT_ID = ADD_ID + 1;
    protected static final int DELETE_ID = EDIT_ID + 1;
	
	protected Button btAdd;   
	protected String[] itens;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_list_window);
        btAdd = (Button) findViewById(R.id.add);
		fillItens();
		registerForContextMenu(getListView());
		if(isMultiItensSelectable()){
			setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,itens));
	        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		}else{
			setListAdapter(new ArrayAdapter<String>(this,R.layout.base_list_item,itens));
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

    public void setActionOnEditItem(MenuItem item){
		
	}
	public void setActionOnDeleteItem(MenuItem item){
	
	}

    public void onClick(View v) {
	}
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
	public void fillItens() {
		this.itens = new String[]{};
	}  

}
