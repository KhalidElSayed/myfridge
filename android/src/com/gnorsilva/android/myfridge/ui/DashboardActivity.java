package com.gnorsilva.android.myfridge.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gnorsilva.android.myfridge.barcodes.ZXingIntentHandler;

import com.gnorsilva.android.myfridge.quickactions.AddItemsQA;
import com.gnorsilva.android.myfridge.R;

public class DashboardActivity extends Activity{
	private AddItemsQA addItemsQA;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		addItemsQA = new AddItemsQA(this);
	}
	
	public void onFridgeClick(View v){
        startActivity(new Intent(this, MyFridgeActivity.class));
    }

    public void onCookingClick(View v) {
    }
    
    public void onExpiringSoonClick(View v) {
        startActivity(new Intent(this, ExpiringSoonActivity.class));
    }
    
    public void onAddItemsClick(View v){
		addItemsQA.show(v);
    }
    
    public void onRefreshClick(View v){
    }
    
    public void onLogoClick(View v){
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		new ZXingIntentHandler().webSearch(requestCode, resultCode, intent, this);
	}
    
}
