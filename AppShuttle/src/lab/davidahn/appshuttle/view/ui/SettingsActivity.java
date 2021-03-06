package lab.davidahn.appshuttle.view.ui;

import lab.davidahn.appshuttle.AppShuttleApplication;
import lab.davidahn.appshuttle.AppShuttlePreferences;
import lab.davidahn.appshuttle.R;
import lab.davidahn.appshuttle.report.Reporter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
//        ActionBar bar = getActionBar();
//        bar.setDisplayHomeAsUpEnabled(true);
	}
	
	public static class SettingsFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
		
		@Override
		public void onResume(){
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);
		}
		
		@Override
		public void onPause(){
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
		}
		
		@Override
		public void onStart() {
			super.onStart();
			EasyTracker.getInstance(AppShuttleApplication.getContext()).activityStart(getActivity());
		}
		
		@Override
		public void onStop() {
			super.onStop();
			EasyTracker.getInstance(AppShuttleApplication.getContext()).activityStop(getActivity());
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals("settings_pref_sleep_mode_key")) {
				NotiBarNotifier.getInstance().updateNotification();
				getActivity().sendBroadcast(new Intent()
				.setAction(AppShuttlePreferences.SLEEP_MODE)
				.putExtra("isOn", AppShuttlePreferences.isSleepMode()));
			} else if(key.equals("settings_pref_system_area_icon_hide_key")){
				NotiBarNotifier notifier = NotiBarNotifier.getInstance();
				notifier.hideNotibar();
				notifier.updateNotification();
				if(AppShuttlePreferences.isSystemAreaIconHidden()){
					String warnMsg = getResources().getString(R.string.settings_pref_system_area_icon_hide_warn_msg);
					Toast.makeText(getActivity(), warnMsg, Toast.LENGTH_LONG).show();
				}
			} else if(key.equals("settings_info_feedback_key")){
				String contents = sharedPreferences.getString(key, "");
				if(!contents.equals(""))
					doFeedback(contents);
				sharedPreferences.edit().putString(key, "").commit();
			}
		}

		@Override
		public void onDestroy() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		private void doFeedback(final String contents) {
			final String[] receivers = {AppShuttleApplication.getContext().getPreferences().getString("report.email.receiver_addr", "appshuttle2@gmail.com")};
			final String subject = "[appshuttle feedback]";

			Thread thread = new Thread(new Runnable(){
				public void run(){
					Reporter reporter = new Reporter(receivers, subject, contents);
					int isSuceess = 0;
					try {
						isSuceess = (reporter.report()) ? 1 : 0;
					} catch (Exception e) {
						isSuceess = -1;
					}
					handler.sendEmptyMessage(isSuceess);
				}
			});
			thread.start();
		}
		
		@SuppressLint("HandlerLeak")
		Handler handler = new Handler() {
			public void handleMessage(Message msg){
				Context cxt = AppShuttleApplication.getContext();
				String successMsg = cxt.getResources().getString(R.string.settings_info_feedback_success_msg);
				String failureMsg = cxt.getResources().getString(R.string.settings_info_feedback_failure_msg);

				if (msg.what == 1) {
//					Log.i("feedback", successMsg);
					Toast.makeText(cxt, successMsg, Toast.LENGTH_SHORT).show();
				} else {
//					Log.d("feedback", failureMsg);
					Toast.makeText(cxt, failureMsg, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
}

//@Override
//public void onBuildHeaders(List<Header> target) {
//	loadHeadersFromResource(R.xml.preference_headers, target);
//}