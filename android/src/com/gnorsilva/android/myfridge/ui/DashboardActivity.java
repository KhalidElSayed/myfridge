package com.gnorsilva.android.myfridge.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.Fridge;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.History;
import com.gnorsilva.android.myfridge.quickactions.AddToFridgeQA;
import com.gnorsilva.android.myfridge.utils.Utils;
import com.gnorsilva.android.myfridge.utils.ZXingIntentIntegrator;
import com.gnorsilva.android.myfridge.utils.ZXingIntentResultHandler;

public class DashboardActivity extends Activity {
	private AddToFridgeQA addItemsQA;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		addItemsQA = new AddToFridgeQA(this);
	}

	public void onFridgeClick(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(Fridge.CONTENT_TYPE);
		startActivity(intent);
	}

	public void onItemHistoryClick(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(History.CONTENT_TYPE);
		startActivity(intent);
	}

	public void onCookingClick(View v) {
	}

	public void onExpiringSoonClick(View v) {

	}

	public void onAddItemsClick(View v) {
		addItemsQA.show(v);
	}

	public void onRefreshClick(View v) {
	}

	public void onLogoClick(View v) {
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(intent == null){
			return;
		}
		
		switch (requestCode) {
			case ZXingIntentIntegrator.REQUEST_CODE:
				new ZXingIntentResultHandler().webSearch(requestCode, resultCode, intent, this);
			case Utils.HISTORY_PICK_REQUEST_ID:
				String name = (String) intent.getStringExtra(History.NAME);
				Intent anIntent = new Intent(Intent.ACTION_INSERT);
				anIntent.putExtra(Fridge.NAME,name);
				anIntent.setType(Fridge.CONTENT_ITEM_TYPE);
				startActivity(anIntent);
		}
	}
	
	

}
