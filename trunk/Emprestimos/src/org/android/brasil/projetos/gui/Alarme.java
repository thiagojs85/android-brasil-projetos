package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.control.AlarmeController;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Emprestimo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarme extends BroadcastReceiver {
	private long idEmprestimo;
	private AlarmeController ac;
	private Emprestimo emprestimo;

	@Override
	public void onReceive(Context context, Intent intent) {
		idEmprestimo = intent.getLongExtra(EmprestimoDAO.COLUNA_ID_EMPRESTIMO,
				0);
		if (idEmprestimo > 0) {

			ac = new AlarmeController(context);

			emprestimo = new Emprestimo();
			emprestimo = ac.getEmprestimo(idEmprestimo);

			if (emprestimo.getAtivarAlarme() == Emprestimo.ATIVAR_ALARME) {
				notificationStatus(context, intent);
				ac.atualizaNotificacao(idEmprestimo);
			}
		}
	}

	private void notificationStatus(Context context, Intent i) {
		final NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final int icon = R.drawable.icon;
		final CharSequence tickerText = context.getString(R.string.app_name);
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText,
				when);
		final Intent notificationIntent = new Intent(context,
				EditarEmprestimo.class);

		notificationIntent.putExtras(i.getExtras());
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), 0, notificationIntent, 0);

		String notificacao = context.getString(R.string.notificacao);
		if (emprestimo.getStatus() == Emprestimo.STATUS_PEGAR_EMPRESTADO) {
			notificacao = context.getString(R.string.hora_de_devolver)
					+ emprestimo.getItem();
		} else {
			notificacao = context.getString(R.string.ja_recebeu_item)
					+ emprestimo.getItem()
					+ context.getString(R.string.de_volta);
		}

		notification.setLatestEventInfo(context, tickerText, notificacao,
				contentIntent);

		notification.vibrate = new long[] { 100, 250, 100, 500 };
		mNotificationManager.notify(R.string.app_name, notification);

	}
}