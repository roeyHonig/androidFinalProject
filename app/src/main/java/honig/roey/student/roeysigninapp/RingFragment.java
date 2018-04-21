package honig.roey.student.roeysigninapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import honig.roey.student.roeysigninapp.dummy.DummyContent;
import honig.roey.student.roeysigninapp.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.RingGlobal;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RingFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // if mColumnCount =1 -> Linear Layout otherwise Grid Layout
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;


    private RecyclerView ringRecyclerView;
    private MyRingRecyclerViewAdapter mAdapter;
    private ArrayList<String> mDataset;
    private ArrayList<String> numOfPlayersPerArenaDataset;
    private ArrayList<String> idOfTheUserArenas;
    private ArrayList<String> superUserOfTheUserArenas;
    private NavDrawer parentActivity;
    private int indexForCountingArenas = 0;
    private int viewVisablity;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RingFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RingFragment newInstance(int columnCount) {
        RingFragment fragment = new RingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Are There Areneas for the current user?
        if (getArguments() != null) {
            // The user Has Arenas to present
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mDataset = getArguments().getStringArrayList("arg1");
            numOfPlayersPerArenaDataset = getArguments().getStringArrayList("arg2");
            idOfTheUserArenas = getArguments().getStringArrayList("arg3");
            superUserOfTheUserArenas = getArguments().getStringArrayList("arg4");
            viewVisablity = View.GONE;

        } else {
            // No Arenas for current User - show the "No Arenea Yet" UI
            viewVisablity = View.VISIBLE;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set the View
        View view = inflater.inflate(R.layout.fragment_ring_list, container, false);
        // Set TextView - "No Arenas Yet"
        TextView textView = view.findViewById(R.id.noArenas);
        textView.setVisibility(viewVisablity);
        // Set FAB
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.addNewArena);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBox();
            }
        });

        ringRecyclerView = view.findViewById(R.id.ringsList);
        // Are There Areneas for the current user?
        if (viewVisablity != View.VISIBLE) {
                    ringRecyclerView.setVisibility(View.VISIBLE);
                    // Set the adapter for recyclerView
                    Context context = ringRecyclerView.getContext();
                    if (mColumnCount <= 1) {
                        ringRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        ringRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                    }
                    indexForCountingArenas = mDataset.size();
                    mAdapter = new MyRingRecyclerViewAdapter(mDataset, numOfPlayersPerArenaDataset, idOfTheUserArenas, superUserOfTheUserArenas,mListener);
                    ringRecyclerView.setAdapter(mAdapter);
        } else {
            // No Arenas for current User - no need to display the recyclerView
            ringRecyclerView.setVisibility(View.GONE);
        }

        return view;


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (NavDrawer) getActivity();
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
        void onListFragmentInteraction(String item);

    }




    public void openDialogBox(){
        //TODO: add a radio button public \ private Arena
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.add_arena_dialog,null);
        final EditText newRingName = dialogView.findViewById(R.id.newRingName);
        builder.setPositiveButton("OK", null)
                .setNegativeButton("Abourt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(),"Later then...",Toast.LENGTH_LONG).show();
                    }
                });

        builder.setView(dialogView);
        final AlertDialog myDialog = builder.create();

        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) myDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputText = newRingName.getText().toString();
                        if (inputText.equals("")) {

                        } else{
                            myDialog.dismiss();

                            if (getArguments() == null){
                                // user has no Arenas
                                String tmpArenaID =parentActivity.pushAndSetNewChildAtArenasTable(inputText, parentActivity.getUid(), parentActivity.getFullNameoFTheCurrentSignedInUser());
                                parentActivity.pushAndSetNewChildAtArenasPerUserTable(parentActivity.getUid(),tmpArenaID);

                            } else if (mDataset.contains(inputText)){
                                //TODO: change to snackbar
                                Toast.makeText(getActivity(),"Same Name Arena Already exists",Toast.LENGTH_LONG).show();

                            } else {
                                String tmpArenaID =parentActivity.pushAndSetNewChildAtArenasTable(inputText, parentActivity.getUid(), parentActivity.getFullNameoFTheCurrentSignedInUser());
                                parentActivity.pushAndSetNewChildAtArenasPerUserTable(parentActivity.getUid(),tmpArenaID);
                            }


                            parentActivity.getNavigationView().setCheckedItem(R.id.nav_rings); // higlight the Rings Item in the Menu on StartUp
                            parentActivity.getNavigationView().getMenu().performIdentifierAction(R.id.nav_rings,0); // Perform Action Associated with Rings Menu Item
                        }
                    }
                });
            }
        });
        myDialog.show();


    }
}

/*

navigationView.setCheckedItem(R.id.nav_rings); // higlight the Rings Item in the Menu on StartUp
        navigationView.getMenu().performIdentifierAction(R.id.nav_rings, 0); // Perform the Action Associated with the Rings Menu Item

 */