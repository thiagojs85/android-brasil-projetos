package org.android.brasil.projetos.gui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Notificacao extends Activity {

	//TODO: Essa classe está sendo usada? Se não estiver, podemos apagar?
	//É uma boa manter o projeto sempre limpo.
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		String tickerText = "Empréstimo";
		CharSequence titulo = "Empréstimo";
		CharSequence mensagem = "Devolução/Recebimento";

		criarNotificacao(this, tickerText, titulo, mensagem, EditarEmprestimo.class);
	}

	protected void criarNotificacao(Context context, CharSequence mensagemBarraStatus, CharSequence titulo,CharSequence mensagem, Class<?> activity) {

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification n = new Notification(R.drawable.icon, mensagemBarraStatus, System.currentTimeMillis());

		PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this, activity), 0);

		n.setLatestEventInfo(this, titulo, mensagem, p);
		n.vibrate = new long[] { 100, 250, 100, 500 };

		nm.notify(R.string.app_name, n);
	}
}
