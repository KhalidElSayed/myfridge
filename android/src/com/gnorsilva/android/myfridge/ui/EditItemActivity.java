package com.gnorsilva.android.myfridge.ui;

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

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeItems;

public class EditItemActivity extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int FIELDS_INCOMPLETE_DIALOG_ID = 2;

	private Button dateButton;
	private EditText addItemNameText;
	private EditText quantityText;

	private String itemName;
	private long quantity;
	private String useByDate;
	private Uri uri;
	
	private int year;
    private int month;
    private int day;
	private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_edit_item);

		dateButton = (Button) findViewById(R.id.btn_edit_item_use_by_date);
		addItemNameText = (EditText) findViewById(R.id.edit_item_name);
		quantityText = (EditText) findViewById(R.id.edit_item_quantity);
		
		uri = (Uri) getIntent().getParcelableExtra("uri");
		Cursor cursor = managedQuery(uri, null, null, null,null);

		cursor.moveToFirst();
		
		itemName = cursor.getString(cursor.getColumnIndex(FridgeItems.NAME));
		quantity = cursor.getLong(cursor.getColumnIndex(FridgeItems.QUANTITY));
		useByDate = cursor.getString(cursor.getColumnIndex(FridgeItems.USE_BY_DATE));

		addItemNameText.setText(itemName);
		quantityText.setText("" + quantity);
		
		String [] dates = useByDate.split("/");
		selectedYear = year = Integer.parseInt(dates[0]);
		selectedMonth = month = Integer.parseInt(dates[1]) - 1;
		selectedDay = day = Integer.parseInt(dates[2]);
		
		dateButton.setText(useByDate);
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
		String productDetails = addItemNameText.getText().toString();
		String date = selectedYear + "/" + ( selectedMonth+1 ) + "/" + selectedDay;
		
		if (TextUtils.isEmpty(quantityString) | TextUtils.isEmpty(productDetails)) {
			showDialog(FIELDS_INCOMPLETE_DIALOG_ID);
			Log.e("MyFridge", "One or more fields were null");
			return;
		}
		
		quantity = Long.parseLong(quantityString);
		ContentResolver contentResolver = getContentResolver();
		ContentValues values = new ContentValues();
		
		//TODO find a way to take into account the total numbers of items added historically
		
		values.put(FridgeItems.NAME, productDetails);
		values.put(FridgeItems.QUANTITY, quantity);
		values.put(FridgeItems.USE_BY_DATE, date);
		
		contentResolver.update(uri, values,null,null);
		
		//TODO show successful update confirmation dialog
		//TODO return user to activity where he came from
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, onDateSetListener, year, month, day);
		case FIELDS_INCOMPLETE_DIALOG_ID:
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
