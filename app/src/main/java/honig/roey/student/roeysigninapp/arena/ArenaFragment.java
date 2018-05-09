package honig.roey.student.roeysigninapp.arena;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.tables.MatchUp;
import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.UserStat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArenaFragment extends Fragment {
    private ArrayList<UserStat> mValues1;
    private ArrayList<UserStat> mValues2;
    private ArrayList<String> matchupKey;
    private RingGlobal globalDataSet = new RingGlobal();
    private ArrayList<MatchUp> individualMatchUpsDataSet = new ArrayList<>();

    public static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;



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

        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

}
