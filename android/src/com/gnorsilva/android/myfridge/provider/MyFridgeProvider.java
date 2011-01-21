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

import com.gnorsilva.android.myfridge.provider.MyFridgeContract.Fridge;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeColumns;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.History;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.HistoryColumns;
import com.gnorsilva.android.myfridge.provider.MyFridgeDatabase.Tables;

public class MyFridgeProvider extends ContentProvider {

	private MyFridgeDatabase dbHelper;
	private static HashMap<String, String> fridgeProjectionMap = buildFridgeProjectionMap();
	private static HashMap<String, String> historyProjectionMap = buildHistoryProjectionMap();
	private static final UriMatcher uriMatcher = buildUriMatcher();

	private static final int FRIDGE_ITEMS = 100;
	private static final int FRIDGE_ITEMS_ID = 101;
	private static final int FRIDGE_NAME = 102;
	
	private static final int HISTORY_ITEMS = 200;
	private static final int HISTORY_ITEMS_ID = 201;
	private static final int HISTORY_NAME = 202;
	
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MyFridgeContract.CONTENT_AUTHORITY;
		
        String path;
        
        path = MyFridgeContract.PATH_FRIDGE + "/" + MyFridgeContract.PATH_ITEMS;
        matcher.addURI(authority, path, FRIDGE_ITEMS);
        
        path = MyFridgeContract.PATH_FRIDGE + "/" + MyFridgeContract.PATH_ITEMS + "/#";
        matcher.addURI(authority, path, FRIDGE_ITEMS_ID);
        
        path = MyFridgeContract.PATH_FRIDGE + "/" + MyFridgeContract.PATH_NAME + "/*";
        matcher.addURI(authority, path, FRIDGE_NAME);
        
        path = MyFridgeContract.PATH_HISTORY + "/" + MyFridgeContract.PATH_ITEMS;
        matcher.addURI(authority, path, HISTORY_ITEMS);
        
        path = MyFridgeContract.PATH_HISTORY + "/" + MyFridgeContract.PATH_ITEMS + "/#";
        matcher.addURI(authority, path, HISTORY_ITEMS_ID);
        
        path = MyFridgeContract.PATH_HISTORY + "/" + MyFridgeContract.PATH_NAME + "/*";
        matcher.addURI(authority, path, HISTORY_NAME);
        
		return matcher;
	}
	
	private static HashMap<String, String> buildFridgeProjectionMap() {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put(BaseColumns._ID, BaseColumns._ID);
		map.put(FridgeColumns.NAME, FridgeColumns.NAME);
		map.put(FridgeColumns.QUANTITY, FridgeColumns.QUANTITY);
		map.put(FridgeColumns.USE_BY_DATE, FridgeColumns.USE_BY_DATE);
		map.put(FridgeColumns.BARCODE, FridgeColumns.BARCODE);
		map.put(FridgeColumns.BARCODE_FORMAT, FridgeColumns.BARCODE_FORMAT);
		return map;
	}
	
	private static HashMap<String,String> buildHistoryProjectionMap(){
		HashMap<String,String> map = new HashMap<String, String>();
		map.put(BaseColumns._ID, BaseColumns._ID);
		map.put(HistoryColumns.NAME, HistoryColumns.NAME);
		map.put(HistoryColumns.TIMES_ADDED, HistoryColumns.TIMES_ADDED);
		map.put(HistoryColumns.TOTAL_QUANTITY, HistoryColumns.TOTAL_QUANTITY);
		map.put(HistoryColumns.BARCODE, HistoryColumns.BARCODE);
		map.put(HistoryColumns.BARCODE_FORMAT, HistoryColumns.BARCODE_FORMAT);
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
			return Fridge.CONTENT_TYPE;
		case FRIDGE_ITEMS_ID | FRIDGE_NAME:
			return Fridge.CONTENT_ITEM_TYPE;
		case HISTORY_ITEMS:
			return History.CONTENT_TYPE;
		case HISTORY_ITEMS_ID:
			return History.CONTENT_ITEM_TYPE;
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
			qb.setProjectionMap(fridgeProjectionMap);
			break;
			
		case FRIDGE_ITEMS_ID:
			qb.setTables(Tables.FRIDGE);
			qb.setProjectionMap(fridgeProjectionMap);
			qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(2));
			break;
			
		case FRIDGE_NAME:
			qb.setTables(Tables.FRIDGE);
			qb.setProjectionMap(fridgeProjectionMap);
			qb.appendWhere(Fridge.NAME + "='" + uri.getPathSegments().get(2) + "'");
			break;
			
		case HISTORY_ITEMS:
			qb.setTables(Tables.HISTORY);
			qb.setProjectionMap(historyProjectionMap);
			break;
			
		case HISTORY_ITEMS_ID:
			qb.setTables(Tables.HISTORY);
			qb.setProjectionMap(historyProjectionMap);
			qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(2));
			break;
			
		case HISTORY_NAME:
			qb.setTables(Tables.HISTORY);
			qb.setProjectionMap(historyProjectionMap);
			qb.appendWhere(History.NAME + "='" + uri.getPathSegments().get(2) + "'");
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy = "";
		if (TextUtils.isEmpty(sortOrder)) {
			switch(uriMatch){
			case FRIDGE_ITEMS:
			case FRIDGE_ITEMS_ID :
			case FRIDGE_NAME:
				orderBy = Fridge.DEFAULT_SORT;
				break;
				
			case HISTORY_ITEMS :
			case HISTORY_ITEMS_ID :
			case HISTORY_NAME:
				orderBy = History.DEFAULT_SORT;
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
			contentUri = Fridge.CONTENT_URI_ITEMS;
			checkValues(values, FridgeColumns.notNullValues); 
			break;
			
		case HISTORY_ITEMS:
			tableName = Tables.HISTORY;
			contentUri = History.CONTENT_URI_ITEMS;
			checkValues(values, HistoryColumns.notNullValues);
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

		String itemID;
		String where;
		int count;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
		case FRIDGE_ITEMS_ID:
			checkValues(values, FridgeColumns.notNullValues);
			itemID = uri.getPathSegments().get(2);
			where = TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')';
			count = db.update(Tables.FRIDGE, values, BaseColumns._ID + "=" + itemID + where, selectionArgs);
			break;

		case HISTORY_ITEMS_ID:
			itemID = uri.getPathSegments().get(2);
			where = TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')';
			count = db.update(Tables.HISTORY, values, BaseColumns._ID + "=" + itemID + where, selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String itemID;
		String where;
		int count;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
		case FRIDGE_ITEMS:
			count = db.delete(Tables.FRIDGE, selection, selectionArgs);
			break;
			
		case FRIDGE_ITEMS_ID:
			itemID = uri.getPathSegments().get(2);
			where = TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')';
			count = db.delete(Tables.FRIDGE, BaseColumns._ID + "=" + itemID + where, selectionArgs);
			break;
			
		case HISTORY_ITEMS:
			count = db.delete(Tables.HISTORY, selection, selectionArgs);
			break;
			
		case HISTORY_ITEMS_ID:
			itemID = uri.getPathSegments().get(2);
			where = TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')';
			count = db.delete(Tables.HISTORY, BaseColumns._ID + "=" + itemID + where, selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private boolean checkValues(ContentValues values, String [] notNullValues){
		for(String s : notNullValues){
			if(!values.containsKey(s)){
				throw new IllegalArgumentException("some of the values passed were not set correctly");
			}
		}
		return false;
	}

}