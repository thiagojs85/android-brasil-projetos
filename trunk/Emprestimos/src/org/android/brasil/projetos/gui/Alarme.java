package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.EmprestimoDAO;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarme extends BroadcastReceiver {
	private long mRowid;
	@Override
	public void onReceive(Context context, Intent intent) {
		mRowid = intent.getLongExtra(EmprestimoDAO.COLUNA_ID_EMPRESTIMO, 0);
		//TODO: Verificar no banco se o emprestimo com esse ID está com o status do alarme ativo, caso 
		// positivo, mostrar a notificação, caso negativo não fazer nada.
		if(mRowid > 0){
			notificationStatus(context, intent );
		}
	}
	
	
	private void notificationStatus(Context context, Intent	i) {
	    final NotificationManager mNotificationManager = (NotificationManager) 
	            context.getSystemService(Context.NOTIFICATION_SERVICE);

	    final int icon = R.drawable.icon;
	    final CharSequence tickerText = context.getString(R.string.app_name);
	    final long when = System.currentTimeMillis();

	    final Notification notification = new Notification(icon, tickerText, when);
	    final Intent notificationIntent = new Intent(context,   EditarEmprestimo.class);
	    
	    notificationIntent.putExtras(i.getExtras());
	    notificationIntent.putExtra(EmprestimoDAO.COLUNA_ATIVAR_ALARME, true);
	    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	    final PendingIntent contentIntent = PendingIntent.getActivity(
	            context.getApplicationContext(), 0, notificationIntent,0);
	    
	    notification.setLatestEventInfo(context, tickerText, context.getString(R.string.notification), contentIntent);
	    
	    notification.vibrate = new long[] { 100,250,100,500};
	    mNotificationManager.notify(R.string.app_name, notification);

	}
}