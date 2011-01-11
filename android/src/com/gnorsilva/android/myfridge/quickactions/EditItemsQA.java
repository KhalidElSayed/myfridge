package com.gnorsilva.android.myfridge.quickactions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.custom.quickactions.ActionItem;
import com.android.custom.quickactions.QuickAction;
import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.ui.EditItemActivity;

public class EditItemsQA {
	private ActionItem edit;
	private ActionItem delete;
	private QuickAction qa;
	private Activity activity;
	private Uri uri;

	private OnClickListener getDeleteListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				activity.getContentResolver().delete(uri, null, null);
			}
		};
	}

	private OnClickListener getEditListener(final QuickAction qa) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa.dismiss();
				
				Intent intent = new Intent(activity,EditItemActivity.class);
				intent.putExtra("uri", uri);
				activity.startActivity(intent);
			}
		};
	}

	public EditItemsQA(Activity activity, Uri uri) {
		this.activity = activity;
		this.uri = uri;
		
		edit = new ActionItem();
		delete = new ActionItem();

		edit.setTitle(activity.getResources().getString(R.string.edit_item));
		edit.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_edit));

		delete.setTitle(activity.getResources().getString(R.string.delete));
		delete.setIcon(activity.getResources().getDrawable(R.drawable.quickaction_btn_delete));
	}

	public void show(View v) {
		qa = new QuickAction(v);

		edit.setOnClickListener(getEditListener(qa));
		delete.setOnClickListener(getDeleteListener(qa));

		qa.addActionItem(edit);
		qa.addActionItem(delete);

		qa.setAnimStyle(QuickAction.ANIM_AUTO);
		qa.show();
	}
}
