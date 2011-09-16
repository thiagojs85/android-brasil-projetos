package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.model.Categoria;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditarCategoria extends Activity  {
	
	private EditText etDescricao;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editar_categoria);
		setTitle(R.string.editar_categoria);
		
		Button btConfirm = (Button) findViewById(R.id.confirmarCategoria);
		etDescricao = (EditText) findViewById(R.id.descricaoCategoria);
		
		btConfirm.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View view) {
				if(!etDescricao.getText().toString().trim().equals("")) {
					saveState();
					Toast.makeText(EditarCategoria.this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(EditarCategoria.this, "Entre com a descrição!", Toast.LENGTH_SHORT).show();
				}				
			}
		});
	}
	
	private void saveState() {
		CategoriaDAO.open(getApplicationContext());
		
		Categoria cat = new Categoria();
		
		cat.setNomeCategoria(etDescricao.getText().toString());
		
		long id = CategoriaDAO.inserir(cat);
		
		CategoriaDAO.close();		
	}

}
