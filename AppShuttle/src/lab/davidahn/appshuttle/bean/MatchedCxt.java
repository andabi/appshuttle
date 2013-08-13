package lab.davidahn.appshuttle.bean;

import lab.davidahn.appshuttle.bhv.UserBhv;
import android.util.SparseArray;

public class MatchedCxt implements Comparable<MatchedCxt> {
	private UserEnv userEnv;
	private UserBhv userBhv;
	private String condition;
	private double likelihood;
	private int numTotalCxt;
	private int numRelatedCxt;
	private SparseArray<Double> relatedCxt;
	
	public MatchedCxt(UserEnv userEnv){
		this.userEnv = userEnv;
	}
	
	public UserEnv getUserEnv() {
		return userEnv;
	}
	public void setUserEnv(UserEnv userEnv) {
		this.userEnv = userEnv;
	}

	public double getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}

	public UserBhv getUserBhv() {
		return userBhv;
	}

	public void setUserBhv(UserBhv bhvName) {
		this.userBhv = bhvName;
	}

	public int getNumTotalCxt() {
		return numTotalCxt;
	}

	public void setNumTotalCxt(int numTotalCxt) {
		this.numTotalCxt = numTotalCxt;
	}

	public int getNumRelatedCxt() {
		return numRelatedCxt;
	}

	public void setNumRelatedCxt(int numRelatedCxt) {
		this.numRelatedCxt = numRelatedCxt;
	}
	
	public SparseArray<Double> getRelatedCxt(){
		return relatedCxt;
	}
	
	public void setRelatedCxt(SparseArray<Double> relatedCxt) {
		this.relatedCxt = relatedCxt;
	}

	public void addRelatedCxt(int CxtId, double relatedness) {
		if(relatedCxt == null) relatedCxt = new SparseArray<Double>();
		relatedCxt.put(CxtId, relatedness);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public int compareTo(MatchedCxt matchedCxt){
		if(likelihood > matchedCxt.likelihood) return -1;
		else if(likelihood == matchedCxt.likelihood) return 0;
		else return 1;
	}
	
	public String toString(){
		StringBuffer msg = new StringBuffer();
		msg.append(userEnv.toString()).append(", ");
		msg.append(userBhv.toString()).append(", ");
		msg.append("condition: ").append(condition).append(", ");
		msg.append("likelihood: ").append(likelihood).append(", ");
		msg.append("numTotalCxt: ").append(numTotalCxt).append(", ");
		msg.append("relatedCxt: ").append(relatedCxt);
		return msg.toString();
	}
}
