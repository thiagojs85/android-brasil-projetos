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
	private Long idCategoria;
	

	//TODO: Essa classe não é mais utilizada, certo? Que tal apagarmos ela? Se concordar, apaga ai.
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
					setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(EditarCategoria.this, "Entre com a descrição!", Toast.LENGTH_SHORT).show();
				}				
			}
		});
		
		idCategoria = (savedInstanceState == null) ? null : (Long) savedInstanceState
				.getSerializable(CategoriaDAO.COLUNA_ID);

		if (idCategoria == null) {
			Bundle extras = getIntent().getExtras();
			idCategoria = extras != null ? extras.getLong(CategoriaDAO.COLUNA_ID) : null;
		}
		
		populateFields();
	}
	
	private void populateFields() {
		if (idCategoria != null) {

			CategoriaDAO.open(getApplicationContext());
			Categoria cat = CategoriaDAO.consultar(idCategoria);
			CategoriaDAO.close();
			
			etDescricao.setText(cat.getNomeCategoria());

		}
	}
	
	private void saveState() {
		CategoriaDAO.open(getApplicationContext());
		Categoria cat = new Categoria();
		
		cat.setNomeCategoria(etDescricao.getText().toString().trim());
		
		if (idCategoria == null) {
			
			long id = CategoriaDAO.inserir(cat);
			
			if(id >0) {
				idCategoria = id;
			}
			
		} else {
			cat.setId(idCategoria);
			CategoriaDAO.atualizar(cat);
		}	
		CategoriaDAO.close();
	}

}