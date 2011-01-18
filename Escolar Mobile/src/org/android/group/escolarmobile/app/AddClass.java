package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.Toast;

public class AddClass extends BaseListWindow {
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);
        ibt.setText("Adicionar Turma");
        
    }
    
	@Override
	public void onClick(View v) {
		//TODO: Chamar CLASSE_DO_NETO abaixo
    	//Intent i = new Intent(this, CLASSE_DO_NETO.class);
    	//startActivityForResult(i, 0);
		Toast.makeText(AddClass.this, "Botão Adicionar Pressionado!", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void fillItens() {
		//TODO: Preencher com o nome de cada turma
		this.itens = new String[]{"linha 0","linha 1","linha 2","linha 3","linha 4","linha 5","linha 6","linha 7","linha 8"};
		//setItens(new String[]{});
	}
	@Override
	public void setActionOnEditItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
/*
		//TODO: Chamar CLASSE_DO_NETO abaixo
    	Intent i = new Intent(this, CLASSE_DO_NETO.class);
		Bundle b = new Bundle();
		//TODO: itens[(int)info.id] retorna o nome da turma que pode ser passado para a CLASSE_DO_NETO quando for editar uma turma
		b.putInt(DbAdapter.COLUMN_NOME, itens[(int)info.id]);
		i.putExtras(b);
    	startActivityForResult(i, EDIT_ID);
*/

		Toast.makeText(AddClass.this, "Botão Editar Pressionado! "+itens[(int)info.id], Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void setActionOnDeleteItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Toast.makeText(AddClass.this, "Botão delete Pressionado!", Toast.LENGTH_LONG).show();
	}
	
}
