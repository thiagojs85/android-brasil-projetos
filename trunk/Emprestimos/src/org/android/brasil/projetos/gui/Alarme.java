package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.EmprestimoDbAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarme extends BroadcastReceiver {
	private long mRowid;
	@Override
	public void onReceive(Context context, Intent intent) {
		mRowid = intent.getLongExtra(EmprestimoDbAdapter.COLUNA_ID, 0);
		if(mRowid > 0){
			Intent i = new Intent(context, EditarEmprestimo.class);
			i.putExtras(intent.getExtras());
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
		
	}
}