package org.android.group.escolarmobile.app;

import java.util.List;

import org.group.dev.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class BaseListWindow extends ListActivity implements OnClickListener,BaseListInterface{

	protected Button btAdd;   
	protected String[] itens;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_list_window);
        btAdd = (Button) findViewById(R.id.add);
        this.setStringItens();
        setListAdapter(new ArrayAdapter<String>(this,R.layout.base_list_item,itens));
		btAdd.setOnClickListener(this);
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
	public void setStringItens() {
		this.itens = new String[]{"linha um","linha dois"};
	}
}
