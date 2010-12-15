package org.group.dev;

import org.group.dev.students.Students;
import org.group.dev.subjects.Subjects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Esta � a classe principal. Assim que criada, ela chamar� o m�todo <code>onCreate</code>.
 * @author Otavio
 *
 */
public class EscolarMobile extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Define a a��o que o bot�o Mat�rias executar� quando pressionado. 
        subjects = (Button) findViewById(R.id.mainSubjects);
        subjects.setOnClickListener(this);
        
        // Define a a��o que o bot�o Mat�rias executar� quando pressionado. 
        students = (Button) findViewById(R.id.mainStudents);
        students.setOnClickListener(this);
        
        // Define a a��o que o bot�o Mat�rias executar� quando pressionado. 
        grades = (Button) findViewById(R.id.mainGrades);
        grades.setOnClickListener(this);
    }
    
    /**
     * M�todo chamado quando um bot�o da tela principal � pressionado.<br/>
     * Este m�todo poderia ser implementado diretamente nas chamadas de <code>setOnClickListener</code>,
     *  mas isso tornaria o c�digo desnecessariamente extenso.
     * @param v Elemento respons�vel pela chamada do m�todo (neste caso, um dos bot�es).
     */
    public void onClick(View v) {
    	Button button = (Button) v;
    	Intent intent;
    	
    	// Atrav�s do ID do bot�o pressionado, define-se a tela que dever� ser chamada.
		switch(button.getId()) {
		case R.id.mainSubjects:
			intent = new Intent().setClass(this, Subjects.class);
			break;
		case R.id.mainStudents:
			intent = new Intent().setClass(this, Students.class);
			break;
		case R.id.mainGrades:
			// TODO Implementa��o para uma pr�xima vers�o.
			//intent = new Intent().setClass(this, Grades.class);
			Toast.makeText(this, "Ainda n�o implementada!", Toast.LENGTH_LONG).show();
			intent = null;
			break;
		default:
			intent = null;				
		}
		
		if(intent != null) {
			startActivity(intent);
		}
	}
    
    private Button subjects;
    private Button students;
    private Button grades;
}