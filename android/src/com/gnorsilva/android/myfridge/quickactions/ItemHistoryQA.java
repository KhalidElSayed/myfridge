package com.gnorsilva.android.myfridge.quickactions;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.custom.quickactions.ActionItem;
import com.android.custom.quickactions.QuickAction;
import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract;

public class ItemHistoryQA {
	private ActionItem addToFridge;
	private ActionItem delete;
	private QuickAction qa;
	private Activity activity;
	private Uri uri;
	
	private OnClickListener getDeleteListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				activity.showDialog(MyFridgeContract.DELETE_CONFIRMATION_DIALOG_ID);
			}
		};
	}
	
	private OnClickListener getAddToFridgeListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				
				
//				Intent intent = new Intent(Intent.ACTION_EDIT,uri);
//				activity.startActivity(intent);
			}
		};
	}

	public ItemHistoryQA(Activity activity, Uri uri) {
		this.activity = activity;
		this.uri = uri;
		
		addToFridge = new ActionItem();
		delete = new ActionItem();

		addToFridge.setTitle(activity.getResources().getString(R.string.add_to_fridge));
		addToFridge.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_add_to_fridge));

		delete.setTitle(activity.getResources().getString(R.string.delete));
		delete.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_delete));
	}

	public void show(View v) {
		qa = new QuickAction(v);

		addToFridge.setOnClickListener(getAddToFridgeListener(qa));
		delete.setOnClickListener(getDeleteListener(qa));

		qa.addActionItem(addToFridge);
		qa.addActionItem(delete);

		qa.setAnimStyle(QuickAction.ANIM_AUTO);
		qa.show();
	}
}
