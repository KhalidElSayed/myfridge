package com.gnorsilva.android.myfridge.quickactions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.custom.quickactions.ActionItem;
import com.android.custom.quickactions.QuickAction;
import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.Fridge;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.History;
import com.gnorsilva.android.myfridge.utils.Utils;
import com.gnorsilva.android.myfridge.utils.ZXingIntentIntegrator;

public class AddToFridgeQA {
	private ActionItem barcode;
	private ActionItem itemHistory;
	private ActionItem manual;
	private QuickAction qa;
	private Activity activity;
	
	public OnClickListener getBarcodeListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				ZXingIntentIntegrator.initiateScan(activity);
			}
		};
	}

	public OnClickListener getItemHistoryListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
		    	Intent intent = new Intent(Intent.ACTION_PICK);
		    	intent.setType(History.CONTENT_TYPE);
		    	activity.startActivityForResult(intent, Utils.HISTORY_PICK_REQUEST_ID);
			}
		};
	}

	public OnClickListener getManualListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
		    	Intent intent = new Intent(Intent.ACTION_INSERT);
		    	intent.setType(Fridge.CONTENT_ITEM_TYPE);
		    	activity.startActivity(intent);
			}
		};
	}
	
	public AddToFridgeQA(Activity activity){
		this.activity = activity;
		
		barcode = new ActionItem();
		itemHistory = new ActionItem();
		manual =  new ActionItem();
		
		barcode.setTitle(activity.getResources().getString(R.string.barcode));
		barcode.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_barcode));
		
		itemHistory.setTitle(activity.getResources().getString(R.string.item_history));
		itemHistory.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_item_history));
		
		manual.setTitle(activity.getResources().getString(R.string.manual));
		manual.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_keyboard));
	}
	
	public void show(View v){
		qa = new QuickAction(v);
		
		barcode.setOnClickListener(getBarcodeListener(qa));
		itemHistory.setOnClickListener(getItemHistoryListener(qa));
		manual.setOnClickListener(getManualListener(qa));
		
		qa.addActionItem(manual);
		qa.addActionItem(barcode);
		qa.addActionItem(itemHistory);
		
		qa.setAnimStyle(QuickAction.ANIM_AUTO);
		qa.show();
	}
}



