package lab.davidahn.appshuttle.view.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.davidahn.appshuttle.AppShuttleApplication;
import lab.davidahn.appshuttle.collect.SnapshotUserCxt;
import lab.davidahn.appshuttle.report.StatCollector;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class InfoFragment extends ListFragment {
	private ListAdapter adapter;
	private List<Map<String, String>> infoList;
	private static final String INFO_KEY = "key";
	private static final String INFO_VALUE = "value";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(android.R.layout.list_content, container,
				false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		infoList = new ArrayList<Map<String,String>>();
		infoList.add(getContextInfo());
		infoList.add(getStatisticsInfo());
		infoList.add(getEfficiencyInfo());
		
		adapter = new SimpleAdapter(getActivity(), infoList, android.R.layout.simple_list_item_2,
				new String[]{INFO_KEY, INFO_VALUE},
				new int[]{android.R.id.text1, android.R.id.text2});
		setListAdapter(adapter);
	}

	private Map<String, String> getContextInfo() {
		Map<String, String> keyValue = new HashMap<String, String>();
		SnapshotUserCxt cxt = AppShuttleApplication.currUserCxt;
		keyValue.put(INFO_KEY, "Context");
		keyValue.put(INFO_VALUE, cxt.toString());
		return keyValue;
	}
	
	private Map<String, String> getStatisticsInfo(){
		Map<String, String> keyValue = new HashMap<String, String>();
		keyValue.put(INFO_KEY, "Statistics");
		keyValue.put(INFO_VALUE, StatCollector.getInstance().toString());
		return keyValue;
	}
	
	private Map<String, String> getEfficiencyInfo() {
		Map<String, String> keyValue = new HashMap<String, String>();
		keyValue.put(INFO_KEY, "Efficiency");
		String value = "";
		value += "last prediction latency: " + AppShuttleApplication.lastPredictionLatency + "\n";
		value += "max prediction latency: " + AppShuttleApplication.maxPredictionLatency;
		keyValue.put(INFO_VALUE, value);
		return keyValue;
	}
}