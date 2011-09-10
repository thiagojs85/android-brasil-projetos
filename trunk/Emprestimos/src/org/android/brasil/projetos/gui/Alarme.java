package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.EmprestimoDAO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Alarme extends BroadcastReceiver {
	private long mRowid;
	@Override
	public void onReceive(Context context, Intent intent) {
		mRowid = intent.getLongExtra(EmprestimoDAO.COLUNA_ID_EMPRESTIMO, 0);
		if(mRowid > 0){
			Intent i = new Intent(context, EditarEmprestimo.class);
			i.putExtras(intent.getExtras());
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Toast.makeText(context, "Alarme ativado!", Toast.LENGTH_LONG).show();
			context.startActivity(i);
		}
		
	}
}