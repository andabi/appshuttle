package lab.davidahn.appshuttle.mine.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lab.davidahn.appshuttle.context.SnapshotUserCxt;
import lab.davidahn.appshuttle.context.bhv.DurationUserBhv;
import lab.davidahn.appshuttle.context.env.DurationUserEnv;
import lab.davidahn.appshuttle.context.env.DurationUserEnvManager;
import lab.davidahn.appshuttle.context.env.EnvType;
import lab.davidahn.appshuttle.context.env.UserPlace;

public class PlaceMatcher extends BaseMatcher {
	int _toleranceInMeter;

	public PlaceMatcher(long duration, double minLikelihood, double minInverseEntropy, int minNumCxt, int toleranceInMeter) {
		super(duration, minLikelihood, minInverseEntropy, minNumCxt);
		_toleranceInMeter = toleranceInMeter;
	}
	
	@Override
	public MatcherType getMatcherType(){
		return MatcherType.PLACE;
	}
	
	@Override
	protected List<MatcherCountUnit> mergeCxtByCountUnit(List<DurationUserBhv> rfdUCxtList, SnapshotUserCxt uCxt) {
		List<MatcherCountUnit> res = new ArrayList<MatcherCountUnit>();
		DurationUserEnvManager durationUserEnvManager = DurationUserEnvManager.getInstance();

		MatcherCountUnit.Builder mergedRfdUCxtBuilder = null;
		UserPlace lastKnownUserPlace = null;
		for(DurationUserBhv rfdUCxt : rfdUCxtList){
			for(DurationUserEnv durationUserEnv : durationUserEnvManager.retrieve(rfdUCxt.getTimeDate()
					, rfdUCxt.getEndTimeDate(), EnvType.PLACE)){
				UserPlace userPlace = (UserPlace)durationUserEnv.getUserEnv();
				if(lastKnownUserPlace == null) {
					mergedRfdUCxtBuilder = new MatcherCountUnit.Builder(rfdUCxt.getUserBhv());
					mergedRfdUCxtBuilder.setProperty("place", userPlace);
				} else {
						if(!userPlace.equals(lastKnownUserPlace)){
							res.add(mergedRfdUCxtBuilder.build());
							mergedRfdUCxtBuilder = new MatcherCountUnit.Builder(rfdUCxt.getUserBhv());
							mergedRfdUCxtBuilder.setProperty("place", userPlace);
						}
				}
				lastKnownUserPlace = userPlace;
			}
		}
		if(mergedRfdUCxtBuilder != null)
			res.add(mergedRfdUCxtBuilder.build());		
//		try {
//			UserPlace currUPlace = ((PlaceUserEnv)uCxt.getUserEnv(EnvType.PLACE)).getPlace();
//			if(lastKnownUserPlace != null && lastKnownUserPlace.isSame(currUPlace)){
//				res.remove(res.size()-1);
//			}
//		} catch (InvalidUserEnvException e) {
//			;
//		}
		return res;
	}
	
//	@Override
//	protected List<MatcherCountUnit> mergeCxtByCountUnit(List<RfdUserCxt> rfdUCxtList) {
//		List<MatcherCountUnit> res = new ArrayList<MatcherCountUnit>();
//		
//		MatcherCountUnit.Builder mergedRfdUCxtBuilder = null;
//		Entry<Date, UserLoc> lastKnownTimeAndPlace = null;
//		for(RfdUserCxt rfdUCxt : rfdUCxtList){
//			for(Entry<Date, UserLoc> timeAndPlace : rfdUCxt.getPlaces().entrySet()){
//				if(lastKnownTimeAndPlace == null) {
//					mergedRfdUCxtBuilder = new MatcherCountUnit.Builder(rfdUCxt.getBhv());
//					mergedRfdUCxtBuilder.setPlace(timeAndPlace.getValue());
//				} else {
//					if(timeAndPlace.getValue().equals(lastKnownTimeAndPlace.getValue())
//							&& !moved(lastKnownTimeAndPlace, timeAndPlace)){
//						;
//					} else {
//						res.add(mergedRfdUCxtBuilder.build());
//						mergedRfdUCxtBuilder = new MatcherCountUnit.Builder(rfdUCxt.getBhv());
//						mergedRfdUCxtBuilder.setPlace(timeAndPlace.getValue());
//					}
//				}
//				lastKnownTimeAndPlace = timeAndPlace;
//			}
//		}
//		if(mergedRfdUCxtBuilder != null)
//			res.add(mergedRfdUCxtBuilder.build());
//		return res;
//	}

