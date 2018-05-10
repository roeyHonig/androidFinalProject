package honig.roey.student.roeysigninapp.arena;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import honig.roey.student.roeysigninapp.NavDrawer;
import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.tables.MatchUp;
import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.UserStat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArenaFragment extends Fragment {
    private RingGlobal globalDataSet = new RingGlobal();
    private ArrayList<MatchUp> individualMatchUpsDataSet = new ArrayList<>();

    public static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private int matchUpIndex; // -1 is global arena data Set. 0 or positive number is an individual matchUP
    private NavDrawer parentActivity;






    public RingGlobal getGlobalDataSet() {
        return globalDataSet;
    }

    public ArenaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get Arguments
        if (getArguments() != null){
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            globalDataSet.setKey(getArguments().getString("argKey"));
            globalDataSet.setName(getArguments().getString("argName"));
            globalDataSet.setPublicViewd(getArguments().getBoolean("argIsPublicViewd"));
            globalDataSet.setSuperUser(getArguments().getString("argSuperUser"));
            globalDataSet.setUserStats(getArguments().getParcelableArrayList("argUserStatArrayList"));
            globalDataSet.setNumPlayers(getArguments().getInt("argNumPlayers"));
            individualMatchUpsDataSet = getArguments().getParcelableArrayList("argMatchUpsDataArrayList");




        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arena, container, false);

        // get Arguments
        if (globalDataSet != null && individualMatchUpsDataSet != null){
            /*
            String tmpConcatation = "";

            for (int i = 0; i < individualMatchUpsDataSet.size(); i++) {
                tmpConcatation += individualMatchUpsDataSet.get(i).getPlayers().get(0).toString();
                tmpConcatation += "* Vs *";
                tmpConcatation += individualMatchUpsDataSet.get(i).getPlayers().get(1).toString();
                tmpConcatation += "//////";
            }

            // just a Test TextView To see i got the arguments out
            TextView tvBuffer =  view.findViewById(R.id.tvBuffer);
            tvBuffer.setText(globalDataSet.toShortString() + tmpConcatation);
            */

            // Set the adapter for the matchUps List RecyclerView
            RecyclerView matchUpRecyclerView = view.findViewById(R.id.matchUplist);
            Context context = matchUpRecyclerView.getContext();
            if (mColumnCount <= 1) {
                matchUpRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                matchUpRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            matchUpRecyclerView.setAdapter(new MyMatchUpRecyclerViewAdapter(individualMatchUpsDataSet, mListener));



            // Create the adapter that will return a fragment for each of the  4 primary sections - pages -  of the activity.
            // This instantiate a new object of class SectionsPagerAdapter, which is an inner class in this activity.
            // inside of that inner class, there will be a call to instantiate a new object of class PlaceholderFragment
            // which is another inner class in this activity.
            // The PlaceholderFragment object is responsiable to the content of the page section
            // how does he knows the page section, he get's it as an argument from the SectionsPagerAdapter
            mSectionsPagerAdapter = new SectionsPagerAdapter(parentActivity.getSupportFragmentManager(), matchUpIndex, globalDataSet.getUserStats());

            mViewPager = (ViewPager) view.findViewById(R.id.container);
            // Set up the ViewPager with the sections adapter.
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) view.findViewById(R.id.tabs);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (NavDrawer) getActivity();
        if (context instanceof MatchUpsFragment.OnListFragmentInteractionListener) {
            mListener = (ArenaFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction between the RecyclerView's List Items
     * and the hosting activity
     *
     */
    public interface OnListFragmentInteractionListener {
        void onMatchUpListInteraction(String MatchUpKey);
    }




    /**
     * A placeholder fragment containing a simple view.
     * This Fragment represent a single page \ section of the tabbed area in the ArenaFragment
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_GLOBAL_DATA = "global_users_stat";
        private TextView textView;
        private int matchUpIndex = -1;
        private ArrayList<UserStat> globalUserStats;

        public void setClicksOnFab(int matchUpIndex) {
            this.matchUpIndex = matchUpIndex;
        }

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, int matchUpIndex, ArrayList<UserStat> globalUserStats) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putParcelableArrayList(ARG_GLOBAL_DATA, globalUserStats);
            fragment.setArguments(args);
            fragment.matchUpIndex = matchUpIndex;
            fragment.globalUserStats = globalUserStats;
            return fragment;
        }

        // since we're using an extenstion of FragmentPagerAdapter, the fragments -representing the pages - are created once and never destroyed.
        // but still when the user scroll them out of the screen, thier views are gone
        // we have to recreate thier respected view every time the user rescroll back to them
        // that is when onCreateView is called
        // so we have to make sure that this methood allaways holds the mose current set of data we want to load
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.stat_page, container, false);
            textView = (TextView) rootView.findViewById(R.id.section_label);

            String tmp = "";
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    //pct
                    for (UserStat globalPlayer: globalUserStats ) {
                        tmp += " *** ";
                        tmp += globalPlayer.getFullName() + " pct% is: " + globalPlayer.getPct();
                    }
                    textView.setText(tmp);
                    break;
                case 2:
                    //loss
                    for (UserStat globalPlayer: globalUserStats ) {
                        tmp += " *** ";
                        tmp += globalPlayer.getFullName() + " #loss is: " + globalPlayer.getLos();
                    }
                    textView.setText(tmp);
                    break;
                case 3:
                    // drw
                    for (UserStat globalPlayer: globalUserStats ) {
                        tmp += " *** ";
                        tmp += globalPlayer.getFullName() + " #drw is: " + globalPlayer.getDrw();
                    }
                    textView.setText(tmp);
                    break;
                case 4:
                    //win
                    for (UserStat globalPlayer: globalUserStats ) {
                        tmp += " *** ";
                        tmp += globalPlayer.getFullName() + " #win is: " + globalPlayer.getWin();
                    }
                    textView.setText(tmp);
                    break;
                default:
                    break;

            }
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int matchUpIndex; // -1 is global arena data Set. 0 or positive number is an individual matchUP
        private FragmentManager fm;
        private ArrayList<PlaceholderFragment> fragments = new ArrayList<PlaceholderFragment>();
        private ArrayList<UserStat> globalUserStats;

        public void setMatchUpIndex(int matchUpIndex) {
            this.matchUpIndex = matchUpIndex;
        }

        public SectionsPagerAdapter(FragmentManager fm, int matchUpIndex, ArrayList<UserStat> globalUserStats) {
            super(fm);
            this.fm = fm;
            this.matchUpIndex = matchUpIndex;
            this.globalUserStats = globalUserStats;
        }

        @Override
        public Fragment getItem(int position) {
            //getItem() is called by the instantiateItem() internally in order to create a new fragment for a given page.
            // Return a PlaceholderFragment (defined as a static inner class abouve).
            PlaceholderFragment temp = PlaceholderFragment.newInstance(position + 1, matchUpIndex ,globalUserStats );
            // by keeping a ref to the fragment created, we would have an option- whenever we want- to update the fragments (pages) of the adapter
            fragments.add(temp);
            return temp;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }





    }

}
