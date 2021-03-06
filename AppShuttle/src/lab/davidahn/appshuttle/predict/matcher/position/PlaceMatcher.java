package lab.davidahn.appshuttle.predict.matcher.position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lab.davidahn.appshuttle.collect.SnapshotUserCxt;
import lab.davidahn.appshuttle.collect.bhv.DurationUserBhv;
import lab.davidahn.appshuttle.collect.env.DurationUserEnv;
import lab.davidahn.appshuttle.collect.env.DurationUserEnvManager;
import lab.davidahn.appshuttle.collect.env.EnvType;
import lab.davidahn.appshuttle.collect.env.UserPlace;
import lab.davidahn.appshuttle.predict.matcher.MatcherConf;
import lab.davidahn.appshuttle.predict.matcher.MatcherCountUnit;
import lab.davidahn.appshuttle.predict.matcher.MatcherType;

public class PlaceMatcher extends PositionMatcher {

	public PlaceMatcher(MatcherConf conf){
		super(conf);
	}
	
	@Override
	public MatcherType getType(){
		return MatcherType.PLACE;
	}
	
	@Override
	protected List<MatcherCountUnit> makeMatcherCountUnit(List<DurationUserBhv> durationUserBhvList, SnapshotUserCxt uCxt) {
		List<MatcherCountUnit> res = new ArrayList<MatcherCountUnit>();
		DurationUserEnvManager durationUserEnvManager = DurationUserEnvManager.getInstance();

		MatcherCountUnit matcherCountUnit = null;
		
		DurationUserBhv prevDurationUserBhv = null;
		UserPlace lastKnownUserPlace = null;
		for(DurationUserBhv durationUserBhv : durationUserBhvList){
			for(DurationUserEnv durationUserEnv : durationUserEnvManager.retrieve(durationUserBhv.getTime()
					, durationUserBhv.getEndTime(), EnvType.PLACE)){
				UserPlace userPlace = (UserPlace)durationUserEnv.getUserEnv();
				if(prevDurationUserBhv == null) {
					matcherCountUnit = makeMatcherCountUnit(durationUserBhv, userPlace);
				} else {
					if(!userPlace.equals(lastKnownUserPlace)){
						res.add(matcherCountUnit);
						matcherCountUnit = makeMatcherCountUnit(durationUserBhv, userPlace);
					} else {
						long time = durationUserBhv.getTime();
						long lastEndTime = prevDurationUserBhv.getEndTime();
						if(time - lastEndTime >= conf.getAcceptanceDelay()){
							res.add(matcherCountUnit);
							matcherCountUnit = makeMatcherCountUnit(durationUserBhv, userPlace);
						}
					}
				}
				
				prevDurationUserBhv = durationUserBhv;
				lastKnownUserPlace = userPlace;
			}
		}

		if(matcherCountUnit != null)
			res.add(matcherCountUnit);
		
		return res;
	}

	private MatcherCountUnit makeMatcherCountUnit(DurationUserBhv durationUserBhv, UserPlace userPlace) {
		MatcherCountUnit matcherCountUnit = new MatcherCountUnit(durationUserBhv.getUserBhv());
		matcherCountUnit.setProperty("place", userPlace);
		return matcherCountUnit;
	}

	@Override
	protected double computeInverseEntropy(List<MatcherCountUnit> matcherCountUnitList) {

		double inverseEntropy = 0;
		Set<UserPlace> uniquePlace = new HashSet<UserPlace>();
		
		for(MatcherCountUnit unit : matcherCountUnitList){
			UserPlace uPlace = ((UserPlace) unit.getProperty("place"));

			if(!uPlace.isValid())
				continue;
			
			Iterator<UserPlace> it = uniquePlace.iterator();
			boolean unique = true;
			if(!uniquePlace.isEmpty()){
				while(it.hasNext()){
					UserPlace uniquePlaceElem = it.next();
					if(uPlace.equals(uniquePlaceElem)){
						unique = false;
						break;
					}
				}
			}
			if(unique)
				uniquePlace.add(uPlace);
		}
		int entropy = uniquePlace.size();
		if(entropy > 0) {
			inverseEntropy = 1.0 / entropy;
		} else {
			inverseEntropy = 0;
		}
		
		assert(0 <= inverseEntropy && inverseEntropy <= 1);
		
		return inverseEntropy;
	}

	@Override
	protected double computeRelatedness(MatcherCountUnit unit, SnapshotUserCxt uCxt) {
		UserPlace uPlace = (UserPlace) uCxt.getUserEnv(EnvType.PLACE);
		if(uPlace.equals((UserPlace) unit.getProperty("place")))
			return 1;
		else
			return 0;
	}
}
