package com.gnorsilva.android.myfridge.ui;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.Fridge;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.History;
import com.gnorsilva.android.myfridge.utils.Utils;

public class AddItemActivity extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int FIELDS_INCORRECT_DIALOG_ID = 1;
	private static final int ITEM_ADDED_DIALOG_ID = 2;
	private static final int ITEM_UPDATED_DIALOG_ID = 3;
	private static final int ADD_MORE_DIALOG_ID = 4;
	private static final int DISCARD_DIALOG_ID = 5;
	private static final int USE_BY_DATE_WARNING_DIALOG_ID = 6;
	private static final int QUANTITY_ERROR_DIALOG_ID = 7;
	
	private Button dateButton;
	private EditText itemNameTextView;
	private EditText quantityTextView;
	
	private String barcode;
	private String barcodeFormat;
	private String quantityString;
	private String itemName;
	private String dateButtonOriginalText;
	
	private int thisYear;
    private int thisMonth;
    private int today;
	private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private boolean update;
	private boolean goingHome;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_add_item);
		
		dateButtonOriginalText = getResources().getString(R.string.use_by_date);
		setTodaysDate();
		dateButton = (Button) findViewById(R.id.btn_add_item_use_by_date);
		itemNameTextView = (EditText) findViewById(R.id.add_item_name);
		quantityTextView = (EditText) findViewById(R.id.add_item_quantity);
		
		Intent intent = getIntent();
		
		if(intent.hasExtra(Fridge.NAME)){
			itemName = intent.getStringExtra(Fridge.NAME);
			itemNameTextView.setText(itemName);
		}
		
		if(intent.hasExtra(Fridge.BARCODE)){
			barcode = intent.getStringExtra(Fridge.BARCODE);
		}
		
		if(intent.hasExtra(Fridge.BARCODE_FORMAT)){
			barcodeFormat = intent.getStringExtra(Fridge.BARCODE_FORMAT);
		}
	}
	
	public void onHomeClick(View v){
		goingHome = true;
		showDialog(DISCARD_DIALOG_ID);
	}
	
	public void onRefreshClick(View v){

	}
	
	public void onUseByDateClick(View v){
		showDialog(DATE_DIALOG_ID);
	}
	
	public void onAcceptClick(View v){
		quantityString = quantityTextView.getText().toString();
		itemName = itemNameTextView.getText().toString().trim();
		
		if (TextUtils.isEmpty(quantityString) | TextUtils.isEmpty(itemName) | dateButton.getText().equals(dateButtonOriginalText)) {
			showDialog(FIELDS_INCORRECT_DIALOG_ID);
			return;
		}
		
		if(selectedDateIsBeforeToday()){
			showDialog(USE_BY_DATE_WARNING_DIALOG_ID);
			return;
		}

		long quantity = Long.parseLong(quantityString);

		if(quantity < 1){
			showDialog(QUANTITY_ERROR_DIALOG_ID);
			return;
		}
		
		String date = selectedYear + "/" + ( selectedMonth+1 ) + "/" + selectedDay;
		
		Uri fridgeUri = Fridge.buildNameUri(itemName);
		Cursor fridgeCursor = managedQuery(fridgeUri, null, null, null, null);
		
		long originalQuantity = 0;
		
		while(fridgeCursor.moveToNext()){
			String testDate = fridgeCursor.getString(fridgeCursor.getColumnIndex(Fridge.USE_BY_DATE));
			if(testDate.equals(date)){
				long itemId = fridgeCursor.getLong(fridgeCursor.getColumnIndex(Fridge._ID));
				originalQuantity = fridgeCursor.getLong(fridgeCursor.getColumnIndex(Fridge.QUANTITY));
				quantity += originalQuantity;
				fridgeUri = Fridge.buildItemUri(itemId);
				update = true;
				break;
			}
		}
		
		ContentResolver contentResolver = getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(Fridge.NAME, itemName);
		values.put(Fridge.QUANTITY, quantity);
		values.put(Fridge.USE_BY_DATE, date);
		
		if(!TextUtils.isEmpty(barcode)){
			values.put(Fridge.BARCODE, barcode);
		}
		
		if(!TextUtils.isEmpty(barcodeFormat)){
			values.put(Fridge.BARCODE_FORMAT, barcodeFormat);
		}
		
		if(update){
			contentResolver.update(fridgeUri, values, null, null);
		}else{
			contentResolver.insert(Fridge.CONTENT_URI_ITEMS, values);
		}
		
		Uri historyUri = History.buildNameUri(itemName);
		Cursor historyCursor = managedQuery(historyUri, null, null, null, null);
		ContentValues historyValues = new ContentValues();
		Uri historyItemUri;
		
		if(historyCursor.moveToFirst()){
			long timesAdded = historyCursor.getLong(historyCursor.getColumnIndex(History.TIMES_ADDED));
			long totalQuantity = historyCursor.getLong(historyCursor.getColumnIndex(History.TOTAL_QUANTITY));
			long itemId = historyCursor.getLong(historyCursor.getColumnIndex(History._ID));

			timesAdded ++;
			totalQuantity += quantity - originalQuantity;
			
			historyItemUri = History.buildItemUri(itemId);
			historyValues.put(History.TIMES_ADDED, timesAdded);
			historyValues.put(History.TOTAL_QUANTITY, totalQuantity);
			contentResolver.update(historyItemUri, historyValues, null, null);
		}else{
			historyValues.put(History.NAME, itemName);
			historyValues.put(History.TIMES_ADDED, 1L);
			historyValues.put(History.TOTAL_QUANTITY, quantity);
			historyItemUri = History.CONTENT_URI_ITEMS;
			
			if(!TextUtils.isEmpty(barcode)){
				values.put(History.BARCODE, barcode);
			}
			
			if(!TextUtils.isEmpty(barcodeFormat)){
				values.put(History.BARCODE_FORMAT, barcodeFormat);
			}
			
			contentResolver.insert(historyItemUri, historyValues);
		}
		
		if(update){
			showDialog(ITEM_UPDATED_DIALOG_ID);
		}else{
			showDialog(ITEM_ADDED_DIALOG_ID);
		}
	}
	
	public void onDiscardClick(View v){
		showDialog(DISCARD_DIALOG_ID);
	}
	
	private boolean selectedDateIsBeforeToday() {
		if(selectedYear < thisYear){
			return true;
		}else if(selectedMonth < thisMonth){
			return true;
		}else if(selectedDay < today){
			return true;
		}
		return false;
	}

	private void setTodaysDate() {
		final Calendar c = Calendar.getInstance();
        thisYear = c.get(Calendar.YEAR);
        thisMonth = c.get(Calendar.MONTH);
        today = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, onDateSetListener, thisYear, thisMonth, today);
		case FIELDS_INCORRECT_DIALOG_ID:
			return getFieldsIncorrectDialog();
		case ITEM_ADDED_DIALOG_ID:
			return getItemAddedDialog();
		case ITEM_UPDATED_DIALOG_ID:
			return getItemUpdatedDialog();
		case ADD_MORE_DIALOG_ID:
			return getAddMoreDialog();
		case DISCARD_DIALOG_ID:
			return getDiscardDialog();
		case USE_BY_DATE_WARNING_DIALOG_ID:
			return getUseByDateWarningDialog();
		case QUANTITY_ERROR_DIALOG_ID:
			return getQuantityErrorDialog();
		}
		return null;
	}

	private Dialog getFieldsIncorrectDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.fields_incomplete))
		       .setCancelable(false)
		       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

		return builder.create();
	}
	
	private Dialog getItemAddedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.item_added_successfully); 
		String buttonText = getResources().getString(R.string.button_ok); 
		
		builder.setMessage(message)
		       .setCancelable(false)
		       .setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                AddItemActivity.this.showDialog(ADD_MORE_DIALOG_ID);
		           }
		       });
		
		return builder.create();
	}

	private Dialog getItemUpdatedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.item_updated_successfully); 
		String buttonText = getResources().getString(R.string.button_ok); 
		
		builder.setMessage(message)
		       .setCancelable(false)
		       .setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                AddItemActivity.this.showDialog(ADD_MORE_DIALOG_ID);
		           }
		       });
		
		return builder.create();
	}
	
	private Dialog getAddMoreDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.like_to_add_more);
		String yes = getResources().getString(R.string.yes);
		String no = getResources().getString(R.string.no);
		
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton(yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                AddItemActivity.this.finish();
		                Intent intent = new Intent(Intent.ACTION_INSERT);
		                intent.setType(Fridge.CONTENT_ITEM_TYPE);
		                AddItemActivity.this.startActivity(intent);
		           }
		       })
		       .setNegativeButton(no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   AddItemActivity.this.finish();
		           }
		       });
		
		AlertDialog alert = builder.create();
		
		return alert;
	}
	
	private Dialog getDiscardDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.like_to_discard))
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   if(goingHome){
		        		   Utils.goHome(AddItemActivity.this);
		        	   }else{
		        		   AddItemActivity.this.showDialog(ADD_MORE_DIALOG_ID);   
		        	   }
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

		return builder.create();
	}
	
	private Dialog getUseByDateWarningDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.use_by_date_warning); 
		String buttonText = getResources().getString(R.string.button_ok); 
		
		builder.setMessage(message)
		       .setCancelable(false)
		       .setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		
		return builder.create();
	}
	
	private Dialog getQuantityErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.quantity_error_warning); 
		String buttonText = getResources().getString(R.string.button_ok); 
		
		builder.setMessage(message)
		       .setCancelable(false)
		       .setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		
		return builder.create();
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
