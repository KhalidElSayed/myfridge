package com.gnorsilva.android.myfridge.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.barcodes.ZXingIntentHandler;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeItems;
import com.gnorsilva.android.myfridge.quickactions.AddItemsQA;
import com.gnorsilva.android.myfridge.quickactions.EditItemsQA;

public class MyFridgeActivity extends ListActivity{
	private AddItemsQA addItemsQA;
	
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
		Uri uri = FridgeItems.CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
		new EditItemsQA(this,uri).show(v);
	}
}
