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
 * Esta é a classe principal. Assim que criada, ela chamará o método <code>onCreate</code>.
 * @author Otavio
 *
 */
public class EscolarMobile extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Define a ação que o botão Matérias executará quando pressionado. 
        subjects = (Button) findViewById(R.id.mainSubjects);
        subjects.setOnClickListener(this);
        
        // Define a ação que o botão Matérias executará quando pressionado. 
        students = (Button) findViewById(R.id.mainStudents);
        students.setOnClickListener(this);
        
        // Define a ação que o botão Matérias executará quando pressionado. 
        grades = (Button) findViewById(R.id.mainGrades);
        grades.setOnClickListener(this);
    }
    
    /**
     * Método chamado quando um botão da tela principal é pressionado.<br/>
     * Este método poderia ser implementado diretamente nas chamadas de <code>setOnClickListener</code>,
     *  mas isso tornaria o código desnecessariamente extenso.
     * @param v Elemento responsável pela chamada do método (neste caso, um dos botões).
     */
    public void onClick(View v) {
    	Button button = (Button) v;
    	Intent intent;
    	
    	// Através do ID do botão pressionado, define-se a tela que deverá ser chamada.
		switch(button.getId()) {
		case R.id.mainSubjects:
			intent = new Intent().setClass(this, Subjects.class);
			break;
		case R.id.mainStudents:
			intent = new Intent().setClass(this, Students.class);
			break;
		case R.id.mainGrades:
			// TODO Implementação para uma próxima versão.
			//intent = new Intent().setClass(this, Grades.class);
			Toast.makeText(this, "Ainda não implementada!", Toast.LENGTH_LONG).show();
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