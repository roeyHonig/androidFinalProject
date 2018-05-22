package honig.roey.student.roeysigninapp.arena;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

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


    private static RingGlobal globalDataSet = new RingGlobal();

    private static ArrayList<MatchUp> individualMatchUpsDataSet = new ArrayList<>();
    private ArrayList<ChartsCollection> globalAndMatchUpsCharts = new ArrayList<>();     // retrived data from the DB

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
    private int matchUpIndex = -1; // -1 is global arena data Set. 0 or positive number is an individual matchUP
    private NavDrawer parentActivity;



    public RingGlobal getGlobalDataSet() {
        return globalDataSet;
    }

    public ArenaFragment() {
        // Required empty public constructor
    }

    public ArrayList<ChartsCollection> getGlobalAndMatchUpsCharts() {
        return globalAndMatchUpsCharts;
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
            mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), matchUpIndex, globalAndMatchUpsCharts);

            mViewPager = (ViewPager) view.findViewById(R.id.container);
            // Set up the ViewPager with the sections adapter.
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) view.findViewById(R.id.tabs);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        }

        //TODO: just a test, not the actual purpose, demonstrates hoe to reload this fragment
        //TODO: i've even made this TextView (in the fragment_arena) visibility as GONE
        // TODO: please delete the TextView and remove this OnClickListener
        TextView tvBuffer = view.findViewById(R.id.tvBuffer);
        tvBuffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reload The Page
                parentActivity.onListFragmentInteraction(globalDataSet.getKey());
            }
        });

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

    // This method is called by the Main Activity as a result of the OnListFragmentInteractionListener interface
    // that is, when the user clicks on a matchup item from the list
    public void onMatchUpListInteraction(String MatchUpKey) {
        //TODO: change the chart to present the relevent data for the choosed MatchUp



            // iterate over all matchups
            for (int j = 0; j < individualMatchUpsDataSet.size() ; j++) {

                // find the index of the selected MatchUp in the List
                if (individualMatchUpsDataSet.get(j).getKey().equals(MatchUpKey)) {
                    // set a field in the adapter, in case some fragments haven't been created yet
                    // the adapter creates the fragments based on this field
                    mSectionsPagerAdapter.setMatchUpIndex(j);

                    for (int i = 0; i < mSectionsPagerAdapter.fragments.size(); i++) {
                        // change a field in the fragments (pages), so when onCreateView is called (while we scroll them back into view) they will indeed reflect the changes
                                 mSectionsPagerAdapter.fragments.get(i).setMatchUpIndex(j);

                        // directlly change the page we're viewing (and also all the others (creted so far), but we don't see the others)
                                ArrayList<PointDataSet> pointDataSets = new ArrayList<>();
                                List<BarEntry> entries = new ArrayList<>();
                                BarChart chart = mSectionsPagerAdapter.fragments.get(i).getChart();

                                // MatchUps
                                PlaceholderFragment.names = mSectionsPagerAdapter.fragments.get(i).setChartsXAxisLabels(individualMatchUpsDataSet.get(j).getPlayers());

                                //iterate over all players
                                for (int k = 0; k < PlaceholderFragment.names.length ; k++) {
                                    pointDataSets.add(new PointDataSet(1f+k, globalAndMatchUpsCharts.get(mSectionsPagerAdapter.fragments.get(i).matchUpIndex + 1).chartsCollection.get(mSectionsPagerAdapter.fragments.get(i).sectionNumber-1).chart.get(k).getyValue()));
                                }


                                for (PointDataSet data : pointDataSets) {
                                    // turn your data into Entry objects
                                    entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                                }

                                BarDataSet set = new BarDataSet(entries, "BarDataSet");
                                // TODO: edit thus all thing
                               // mSectionsPagerAdapter.fragments.get(i).setSingleChart(chart,set, mSectionsPagerAdapter.fragments.get(i).getSectionNumber(), mSectionsPagerAdapter.fragments.get(i).matchUpIndex, mSectionsPagerAdapter.fragments.get(i).formatter);

                    }

                }
            }





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
     * A placeholder fragment containing a single chart
     * This Fragment represent a single page \ section of the tabbed area in the ArenaFragment
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_GLOBAL_DATA = "global_users_stat";
        private static final String ARG_MATCHUP_INDEX = "matchUPs Index";
        private TextView textView;
        private int matchUpIndex;
        private int sectionNumber;
        private ArrayList<ChartsCollection> globalAndMatchUpsCharts;
        private static String[] names;
        public static IAxisValueFormatter formatter;
        private BarChart chart;

        public void setMatchUpIndex(int matchUpIndex) {
            this.matchUpIndex = matchUpIndex;
        }

        public BarChart getChart() {
            return chart;
        }

        public int getSectionNumber() {
            return sectionNumber;
        }

        //TODO: cancel this fab event
        public void setClicksOnFab(int matchUpIndex) {
            this.matchUpIndex = matchUpIndex;
        }

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, int matchUpIndex, ArrayList<ChartsCollection> globalAndMatchUpsCharts) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt(ARG_MATCHUP_INDEX, matchUpIndex);
            args.putParcelableArrayList(ARG_GLOBAL_DATA, globalAndMatchUpsCharts);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.matchUpIndex = getArguments().getInt(ARG_MATCHUP_INDEX);
            this.globalAndMatchUpsCharts = getArguments().getParcelableArrayList(ARG_GLOBAL_DATA);
            this.sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER) ;
        }

        // since we're using an extenstion of FragmentPagerAdapter, the fragments -representing the pages - are created once and never destroyed.
        // but still when the user scroll them out of the screen, thier views are gone
        // we have to recreate thier respected view every time the user rescroll back to them
        // that is when onCreateView is called
        // so we have to make sure that this methood allaways holds the mose current set of data we want to load
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            // standard Bar Chart
            ArrayList<PointDataSet> pointDataSets = new ArrayList<>();
            List<BarEntry> entries = new ArrayList<>();
            BarDataSet set;

            // Grouped Bar Chart
            ArrayList<PointDataSet> pointDataSets1 = new ArrayList<>();
            ArrayList<PointDataSet> pointDataSets2 = new ArrayList<>();
            ArrayList<PointDataSet> pointDataSets3 = new ArrayList<>();
            ArrayList<PointDataSet> pointDataSets4 = new ArrayList<>();
            List<BarEntry> entries1 = new ArrayList<>();
            List<BarEntry> entries2 = new ArrayList<>();
            List<BarEntry> entries3 = new ArrayList<>();
            List<BarEntry> entries4 = new ArrayList<>();
            BarDataSet set1;
            BarDataSet set2;
            BarDataSet set3;
            BarDataSet set4;



            View rootView = inflater.inflate(R.layout.stat_page, container, false);

            //TODO: if no arguments came, set something insted of the chart
            //textView = (TextView) rootView.findViewById(R.id.section_label);
            chart =  rootView.findViewById(R.id.chart);

            FloatingActionButton addMatchResultFAB = rootView.findViewById(R.id.addMatchResult);

            SeekBar xAxisSeekBar = rootView.findViewById(R.id.xAxisSeekBar);
            xAxisSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    float xMax;
                    float range;

                    if (sectionNumber == 2 || sectionNumber == 3){
                        xMax = (float) names.length + 0.25f;
                    } else {
                        xMax = names.length+1f;
                    }

                    range = xMax - 1.5f;

                    if (b) {
                        // initated by the usert
                        chart.setVisibleXRange(1.5f,(range + 1.5f)-range*i/100f);
                        chart.invalidate();
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    float xMax;

                    if (sectionNumber == 2 || sectionNumber == 3){
                        xMax = (float) names.length + 0.25f;
                    } else {
                        xMax = names.length+1f;
                    }

                    chart.setVisibleXRangeMaximum(xMax);
                    chart.setVisibleXRangeMinimum(1.5f); // If this is e.g. set to 10, it is not possible to zoom in further than 10 values on the x-axis.


                }
            });

            chart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                    if (names != null){

                        float xMax;

                        if (sectionNumber == 2 || sectionNumber == 3){
                            xMax = (float) names.length + 0.25f;
                        } else {
                            xMax = names.length+1f;
                        }

                        float range = xMax - 1.5f;
                        xAxisSeekBar.setProgress((int)(((range+1.5)-chart.getVisibleXRange())*100/range));
                    }

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {

                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                    if (names != null){

                        float xMax;

                        if (sectionNumber == 2 || sectionNumber == 3){
                            xMax = (float) names.length + 0.25f;
                        } else {
                            xMax = names.length+1f;
                        }

                        float range = xMax - 1.5f;
                        xAxisSeekBar.setProgress((int)(((range+1.5)-chart.getVisibleXRange())*100/range));
                    }

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });






            if (this.matchUpIndex == -1) {
                // Global
                names = setChartsXAxisLabels(globalDataSet.getUserStats());
            } else {
                // MatchUps
                names = setChartsXAxisLabels(individualMatchUpsDataSet.get(this.matchUpIndex).getPlayers());
            }


            formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value <= names.length) {
                        return names[(int) (value-1)];
                    } else {
                        return "";
                    }

                }
            };


            float winingStrikeRecord;
            int winingStrikeRecordIndex;
            String winingStrikeRecordHolderFullName;
            //iterate over all players
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    // Success%
                    winingStrikeRecord = 0f;
                    winingStrikeRecordIndex = 0;
                    winingStrikeRecordHolderFullName = "";
                    for (int i = 0; i < names.length ; i++) {
                        pointDataSets.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(0).chart.get(i).getyValue()));
                    }

                    for (PointDataSet data : pointDataSets) {
                        // turn your data into Entry objects
                        entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                    }

                    set = new BarDataSet(entries, "Success%");
                    setSingleChart(chart,set, sectionNumber, this.matchUpIndex, formatter, winingStrikeRecord, winingStrikeRecordHolderFullName);

                    break;
                case 2:
                   // Games
                        // Loss
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets1.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(1).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets1) {
                            // turn your data into Entry objects
                            entries1.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                       set1 = new BarDataSet(entries1, "Loss");


                        // Draw
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets2.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(2).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets2) {
                            // turn your data into Entry objects
                            entries2.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set2 = new BarDataSet(entries2, "Draws");



                        // Win
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets3.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(3).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets3) {
                            // turn your data into Entry objects
                            entries3.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set3 = new BarDataSet(entries3, "Win");


                        // #games
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets4.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(4).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets4) {
                            // turn your data into Entry objects
                            entries4.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set4 = new BarDataSet(entries4, "Games");


                      setSingleGroupedBarChart(chart,set1, set2, set3, set4, sectionNumber, this.matchUpIndex, formatter);

                    break;
                case 3:
                    // Goals

                        // Goals For
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets1.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(5).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets1) {
                            // turn your data into Entry objects
                            entries1.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set1 = new BarDataSet(entries1, "Goals For");


                        // Goals Against
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets2.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(6).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets2) {
                            // turn your data into Entry objects
                            entries2.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set2 = new BarDataSet(entries2, "Goals Against");



                        // Goals For Avarge
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets3.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(7).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets3) {
                            // turn your data into Entry objects
                            entries3.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set3 = new BarDataSet(entries3, "Goals For Avarge");


                        // Goals Against Avarge
                        for (int i = 0; i < names.length ; i++) {
                            pointDataSets4.add(new PointDataSet(1f+i, globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(8).chart.get(i).getyValue()));
                        }

                        for (PointDataSet data : pointDataSets4) {
                            // turn your data into Entry objects
                            entries4.add(new BarEntry(data.getxValue(), data.getyValue()));
                        }

                        set4 = new BarDataSet(entries4, "Goals Against Avarge");


                        setSingleGroupedBarChart(chart,set1, set2, set3, set4, sectionNumber, this.matchUpIndex, formatter);

                    break;
                case 4:
                   // Wining Strike
                    winingStrikeRecord = 0f;
                    winingStrikeRecordIndex = 0;
                    winingStrikeRecordHolderFullName = "";
                    for (int i = 0; i < names.length ; i++) {
                        float fullValue = globalAndMatchUpsCharts.get(this.matchUpIndex + 1).chartsCollection.get(9).chart.get(i).getyValue();
                        float reminderValue = fullValue % 1000f;
                        float currentWiningStrike = reminderValue;
                        float tmpWiningStrikeRecord = (fullValue - reminderValue) / 1000;
                        if (tmpWiningStrikeRecord > winingStrikeRecord){
                            winingStrikeRecord = tmpWiningStrikeRecord;
                            winingStrikeRecordIndex = i;
                        }

                        pointDataSets.add(new PointDataSet(1f+i, currentWiningStrike));
                    }

                    for (PointDataSet data : pointDataSets) {
                        // turn your data into Entry objects
                        entries.add(new BarEntry(data.getxValue(), data.getyValue()));
                    }

                    winingStrikeRecordHolderFullName = names[winingStrikeRecordIndex];

                    set = new BarDataSet(entries, "Wining Strike");
                    setSingleChart(chart,set, sectionNumber, this.matchUpIndex, formatter, winingStrikeRecord, winingStrikeRecordHolderFullName);

                    break;
            }





            return rootView;
        }

        public void setSingleChart(BarChart chart, BarDataSet set, int sectionNumber, int matchUpIndex, IAxisValueFormatter formatter, float winingStrikeRecord, String winingStrikeRecordHolderFullName) {
            BarData barData;
            barData = new BarData(set);
            barData.setBarWidth(0.9f); // set custom bar width
            chart.setData(barData);
            // find max value to
            int tmpIndex = 0;
            for (int i = 0; i < set.getEntryCount(); i++) {
                if (set.getEntryForIndex(i).getY() > set.getEntryForIndex(tmpIndex).getY()) {
                    tmpIndex = i;
                }
            }
            // HighLight the Max Value
            chart.highlightValue(set.getEntryForIndex(tmpIndex).getX(),0);

            //chart.setPinchZoom(true); // zooming X & Y Axis at one gesture

            // disable highlight of values by the user's gestures
            chart.setHighlightPerDragEnabled(false);
            chart.setHighlightPerTapEnabled(false);


            XAxis xAxis = chart.getXAxis();
            YAxis yAxisLeft = chart.getAxisLeft();
            YAxis yAxisRight = chart.getAxisRight();


        /*
        This will prevent the formatter from drawing duplicate axis labels (caused by axis intervals < 1).
        As soon as the "zoom level" of the chart is high enough, it will stop recalculating smaller intervals.
         */
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

            xAxis.setValueFormatter(formatter);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTextSize();
            // set an angle for the xAxis Labels
            xAxis.setLabelRotationAngle(22.5f);
            // max #values
            xAxis.setLabelCount(6);

            yAxisLeft.setAxisMinimum(0f);
            yAxisRight.setAxisMinimum(0f);
            yAxisLeft.setAxisMaximum(set.getEntryForIndex(tmpIndex).getY()*1.1f);
            yAxisRight.setAxisMaximum(set.getEntryForIndex(tmpIndex).getY()*1.1f);


            // Sets the Legend enabled or disabled
            chart.getLegend().setEnabled(true);
            // make the x-axis fit \ or not exactly all bars
            chart.setFitBars(true);

            // set an emphty ("") description in the right bottom corrner of the chart
            Description description = new Description();
            description.setText("");
            chart.setDescription(description);

            // Limit Lines
            // this is just an example, in this exampl i want to add a limit line to the last section , which was section #4 at the time

            if (sectionNumber == 4) {
                // add limit line
                float limit = winingStrikeRecord;
                LimitLine ll = new LimitLine(limit, "All Time Record Score: " + limit + " (" + winingStrikeRecordHolderFullName + ")");
                ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                ll.setLineColor(Color.RED);
                ll.setLineWidth(0.1f);
                ll.setTextColor(Color.BLACK);
                ll.setTextSize(1f);
                yAxisLeft.addLimitLine(ll);
                yAxisRight.addLimitLine(ll);
                if (limit > set.getEntryForIndex(tmpIndex).getY()) {
                    yAxisLeft.setAxisMaximum(limit * 1.1f);
                    yAxisRight.setAxisMaximum(limit * 1.1f);
                }


            }




            chart.setVisibleXRangeMaximum(names.length+1f);
            chart.setVisibleXRangeMinimum(1.5f); // If this is e.g. set to 10, it is not possible to zoom in further than 10 values on the x-axis.
            chart.invalidate(); // refresh
        }


        public void setSingleGroupedBarChart(BarChart chart, BarDataSet set1, BarDataSet set2 , BarDataSet set3, BarDataSet set4 , int sectionNumber, int matchUpIndex, IAxisValueFormatter formatter) {
            float groupSpace = 0.06f;
            float barSpace = 0.01f; // x4 dataset
            float barWidth = 0.225f; // x4 dataset
            // (0.01 + 0.225) * 4 + 0.06 = 1.00 -> interval per "group"

            set1.setColor(getResources().getColor(R.color.colorAccent));
            set2.setColor(getResources().getColor(R.color.colorGreen));
            set3.setColor(getResources().getColor(R.color.colorYellow));
            set4.setColor(getResources().getColor(R.color.colorPrimaryDark));




            BarData barData;
            barData = new BarData(set1, set2, set3, set4);
            barData.setBarWidth(barWidth); // set custom bar width
            chart.setData(barData);
            chart.groupBars(0.5f, groupSpace, barSpace); // perform the "explicit" grouping

            float leftYAxisMaxValue=0;
            BarDataSet[] sets = new BarDataSet[]{set1, set2, set3, set4};
            for (int j = 0; j < sets.length; j++) {
                int tmpIndex = 0;
                for (int i = 0; i < sets[j].getEntryCount(); i++) {
                    if (sets[j].getEntryForIndex(i).getY() > sets[j].getEntryForIndex(tmpIndex).getY()) {
                        tmpIndex = i;
                    }
                }

                leftYAxisMaxValue = (sets[j].getEntryForIndex(tmpIndex).getY() > leftYAxisMaxValue ) ?  sets[j].getEntryForIndex(tmpIndex).getY()  : leftYAxisMaxValue;

            }

            if (leftYAxisMaxValue == 0) {
                leftYAxisMaxValue = 1;
            }

            // only to be used in Section 3 (2 Yaxis dependecny)
            float rightYAxisMaxValue=0;
            BarDataSet[] rightSets = new BarDataSet[]{set3, set4};
            for (int j = 0; j < rightSets.length; j++) {
                int tmpIndex = 0;
                for (int i = 0; i < rightSets[j].getEntryCount(); i++) {
                    if (rightSets[j].getEntryForIndex(i).getY() > rightSets[j].getEntryForIndex(tmpIndex).getY()) {
                        tmpIndex = i;
                    }
                }

                rightYAxisMaxValue = (rightSets[j].getEntryForIndex(tmpIndex).getY() > rightYAxisMaxValue ) ?  rightSets[j].getEntryForIndex(tmpIndex).getY()  : rightYAxisMaxValue;

            }

            if (rightYAxisMaxValue == 0) {
                rightYAxisMaxValue = 1;
            }


            // disable highlight of values by the user's gestures
            chart.setHighlightPerDragEnabled(false);
            chart.setHighlightPerTapEnabled(false);


            XAxis xAxis = chart.getXAxis();
            YAxis yAxisLeft = chart.getAxisLeft();
            YAxis yAxisRight = chart.getAxisRight();


        /*
        This will prevent the formatter from drawing duplicate axis labels (caused by axis intervals < 1).
        As soon as the "zoom level" of the chart is high enough, it will stop recalculating smaller intervals.
         */
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

            xAxis.setValueFormatter(formatter);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTextSize();
            // set an angle for the xAxis Labels
            xAxis.setLabelRotationAngle(22.5f);
            // max #values
            xAxis.setLabelCount(6);

            yAxisLeft.setAxisMinimum(0f);
            yAxisRight.setAxisMinimum(0f);
            yAxisLeft.setAxisMaximum(leftYAxisMaxValue*1.1f);
            yAxisRight.setAxisMaximum(leftYAxisMaxValue*1.1f);

            if (sectionNumber == 3) {
                // goals
                set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set4.setAxisDependency(YAxis.AxisDependency.RIGHT);
                yAxisRight.setAxisMaximum(rightYAxisMaxValue*1.1f);
            }


            // Sets the Legend enabled or disabled
            chart.getLegend().setEnabled(true);
            // make the x-axis fit \ or not exactly all bars
            chart.setFitBars(true);

            // set an emphty ("") description in the right bottom corrner of the chart
            Description description = new Description();
            description.setText("");
            chart.setDescription(description);



            // add limit lines
            for (int i = 0; i < names.length-1; i++) {
                float limit = 1.5f + i;
                LimitLine ll = new LimitLine(limit, "");
                ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                ll.setLineColor(Color.RED);
                ll.setLineWidth(0.5f);
                ll.setTextColor(Color.TRANSPARENT);
                ll.setTextSize(1f);
                xAxis.addLimitLine(ll);
            }


            chart.setVisibleXRangeMaximum(names.length+1f);
            chart.setVisibleXRangeMinimum(1.5f); // If this is e.g. set to 10, it is not possible to zoom in further than 10 values on the x-axis.
            chart.invalidate(); // refresh
        }

        // set the names of the players as the X-Axis labels for all charts
        public static String[] setChartsXAxisLabels(ArrayList<UserStat> userStats){
            String[] names = new String[userStats.size()]; // BarChart XAxis Labels - names of the players
            for (int i = 0; i < userStats.size(); i++) {
                names[i] = userStats.get(i).getFullName();
            }
            return names;
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
        private ArrayList<ChartsCollection> globalAndMatchUpsCharts = new ArrayList<>();



        public void setMatchUpIndex(int matchUpIndex) {
            this.matchUpIndex = matchUpIndex;
        }

        public SectionsPagerAdapter(FragmentManager fm, int matchUpIndex, ArrayList<ChartsCollection> globalAndMatchUpsCharts) {
            super(fm);
            this.fm = fm;
            this.matchUpIndex = matchUpIndex;
            this.globalAndMatchUpsCharts = globalAndMatchUpsCharts;
        }

        @Override
        public Fragment getItem(int position) {
            //getItem() is called by the instantiateItem() internally in order to create a new fragment for a given page.
            // Return a PlaceholderFragment (defined as a static inner class abouve).
            PlaceholderFragment temp = PlaceholderFragment.newInstance(position + 1, matchUpIndex ,globalAndMatchUpsCharts );
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

        globalAndMatchUpsCharts.clear();
        for (int i = 0; i < individualMatchUpsDataSet.size() + 1 ; i++) {
            ArrayList<Chart> collection = new ArrayList<>(); //TODO: not hardcoded
            if (i == 0){
                //global

                    // PCT
                    ArrayList<PointDataSet> pointDataSetsPct = new ArrayList<>();
                    //pointDataSets.clear();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsPct.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getPct())); //to make sure xValue sorted
                    }
                    Chart pct = new Chart(pointDataSetsPct);
                    collection.add(pct);

                    // #loss
                    ArrayList<PointDataSet> pointDataSetsLos = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsLos.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getLos())); //to make sure xValue sorted
                    }
                    Chart los = new Chart(pointDataSetsLos);
                    collection.add(los);

                     // #drw
                    ArrayList<PointDataSet> pointDataSetsDrw = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsDrw.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getDrw())); //to make sure xValue sorted
                    }
                    Chart drw = new Chart(pointDataSetsDrw);
                    collection.add(drw);

                    // #wins
                    ArrayList<PointDataSet> pointDataSetsWin = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsWin.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getWin())); //to make sure xValue sorted
                    }
                    Chart wins = new Chart(pointDataSetsWin);
                    collection.add(wins);

                    // #Games
                    ArrayList<PointDataSet> pointDataSetsGames = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsGames.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getNumGames())); //to make sure xValue sorted
                    }
                    Chart numGames = new Chart(pointDataSetsGames);
                    collection.add(numGames);

                    // #GoalsFor
                    ArrayList<PointDataSet> pointDataSetsGoalsFor = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsGoalsFor.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getGoalsFor())); //to make sure xValue sorted
                    }
                    Chart goalsFor = new Chart(pointDataSetsGoalsFor);
                    collection.add(goalsFor);

                    // #GoalsAgainst
                    ArrayList<PointDataSet> pointDataSetsGoalsAgainst = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsGoalsAgainst.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getGoalsAgainst())); //to make sure xValue sorted
                    }
                    Chart goalsAgainst = new Chart(pointDataSetsGoalsAgainst);
                    collection.add(goalsAgainst);

                    // #GoalsForAverage
                    ArrayList<PointDataSet> pointDataSetsGoalsForAvaerage = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsGoalsForAvaerage.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getGoalsForAverage())); //to make sure xValue sorted
                    }
                    Chart goalsForAvarage = new Chart(pointDataSetsGoalsForAvaerage);
                    collection.add(goalsForAvarage);

                    // #GoalsAgainstAverage
                    ArrayList<PointDataSet> pointDataSetsGoalsAgainstAvaerage = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsGoalsAgainstAvaerage.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getGoalsAgainstAverage())); //to make sure xValue sorted
                    }
                    Chart goalsAgainstAvarage = new Chart(pointDataSetsGoalsAgainstAvaerage);
                    collection.add(goalsAgainstAvarage);

                    // #WiningStrike
                    ArrayList<PointDataSet> pointDataSetsWiningStrike = new ArrayList<>();
                    for (int j = 0; j < globalDataSet.getUserStats().size(); j++) {
                        pointDataSetsWiningStrike.add(new PointDataSet((float) (j + 1), (float) globalDataSet.getUserStats().get(j).getWinningStrike())); //to make sure xValue sorted
                    }
                    Chart winingStrike = new Chart(pointDataSetsWiningStrike);
                    collection.add(winingStrike);



            } else {
                //individual Matchup

                    // PCT
                ArrayList<PointDataSet> pointDataSetsPct = new ArrayList<>();
                //pointDataSets.clear();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsPct.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getPct())); //to make sure xValue sorted
                }
                Chart pct = new Chart(pointDataSetsPct);
                collection.add(pct);

                // #loss
                ArrayList<PointDataSet> pointDataSetsLos = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsLos.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getLos())); //to make sure xValue sorted
                }
                Chart los = new Chart(pointDataSetsLos);
                collection.add(los);

                // #drw
                ArrayList<PointDataSet> pointDataSetsDrw = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsDrw.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getDrw())); //to make sure xValue sorted
                }
                Chart drw = new Chart(pointDataSetsDrw);
                collection.add(drw);

                // #wins
                ArrayList<PointDataSet> pointDataSetsWin = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsWin.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getWin())); //to make sure xValue sorted
                }
                Chart wins = new Chart(pointDataSetsWin);
                collection.add(wins);

                // #Games
                ArrayList<PointDataSet> pointDataSetsGames = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsGames.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getNumGames())); //to make sure xValue sorted
                }
                Chart numGames = new Chart(pointDataSetsGames);
                collection.add(numGames);

                // #GoalsFor
                ArrayList<PointDataSet> pointDataSetsGoalsFor = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsGoalsFor.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getGoalsFor())); //to make sure xValue sorted
                }
                Chart goalsFor = new Chart(pointDataSetsGoalsFor);
                collection.add(goalsFor);

                // #GoalsAgainst
                ArrayList<PointDataSet> pointDataSetsGoalsAgainst = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsGoalsAgainst.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getGoalsAgainst())); //to make sure xValue sorted
                }
                Chart goalsAgainst = new Chart(pointDataSetsGoalsAgainst);
                collection.add(goalsAgainst);

                // #GoalsForAverage
                ArrayList<PointDataSet> pointDataSetsGoalsForAvaerage = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsGoalsForAvaerage.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getGoalsForAverage())); //to make sure xValue sorted
                }
                Chart goalsForAvarage = new Chart(pointDataSetsGoalsForAvaerage);
                collection.add(goalsForAvarage);

                // #GoalsAgainstAverage
                ArrayList<PointDataSet> pointDataSetsGoalsAgainstAvaerage = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsGoalsAgainstAvaerage.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getGoalsAgainstAverage())); //to make sure xValue sorted
                }
                Chart goalsAgainstAvarage = new Chart(pointDataSetsGoalsAgainstAvaerage);
                collection.add(goalsAgainstAvarage);

                // #WiningStrike
                ArrayList<PointDataSet> pointDataSetsWiningStrike = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    pointDataSetsWiningStrike.add(new PointDataSet((float) (j + 1), (float) individualMatchUpsDataSet.get(i-1).getPlayers().get(j).getWinningStrike())); //to make sure xValue sorted
                }
                Chart winingStrike = new Chart(pointDataSetsWiningStrike);
                collection.add(winingStrike);

            }


            ChartsCollection chartsCollection = new ChartsCollection(collection);
            globalAndMatchUpsCharts.add(chartsCollection);
        }

    }



}
