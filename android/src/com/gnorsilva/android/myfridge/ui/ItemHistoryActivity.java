package com.gnorsilva.android.myfridge.ui;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.History;

public class ItemHistoryActivity extends ListActivity {

	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_item_history);
		
		Cursor cursor = managedQuery(History.CONTENT_URI_ITEMS, null, null, null, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_my_fridge_contents, cursor,
				new String[] { History.TIMES_ADDED, History.TOTAL_QUANTITY, History.NAME  },
				new int[] { R.id.fridge_usebydate, R.id.fridge_quantity, R.id.fridge_name });
        
        setListAdapter(adapter);
	}
	
	public void onHomeClick(View v){
		
	}
	
	public void onRefreshClick(View v){
		
	}

}
