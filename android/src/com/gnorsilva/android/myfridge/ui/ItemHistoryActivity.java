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
import android.widget.TextView;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.History;
import com.gnorsilva.android.myfridge.quickactions.ItemHistoryQA;

public class ItemHistoryActivity extends ListActivity {
	private ItemHistoryQA itemHistoryQA;
	protected Uri selectedItemUri;
	
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_item_history);
		
		Cursor cursor = managedQuery(History.CONTENT_URI_ITEMS, null, null, null, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_item_history_contents, cursor,
				new String[] { History.TIMES_ADDED, History.TOTAL_QUANTITY, History.NAME  },
				new int[] { R.id.history_item_times_added, R.id.history_item_total_quantity, R.id.history_item_name});
        
        setListAdapter(adapter);
	}
	
	public void onHomeClick(View v){
		
	}
	
	public void onRefreshClick(View v){
		
	}

    @Override
    protected void onListItemClick(ListView l, View view, int position, long id) {
        String action = getIntent().getAction();
        
        if (Intent.ACTION_PICK.equals(action)) {
        	TextView textView = (TextView) view.findViewById(R.id.history_item_name);
        	String name = (String) textView.getText();
            setResult(RESULT_OK, new Intent().putExtra(History.NAME, name));
            finish();
        } else {
        	selectedItemUri = History.buildItemUri(id);
        	itemHistoryQA = new ItemHistoryQA(this, selectedItemUri);
        	itemHistoryQA.show(view);
        }
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
		String yes = getResources().getString(R.string.yes);
		String no = getResources().getString(R.string.no);
		
		builder.setMessage(getResources().getString(R.string.delete_item_confirmation))
		       .setCancelable(false)
		       .setPositiveButton(yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Uri uri = ItemHistoryActivity.this.selectedItemUri;
		        	   ItemHistoryActivity.this.getContentResolver().delete(uri, null, null);
		           }
		       })
		       .setNegativeButton(no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

		return builder.create();
	} 
	
}
