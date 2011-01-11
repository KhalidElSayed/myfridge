package com.gnorsilva.android.myfridge.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;

import com.gnorsilva.android.myfridge.R;

public class RemoveItemsActivity extends ListActivity {
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_my_fridge);
	}

    public void onHomeClick(View v) {
    }

    public void onRefreshClick(View v) {
    }
}
