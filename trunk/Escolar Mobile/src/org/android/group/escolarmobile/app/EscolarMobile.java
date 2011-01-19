package org.android.group.escolarmobile.app;


import org.group.dev.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Classe inicial do app.
 * 
 * @author Neto
 *
 */
public class EscolarMobile extends Activity implements OnClickListener {
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
        ArrayAdapter<CharSequence> adapterMaterias = ArrayAdapter.createFromResource(
                this, R.array.materias, android.R.layout.simple_spinner_item);
        adapterMaterias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materias.setAdapter(adapterMaterias);
        materias.setSelection(6);
        
        AutoCompleteTextView alunos = (AutoCompleteTextView) findViewById(R.id.AC_alunos);
        String[] arrayAlunos = getResources().getStringArray(R.array.alunos);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.list_item, arrayAlunos);
        alunos.setAdapter(adapter3);
        
        
        btVisualizar.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
	    	Intent i = new Intent(this, ListClass.class);
	    	startActivityForResult(i, 0);
		}
}