package com.gnorsilva.android.myfridge.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;

import com.gnorsilva.android.myfridge.R;

public class ExpiringSoonActivity extends ListActivity {
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_expiring_soon);
	}
	
    public void onHomeClick(View v) {
    }

    public void onRefreshClick(View v) {
    }
    
	//TODO read contents of database to ArrayList
	//TODO sort items by date
	
}
