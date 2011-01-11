package com.gnorsilva.android.myfridge.barcodes;

import android.app.Activity;
import android.content.Intent;

import com.gnorsilva.android.myfridge.ui.WebSearchResultsActivity;
import com.gnorsilva.android.myfridge.utils.ZXingIntentIntegrator;
import com.gnorsilva.android.myfridge.utils.ZXingIntentResult;

public class ZXingIntentHandler {

	public void webSearch(int requestCode, int resultCode, Intent intent, Activity activity) {
		ZXingIntentResult barcodeScan = ZXingIntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

		if(barcodeScan.getContents()!=null){
			Intent itemSearchIntent = new Intent(activity, WebSearchResultsActivity.class);
			itemSearchIntent.putExtra("itemProductData", barcodeScan.getContents());
			itemSearchIntent.putExtra("itemDataFormat", barcodeScan.getFormatName());
			activity.startActivity(itemSearchIntent);
		}
	}
	
}
