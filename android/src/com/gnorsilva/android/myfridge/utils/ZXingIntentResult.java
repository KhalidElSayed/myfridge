package com.gnorsilva.android.myfridge.utils;


/**
 * <p>Encapsulates the result of a barcode scan invoked through {@link ZXingIntentIntegrator}.</p>
 *
 * @author Sean Owen
 */
public final class ZXingIntentResult extends Object{

  private final String contents;
  private final String formatName;

  ZXingIntentResult(String contents, String formatName) {
    this.contents = contents;
    this.formatName = formatName;
  }

  /**
   * @return raw content of barcode
   */
  public String getContents() {
    return contents;
  }

  /**
   * @return name of format, like "QR_CODE", "UPC_A". See <code>BarcodeFormat</code> for more format names.
   */
  public String getFormatName() {
    return formatName;
  }

}

