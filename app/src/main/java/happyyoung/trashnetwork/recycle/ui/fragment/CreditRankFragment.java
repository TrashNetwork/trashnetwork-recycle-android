package happyyoung.trashnetwork.recycle.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;

public class CreditRankFragment extends Fragment {
    private View rootView;
    @BindView(R.id.tab_credit_rank)
    TabLayout tabLayout;
    @BindView(R.id.tab_viewpager_credit_rank)
    ViewPager viewPager;
    private CreditRankListFragment dailyRankFragment;
    private CreditRankListFragment weeklyRankFragment;

    public CreditRankFragment() {
        // Required empty public constructor
    }

    public static CreditRankFragment newInstance(Context context) {
        CreditRankFragment fragment = new CreditRankFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_credit_rank, container, false);
        ButterKnife.bind(this, rootView);
        viewPager.setAdapter(new CreditRankPageAdapter(getFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        dailyRankFragment = CreditRankListFragment.newInstance(getContext(), CreditRankListFragment.RANK_TYPE_DAY);
        weeklyRankFragment = CreditRankListFragment.newInstance(getContext(), CreditRankListFragment.RANK_TYPE_WEEK);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            dailyRankFragment.updateRank();
            weeklyRankFragment.updateRank();
        }else{
            dailyRankFragment.clearRankList();
            weeklyRankFragment.clearRankList();
        }
        super.onHiddenChanged(hidden);
    }

    private class CreditRankPageAdapter extends FragmentPagerAdapter{
        public CreditRankPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return dailyRankFragment;
                case 1:
                    return weeklyRankFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.daily_rank);
                case 1:
                    return getString(R.string.weekly_rank);
            }
            return null;
        }
    }
}
