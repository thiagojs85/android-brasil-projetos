package org.android.brasil.projetos.control;

import org.android.brasil.projetos.gui.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;

public class ContatosController {
	private Activity act;
	private Cursor cursorContato;
	private boolean isClosed;
	
	public ContatosController(Activity activity) {
		act = activity;
		isClosed = false;
	}
	
	
	public SimpleCursorAdapter getContatoAdapter() {
		// Fix para Android 3.0 ou superiores
		if (cursorContato != null && !cursorContato.isClosed()) {
			act.stopManagingCursor(cursorContato);
			cursorContato.close();
		}

		ContentResolver cr = act.getContentResolver();

		cursorContato = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		
		String[] from = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
		int[] to = new int[] { R.id.text1 };

		act.startManagingCursor(cursorContato);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
				R.layout.linha_spinner, cursorContato,
				from,to);

		return adapter;
	}
	public boolean isClosed() {
		return isClosed;
	}
	
	public void close(){
		if(cursorContato != null){
			isClosed = true;
			act.stopManagingCursor(cursorContato);
			cursorContato.close();
		}

	}

}
