package lab.davidahn.appshuttle.predict.matcher;

import java.util.Map;

import lab.davidahn.appshuttle.collect.SnapshotUserCxt;
import lab.davidahn.appshuttle.predict.matcher.conf.RecentMatcherConf;

public class FrequentlyRecentMatcher extends RecentMatcher {
	
	public FrequentlyRecentMatcher(RecentMatcherConf conf){
		super(conf);
	}
	
	@Override
	public MatcherType getMatcherType(){
		return MatcherType.FREQUENTLY_RECENT;
	}

	@Override
	protected double computeLikelihood(int numTotalHistory, Map<MatcherCountUnit, Double> relatedHistoryMap, SnapshotUserCxt uCxt){
		double likelihood = 0;
		likelihood = 1.0 * relatedHistoryMap.size() / Integer.MAX_VALUE;
		return likelihood;
	}

}