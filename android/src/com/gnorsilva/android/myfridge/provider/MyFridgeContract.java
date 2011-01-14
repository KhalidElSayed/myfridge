package com.gnorsilva.android.myfridge.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MyFridgeContract {

	private MyFridgeContract() {
	}
	
	interface FridgeColumns {
		String NAME = "name";
		String QUANTITY = "quantity";
		String USE_BY_DATE = "use_by_date";
		String BARCODE = "barcode";
		String BARCODE_FORMAT = "barcode_format";
		String [] notNullValues = new String[]{NAME,QUANTITY,USE_BY_DATE};
	}

	interface HistoryColumns {
		String NAME = "name";
		String TIMES_ADDED = "times_added";
		String TOTAL_QUANTITY = "total_quantity";
		String BARCODE = "barcode";
		String BARCODE_FORMAT = "barcode_format";
		String [] notNullValues = new String[]{NAME,TIMES_ADDED,TOTAL_QUANTITY};
	}

	public static final String CONTENT_AUTHORITY = "com.gnorsilva.android.myfridge";
	
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_FRIDGE = "fridge";
	public static final String PATH_HISTORY = "history";
	public static final String PATH_ITEMS = "items";
	public static final String PATH_NAME = "name";

	public static final Uri FRIDGE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIDGE).build();
	public static final Uri HISTORY_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();
	
	public static final String ITEM_NAME = "itemName";
	public static final String BARCODE = "barcode";
	public static final String BARCODE_FORMAT = "barcodeFormat";
	
	public static final int DELETE_CONFIRMATION_DIALOG_ID = 101010;

	public static class Fridge implements FridgeColumns, BaseColumns {
		public static final Uri CONTENT_URI_ITEMS = FRIDGE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();
		public static final Uri CONTENT_URI_NAME = FRIDGE_CONTENT_URI.buildUpon().appendPath(PATH_NAME).build();
		
		public static final String DEFAULT_SORT = USE_BY_DATE + " ASC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myfridge.fridgeitem";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.myfridge.fridgeitem";

		public static Uri buildItemUri(long itemId) {
			return ContentUris.withAppendedId(CONTENT_URI_ITEMS, itemId);
		}

		public static long getItemId(Uri uri) {
			return ContentUris.parseId(uri);
		}
		
		public static Uri buildNameUri(String itemName) {
			return CONTENT_URI_NAME.buildUpon().appendPath(itemName).build();
		}

		public static String getItemName(Uri uri) {
			return uri.getPathSegments().get(2);
		}
	}
	
	public static class History implements HistoryColumns, BaseColumns {
		public static final Uri CONTENT_URI_ITEMS = HISTORY_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();
		public static final Uri CONTENT_URI_NAME = HISTORY_CONTENT_URI.buildUpon().appendPath(PATH_NAME).build();

		public static final String DEFAULT_SORT = TIMES_ADDED + " DESC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myfridge.historyitem";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.myfridge.historyitem";

		public static Uri buildItemUri(long itemId) {
			return ContentUris.withAppendedId(CONTENT_URI_ITEMS, itemId);
		}

		public static long getItemId(Uri uri) {
			return ContentUris.parseId(uri);
		}
		
		public static Uri buildNameUri(String itemName) {
			return CONTENT_URI_NAME.buildUpon().appendPath(itemName).build();
		}

		public static String getItemName(Uri uri) {
			return uri.getPathSegments().get(2);
		}
	}

}
