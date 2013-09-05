package lab.davidahn.appshuttle.collect;

import static lab.davidahn.appshuttle.Settings.preferenceSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.davidahn.appshuttle.GlobalState;
import lab.davidahn.appshuttle.context.DuratinoUserBhvDao;
import lab.davidahn.appshuttle.context.DurationUserBhv;
import lab.davidahn.appshuttle.context.SnapshotUserCxt;
import lab.davidahn.appshuttle.context.SnapshotUserCxtDao;
import lab.davidahn.appshuttle.context.bhv.UserBhv;
import lab.davidahn.appshuttle.context.bhv.UserBhvManager;
import lab.davidahn.appshuttle.context.env.DurationUserEnv;
import lab.davidahn.appshuttle.context.env.DurationUserEnvDao;
import lab.davidahn.appshuttle.context.env.EnvType;
import lab.davidahn.appshuttle.context.env.UserEnv;
import android.app.IntentService;
import android.content.Intent;

public class CollectionService extends IntentService {
	private Calendar calendar;
    Map<EnvType, EnvSensor> sensors;
    List<BhvCollector> collectors;
	
	public CollectionService() {
		this("CollectionService");
	}
	
	public CollectionService(String name) {
		super(name);
	}

	public void onCreate() {
		super.onCreate();
		calendar = Calendar.getInstance();

		sensors = new HashMap<EnvType, EnvSensor>();
		sensors.put(EnvType.LOCATION, LocEnvSensor.getInstance(getApplicationContext()));
		sensors.put(EnvType.PLACE, PlaceEnvSensor.getInstance(getApplicationContext()));
		
		collectors = new ArrayList<BhvCollector>();
		collectors.add(AppBhvCollector.getInstance(getApplicationContext()));
		collectors.add(CallBhvCollector.getInstance(getApplicationContext()));
	}
	
	public void onHandleIntent(Intent intent){
		SnapshotUserCxt uCxt = new SnapshotUserCxt();

		uCxt.setTime(new Date(System.currentTimeMillis()));
		uCxt.setTimeZone(calendar.getTimeZone());
		
		for(EnvType envType : sensors.keySet()){
			EnvSensor sensor = sensors.get(envType);
			UserEnv uEnv = sensor.sense();
			
			uCxt.addUserEnv(envType, uEnv);

			DurationUserEnv durationUserEnv = sensor.refineDurationUserEnv(uCxt);
			if(durationUserEnv != null)
				storeDurationUserEnv(durationUserEnv);
		}
		
		for(BhvCollector collector : collectors){
			List<UserBhv> userBhvList = collector.collect();
			uCxt.addUserBhvAll(userBhvList);
			
			List<DurationUserBhv> durationUserBhvList = 
					collector.refineDurationUserBhv(uCxt.getTime(), uCxt.getTimeZone(), userBhvList);
			storeDurationUserBhv(durationUserBhvList);

			UserBhvManager userBhvManager = UserBhvManager.getInstance(getApplicationContext());
			for(DurationUserBhv durationUserBhv : durationUserBhvList){
				UserBhv uBhv = durationUserBhv.getBhv();
				if(uBhv.isValid(getApplicationContext()))
					userBhvManager.registerBhv(uBhv);
			}
		}
		
		GlobalState.currUserCxt = uCxt;
		storeSnapshotCxt(uCxt);
	}
	
	private void storeSnapshotCxt(SnapshotUserCxt uCxt) {
		if(preferenceSettings.getBoolean("collection.store_cxt.enabled", false)) {
			SnapshotUserCxtDao snapshotUserCxtDao = SnapshotUserCxtDao.getInstance(getApplicationContext());
			snapshotUserCxtDao.storeCxt(uCxt);
		}
	}

	private void storeDurationUserEnv(DurationUserEnv durationUserEnv) {
		DurationUserEnvDao durationUserEnvDao = DurationUserEnvDao.getInstance(getApplicationContext());
		durationUserEnvDao.storeDurationUserEnv(durationUserEnv);
	}
	
	private void storeDurationUserBhv(List<DurationUserBhv> durationUserBhvList) {
		DuratinoUserBhvDao durationUserBhvDao = DuratinoUserBhvDao.getInstance(getApplicationContext());
		for(DurationUserBhv durationUserBhv : durationUserBhvList){
			durationUserBhvDao.storeRfdCxt(durationUserBhv);
		}		
	}

	public void onDestroy() {
		super.onDestroy();
	}
}