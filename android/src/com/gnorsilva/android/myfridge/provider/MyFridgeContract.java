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

	interface AddedColumns {
		String NAME = "name";
		String TOTAL_ADDED = "total_added";
		String TOTAL_QUANTITY = "total_quantity";
		String BARCODE = "barcode";
		String BARCODE_FORMAT = "barcode_format";
		String [] notNullValues = new String[]{NAME,TOTAL_ADDED,TOTAL_QUANTITY};
	}

	public static final String CONTENT_AUTHORITY = "com.gnorsilva.android.myfridge";
	
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_FRIDGE = "fridge";
	private static final String PATH_ADDED = "added";
	private static final String PATH_ITEMS = "items";
	private static final String PATH_NAME = "name";

	private static final Uri FRIDGE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIDGE).build();
	private static final Uri ADDED_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADDED).build();
	
	public static final String ITEM_NAME = "itemName";
	public static final String BARCODE = "barcode";
	public static final String BARCODE_FORMAT = "barcodeFormat";
	
	public static final int DELETE_CONFIRMATION_DIALOG_ID = 101010;

	public static class FridgeItems implements FridgeColumns, BaseColumns {
		public static final Uri CONTENT_URI = FRIDGE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

		public static final String DEFAULT_SORT = USE_BY_DATE + " ASC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myfridge.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.myfridge.item";

		public static Uri buildFridgeItemUri(long itemId) {
			return ContentUris.withAppendedId(CONTENT_URI, itemId);
		}

		public static long getFridgeItemId(Uri uri) {
			return ContentUris.parseId(uri);
		}
	}
	
	public static class FridgeName implements FridgeColumns, BaseColumns {
		public static final Uri CONTENT_URI = FRIDGE_CONTENT_URI.buildUpon().appendPath(PATH_NAME).build();
		
		public static final String DEFAULT_SORT = USE_BY_DATE + " ASC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myfridge.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.myfridge.item";
		
		public static Uri buildFridgeNameUri(String itemName) {
			return CONTENT_URI.buildUpon().appendPath(itemName).build();
		}

		public static String getFridgeItemName(Uri uri) {
			return uri.getPathSegments().get(2);
		}
	}

	public static class AddedItems implements AddedColumns, BaseColumns {
		public static final Uri CONTENT_URI = ADDED_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

		public static final String DEFAULT_SORT = TOTAL_ADDED + " DESC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myfridge.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.myfridge.item";

		public static Uri buildAddedItemUri(long itemId) {
			return ContentUris.withAppendedId(CONTENT_URI, itemId);
		}

		public static long getAddedItemId(Uri uri) {
			return ContentUris.parseId(uri);
		}
	}

}
