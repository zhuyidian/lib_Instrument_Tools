package com.dunn.tools.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AppDatabase {
	private AtomicInteger mOpenCounter = new AtomicInteger();

	/**
	 * Database Name
	 */
	public static String DATABASE_NAME = "app_database";
	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "appinfolist"; //应用列表
	/**
	 * option
	 */
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;


	/**
	 * Table columns
	 */
	public static final String KEY_ID = "id";
	public static final String KEY_PACKAGE_NAME = "package_name";
	public static final String KEY_CLASS_NAME = "class_name";
	public static final String KEY_LABEL = "label";
	public static final String KEY_SORT = "sort";
	public static final String KEY_BLOB = "blob";
	/**
	 * 表列数据集合
	 **/
	private String[] col = {KEY_ID, KEY_PACKAGE_NAME, KEY_CLASS_NAME, KEY_LABEL, KEY_SORT, KEY_BLOB};

	private static final String CREATE_DATABASE_TABLE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_PACKAGE_NAME
			+ " text not null, " + KEY_CLASS_NAME + " text not null, "
			+ KEY_LABEL + " text not null, " + KEY_SORT + " integer default '0', " + KEY_BLOB + " BLOB);";


	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx the Context within which to work
	 */
	public AppDatabase(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * This method is used for creating/opening connection
	 *
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	public synchronized AppDatabase open() throws SQLException {
		if (mOpenCounter.incrementAndGet() == 1) {
			mDbHelper = new DatabaseHelper(mCtx);
			mDb = mDbHelper.getWritableDatabase();
		}
		return this;
	}

	/**
	 * This method is used for closing the connection.
	 */
	public synchronized void close() {
		if (mOpenCounter.decrementAndGet() == 0) {
			//mDb.endTransaction();
			mDbHelper.close();
		}
	}

	/**
	 * @param packageName
	 * @param sort
	 * @param bmp
	 * @return
	 */
	public long setData(String packageName, String className, String label, int sort, Bitmap bmp) {
		ContentValues initialValues = new ContentValues();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
		initialValues.put(KEY_PACKAGE_NAME, packageName);
		initialValues.put(KEY_CLASS_NAME, className);
		initialValues.put(KEY_LABEL, label);
		initialValues.put(KEY_SORT, sort);
		initialValues.put(KEY_BLOB, os.toByteArray());//以字节形式保存

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = null;

		Cursor cursor = mDb.query(DATABASE_TABLE, col, null, null, null, null, null);//数据的查询
		HashMap<String, Object> bindData = null;
		list = new ArrayList<Map<String, Object>>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			bindData = new HashMap<String, Object>();
			bindData.put(KEY_ID, cursor.getLong(0));
			bindData.put(KEY_PACKAGE_NAME, cursor.getString(1));
			bindData.put(KEY_CLASS_NAME, cursor.getString(2));
			bindData.put(KEY_LABEL, cursor.getString(3));
			bindData.put(KEY_SORT, cursor.getInt(4));
			/**得到Bitmap字节数据**/
			byte[] in = cursor.getBlob(5);
			/**
			 * 根据Bitmap字节数据转换成 Bitmap对象
			 * BitmapFactory.decodeByteArray() 方法对字节数据，从0到字节的长进行解码，生成Bitmap对像。
			 **/
			Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
			bindData.put(KEY_BLOB, bmpout);

			list.add(bindData);
		}
		cursor.close();
		return list;
	}

	/**
	 * Inner private class. Database Helper class for creating and updating
	 * database.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * onCreate method is called for the 1st time when database doesn't
		 * exists.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DATABASE_TABLE);
		}

		/**
		 * onUpgrade method is called when database version changes.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			if (oldVersion == 1) {
//				String addCameraMac = "alter table " + DATABASE_TABLE
//						+ " add column " + KEY_CAMERA_MAC + " text";
//				String addCameraPushWeixin = "alter table " + DATABASE_TABLE
//						+ " add column " + KEY_PUSH_WX + " text";
//				db.execSQL(addCameraMac);
//				db.execSQL(addCameraPushWeixin);
//
//				oldVersion = 2;
//				db.setVersion(2);
//			}
//			if (oldVersion == 2) {
//				String addPermisson = "alter table " + DATABASE_TABLE
//						+ " add column " + KEY_PERMISSION + " text";
//				String addLawsPwd = "alter table " + DATABASE_TABLE
//						+ " add column " + KEY_LAWS_PWD + " text";
//				String addDualAuthentication = "alter table " + DATABASE_TABLE
//						+ " add column " + KEY_DUALAUTHENTICATION
//						+ " integer default '-1'";
//				db.execSQL(addPermisson);
//				db.execSQL(addLawsPwd);
//				db.execSQL(addDualAuthentication);
//
//				oldVersion = 3;
//				db.setVersion(3);
//			}
		}
	}
}