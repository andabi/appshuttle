package lab.davidahn.appshuttle.context.bhv;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lab.davidahn.appshuttle.DBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class UserBhvDao {
	private static UserBhvDao userBhvDao;
	private SQLiteDatabase db;
	
	private UserBhvDao(Context cxt) {
		db = DBHelper.getInstance(cxt).getWritableDatabase();
	}

	public static UserBhvDao getInstance(Context cxt) {
		if (userBhvDao == null)
			userBhvDao = new UserBhvDao(cxt);
		return userBhvDao;
	}

	public void storeUserBhv(UserBhv uBhv) {
		Gson gson = new GsonBuilder().setDateFormat("EEE MMM dd hh:mm:ss zzz yyyy").create();

		ContentValues row = new ContentValues();
		row.put("bhv_type", uBhv.getBhvType().toString());
		row.put("bhv_name", uBhv.getBhvName());
		row.put("metas", gson.toJson(uBhv.getMetas()));
//		db.insert("user_bhv", null, row);
		db.insertWithOnConflict("list_user_bhv", null, row, SQLiteDatabase.CONFLICT_REPLACE);
		Log.i("stored user bhv", uBhv.toString());
	}
	
	public Set<UserBhv> retrieveUserBhv() {
		Gson gson = new GsonBuilder().setDateFormat("EEE MMM dd hh:mm:ss zzz yyyy").create();

		Cursor cur = db.rawQuery("SELECT * FROM list_user_bhv;", null);
		Set<UserBhv> res = new HashSet<UserBhv>();
		
		while (cur.moveToNext()) {
			BhvType bhvType= BhvType.valueOf(cur.getString(0));
			String bhvName= cur.getString(1);
			Type listType = new TypeToken<HashMap<String, Object>>(){}.getType();
			Map<String, Object> metas = gson.fromJson(cur.getString(2), listType);
			UserBhv uBhv = new UserBhv(bhvType, bhvName);
			uBhv.setMetas(metas);
			res.add(uBhv);
		}
		cur.close();
//		Log.i("retrieved userBhv", res.toString());
		return res;
	}
	
	public void deleteUserBhv(UserBhv uBhv) {
		db.execSQL("DELETE FROM list_user_bhv WHERE bhv_type = '"
				+ uBhv.getBhvType() + "' AND bhv_name = '" + uBhv.getBhvName() +"';");
	}
}