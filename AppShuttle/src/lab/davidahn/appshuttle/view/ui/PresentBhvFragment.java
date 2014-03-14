package lab.davidahn.appshuttle.view.ui;

import java.util.List;

import lab.davidahn.appshuttle.AppShuttleApplication;
import lab.davidahn.appshuttle.R;
import lab.davidahn.appshuttle.report.StatCollector;
import lab.davidahn.appshuttle.view.PresentBhv;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PresentBhvFragment extends ListFragment {
	private PresentBhvAdapter adapter;
	private List<PresentBhv> presentBhvList;
	private int posMenuOpened = -1; //menu closed
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

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

		if(System.currentTimeMillis() - AppShuttleApplication.launchTime < 3000)
			setEmptyText(getResources().getString(R.string.msg_wait));
		else
			setEmptyText(getResources().getString(R.string.msg_no_results));
		
		presentBhvList = PresentBhv.getPresentBhvListFilteredSorted(Integer.MAX_VALUE);
		
		adapter = new PresentBhvAdapter();
		setListAdapter(adapter);

		if (presentBhvList == null) {
			setListShown(false);
		} else {
			setListShown(true);
		}
		
		posMenuOpened = -1;
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
	        	if(!isMenuOpened()){
	        		openMenu(v, position);
	        	} else {
	        		if(posMenuOpened != position) {
	        			closeMenu();
		        		openMenu(v, position);
	        		}
	        	}
	            return true;
	        }
	    });
		
		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
						closeMenu();
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(posMenuOpened == position)
			return;
			
		Intent intent = adapter.getItem(position).getLaunchIntent();
		if(intent == null)
			return;
		
		StatCollector.getInstance().notifyBhvTransition(adapter.getItem(position).getUserBhv(), true);
		getActivity().startActivity(intent);
	}
	
	private void openMenu(View v, int pos) {
		if(pos < 0) return;
		View menu = v.findViewById(R.id.listview_present_menu);
		menu.setVisibility(View.VISIBLE);
		posMenuOpened = pos;
	}
	
	private void closeMenu() {
		for(int i=0;i<getListView().getChildCount();i++){
			View menu = getListView().getChildAt(i).findViewById(R.id.listview_present_menu);
			menu.setVisibility(View.GONE);
		}
		posMenuOpened = -1;
	}
	
	private boolean isMenuOpened(){
		if(posMenuOpened < 0 ) return false;
		else return true;
	}
	
	public class PresentBhvAdapter extends ArrayAdapter<PresentBhv> {

		public PresentBhvAdapter() {
			super(getActivity(), R.layout.listview_present, presentBhvList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.listview_present, parent, false);
			PresentBhv presentBhv = presentBhvList.get(position);

			ImageView iconView = (ImageView) itemView.findViewById(R.id.listview_present_item_image);
			iconView.setImageDrawable(presentBhv.getIcon());

			TextView firstLineView = (TextView) itemView.findViewById(R.id.listview_present_item_firstline);
			firstLineView.setText(presentBhv.getBhvNameText());

			TextView secondLineView = (TextView) itemView.findViewById(R.id.listview_present_item_secondline);
			secondLineView.setText(presentBhv.getViewMsg());

			View.OnClickListener menuItemListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AppShuttleMainActivity mainActivity = (AppShuttleMainActivity)getActivity();
					String actionMsg = mainActivity.doActionAndGetMsg(presentBhvList.get(posMenuOpened), v.getId());
					mainActivity.doPostAction();
					mainActivity.showToastMsg(actionMsg);
				}
			};
			ImageView favoriteView = (ImageView) itemView.findViewById(R.id.listview_present_menu_favorite);
			favoriteView.setOnClickListener(menuItemListener);

			ImageView ignoreView = (ImageView) itemView.findViewById(R.id.listview_present_menu_ignore);
			ignoreView.setOnClickListener(menuItemListener);
			
//			ViewGroup menu = (ViewGroup)itemView.findViewById(R.id.listview_menu);
//			menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
//					ViewGroup.LayoutParams.MATCH_PARENT));

//			ImageView favoriteView = new ImageView(getContext());
//			favoriteView.setLayoutParams(new android.view.ViewGroup.LayoutParams(30,30));
//			favoriteView.setImageResource(R.id.favorate);
//			menu.addView(favoriteView);
//
//			ImageView ignoreView = new ImageView(getContext());
//			ignoreView.setLayoutParams(new android.view.ViewGroup.LayoutParams(30,30));
//			ignoreView.setImageResource(R.id.block);
//			menu.addView(ignoreView);
			
			return itemView;
		}
	}
}