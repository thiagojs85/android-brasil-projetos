package org.group.dev.subjects;

import org.group.dev.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Tela para adicionar uma nova matéria à lista de matérias cadastradas no sistema.
 * @author Otavio
 *
 */
public class AddSubject extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subject);
		
		okBtn = (Button) findViewById(R.id.okButton);
		okBtn.setOnClickListener(this);
		
		cancelBtn = (Button) findViewById(R.id.cancelButton);
		cancelBtn.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.okButton:
			// TODO Salvar alterações no banco de dados.
		case R.id.cancelButton:
			this.finish();
		}
	}
	
	private Button okBtn;
	private Button cancelBtn;
}
