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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import honig.roey.student.roeysigninapp.NavDrawer;
import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.tables.MatchUp;
import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.UserStat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArenaFragment extends Fragment {

    private ArrayList<PointDataSet> pointDataSets = new ArrayList<>();

    private BarData barData;
    private BarChart chart;
    private static IAxisValueFormatter formatter;


    private static RingGlobal globalDataSet = new RingGlobal();
    private static String[] names = setChartsXAxisLabels();
    private ArrayList<MatchUp> individualMatchUpsDataSet = new ArrayList<>();
    private ArrayList<ChartsCollection> globalAndMatchUpsCharts = new ArrayList<>();     // retrived data from the DB
    private static ArrayList<SetCollection> globalAndMatchUpsBarChartsData = new ArrayList<>(); // retrived data from the DB arranged in special class (BarData) for the mChart
    // The type of data we present in chartView
    private final int FOR_GLOBAL = 0;
    private final int FOR_MATCHUP = 1;
    private final int OF_TYPE_PCT = 0;
    private final int OF_TYPE_NUM_WINS = 1;
    private final int OF_TYPE_NUM_LOSS = 2;
    private final int OF_TYPE_NUM_DRW = 3;

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

            setCharts();


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

        private BarData barData;
        private BarChart chart;
        private BarDataSet set;


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
            //textView = (TextView) rootView.findViewById(R.id.section_label);
            chart =  rootView.findViewById(R.id.chart);
            formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return names[(int) (value-1)];
                }
            };

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER) ;
            // TODO: it's 0, meaning global, next step, all of the matchups as well
            //TODO: right now: section 1 -> pct , section 2 -> win , section 3 -> los , section 4 -> drw
            set = globalAndMatchUpsBarChartsData.get(0).setCollection.get(sectionNumber-1);
            setBarChart(set);

            return rootView;
        }


        private void setBarChart(BarDataSet set) {
            barData = new BarData(set);
            barData.setBarWidth(0.9f); // set custom bar width
            chart.setData(barData);


            // .. and more styling options
            //chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.setPinchZoom(true); // zooming X & Y Axis at one gesture

            // disable highlight of values by the user's gestures
            chart.setHighlightPerDragEnabled(false);
            chart.setHighlightPerTapEnabled(false);

            XAxis xAxis = chart.getXAxis();

        /*
        This will prevent the formatter from drawing duplicate axis labels (caused by axis intervals < 1).
        As soon as the "zoom level" of the chart is high enough, it will stop recalculating smaller intervals.
         */
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

            xAxis.setValueFormatter(formatter);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTextSize();


            chart.getAxisLeft().setAxisMinimum(0f);
            chart.getAxisRight().setAxisMinimum(0f);

            // Sets the Legend enabled or disabled
            chart.getLegend().setEnabled(false);

            //chart.setFitBars(true); // make the x-axis fit \ or not exactly all bars
            // only 3 bars at the viewport
            chart.setVisibleXRange(0,3);
            // HighLight the Max Value
            //chart.highlightValue(4,0);
            // set an emphty ("") description in the right bottom corrner of the chart
            Description description = new Description();
            description.setText("");
            chart.setDescription(description);

            chart.invalidate(); // refresh
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

    // init globalAndMatchUpsCharts
    private void setCharts() {


        /*
        for (PointDataSet data : pointDataSets) {
            // turn your data into Entry objects
            entries.add(new BarEntry(data.getxValue(), data.getyValue()));
        }

        set = new BarDataSet(entries, "BarDataSet");
        */

        for (int i = 0; i < individualMatchUpsDataSet.size() + 1 ; i++) {
            globalAndMatchUpsCharts.clear();
            globalAndMatchUpsBarChartsData.clear();

            ChartsCollection chartsCollection;
            SetCollection barSetCollection;
            if (i ==0){
                //global
                ArrayList<Chart> collection = new ArrayList<>();
                ArrayList<BarDataSet> setCollection = new ArrayList<>();
                List<BarEntry> entries = new ArrayList<>();
                BarDataSet set;

                    // PCT
                    pointDataSets.clear();
                    entries.clear();

                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSets.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getPct())); //to make sure xValue sorted
                    }
                    Chart chart = new Chart(pointDataSets);
                    collection.add(chart);

                    // turn your data into Entry objects
                    for (PointDataSet data: pointDataSets) {
                        entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                        set = new BarDataSet(entries, "PCT");
                        setCollection.add(set);
                    }




                    // #wins
                    pointDataSets.clear();
                    entries.clear();

                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSets.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getWin())); //to make sure xValue sorted
                    }
                    chart = new Chart(pointDataSets);
                    collection.add(chart);

                    // turn your data into Entry objects
                    for (PointDataSet data: pointDataSets) {
                        entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                        set = new BarDataSet(entries, "WIN");
                        setCollection.add(set);
                    }




                    // #loss
                    pointDataSets.clear();
                    entries.clear();

                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSets.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getLos())); //to make sure xValue sorted
                    }
                    chart = new Chart(pointDataSets);
                    collection.add(chart);

                    // turn your data into Entry objects
                    for (PointDataSet data: pointDataSets) {
                        entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                        set = new BarDataSet(entries, "LOS");
                        setCollection.add(set);
                    }




                    // #drw
                    pointDataSets.clear();
                    entries.clear();

                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSets.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getDrw())); //to make sure xValue sorted
                    }
                    chart = new Chart(pointDataSets);
                    collection.add(chart);

                    // turn your data into Entry objects
                    for (PointDataSet data: pointDataSets) {
                        entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                        set = new BarDataSet(entries, "DRW");
                        setCollection.add(set);
                    }





                chartsCollection = new ChartsCollection(collection);
                globalAndMatchUpsCharts.add(chartsCollection);

                barSetCollection = new SetCollection(setCollection);
                globalAndMatchUpsBarChartsData.add(barSetCollection);


            } else {
                // matchup
                ArrayList<Chart> collection = new ArrayList<>();
                ArrayList<BarDataSet> setCollection = new ArrayList<>();
                List<BarEntry> entries = new ArrayList<>();
                BarDataSet set;

                // PCT
                pointDataSets.clear();
                entries.clear();

                for (int j = 0; j < 2; j++) {
                    pointDataSets.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getPct())); //to make sure xValue sorted
                }
                Chart chart = new Chart(pointDataSets);
                collection.add(chart);

                // turn your data into Entry objects
                for (PointDataSet data: pointDataSets) {
                    entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                    set = new BarDataSet(entries, "PCT");
                    setCollection.add(set);
                }




                // #wins
                pointDataSets.clear();
                entries.clear();

                for (int j = 0; j < 2; j++) {
                    pointDataSets.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getWin())); //to make sure xValue sorted
                }
                chart = new Chart(pointDataSets);
                collection.add(chart);

                // turn your data into Entry objects
                for (PointDataSet data: pointDataSets) {
                    entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                    set = new BarDataSet(entries, "WIN");
                    setCollection.add(set);
                }




                // #loss
                pointDataSets.clear();
                entries.clear();

                for (int j = 0; j < 2; j++) {
                    pointDataSets.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getLos())); //to make sure xValue sorted
                }
                chart = new Chart(pointDataSets);
                collection.add(chart);

                // turn your data into Entry objects
                for (PointDataSet data: pointDataSets) {
                    entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                    set = new BarDataSet(entries, "LOS");
                    setCollection.add(set);
                }




                // #drw
                pointDataSets.clear();
                entries.clear();

                for (int j = 0; j < 2; j++) {
                    pointDataSets.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getDrw())); //to make sure xValue sorted
                }
                chart = new Chart(pointDataSets);
                collection.add(chart);

                // turn your data into Entry objects
                for (PointDataSet data: pointDataSets) {
                    entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                    set = new BarDataSet(entries, "DRW");
                    setCollection.add(set);
                }





                chartsCollection = new ChartsCollection(collection);
                globalAndMatchUpsCharts.add(chartsCollection);

                barSetCollection = new SetCollection(setCollection);
                globalAndMatchUpsBarChartsData.add(barSetCollection);

            }
        }

    }
    // set the names of the players as the X-Axis labels for all charts
    private static String[] setChartsXAxisLabels(){
        String[] names = new String[globalDataSet.getUserStats().size()]; // BarChart XAxis Labels - names of the players
        for (int i = 0; i < globalDataSet.getUserStats().size(); i++) {
            names[i] = globalDataSet.getUserStats().get(i).getFullName();
        }
        return names;
    }



}