	@Override
	protected double computeRelatedness(MatcherCountUnit unit, SnapshotUserCxt uCxt) {
		UserPlace uPlace = (UserPlace) uCxt.getUserEnv(EnvType.PLACE);
//			UserLoc uLoc = ((LocUserEnv) uCxt.getUserEnv(EnvType.LOCATION)).getLoc();
		if(uPlace.equals((UserPlace) unit.getProperty("place")))
			return 1;
		else
			return 0;
	}
//		if(rfdUCxt.getPlace().equals(((PlaceUserEnv)uCxt.getUserEnv(EnvType.PLACE)).getPlace()))
//			return 1;
//		else
//			return 0;
//		Map<Date, UserLoc> locs = rfdUCxt.getLocs();
//		for(UserLoc uLoc : locs.values()){
//			try {
//				if(Utils.Proximity(uLoc, uEnv.getLoc(), toleranceInMeter)){
//					return 1;
//				}
//			} catch (InvalidLocationException e) {
//				return 0;
//			}
//		}
	
	@Override
	protected double computeLikelihood(int numRelatedCxt, Map<MatcherCountUnit, Double> relatedCxtMap, SnapshotUserCxt uCxt){
//		int numTotalCxt = matchedCxt.getNumTotalCxt();
//		Map<MatcherCountUnit, Double> relatedCxtMap = matchedCxt.getRelatedCxt();
		
		double likelihood = 0;
		for(double relatedness : relatedCxtMap.values()){
			likelihood+=relatedness;
		}
		if(numRelatedCxt > 0)
			likelihood /= numRelatedCxt;
		else
			likelihood = 0;
		return likelihood;
	}
	
