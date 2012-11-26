package org.android.brasil.projetos.control;

import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Emprestimo;

import android.content.Context;

public class AlarmeController {
	private Context ctx;

	public AlarmeController(Context context) {
		ctx = context;
	}
	
	public Emprestimo getEmprestimo(long id) {
		Emprestimo emprestimo = new EmprestimoDAO(ctx).consultar(id);
		return emprestimo;
	}

	public void atualizaNotificacao(long id) {
		new EmprestimoDAO(ctx).atualizarAlarme(id);
	}

}
