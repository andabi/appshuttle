package lab.davidahn.appshuttle.collect.bhv;

import java.util.Date;
import java.util.TimeZone;

public class DurationUserBhv implements UserBhv {
	private final Date _timeDate;
	private final long _duration;
	private final Date _endTimeDate;
	private final TimeZone _timeZone;
	private final UserBhv _uBhv;

	private DurationUserBhv(Builder builder){
		_timeDate = builder._timeDate;
		_duration = builder._duration;
		_endTimeDate = builder._endTimeDate;
		_timeZone = builder._timeZone;
		_uBhv = builder._uBhv;
	}
	@Override
	public UserBhvType getBhvType() {
		return _uBhv.getBhvType();
	}
	@Override
	public void setBhvType(UserBhvType bhvType) {
		_uBhv.setBhvType(bhvType);
	}
	@Override
	public String getBhvName() {
		return _uBhv.getBhvName();
	}
	@Override
	public void setBhvName(String bhvName) {
		_uBhv.setBhvName(bhvName);
	}
	@Override
	public Object getMeta(String key) {
		return _uBhv.getMeta(key);
	}
	@Override
	public void setMeta(String key, Object val){
		_uBhv.setMeta(key, val);
	}

	public Date getTimeDate() {
		return _timeDate;
	}
	public long getDuration() {
		return _duration;
	}
	public Date getEndTimeDate() {
		return _endTimeDate;
	}
	public TimeZone getTimeZone() {
		return _timeZone;
	}
	public UserBhv getUserBhv() {
		return _uBhv;
	}

	public String toString(){
		StringBuffer msg = new StringBuffer();
		msg.append("start time: ").append(_timeDate).append(", ");
		msg.append("duration: ").append(_duration).append(", ");
		msg.append("end time: ").append(_endTimeDate).append(", ");
		msg.append("timeZone: ").append(_timeZone.getID()).append("\n");
		msg.append(_uBhv.toString()).append(", ");
		return msg.toString();
	}
	
	public static class Builder {
		private Date _timeDate = null;
		private long _duration = 0;
		private Date _endTimeDate = null;
		private TimeZone _timeZone = null;
		private UserBhv _uBhv = null;

		public Builder(){}
		
		public DurationUserBhv build(){
			if(_timeDate != null && _endTimeDate != null)
				_duration = _endTimeDate.getTime() - _timeDate.getTime();
			return new DurationUserBhv(this);
		}
		
		public Date getEndTime() {
			return _endTimeDate;
		}
		
		public Builder setTime(Date sTime) {
			_timeDate = sTime;
			return this;
		}
		
		public Builder setDuration(long duration) {
			_duration = duration;
			return this;
		}

		public Builder setEndTime(Date eTime) {
			_endTimeDate = eTime;
			return this;
		}

		public Builder setTimeZone(TimeZone timeZone) {
			_timeZone = timeZone;
			return this;
		}

		public Builder setBhv(UserBhv uBhv) {
			_uBhv = uBhv;
			return this;
		}
	}
}