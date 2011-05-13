package org.android.brasil.projetos.escolarmobile.base;

import org.android.brasil.projetos.escolarmobile.R;

import android.app.Activity;
import android.os.Bundle;

public class Sobre extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.titulo_sobre);
		setContentView(R.layout.sobre);
	}
}
