package com.android.vantage;

import com.android.vantage.Fragments.BaseFragment;
import com.android.vantage.Fragments.HomeFragment;
import com.android.vantage.Fragments.SearchFragment;
import com.android.vantage.utility.AppConstants;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class FragmentHolderActivity extends BaseActivity {

	public static final int FRAGMENT_TYPE_HOME = 1;
	public static final int FRAGMENT_TYPE_SEARCH = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_holder);
		
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		int typeOfFragment = getIntent().getExtras().getInt(
				AppConstants.BUNDLE_KEYS.FRAGMENT_TYPE);
		fragment = (BaseFragment) getFragment(typeOfFragment);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame, fragment).commit();
		setActionTitle("");
		bar.setDisplayHomeAsUpEnabled(true);
	}

	private Fragment getFragment(int type) {
		switch (type) {
		case FRAGMENT_TYPE_HOME:
			return HomeFragment.getInstance(getIntent().getExtras().getString(
					AppConstants.BUNDLE_KEYS.KEY_ROOM_NAME));
		case FRAGMENT_TYPE_SEARCH:
			return new SearchFragment();
		}
		return null;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
	}

}