	@Override
	protected double computeInverseEntropy(List<MatcherCountUnit> matcherCountUnitList) {
		assert(matcherCountUnitList.size() >= _minNumCxt);
		
		double inverseEntropy = 0;
		Set<UserPlace> uniquePlace = new HashSet<UserPlace>();
		
		for(MatcherCountUnit unit : matcherCountUnitList){
			UserPlace uPlace = ((UserPlace) unit.getProperty("place"));
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
	
	protected double computeScore(MatcherResult matchedResult) {
		double likelihood = matchedResult.getLikelihood();
		double inverseEntropy = matchedResult.getInverseEntropy();
		
		double score = (1 + 0.5 * inverseEntropy + 0.1 * likelihood);
		
		return score;
	}
	
//	private boolean moved(Entry<Date, UserLoc> start, Entry<Date, UserLoc> end){
//		ChangeUserEnvDao changedUserEnvDao = ChangeUserEnvDao.getInstance(cxt);
//		
//		Date sTime = start.getKey();
//		Date eTime = end.getKey();
//		if(changedUserEnvDao.retrieveChangedUserEnv(sTime, eTime, EnvType.PLACE).size() > 0){
//			Log.d("PlaceContextMatcher", "moved");
//			return true;
//		} else
//			return false;
//	}
}
	
//	public List<MatchedCxt> matchAndGetResult(UserEnv uEnv){
//		Map<String, Integer> totalNumMap = new HashMap<String, Integer>();
//		Map<String, Integer> matchedNumMap = new HashMap<String, Integer>();
//
//		Cursor cur = db.rawQuery("SELECT context_id, location_list, bhv_name FROM refined_context;", null);
//		Gson gson = new Gson();
//		while (cur.moveToNext()) {
//			int contextId = cur.getInt(0);
//			String loc = cur.getString(1).toString();
//			String bhvName= cur.getString(2);
//			Type listType = new TypeToken<ArrayList<LocFreq>>(){}.getType();
//			List<LocFreq> locFreqList = gson.fromJson(loc, listType);
//			
//			
//			if(totalNumMap.containsKey(bhvName)) totalNumMap.put(bhvName, totalNumMap.get(bhvName) + 1);
//			else totalNumMap.put(bhvName, 1);
//			
//			if(!matchedNumMap.containsKey(bhvName)) matchedNumMap.put(bhvName, 0);
//			for(LocFreq locFreq : locFreqList){
//				if(Proximity(locFreq.getULoc(), uEnv.getLoc(), toleranceInMeter)){
//					matchedNumMap.put(bhvName, matchedNumMap.get(bhvName) + 1);
//					break;
//				}
//			}
//		}
//		cur.close();
//
//		Map<String, Double> res = new HashMap<String, Double>();
//		for(String bhvName : totalNumMap.keySet()){
//			if(totalNumMap.get(bhvName) < 2 || matchedNumMap.get(bhvName) == 0) continue;
//			double likelihood = matchedNumMap.get(bhvName)*1.0 / totalNumMap.get(bhvName) * 100;
//			if(likelihood >= threshold) res.put(bhvName, likelihood);
//		}
//		res = MapUtil.sortByValue(res);
//		return res;
//		return null;
//	}
	
	
//	@Override
//	public List<MatchedCxt> matchAndGetResult(UserEnv uEnv){
//		List<MatchedCxt> res = new ArrayList<MatchedCxt>();
//		
//		List<RfdUserCxt> rfdUCxtList = retrieveCxt(uEnv);
//		totalCxtSize = rfdUCxtList.size();
//		Map<UserBhv, SparseArray<Double>> relatednessSparseArrayMap = new HashMap<UserBhv, SparseArray<Double>>();
//
//		for(RfdUserCxt rfdUCxt : rfdUCxtList) {
//			int contextId = rfdUCxt.getContextId();
//			UserBhv userBhv = rfdUCxt.getBhv();
//			
//			if(!relatednessSparseArrayMap.containsKey(userBhv)) relatednessSparseArrayMap.put(userBhv, new SparseArray<Double>());
//			SparseArray<Double> relatedCxtSparseArray = relatednessSparseArrayMap.get(userBhv);
//			double relatedness = computeRelatedness(rfdUCxt, uEnv);
//			relatedCxtSparseArray.put(contextId, relatedness);
//			relatednessSparseArrayMap.put(userBhv, relatedCxtSparseArray);
//		}
//
//		for(UserBhv userBhv : relatednessSparseArrayMap.keySet()){
//			SparseArray<Double> relatedCxtMap = relatednessSparseArrayMap.get(userBhv);
//			double likelihood = computeLikelihood(relatedCxtMap);
//			if(likelihood <= threshold) continue;
//
//			MatchedCxt matchedCxt = new MatchedCxt(uEnv);
//			matchedCxt.setUserBhv(userBhv);
//			matchedCxt.setNumTotalCxt(relatedCxtMap.size());
//			matchedCxt.setLikelihood(likelihood);
//			matchedCxt.setRelatedCxt(relatedCxtMap);
//			matchedCxt.setCondition(conditionName());
//			res.add(matchedCxt);
//		}
//		return res;
//	}
	
//	Map<UserBhv, RfdUserCxt> ongoingBhvMap = new HashMap<UserBhv, RfdUserCxt>();
//	
//	UserLoc curPlace = null;
////	RfdUserCxt prevRfdUCxt = null;
//	boolean isMoved = false;
//	for(RfdUserCxt rfdUCxt : rfdUCxtList){
//		if(rfdUCxt.getPlaces().isEmpty()) continue;
//
//		if(curPlace == null) curPlace = rfdUCxt.getPlaces().get(0).getULoc();
////		if(prevRfdUCxt == null) prevRfdUCxt = rfdUCxt;
//		
//		UserBhv uBhv = rfdUCxt.getBhv();
//		if(ongoingBhvMap.isEmpty() || !ongoingBhvMap.containsKey(uBhv)) {
//			ongoingBhvMap.put(uBhv, rfdUCxt);
//		}
//		else {
////			if()
//			for(LocFreq placeFreq : rfdUCxt.getPlaces()){
//				
//			}
//		}
////		prevRfdUCxt = rfdUCxt;
//		
//		if(isMoved){
//			for(UserBhv ongoingBhv : ongoingBhvMap.keySet()){
//				res.add(ongoingBhvMap.get(ongoingBhv));
//			}
//			ongoingBhvMap.clear();
//			isMoved = false;
//		}
//	}
//	
//	for(UserBhv ongoingBhv : ongoingBhvMap.keySet()){
//		RfdUserCxt restRfdUCxt = ongoingBhvMap.get(ongoingBhv);
//		res.add(restRfdUCxt);
//	}

//private boolean Proximity(RfdUserCxt prevRfdUCxt, RfdUserCxt curRfdUCxt){
//	if(curRfdUCxt.getPlaces().isEmpty()) return false;
//	if(curRfdUCxt.getPlaces().size() > 1) return true;
//	else if(!prevRfdUCxt.getPlaces().get(prevRfdUCxt.getPlaces().size()-1).
//	equals(curRfdUCxt.getPlaces().get(0)))
//		return true;
//	else
//		return false;
//}

//@Override
//protected List<RfdUserCxt> retrieveCxtByBhv(UserEnv uEnv){
//	long time = uEnv.getTime().getTime();
//	List<RfdUserCxt> res = contextManager.retrieveRfdCxt(time - settings.getLong("matcher.duration", 5 * AlarmManager.INTERVAL_DAY), time);
//	return res;
//}
//