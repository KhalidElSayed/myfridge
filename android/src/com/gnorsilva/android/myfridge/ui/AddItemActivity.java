package com.gnorsilva.android.myfridge.ui;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.gnorsilva.android.myfridge.Item;
import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeItems;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeName;

public class AddItemActivity extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int ADD_MORE_DIALOG_ID = 1;
	private static final int FIELDS_INCORRECT_DIALOG_ID = 2;
	
	private Item item;
	private Button dateButton;
	private EditText addItemNameText;
	private EditText quantityText;
	
	private String barcode;
	private String barcodeFormat;
	
	private int year;
    private int month;
    private int day;
	private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_add_item);
		
		setTodaysDate();
		dateButton = (Button) findViewById(R.id.btn_add_item_use_by_date);
		addItemNameText = (EditText) findViewById(R.id.add_item_name);
		quantityText = (EditText) findViewById(R.id.add_item_quantity);
		
		item = (Item) getIntent().getParcelableExtra("SelectedItem");
		if(item != null){
			addItemNameText.setText(item.getItemDetails());
		}
	}
	
	public void onHomeClick(View v){
		
	}
	
	public void onRefreshClick(View v){
		
	}
	
	public void onUseByDateClick(View v){
		showDialog(DATE_DIALOG_ID);
	}
	
	public void onAcceptClick(View v){
		String quantityString = quantityText.getText().toString();
		String itemName = addItemNameText.getText().toString();
		String dateButtonOriginalText = getResources().getString(R.string.use_by_date);
		
		if (TextUtils.isEmpty(quantityString) | 
			TextUtils.isEmpty(itemName) | 
			dateButton.getText().equals(dateButtonOriginalText)) {
			
			showDialog(FIELDS_INCORRECT_DIALOG_ID);
			Log.e("MyFridge", "One or more fields were not set correctly");
			return;
		}

		String date = selectedYear + "/" + ( selectedMonth+1 ) + "/" + selectedDay;
		long quantity = Long.parseLong(quantityString);
		
		//		TODO find a way to take into account the total numbers of items added historically
//		item.increaseTotalQuantity(quantity);
		
		Uri uri = FridgeName.buildFridgeNameUri(itemName);
		boolean update = false;
		
		Cursor cursor = managedQuery(uri, null, null, null, null);
		
		while(cursor.moveToNext()){
			String testDate = cursor.getString(cursor.getColumnIndex(FridgeName.USE_BY_DATE));
			if(testDate.equals(date)){
				long itemId = cursor.getLong(cursor.getColumnIndex(FridgeName._ID));
				quantity += cursor.getLong(cursor.getColumnIndex(FridgeName.QUANTITY));
				uri = FridgeItems.buildFridgeItemUri(itemId);
				update = true;
				break;
			}
		}
		
		ContentResolver contentResolver = getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(FridgeItems.NAME, itemName);
		values.put(FridgeItems.QUANTITY, quantity);
		values.put(FridgeItems.USE_BY_DATE, date);
		
		if(!TextUtils.isEmpty(barcode)){
			values.put(FridgeItems.QUANTITY, barcode);
		}
		
		if(!TextUtils.isEmpty(barcodeFormat)){
			values.put(FridgeItems.QUANTITY, barcodeFormat);
		}
		
		if(update){
			contentResolver.update(uri, values, null, null);
		}else{
			contentResolver.insert(FridgeItems.CONTENT_URI, values);
		}
		
		showDialog(ADD_MORE_DIALOG_ID);
	}
	
	private void setTodaysDate() {
		final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, onDateSetListener, year, month, day);
		case ADD_MORE_DIALOG_ID:
			// TODO implement "add any more items?" Dialog
			return null;
		case FIELDS_INCORRECT_DIALOG_ID:
			// TODO implement "FIELDS_INCOMPLETE" Dialog
			return null;
		}
		return null;
	}
	
	private OnDateSetListener onDateSetListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int currentYear, int monthOfYear, int dayOfMonth) {
			selectedYear = currentYear;
			selectedMonth = monthOfYear;
			selectedDay = dayOfMonth;
			updateDateHasChanged();
		}
	};
	
	private void updateDateHasChanged() {
		java.util.Date date = new java.util.Date((selectedYear - 1900), selectedMonth, selectedDay);
		String formattedDate = date.toLocaleString().replace(" 00:00:00", "");
		dateButton.setText(formattedDate);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		String dateButtonOriginalText = getResources().getString(R.string.use_by_date);
		if (!dateButton.getText().equals(dateButtonOriginalText)) {
			outState.putInt("year", selectedYear);
			outState.putInt("month", selectedMonth);
			outState.putInt("day", selectedDay);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("year")) {
			selectedYear = savedInstanceState.getInt("year");
			selectedMonth = savedInstanceState.getInt("month");
			selectedDay = savedInstanceState.getInt("day");
			updateDateHasChanged();
		}
	}
}
