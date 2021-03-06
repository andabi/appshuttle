package lab.davidahn.appshuttle.view;

import java.util.ArrayList;
import java.util.List;

import lab.davidahn.appshuttle.AppShuttleApplication;
import lab.davidahn.appshuttle.R;
import lab.davidahn.appshuttle.collect.bhv.UserBhv;
import lab.davidahn.appshuttle.predict.PredictedBhv;
import android.content.Context;
import android.text.format.DateUtils;

public class HistoryPresentBhv extends PresentBhv implements Comparable<HistoryPresentBhv>{
	private long recentPredictionTime;
	private double recentPredictionScore;
	private static HistoryPresentBhvDao dao = HistoryPresentBhvDao.getInstance();
	
	public HistoryPresentBhv(UserBhv uBhv) {
		super(uBhv);
		dao = HistoryPresentBhvDao.getInstance();
	}
	
	public long getRecentPredictionTime() {
		return recentPredictionTime;
	}
	public double getRecentPredictionScore() {
		return recentPredictionScore;
	}

	public void setRecentPredictionTime(long recentPredictionTime) {
		this.recentPredictionTime = recentPredictionTime;
	}
	public void setRecentPredictionScore(double recentPredictionScore) {
		this.recentPredictionScore = recentPredictionScore;
	}

	@Override
	public ViewableBhvType getViewableBhvType() {
		return ViewableBhvType.HISTORY;
	}

	@Override
	public String getViewMsg() {
		StringBuffer msg = new StringBuffer();
		viewMsg = msg.toString();
		
		CharSequence relativeTimeSpan = DateUtils.getRelativeTimeSpanString(recentPredictionTime, 
				System.currentTimeMillis(), 
				DateUtils.MINUTE_IN_MILLIS, 
				0);
		
		Context cxt = AppShuttleApplication.getContext();
		msg.append(String.format(cxt.getString(R.string.history_present_bhv_view_msg), relativeTimeSpan));

		viewMsg = msg.toString();
		
		return viewMsg;
	}
	
	@Override
	public int compareTo(HistoryPresentBhv _uBhv) {
		long time = _uBhv.getRecentPredictionTime();
		if (recentPredictionTime > time)	return 1;
		else if (recentPredictionTime < time) return -1;
		else {
			double score = _uBhv.getRecentPredictionScore();
			if (recentPredictionScore > score)	return 1;
			else if (recentPredictionScore == score) return 0;
			else return -1;
		}
	}
	
	public static List<HistoryPresentBhv> extractHistoryPresentBhvList() {
		List<HistoryPresentBhv> res = new ArrayList<HistoryPresentBhv>();
		for (UserBhv bhv : PredictedPresentBhv.getPredictedPresentBhvList()) {
			if (PredictedBhv.getPredictedBhvList().contains(bhv))
				continue;
			HistoryPresentBhv hisPresentBhv = new HistoryPresentBhv(bhv);
			PredictedBhv predictedBhv = PredictedPresentBhv.getPredictedPresentBhv(bhv).getRecentPredictedBhv();
			hisPresentBhv.setRecentPredictionTime(predictedBhv.getTime());
			hisPresentBhv.setRecentPredictionScore(predictedBhv.getScore());
			res.add(hisPresentBhv);
		}
		return res;
	}
	
	public static void storeHistoryPresentBhvList(List<HistoryPresentBhv> bhvList){
		for(HistoryPresentBhv bhv : bhvList)
			storeHistoryPresentBhv(bhv);
	}
	
	public static void storeHistoryPresentBhv(HistoryPresentBhv bhv){
		dao.store(bhv);
	}
	
	public static List<HistoryPresentBhv> retrieveHistoryPresentBhvListSorted(){
		return dao.retrieveRecent();
	}
}