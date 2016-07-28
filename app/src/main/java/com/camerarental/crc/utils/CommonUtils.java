package com.camerarental.crc.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.camerarental.crc.R;
import com.camerarental.crc.SplashActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    private static final String PREF_RESERVED_CART_ARRAY = "pref_reserved_cart_array";

    public static DisplayImageOptions mThumbOptions;
    public static DisplayImageOptions mImageOptions;

    /**********************************************************************************************/
    /*                                  Related User Interface                                    */
    /**********************************************************************************************/

    /**
     * Move to destination activity class with animate transition.
     */
    public static void moveNextActivity(Activity source, Class<?> destinationClass, boolean removeSource) {
        Intent intent = new Intent(source, destinationClass);

        if (removeSource) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        source.startActivity(intent);

        if (removeSource) {
            source.finish();
        }

        source.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * Create error AlertDialog.
     */
    public static Dialog createErrorAlertDialog(final Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null).create();
    }

    /**
     * Check if given Intent on current device
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0/*PackageManager.MATCH_DEFAULT_ONLY*/);

        return list.size() > 0;
    }

    /**
     * Check if given mail has correct format.
     */
    public static boolean isEmailValid(String email) {
        String regExpression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

        Pattern pattern = Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    /**
     * Return registered email in Android device.
     *
     * @param type substring that be searched
     */
    public static String getRegisteredEmail(Context context, String type) {
        Account[] accounts = AccountManager.get(context).getAccounts();

        for (Account account : accounts) {
            // account.name as an email address only for certain account.type values.
            String possibleEmail = account.name;
            Log.i("Email ID", possibleEmail);

            if (possibleEmail.contains(type)) {
                return possibleEmail;
            }
        }

        return null;
    }

    /**
     * Convert dip to pixels
     */
    public static int dipToPixels(Context context, int dip) {
        /*DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;*/
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    public static int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int pixelToDip(Context context, int px) {
        /*Resources r = context.getResources();
        float dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, r.getDisplayMetrics());
        return (int) dip;*/
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static ShapeDrawable getDefaultBackground(Context context, int radius) {
        int r = dipToPixels(context, radius);
        float[] outerR = new float[] {r, r, r, r, r, r, r, r};

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(context.getResources().getColor(R.color.badge_color));

        return drawable;
    }

    public static boolean inViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];

        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        if (size.x > size.y) {
            size = new Point(size.y, size.x);
        }

        return size;
    }

    /**********************************************************************************************/
    /*                                      SharedPreferences                                     */
    /**********************************************************************************************/

    /* Preference setting */
    public static Set<String> loadCartArray(Context context) {
        if (context == null) {
            return new LinkedHashSet<>();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(PREF_RESERVED_CART_ARRAY, null);
    }

    public static void saveCartArray(Context context, Set<String> cartArray) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet(PREF_RESERVED_CART_ARRAY, cartArray);
        editor.apply();
    }

    /**********************************************************************************************/
    /*                                  Related Image and audio                                   */
    /**********************************************************************************************/

    /**
     * directory name to store captured images and videos
     */
    private static final String IMAGE_DIRECTORY_NAME = "captured_image";

    public static File getOutputMediaFile(Context context) {
        // External sdcard location
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists())
//        {
//            if (!mediaStorageDir.mkdirs())
//            {
//                Log.d(TAG, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
//                return null;
//            }
//        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static String getMimeType(Context context, Uri fileUri) {
        ContentResolver cr = context.getContentResolver();
        String mimeType = cr.getType(fileUri);

        Log.d(TAG, "returned mime_type = " + mimeType);
        return mimeType;
    }

    /**
     * Convert image uri to file
     */
    public static String/*File*/ convertImageUriToFile(Context context, Uri imageUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID/*, MediaStore.Images.ImageColumns.ORIENTATION*/};
            cursor = context.getContentResolver().query(
                    imageUri,
                    projection, // Which columns to return
                    null,       // WHERE clause; which rows to return (all rows)
                    null,       // WHERE clause selection arguments (none)
                    null);      // Order-by clause (ascending by name)

            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            if (cursor.moveToFirst()) {
                //String orientation = cursor.getString(orientation_ColumnIndex);
                return cursor.getString(file_ColumnIndex)/*new File(cursor.getString(file_ColumnIndex))*/;
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Get bitmap from internal image file.
     */
    public static Bitmap getBitmapFromUri(Uri fileUri) {
        // bitmap factory
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        // downsizing photoImage as it throws OutOfMemory Exception for larger
//        // images
//        options.inSampleSize = 8;
//        options.inMutable = true;
//
//        return BitmapFactory.decodeFile(fileUri.getPath(), options);

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), newOpts);// 此时返回bm为空

        if (bitmap != null)
            bitmap.recycle();

        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 480f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(fileUri.getPath(), newOpts);

        return bitmap;
    }

    /**
     * Get Thumbnail bitmap
     */
    public static Bitmap getThumbnail(Bitmap origin) {
        Bitmap bmpThumbnail = origin;

        int nImgWidth = origin.getWidth();
        int nImgHeight = origin.getHeight();

        if (nImgWidth > 150 || nImgHeight > 150) {
            bmpThumbnail = Bitmap.createScaledBitmap(origin, 150, 150, false);

            //bmpThumbnail = compressBitmap(bmpThumbnail, 10 * 1024);
        }

        return bmpThumbnail;
    }

    /**
     * Compress bitmap
     */
    public static byte[] compressBitmap(Bitmap origin, int nTargetSize) {
        int nRatio = 90;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        origin.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        int nSize = stream.toByteArray().length;

        while (nSize > nTargetSize) {
            stream.reset();
            origin.compress(Bitmap.CompressFormat.JPEG, nRatio, stream);
            nRatio -= 10;

            nSize = stream.toByteArray().length;
        }

        return stream.toByteArray();
    }

    /**
     * Delete a files with given file path
     */
    public static void deleteFile(String strFilePath) {
        File file = new File(strFilePath);
        file.delete();
    }

    /**
     * Get current date on local
     */
    public static Date currentDate() {
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("gmt"));
        String strTime = format.format(currentDate);

        SimpleDateFormat formatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            currentDate = formatLocal.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return currentDate;
    }

    /**
     * Get the string that are represents "<u>underlineStr</u> normalStr"
     */
    public static SpannableString getNormalUnderlineString(String normalStr, String underlineStr) {
        SpannableString ss;

        if (TextUtils.isEmpty(normalStr))
            normalStr = "";

        if (TextUtils.isEmpty(underlineStr)) {
            ss = new SpannableString(normalStr);
        } else {
            int pos = underlineStr.length();

            ss = new SpannableString(normalStr + underlineStr);
            //ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new UnderlineSpan(), normalStr.length(), normalStr.length() + underlineStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //ss.setSpan(new ForegroundColorSpan(Color.GRAY), pos + 2, boldStr.length() + normalStr.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ss;
    }

    /**
     * Get the string that are represents "<b>colorStr</b> normalStr"
     */
    public static SpannableString getColorNormalString(Context context, String colorStr, String normalStr) {
        SpannableString ss;

        if (TextUtils.isEmpty(colorStr)) {
            ss = new SpannableString(normalStr);
        } else {
            int pos = colorStr.length();

            ss = new SpannableString(colorStr + " " + normalStr);
            ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.tint_color)), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), pos + 1, colorStr.length() + normalStr.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ss;
    }

    /**
     * Get formatted date string
     */
    public static String getFormattedDateString(Date date, String format) {
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(date);
    }

    /**********************************************************************************************/
    /*                                  Get System Information                                    */
    /**********************************************************************************************/

    /**
     * Get device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Get App version
     */
    public static String getAppVersion(Context context) {
        final String unknown = "Unknown";

        if (context == null) return unknown;

        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException ex) {
        }

        return unknown;
    }

    /*
     * Get SHA value to register Facebook
     */
    public static void getKeyHashFacebook(Context context) {
        // Add code to print out the key hash
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************************************/
    /*                                  Hide / Show Keyboard                                      */
    /**********************************************************************************************/

    /**
     * Hide always Soft Keyboard
     *
     * @param context is current Activity
     */
    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            //editText.clearFocus();
            //editText.setInputType(0);
        }
    }

    /**
     * Show always Soft Keyboard
     *
     * @param context is current Activity
     */
    public static void showKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText != null) {
            imm.showSoftInput(editText, 0);
        }
    }

    /**********************************************************************************************/
    /*                  Badge function was enabled in only Samsung products                       */
    /**********************************************************************************************/

    /**
     * Get badge count
     */
    public static int getBadge(Context context) {
        int badgeCount = 0;
        Cursor cursor = null;

        try {
            // This is the content uri for the BadgeProvider
            Uri uri = Uri.parse("content://com.sec.badge/apps");

            cursor = context.getContentResolver().query(uri, null, "package IS ?", new String[]{context.getPackageName()}, null);

            // This indicates the provider doesn't exist and you probably aren't running
            // on a Samsung phone running TWLauncher. This has to be outside of try/finally block
            if (cursor == null) {
                return -1;
            }

            if (!cursor.moveToFirst()) {
                // No results. Nothing to query
                return -1;
            }

            do {
                String pkg = cursor.getString(1);
                String clazz = cursor.getString(2);
                badgeCount = cursor.getInt(3);
                Log.d("BadgeTest", "package: " + pkg + ", class: " + clazz + ", count: " + String.valueOf(badgeCount));
            } while (cursor.moveToNext());
        } finally {
            if (cursor != null) cursor.close();
        }

        return badgeCount;
    }

    /**
     * Increment badge count
     */
    public static void incrementBadge(Context context) {
        int count = getBadge(context);
        setBadge(context, count + 1);
    }

    /**
     * Set badge count
     */
    public static void setBadge(Context context, int count) {
        try {
            //context.getContentResolver().delete(Uri.parse("content://com.sec.badge/apps"), "package IS ?", new String[] {context.getPackageName()});

            ContentValues cv = new ContentValues();
            cv.put("package", context.getPackageName());

            // Name of your activity declared in the manifest as android.intent.action.MAIN.
            // Must be fully qualified name as shown below
            cv.put("class", context.getPackageName() + "." + SplashActivity.class.getSimpleName());
            cv.put("badgecount", count); // integer count you want to display

            if (getBadge(context) == -1) {
                // Execute insert
                context.getContentResolver().insert(Uri.parse("content://com.sec.badge/apps"), cv);
            } else {
                context.getContentResolver().update(Uri.parse("content://com.sec.badge/apps"), cv, "package IS ?", new String[]{context.getPackageName()});
            }
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    /**
     * Clear badge count to 0
     */
    public static void clearBadge(Context context) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("badgecount", 0);
            context.getContentResolver().update(Uri.parse("content://com.sec.badge/apps"), cv, "package IS ?", new String[]{context.getPackageName()});
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

}
