package org.android.brasil.projetos.control;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;

public abstract class Controller implements
		LoaderManager.LoaderCallbacks<Cursor> {
	protected SimpleCursorAdapter adapter;
	protected FragmentActivity act;
	private static int counterID = 0;
	private int CONTROLLER_IDENTIFIER;

	protected Controller(FragmentActivity activity) {
		act = activity;
		CONTROLLER_IDENTIFIER = ++counterID;
	}

	protected int getControllerIdentifier() {
		return CONTROLLER_IDENTIFIER;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);

	}
}
