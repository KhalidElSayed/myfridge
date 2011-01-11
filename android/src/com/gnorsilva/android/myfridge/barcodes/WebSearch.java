package com.gnorsilva.android.myfridge.barcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.gnorsilva.android.myfridge.Item;

@SuppressWarnings("unused")
public class WebSearch {

	public static final String NAME_SEARCH = "Name Search";
	public static final String BARCODE_SEARCH = "Barcode Search";
	public static final String PRODUCTID_SEARCH = "ProductId Search";
	public static final String UPC_DATABASE_RPC_KEY = "ba88ded7443fb2c270bb2a08e7382d72081cfcc4";

	private WebSearch() {
	}

	public static Item[] searchDataContentsAndFormat(String itemProductData, String itemDataFormat) {
//		if (itemDataFormat.equalsIgnoreCase("UPC_A")) {
//			return searchUPC_ADatabases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("UPC_E")) {
//			return searchUPC_EDatabases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("EAN_8")) {
//			return searchEAN_8Databases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("EAN_13")) {
//			return searchEAN_13Databases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("CODE_39")) {
//			return searchCODE_39Databases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("CODE_93")) {
//			return searchCODE_93Databases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("CODE_128")) {
//			return searchCODE_128Databases(itemProductData, itemDataFormat);
//
//		} else if (itemDataFormat.equalsIgnoreCase("QR_CODE")) {
//			return searchQR_CODEDatabases(itemProductData, itemDataFormat);
//
//		}
//		return null;
		return getItems(itemProductData, itemDataFormat, 20);
	}



	@SuppressWarnings("unchecked")
	private static Item[] searchEAN_13Databases(String itemProductData, String itemDataFormat) {
		List<Item> itemsFound = new ArrayList<Item>();

		HashMap result = searchUPCdatabase("ean", itemProductData);

		if (result != null) {
			String resultSize = result.get("size").toString();
			String resultDesc = result.get("description").toString();

			itemsFound.add(new Item(resultDesc, itemProductData, itemDataFormat));
			
			Log.e("XXXXXXXX___SIZE", resultSize);
			Log.e("XXXXXXXX___DESC", resultDesc);
		}

		return itemsFound.toArray(new Item[] {});
	}

	
	private static Item[] searchUPC_ADatabases(String itemProductData, String itemDataFormat) {
		return null;
	}

	private static Item[] searchQR_CODEDatabases(String itemProductData, String itemDataFormat) {
		return null;
	}

	private static Item[] searchCODE_128Databases(String itemProductData, String itemDataFormat) {
		return null;
	}

	private static Item[] searchCODE_93Databases(String itemProductData, String itemDataFormat) {
		return null;
	}

	private static Item[] searchCODE_39Databases(String itemProductData, String itemDataFormat) {
		return null;
	}

	private static Item[] searchEAN_8Databases(String itemProductData, String itemDataFormat) {
		return null;
	}

	private static Item[] searchUPC_EDatabases(String itemProductData, String itemDataFormat) {
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private static HashMap searchUPCdatabase(String code, String codeFormat) {
//		try {
//			XMLRPCClient client = new XMLRPCClient("http://www.upcdatabase.com/xmlrpc");
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("rpc_key", UPC_DATABASE_RPC_KEY);
//			params.put(codeFormat, code);
////			return (HashMap) client.call("lookup", params);
//			return (HashMap) client.call("test", params);
//		} catch (NullPointerException nl) {
//			nl.printStackTrace();
//		} catch (XMLRPCException e) {
//			Log.e("XMLRPCException", e.getMessage());
//		}
		return null;
	}
	
	private static Item[] getItems(String itemProductData, String itemDataFormat, int num) {
		Item[] items = new Item[num];
		for (int i = 0; i < num; i++) {
			items[i] = new Item("Item number: " + i, itemProductData, itemDataFormat);
		}
		return items;
	}


}
