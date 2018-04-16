package honig.roey.student.roeysigninapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honig.roey.student.roeysigninapp.dummy.DummyContent;
import honig.roey.student.roeysigninapp.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.UserStat;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlayerStatFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    // if mColumnCount =1 -> Linear Layout otherwise Grid Layout
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private String key;
    private String name;
    private int numPlayers;
    private boolean isPublicViewd;
    private ArrayList<UserStat> userStats;

    // TODO: this is junk just to try and pass data to the adapter
    private ArrayList<String> trymeout = new ArrayList<String>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerStatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlayerStatFragment newInstance(int columnCount) {
        PlayerStatFragment fragment = new PlayerStatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            // TODO: this is junk just to try and pass data to the adapter
            // *******
            trymeout.clear();
            trymeout.add("user1");
            trymeout.add("user2");
            trymeout.add("user3");
            // *********

            key = getArguments().getString("argKey");
            name = getArguments().getString("argName");
            numPlayers =getArguments().getInt("argNumPlayers") ;
            isPublicViewd =getArguments().getBoolean("argIsPublicViewd") ;
            userStats = getArguments().getParcelableArrayList("argUserStatArrayList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playerstat_list, container, false);

        //TODO: this is an example to see if we got all the data right from the Parent Activity
        // ******
        TextView roey = view.findViewById(R.id.arenaNameTitle);
        roey.setText(key);
        // *******

        RecyclerView recyclerView = view.findViewById(R.id.usersList);
        // Set the adapter
            Context context = recyclerView.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new MyPlayerStatRecyclerViewAdapter(trymeout, mListener));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
