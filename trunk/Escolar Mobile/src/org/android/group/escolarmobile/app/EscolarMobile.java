package org.android.group.escolarmobile.app;


import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

/**
 * Classe inicial do app.
 * 
 * @author Neto
 *
 */
public class EscolarMobile extends Activity implements OnClickListener {
	private DbAdapter mDbAdapter = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btVisualizar = (Button) findViewById(R.id.visualizar);
        
        Spinner turmas = (Spinner) findViewById(R.id.sp_turmas);
        ArrayAdapter<CharSequence> adapterTurmas = ArrayAdapter.createFromResource(
                this, R.array.turmas, android.R.layout.simple_spinner_item);
        adapterTurmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        turmas.setAdapter(adapterTurmas);
        turmas.setSelection(9);
        
        Spinner materias = (Spinner) findViewById(R.id.sp_materias);
        /*
        ArrayAdapter<CharSequence> adapterMaterias = ArrayAdapter.createFromResource(
                this, R.array.materias, android.R.layout.simple_spinner_item);
        adapterMaterias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materias.setAdapter(adapterMaterias);
        materias.setSelection(6);
        */
        
        //FIXME [Otavio] Esta parte não está funcionando! Apesar de não dar erro, o Spinner está em branco!
        mDbAdapter = new DbAdapter(this).open();
		Bundle bundle = getIntent().getExtras();

		Cursor c = mDbAdapter.consultarTodos(DbAdapter.TABLE_MATERIA, 
				new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		materias.setAdapter(new SimpleCursorAdapter(this, 
				R.layout.base_list_item, 
				c, 
				new String[]{DbAdapter.COLUMN_NOME}, //tem que adicionar a coluna DbAdapter.COLUMN_REGISTRO aqui
				new int[]{R.id.n_prontuario}));
		// Fim do FIXME
        
        AutoCompleteTextView alunos = (AutoCompleteTextView) findViewById(R.id.AC_alunos);
        String[] arrayAlunos = getResources().getStringArray(R.array.alunos);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.list_item, arrayAlunos);
        alunos.setAdapter(adapter3);
        
        
        btVisualizar.setOnClickListener(this);


    }

    
    public void onClick(View v) {
    	// TODO [Otavio] Visualizar os dados conforme a seleção das caixas.
//    	Intent i = new Intent(this, ListClass.class);
//	    startActivityForResult(i, 0);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.turmas, menu);
        inflater.inflate(R.menu.professores, menu);//adiciona o menu dos professores
        //[Otavio] As opções de cadastrar/visualizar materias e alunos estarão disponíveis dentro de turma.
        //inflater.inflate(R.menu.materias, menu);
        //inflater.inflate(R.menu.alunos, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i = null;
    	
        switch (item.getItemId()) {
        case R.id.menu_turma:
        	i = new Intent(this, ListClass.class);
    	    startActivityForResult(i, 0);
            return true;
        case R.id.menu_professores://seleciona o menu de acordo com o nome do xml
        	i = new Intent(this, ListaProfessores.class);
    	    startActivityForResult(i, 0);
            return true;
        case R.id.menu_materias:
        	i = new Intent(this, ListaMaterias.class);
    	    startActivityForResult(i, 0);
            return true;
        case R.id.menu_alunos:
        	i = new Intent(this, ListaAlunos.class);
    	    startActivityForResult(i, 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}