package com.gnorsilva.android.myfridge.utils;

import android.content.Context;
import android.content.Intent;

import com.gnorsilva.android.myfridge.ui.DashboardActivity;

public class Utils {

	public static final int HISTORY_PICK_REQUEST_ID = 0x0415708e;
	
    public static void goHome(Context context) {
        final Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
	
}
