package org.android.group.escolarmobile.app;

import org.group.dev.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
	public void setStringItens() {
		//TODO: Preencher com o nome de cada turma
		//this.itens = new String[]{"linha um","linha dois","linha tres"};
		this.itens = new String[]{};
	}
}
