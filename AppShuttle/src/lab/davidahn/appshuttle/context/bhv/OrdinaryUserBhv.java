package lab.davidahn.appshuttle.context.bhv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lab.davidahn.appshuttle.mine.matcher.MatcherType;
import lab.davidahn.appshuttle.mine.matcher.MatcherTypeComparator;
import lab.davidahn.appshuttle.mine.matcher.PredictionInfo;
import lab.davidahn.appshuttle.mine.matcher.Predictor;
import lab.davidahn.appshuttle.view.ViewableUserBhv;


public class OrdinaryUserBhv extends ViewableUserBhv implements Comparable<OrdinaryUserBhv> {

	private static List<OrdinaryUserBhv> extractedViewListSorted;
	
	public OrdinaryUserBhv(UserBhv uBhv){
		super(uBhv);
	}
	
	@Override
	public int compareTo(OrdinaryUserBhv uBhv) {
		Predictor predictor = Predictor.getInstance();
		double _score = predictor.getPredictionInfo(_uBhv).getScore();
		double score = predictor.getPredictionInfo(uBhv).getScore();

		if(_score < score)
			return 1;
		else if(_score == score)
			return 0;
		else 
			return -1;
	}
	
	@Override
	public String getViewMsg() {
		StringBuffer msg = new StringBuffer();
		_viewMsg = msg.toString();

		Predictor predictor = Predictor.getInstance();
		PredictionInfo predictedBhv = predictor.getPredictionInfo(_uBhv);
		
		if(predictedBhv == null) {
			return _viewMsg;
		}
		
		List<MatcherType> matcherTypeList = new ArrayList<MatcherType>(predictedBhv.getMatchedResultMap().keySet());
		Collections.sort(matcherTypeList, new MatcherTypeComparator());
		
		for (MatcherType matcherType : matcherTypeList) {
			msg.append(matcherType.viewMsg).append(", ");
		}
		msg.delete(msg.length() - 2, msg.length());
		_viewMsg = msg.toString();
		
		return _viewMsg;
	}
	
	public static synchronized void extractViewListSorted() {
		Map<UserBhv, PredictionInfo> predictionInfoMap = Predictor.getInstance().getRecentPredictionInfoMap();
		extractedViewListSorted = new ArrayList<OrdinaryUserBhv>();
		
		if(predictionInfoMap == null)
			return;
		
		Set<OrdinaryUserBhv> ordinaryUserBhvSet = UserBhvManager.getInstance().getOrdinaryBhvSet();
		for(OrdinaryUserBhv ordinaryUserBhv : ordinaryUserBhvSet){
			if(predictionInfoMap.keySet().contains(ordinaryUserBhv))
				extractedViewListSorted.add(ordinaryUserBhv);
		}
		
		Collections.sort(extractedViewListSorted);
	}

	public static synchronized List<OrdinaryUserBhv> getExtractedViewListSorted(int topN) {
		if(extractedViewListSorted == null)
			extractViewListSorted();
		
		return extractedViewListSorted.subList(0, Math.min(extractedViewListSorted.size(), topN));
	}
}
