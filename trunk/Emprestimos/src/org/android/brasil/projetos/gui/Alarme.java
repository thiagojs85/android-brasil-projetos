package org.android.brasil.projetos.gui;

import java.util.Calendar;

import org.android.brasil.projetos.control.AlarmeController;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Emprestimo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class Alarme extends BroadcastReceiver {
	public static final String CANCEL_NOTIFICATION = "CANCEL_NOTIFICATION";
	private long idEmprestimo;
	private AlarmeController ac;
	private Emprestimo emprestimo;

	@Override
	public void onReceive(Context context, Intent intent) {
		idEmprestimo = intent
				.getLongExtra(EmprestimoDAO.TABELA_EMPRESTIMOS, -1);
		if (idEmprestimo >= 0) {

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
		final long when = Calendar.getInstance().getTimeInMillis();

		Builder notification = new NotificationCompat.Builder(context)
				.setSmallIcon(icon)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								icon)).setTicker(tickerText).setWhen(when);
		final Intent notificationIntent = new Intent(context,
				EditarEmprestimo.class);

		notificationIntent.putExtras(new Bundle());
		notificationIntent.putExtra(CANCEL_NOTIFICATION, true);
		notificationIntent.putExtra(EmprestimoDAO.TABELA_EMPRESTIMOS,
				idEmprestimo);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);

		final PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		String notificacao = context.getString(R.string.notificacao);
		if (emprestimo.getStatus() == Emprestimo.STATUS_PEGAR_EMPRESTADO) {
			notificacao = context.getString(R.string.hora_de_devolver)
					+ emprestimo.getItem();
		} else {
			notificacao = context.getString(R.string.ja_recebeu_item) + " "
					+ emprestimo.getItem() + " "
					+ context.getString(R.string.de_volta);
		}

		notification.setContentText(notificacao);
		notification.setContentIntent(contentIntent);

		notification.setVibrate(new long[] { 100, 250, 100, 500 });
		mNotificationManager.cancel(context.getText(R.string.app_name) + "",
				(int) emprestimo.getIdEmprestimo());
		mNotificationManager.notify(context.getText(R.string.app_name) + "",
				(int) emprestimo.getIdEmprestimo(), notification.build());

	}
}