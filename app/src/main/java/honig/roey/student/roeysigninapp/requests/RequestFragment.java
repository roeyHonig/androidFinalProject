package honig.roey.student.roeysigninapp.requests;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import honig.roey.student.roeysigninapp.NavDrawer;
import honig.roey.student.roeysigninapp.OnGetDataFromFirebaseDbListener;
import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.requests.dummy.DummyContent;
import honig.roey.student.roeysigninapp.requests.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RequestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private  int noRequestsVisibility;
    private  int noInvitesVisibility;
    private ArrayList<Request> userAproves;
    private ArrayList<Request> userInvites;
    private MyRequestRecyclerViewAdapter mInvitesAdapter;
    private MyRequestRecyclerViewAdapter mRequestsAdapter;
    private NavDrawer parentActivity;
    private AlertDialog myDialog;
    private Request newRequest = new Request("1","null","null","null","null","null","null",2);
    private int errorCode = 0;
    /*
    1 - I invited a user that doesn't exsists


     */

    // Interface to call methods after reading the FireBase Realtime DB
    // ****************************************************************
    // Do this after reading the lists of Arenas Id's the current user have
        OnGetDataFromFirebaseDbListener cureentUserArenas = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {

            for (DataSnapshot record: data.getChildren()
                 ) {

            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do this after reading the table UsersLoginTime
    OnGetDataFromFirebaseDbListener tableUsersLoginTimeSnapShot = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    }

    // ****************************************************************


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RequestFragment newInstance(int columnCount) {
        RequestFragment fragment = new RequestFragment();
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
            userAproves=getArguments().getParcelableArrayList("arg1");
            userInvites = getArguments().getParcelableArrayList("arg2");


            if (userAproves.size()!=0){
                noRequestsVisibility = View.GONE;
            } else {
                noRequestsVisibility = View.VISIBLE;
            }


            if (userInvites.size()!=0){
                noInvitesVisibility = View.GONE;
            } else {
                noInvitesVisibility = View.VISIBLE;
            }

            /*
            Parcel roey = Parcel.obtain();
            userAproves.get(0).writeToParcel(roey,0);
            userAproves.add(Request.CREATOR.createFromParcel(roey));
            */
        } else {
            // No Requests of any type for current User - show the "No Arenea Yet" UI
            noRequestsVisibility = View.VISIBLE;
            noInvitesVisibility = View.VISIBLE;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set the View
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);
        // Set the invite a Friend Fab
        FloatingActionButton btInvite =view.findViewById(R.id.btInvite);
        // Set TextViews visibility - "No Requests \ Invites"
        TextView textViewNoInvites = view.findViewById(R.id.noInvites);
        textViewNoInvites.setVisibility(noInvitesVisibility);
        TextView textViewNoRequests = view.findViewById(R.id.noRequests);
        //textViewNoRequests.setVisibility(noRequestsVisibility);

        // set recyclerViews
        RecyclerView recyclerViewInvitesList = view.findViewById(R.id.invitesList);
        RecyclerView recyclerViewRequestsList = view.findViewById(R.id.aprovalsList);


        // set Toggle buttons
        Button toggleButtonInvites = view.findViewById(R.id.toggleButtonInvites);
        Button toggleButtonRequests = view.findViewById(R.id.toggleButtonRequests);

        toggleButtonInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewInvitesList.setVisibility(View.VISIBLE);
                textViewNoInvites.setVisibility(noInvitesVisibility);

                recyclerViewRequestsList.setVisibility(View.GONE);
                textViewNoRequests.setVisibility(View.GONE);

                btInvite.setVisibility(View.VISIBLE);
            }
        });

        toggleButtonRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewInvitesList.setVisibility(View.GONE);
                textViewNoInvites.setVisibility(View.GONE);

                recyclerViewRequestsList.setVisibility(View.VISIBLE);
                textViewNoRequests.setVisibility(noRequestsVisibility);

                btInvite.setVisibility(View.GONE);
            }
        });


        RecyclerView invitesRecyclerView = view.findViewById(R.id.invitesList);
        if (noInvitesVisibility == View.GONE){
            //invitesRecyclerView.setVisibility(View.VISIBLE);
            // Set the adapter for recyclerView
            Context invitesRecyclerViewContext = invitesRecyclerView.getContext();
            if (mColumnCount <= 1) {
                invitesRecyclerView.setLayoutManager(new LinearLayoutManager(invitesRecyclerViewContext));
            } else {
                invitesRecyclerView.setLayoutManager(new GridLayoutManager(invitesRecyclerViewContext, mColumnCount));
            }
            mInvitesAdapter = new MyRequestRecyclerViewAdapter(userInvites,mListener,1);
            invitesRecyclerView.setAdapter(mInvitesAdapter);


        } else {
            // No invites
            invitesRecyclerView.setVisibility(View.GONE);
        }



        RecyclerView requestRecyclerView = view.findViewById(R.id.aprovalsList);
        if (noRequestsVisibility == View.GONE){
            //requestRecyclerView.setVisibility(View.VISIBLE);
            // Set the adapter for recyclerView
            Context requestRecyclerViewContext = invitesRecyclerView.getContext();
            if (mColumnCount <= 1) {
                invitesRecyclerView.setLayoutManager(new LinearLayoutManager(requestRecyclerViewContext));
            } else {
                invitesRecyclerView.setLayoutManager(new GridLayoutManager(requestRecyclerViewContext, mColumnCount));
            }
            mRequestsAdapter = new MyRequestRecyclerViewAdapter(userAproves,mListener,2);
            requestRecyclerView.setAdapter(mRequestsAdapter);

        } else {
            // No requests
            requestRecyclerView.setVisibility(View.GONE);
        }



        // set a clickable listener
        btInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBox();
            }
        });





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
        void onListFragmentInteraction(int item);
    }

    private void openDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.add_request_dialog,null);
        final EditText approvingEmail = dialogView.findViewById(R.id.approvingEmail);
        final EditText arenaID = dialogView.findViewById(R.id.arenaID);
        builder.setPositiveButton("OK", null)
                .setNegativeButton("Abourt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(),"Later then...",Toast.LENGTH_LONG).show();
                    }
                });

        builder.setView(dialogView);
        myDialog = builder.create();

        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) myDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorCode = 0;
                        String inputEmail = approvingEmail.getText().toString();
                        String inputArena = arenaID.getText().toString();
                        if (inputEmail.equals(parentActivity.getmAuth().getCurrentUser().getEmail())){
                            Toast.makeText(getActivity(),"Don't invite yourself, that's sad...",Toast.LENGTH_LONG).show();
                        } else if (inputEmail.equals("")) {

                        } else if (inputArena.equals("")){

                        }else{
                            //TODO: present some spinner animation

                            newRequest.setRequestingUID(parentActivity.getmAuth().getCurrentUser().getUid());
                            newRequest.setRequestingName(parentActivity.getmAuth().getCurrentUser().getDisplayName());
                            isApprovingUserExsists(inputEmail, inputArena);







                            /*
                            parentActivity.getNavigationView().setCheckedItem(R.id.nav_requests); // higlight the share Item in the Menu on StartUp
                            parentActivity.getNavigationView().getMenu().performIdentifierAction(R.id.nav_requests,0); // Perform Action Associated with Share Menu Item
                            */
                        }
                    }
                });
            }
        });
        myDialog.show();
    }

    private void isApprovingUserExsists(String inputEmail, String inputArena) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("UsersLoginTime");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                
                /*
                for (DataSnapshot appUser: dataSnapshot.getChildren())
                {

                    if (appUser.child("email").getValue(String.class).equals(inputEmail)){
                        newRequest.setRequestingUID(appUser.child("uid").getValue(String.class));
                        newRequest.setRequestingUID(appUser.child("display name").getValue(String.class));
                        errorCode = 0;
                        break;
                    }
                    else {
                        errorCode = 1;
                    }

                }

                if (errorCode == 0){

                    //TODO: continue with inspection
                    //isValidArenaName(inputArena)

                } else {
                    Toast.makeText(getActivity(),"No such user or wrong email",Toast.LENGTH_LONG).show();

                }
                myDialog.dismiss();
                parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isValidArenaName() {
        String currentUser = parentActivity.getmAuth().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("ArenasPerUser").child(currentUser);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cureentUserArenas.onDataListenerSuccess(dataSnapshot,dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
