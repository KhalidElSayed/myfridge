package com.gnorsilva.android.myfridge.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeColumns;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeItems;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeName;
import com.gnorsilva.android.myfridge.provider.MyFridgeDatabase.Tables;

public class MyFridgeProvider extends ContentProvider {

	private MyFridgeDatabase dbHelper;
	private static HashMap<String, String> fridgeItemsProjectionMap = buildFridgeItemsProjectionMap();
	private static final UriMatcher uriMatcher = buildUriMatcher();

	private static final int FRIDGE_ITEMS = 100;
	private static final int FRIDGE_ITEMS_ID = 101;
	private static final int FRIDGE_NAME = 102;
	
	
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MyFridgeContract.CONTENT_AUTHORITY;
		
        matcher.addURI(authority, "fridge/items", FRIDGE_ITEMS);
        matcher.addURI(authority, "fridge/items/#", FRIDGE_ITEMS_ID);
        matcher.addURI(authority, "fridge/name/*", FRIDGE_NAME);
        
		return matcher;
	}
	
	private static HashMap<String, String> buildFridgeItemsProjectionMap() {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put(BaseColumns._ID, BaseColumns._ID);
		map.put(FridgeColumns.NAME, FridgeColumns.NAME);
		map.put(FridgeColumns.QUANTITY, FridgeColumns.QUANTITY);
		map.put(FridgeColumns.USE_BY_DATE, FridgeColumns.USE_BY_DATE);
		map.put(FridgeColumns.BARCODE, FridgeColumns.BARCODE);
		map.put(FridgeColumns.BARCODE_FORMAT, FridgeColumns.BARCODE_FORMAT);
		return map;
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = new MyFridgeDatabase(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case FRIDGE_ITEMS:
			return FridgeItems.CONTENT_TYPE;
		case FRIDGE_ITEMS_ID:
			return FridgeItems.CONTENT_ITEM_TYPE;
		case FRIDGE_NAME:
			return FridgeName.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int uriMatch = uriMatcher.match(uri);
		
		switch (uriMatch) {
		case FRIDGE_ITEMS:
			qb.setTables(Tables.FRIDGE);
			qb.setProjectionMap(fridgeItemsProjectionMap);
			break;
		case FRIDGE_ITEMS_ID:
			qb.setTables(Tables.FRIDGE);
			qb.setProjectionMap(fridgeItemsProjectionMap);
			qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(2));
			break;
		case FRIDGE_NAME:
			qb.setTables(Tables.FRIDGE);
			qb.setProjectionMap(fridgeItemsProjectionMap);
			qb.appendWhere(FridgeName.NAME + "='" + uri.getPathSegments().get(2) + "'");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy = "";
		if (TextUtils.isEmpty(sortOrder)) {
			switch(uriMatch){
			case FRIDGE_ITEMS | FRIDGE_ITEMS_ID:
				orderBy = FridgeItems.DEFAULT_SORT;
				break;
			case FRIDGE_NAME:
				orderBy = FridgeName.DEFAULT_SORT;
				break;
			}
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values == null) {
			throw new IllegalArgumentException("values passed were null");
		}

		String tableName;
		Uri contentUri;

		switch (uriMatcher.match(uri)) {
		case FRIDGE_ITEMS:
			tableName = Tables.FRIDGE;
			contentUri = FridgeItems.CONTENT_URI;
			if (necessaryValuesMissing(values, FridgeColumns.notNullValues)) {
				throw new IllegalArgumentException("some of the values passed were not set correctly");
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insert(tableName, "", values);

		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(contentUri, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (values == null) {
			throw new IllegalArgumentException("values passed were null");
		}

		int count;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
		case FRIDGE_ITEMS:
			if (necessaryValuesMissing(values, FridgeColumns.notNullValues)) {
				throw new IllegalArgumentException("some of the values passed were not set correctly");
			}
			count = db.update(Tables.FRIDGE, values, selection, selectionArgs);
			break;
		case FRIDGE_ITEMS_ID:
			if (necessaryValuesMissing(values, FridgeColumns.notNullValues)) {
				throw new IllegalArgumentException("some of the values passed were not set correctly");
			}
			String itemID = uri.getPathSegments().get(2);
			String where = TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')';
			count = db.update(Tables.FRIDGE, values, BaseColumns._ID + "=" + itemID + where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
		case FRIDGE_ITEMS:
			count = db.delete(Tables.FRIDGE, selection, selectionArgs);
			break;
		case FRIDGE_ITEMS_ID:
			String itemID = uri.getPathSegments().get(2);
			String where = TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')';
			count = db.delete(Tables.FRIDGE, BaseColumns._ID + "=" + itemID + where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private boolean necessaryValuesMissing(ContentValues values, String [] notNullValues){
		for(String s : notNullValues){
			if(!values.containsKey(s)){
				return true;
			}
		}
		return false;
	}

}