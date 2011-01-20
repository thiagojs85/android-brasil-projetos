package org.android.group.escolarmobile.app;

import org.group.dev.R;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class cadastroTurmas extends Activity {
	
	private Button ok, cancelar, cadastrarMaterias;
	private EditText materia, descricao;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);
        
        ok = (Button) findViewById(R.id.bt_ok);
        cancelar = (Button) findViewById(R.id.bt_cancelar);
        cadastrarMaterias = (Button) findViewById(R.id.bt_cadastrar_turmas);
		materia = (EditText) findViewById(R.id.et_nome_materia);
		descricao = (EditText) findViewById(R.id.et_descricao);
        
        
        
	
	
	ok.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
			//TODO: Salvar as informações no banco e continua na mesma tela
			
			Toast.makeText(cadastroTurmas.this, "Botão ok Pressionado!", Toast.LENGTH_SHORT).show();
			
		}
	});
	
	cancelar.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
			//TODO: cancelar não salvar nada e voltar pra tela anterior
			Toast.makeText(cadastroTurmas.this, "Botão cancelar Pressionado!", Toast.LENGTH_SHORT).show();
			
			
		}
	});
	
	cadastrarMaterias.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
			//TODO: Chamar a tela de cadastro de matérias
			
			Toast.makeText(cadastroTurmas.this, "Botão cadastro de matérias Pressionado!", Toast.LENGTH_SHORT).show();
			
			
		}
	});
	
	
	}
}
