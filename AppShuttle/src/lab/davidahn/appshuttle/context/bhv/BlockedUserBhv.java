package lab.davidahn.appshuttle.context.bhv;

import lab.davidahn.appshuttle.view.ViewableUserBhv;
import android.text.format.DateUtils;


public class BlockedUserBhv extends ViewableUserBhv implements Comparable<BlockedUserBhv> {
	private long _blockedTime;
	
	public BlockedUserBhv(UserBhv uBhv, long blockedTime){
		super(uBhv);
		_blockedTime = blockedTime;
	}
	
	public long getBlockedTime() {
		return _blockedTime;
	}
	
	@Override
	public int compareTo(BlockedUserBhv uBhv) {
		if(_blockedTime < uBhv._blockedTime)
			return 1;
		else if(_blockedTime == uBhv._blockedTime)
			return 0;
		else
			return -1;
	}

	@Override
	public String getViewMsg() {
//		long blockedTime = ((BlockedUserBhv)_uBhv).getBlockedTime();
		StringBuffer msg = new StringBuffer();
		_viewMsg = msg.toString();
		
		msg.append(DateUtils.getRelativeTimeSpanString(_blockedTime, 
				System.currentTimeMillis(), 
				DateUtils.MINUTE_IN_MILLIS, 
				0
				));
		_viewMsg = msg.toString();
		
		return _viewMsg;
	}
}
