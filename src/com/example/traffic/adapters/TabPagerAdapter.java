package com.example.traffic.adapters;

import com.example.traffic.fragments.RecentUploadsFragment;
import com.example.traffic.fragments.StatisticsFragment;
import com.example.traffic.fragments.UploadFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class TabPagerAdapter extends FragmentPagerAdapter {
	public TabPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		// Create the bundle to pass in to the new fragment class.
		Bundle bundle = new Bundle();
		Fragment selectedPage;
		switch(index) {
			case 0:
				selectedPage = new UploadFragment();
				selectedPage.setArguments(bundle);
				break;
			case 1:
				selectedPage = new RecentUploadsFragment();
				selectedPage.setArguments(bundle);
				break;
			case 2:
				selectedPage = new StatisticsFragment();
				selectedPage.setArguments(bundle);
				break;
			default:
				selectedPage = new UploadFragment();
				selectedPage.setArguments(bundle);
		}
		
		return selectedPage;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
}
