package com.gnorsilva.android.myfridge.quickactions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.custom.quickactions.ActionItem;
import com.android.custom.quickactions.QuickAction;
import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.ui.AddItemActivity;
import com.gnorsilva.android.myfridge.ui.AddedItemsActivity;
import com.gnorsilva.android.myfridge.utils.ZXingIntentIntegrator;

public class AddItemsQA {
	private ActionItem barcode;
	private ActionItem addedItems;
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

	public OnClickListener getAddedItemsListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				activity.startActivity(new Intent(activity, AddedItemsActivity.class));
			}
		};
	}

	public OnClickListener getKeyboardListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				activity.startActivity(new Intent(activity, AddItemActivity.class));
			}
		};
	}
	
	public AddItemsQA(Activity activity){
		this.activity = activity;
		
		barcode = new ActionItem();
		addedItems = new ActionItem();
		manual =  new ActionItem();
		
		barcode.setTitle(activity.getResources().getString(R.string.barcode));
		barcode.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_barcode));
		
		addedItems.setTitle(activity.getResources().getString(R.string.added_items));
		addedItems.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_added_items));
		
		manual.setTitle(activity.getResources().getString(R.string.manual));
		manual.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_keyboard));
	}
	
	public void show(View v){
		qa = new QuickAction(v);
		
		barcode.setOnClickListener(getBarcodeListener(qa));
		addedItems.setOnClickListener(getAddedItemsListener(qa));
		manual.setOnClickListener(getKeyboardListener(qa));
		
		qa.addActionItem(barcode);
		qa.addActionItem(addedItems);
		qa.addActionItem(manual);
		
		qa.setAnimStyle(QuickAction.ANIM_AUTO);
		qa.show();
	}
}



