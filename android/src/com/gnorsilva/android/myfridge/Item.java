package com.gnorsilva.android.myfridge;

import com.gnorsilva.android.myfridge.utils.CustomDate;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable{

	private long databaseId;
	private String itemDetails;
	private String itemProductData;
	private String itemDataFormat = "default";
	private long quantity;
	private CustomDate useByDate;
	private long totalQuantity = 0;
	
	public Item() {
	}
	
	public Item(String itemDetails, String itemProductData, String itemDataFormat) {
		this.itemDetails = itemDetails;
		this.itemProductData = itemProductData;
		this.itemDataFormat = itemDataFormat;
	}

	@Override
	public int describeContents() {
		try {
			return Integer.parseInt(itemProductData);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(itemDetails);
		out.writeString(itemProductData);
		out.writeString(itemDataFormat);
	}

	private Item(Parcel in) {
		itemDetails = in.readString();
		itemProductData = in.readString();
		itemDataFormat = in.readString();
	}

	public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
		public Item createFromParcel(Parcel in) {
			return new Item(in);
		}

		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

	
	public String toString(){
		return databaseId + " / " + itemDetails + " / " + itemProductData + " / " + itemDataFormat  + " / " + 
		quantity  + " / " +  useByDate.toString()  + " / " + totalQuantity;
	}
	
	public String getItemDetails() {
		return itemDetails;
	}

	public String getItemProductData() {
		return itemProductData;
	}
	
	public String getItemDataFormat() {
		return itemDataFormat;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public long getTotalQuantity() {
		return totalQuantity;
	}
	
	public void setTotalQuantity(long totalQuantity){
		this.totalQuantity = totalQuantity;
	}
	public void increaseTotalQuantity(long quantity) {
		this.totalQuantity += quantity;
	}

	public CustomDate getUseByDate() {
		return useByDate;
	}

	public void setUseByDate(CustomDate useByDate) {
		this.useByDate = useByDate;
	}
	
	public void setDatabaseId(long databaseId){
		this.databaseId = databaseId;
	}

	public void setDetails(String itemDetails) {
		this.itemDetails = itemDetails;
	}

}
