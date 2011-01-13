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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gnorsilva.android.myfridge.R;
import com.gnorsilva.android.myfridge.provider.MyFridgeContract.FridgeItems;

public class EditItemActivity extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int FIELDS_INCOMPLETE_DIALOG_ID = 1;
	private static final int ITEM_UPDATED_DIALOG_ID = 2;
	private static final int USE_BY_DATE_WARNING_DIALOG_ID = 3;
	private static final int DISCARD_DIALOG_ID = 4;

	private Button dateButton;
	private EditText addItemNameText;
	private EditText quantityText;

	private String itemName;
	private long quantity;
	private String useByDate;
	private Uri uri;
	
	private int thisYear;
    private int thisMonth;
    private int today;
	private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_add_item);
		
		((TextView) findViewById(R.id.add_item_title)).setText(R.string.edit_item);

		dateButton = (Button) findViewById(R.id.btn_add_item_use_by_date);
		addItemNameText = (EditText) findViewById(R.id.add_item_name);
		quantityText = (EditText) findViewById(R.id.add_item_quantity);
		
		uri = (Uri) getIntent().getParcelableExtra("uri");
		Cursor cursor = managedQuery(uri, null, null, null,null);

		cursor.moveToFirst();
		
		itemName = cursor.getString(cursor.getColumnIndex(FridgeItems.NAME));
		quantity = cursor.getLong(cursor.getColumnIndex(FridgeItems.QUANTITY));
		useByDate = cursor.getString(cursor.getColumnIndex(FridgeItems.USE_BY_DATE));

		addItemNameText.setText(itemName);
		quantityText.setText("" + quantity);
		
		String [] dates = useByDate.split("/");
		selectedYear = Integer.parseInt(dates[0]);
		selectedMonth = Integer.parseInt(dates[1]) - 1;
		selectedDay = Integer.parseInt(dates[2]);
		
		dateButton.setText(useByDate);
		
		setTodaysDate();
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
		
		if (TextUtils.isEmpty(quantityString) | TextUtils.isEmpty(productDetails) ){
			showDialog(FIELDS_INCOMPLETE_DIALOG_ID);
			return;
		}
		
		if(selectedDateIsBeforeToday()){
			showDialog(USE_BY_DATE_WARNING_DIALOG_ID);
			return;
		}
		
		quantity = Long.parseLong(quantityString);
		ContentResolver contentResolver = getContentResolver();
		ContentValues values = new ContentValues();
		
		//TODO take into account the total numbers of items added historically
		//---need to check for item's name in the added database, then add quantity to total_quantity
		
		values.put(FridgeItems.NAME, productDetails);
		values.put(FridgeItems.QUANTITY, quantity);
		values.put(FridgeItems.USE_BY_DATE, date);
		
		contentResolver.update(uri, values,null,null);
		
		showDialog(ITEM_UPDATED_DIALOG_ID);
	}
	
	public void onDiscardClick(View v){
		showDialog(DISCARD_DIALOG_ID);
	}
	
	private boolean selectedDateIsBeforeToday(){
		if(selectedYear < thisYear){
			return true;
		}else if(selectedMonth < thisMonth){
			return true;
		}else if(selectedDay < today){
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, onDateSetListener, selectedYear, selectedMonth, selectedDay);
		case ITEM_UPDATED_DIALOG_ID:
			return getItemUpdatedDialog();
		case FIELDS_INCOMPLETE_DIALOG_ID:
			return getFieldsIncompleteDialog();
		case USE_BY_DATE_WARNING_DIALOG_ID:
			return getUseByDateWarningDialog();
		case DISCARD_DIALOG_ID:
			return getDiscardDialog();
		}
		return null;
	}
	
	private Dialog getItemUpdatedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.item_updated_successfully); 
		String buttonText = getResources().getString(R.string.button_ok); 
		
		builder.setMessage(message)
		       .setCancelable(false)
		       .setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                EditItemActivity.this.finish();
		           }
		       });
		
		return builder.create();
	}

	private Dialog getFieldsIncompleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getResources().getString(R.string.fields_incomplete); 
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
	
	private Dialog getDiscardDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.like_to_discard_changes))
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   EditItemActivity.this.finish();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

		return builder.create();
	}
	
	private void setTodaysDate() {
		final Calendar c = Calendar.getInstance();
        thisYear = c.get(Calendar.YEAR);
        thisMonth = c.get(Calendar.MONTH);
        today = c.get(Calendar.DAY_OF_MONTH);
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
