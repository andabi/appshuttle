package lab.davidahn.appshuttle.collect;

import java.util.Date;
import java.util.List;

import lab.davidahn.appshuttle.AppShuttleApplication;
import lab.davidahn.appshuttle.context.SnapshotUserCxt;
import lab.davidahn.appshuttle.context.bhv.BaseUserBhv;
import lab.davidahn.appshuttle.context.bhv.DurationUserBhv;
import lab.davidahn.appshuttle.context.bhv.DurationUserBhvDao;
import lab.davidahn.appshuttle.context.bhv.UserBhvManager;
import lab.davidahn.appshuttle.context.env.DurationUserEnvManager;
import lab.davidahn.appshuttle.mine.matcher.MatchedResultDao;
import lab.davidahn.appshuttle.mine.matcher.PredictedBhvDao;
import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

public class CompactionService extends IntentService {
	private SnapshotUserCxt _currUserCxt;
	
	public CompactionService() {
		this("CompactionService");
	}
	
	public CompactionService(String name) {
		super(name);
	}

	public void onCreate() {
		super.onCreate();
		_currUserCxt = AppShuttleApplication.currUserCxt;
	}
	
	public void onHandleIntent(Intent intent){
		SharedPreferences preferenceSettings = AppShuttleApplication.getContext().getPreferenceSettings();

		long expirationDuration = preferenceSettings.getLong("service.compaction.expiration", 15 * AlarmManager.INTERVAL_DAY);
		Date expirationBoundTimeDate = new Date(_currUserCxt.getTimeDate().getTime() - expirationDuration);

		compactHistoryUserBhv(expirationBoundTimeDate);
		compactHistoryUserEnv(expirationBoundTimeDate);
		
		compactPredictedBhv(expirationBoundTimeDate);
		compactMatchedResult(expirationBoundTimeDate);

		compactUserBhv(expirationBoundTimeDate);
	}
	
	public void onDestroy() {
		super.onDestroy();
	}

	private void compactHistoryUserBhv(Date expirationBoundTimeDate) {
		DurationUserBhvDao durationUserBhvDao = DurationUserBhvDao.getInstance();
		durationUserBhvDao.deleteBefore(expirationBoundTimeDate);
	}

	private void compactHistoryUserEnv(Date expirationBoundTimeDate) {
		DurationUserEnvManager durationUserEnvManager = DurationUserEnvManager.getInstance();
		durationUserEnvManager.deleteAllBefore(expirationBoundTimeDate);
	}
	
	private void compactPredictedBhv(Date expirationBoundTimeDate) {
		PredictedBhvDao predictedBhvDao = PredictedBhvDao.getInstance();
		predictedBhvDao.deletePredictedBhvInfo(expirationBoundTimeDate);
	}

	private void compactMatchedResult(Date expirationBoundTimeDate) {
		MatchedResultDao matchedResultDao = MatchedResultDao.getInstance();
		matchedResultDao.deleteMatchedResult(expirationBoundTimeDate);
	}
	
	private void compactUserBhv(Date expirationBoundTimeDate) {
		UserBhvManager userBhvManager = UserBhvManager.getInstance();
		DurationUserBhvDao durationUserBhvDao = DurationUserBhvDao.getInstance();
		
		for(BaseUserBhv uBhv : userBhvManager.getOrdinaryBhvSet()){
			List<DurationUserBhv> durationUserBhvList = durationUserBhvDao.retrieveByBhv(expirationBoundTimeDate, _currUserCxt.getTimeDate(), uBhv);
			if(!durationUserBhvList.isEmpty())
				continue;
			userBhvManager.unregisterBhv(uBhv);
		}
	}
}
