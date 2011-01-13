package com.gnorsilva.android.myfridge.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.barcodes.ZXingIntentHandler;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeItems;
import com.gnorsilva.android.myfridge.quickactions.AddItemsQA;
import com.gnorsilva.android.myfridge.quickactions.EditItemsQA;

public class MyFridgeActivity extends ListActivity{
	private AddItemsQA addItemsQA;
	private Uri selectedItemUri;
	
	// TODO provide sorting mechanism

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_my_fridge);
		
		addItemsQA = new AddItemsQA(this);

		Cursor cursor = managedQuery(FridgeItems.CONTENT_URI, null, null, null, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_my_fridge_contents, cursor,
				new String[] { FridgeItems.USE_BY_DATE, FridgeItems.QUANTITY, FridgeItems.NAME  },
				new int[] { R.id.fridge_usebydate, R.id.fridge_quantity, R.id.fridge_name });
        
        setListAdapter(adapter);
	}
	
	public void onAddItemsClick(View v){
		addItemsQA.show(v);
	}
	
    public void onHomeClick(View v) {
    }

    public void onRefreshClick(View v) {
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		new ZXingIntentHandler().webSearch(requestCode, resultCode, intent, this);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id){
		selectedItemUri = FridgeItems.CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
		new EditItemsQA(this,selectedItemUri).show(v);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case MyFridgeContract.DELETE_CONFIRMATION_DIALOG_ID:
			return getDeleteConfirmationDialog();
		}
		return null;
	}

	private Dialog getDeleteConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.delete_item_confirmation))
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Uri uri = MyFridgeActivity.this.selectedItemUri;
		        	   MyFridgeActivity.this.getContentResolver().delete(uri, null, null);
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

		return builder.create();
	}
}
