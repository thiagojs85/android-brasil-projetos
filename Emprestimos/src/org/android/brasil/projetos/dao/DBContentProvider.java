package org.android.brasil.projetos.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DBContentProvider extends ContentProvider {
	private static final String AUTHORITY = "org.android.brasil.projetos.emprestimos";
	public static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	private static final UriMatcher uriMatcher;
	private static final int LIST = 1;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "/*", LIST);
	}
	protected static SQLiteDatabase mDb;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = uriMatcher.match(uri);
		switch (uriType) {
		case LIST:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		String table = uri.getLastPathSegment();
		int count = mDb.delete(table, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = uri.getLastPathSegment();
		long id = mDb.insert(table, null, values);
		if (id > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, id);
			getContext().getContentResolver().notifyChange(uri, null);
			return newUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mDb = DbAdapter.open(getContext());
		return (mDb != null && mDb.isOpen());
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String table = uri.getLastPathSegment();
		Cursor c = mDb.query(false, table, projection, selection, null, null,
				null, sortOrder, null);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String table = uri.getLastPathSegment();
		int count = mDb.update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
