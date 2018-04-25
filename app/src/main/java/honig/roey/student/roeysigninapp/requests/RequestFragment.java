package honig.roey.student.roeysigninapp.requests;

import android.content.Context;
import android.content.DialogInterface;
import android.media.RemoteControlClient;
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

import com.airbnb.lottie.LottieAnimationView;
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
    private Button btPositive;
    private String inputEmail="";
    private String inputArena="";
    private Request newRequest;
    private int errorCode = 0;
    private boolean isApprovingUserExsists = false;
    private boolean isArenaNameValid = false;
    private boolean isSuperUser = false;
    private long itearationOverAppUsersCounter = 0;
    private long itearationOverUserArenasCounter = 0;
    private long itearteOverAllRequests = 0;
    private boolean isValidRequest = true;
    private LottieAnimationView loadingDialog;


    // Interface to call methods after reading the FireBase Realtime DB
    // ****************************************************************
    // Do this after reading arena per user table. this is a dataSnapsot of the lists of Arenas Id's the current user have
        OnGetDataFromFirebaseDbListener cureentUserArenas = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;

            if (num == 0){
                // the current user have no arenas
                // Obviuslly he can't invite any one to anywhere
                Toast.makeText(getActivity(),"No Arena with that name or you're not the Super User",Toast.LENGTH_LONG).show();
                myDialog.dismiss();
                parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);

            } else {
                // cheack if the user invited someone to an exsisting arena in which he is the superuser
                for (DataSnapshot arena: data.getChildren())
                {
                    myRef = database.getReference().child("Arenas").child(arena.getKey());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            aSingleArenaTheUserHave.onDataListenerSuccess(dataSnapshot,num);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }



        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do this after reading a single arena. this is a dataSnapsot of a specific arena the current user have
        OnGetDataFromFirebaseDbListener aSingleArenaTheUserHave = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {

            itearationOverUserArenasCounter = itearationOverUserArenasCounter + 1;
            String tmp = parentActivity.getmAuth().getCurrentUser().getUid();

            if (data.child("name").getValue(String.class).equals(inputArena) && data.child("superUser").getValue(String.class).equals(tmp)){
                isArenaNameValid = true;
                isSuperUser = true;
                newRequest.setArenaName(inputArena);
                newRequest.setArenaID(data.child("key").getValue(String.class));
            }

            if (itearationOverUserArenasCounter == num){
                if (isArenaNameValid && isSuperUser){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("Arenas").child(newRequest.getArenaID()).child(newRequest.getApprovingUID());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()){
                                Toast.makeText(getActivity(),"User is allready in this Arena",Toast.LENGTH_LONG).show();
                                myDialog.dismiss();
                                parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
                            } else {
                                isValidRequest();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                } else {
                    Toast.makeText(getActivity(),"No Arena with that name or you're not the Super User",Toast.LENGTH_LONG).show();
                    myDialog.dismiss();
                    parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
                }

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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;

                for (DataSnapshot appUser: data.getChildren())
                {

                    String uid = appUser.getKey();
                    if (!uid.equals("readme")){
                        // read all app users but not the dammy child of "readme"
                        myRef = database.getReference().child("UsersLoginTime").child(uid);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                aSingleAppUser.onDataListenerSuccess(dataSnapshot,num);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }



        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do this after reading a single user record in table UsersLoginTime
        OnGetDataFromFirebaseDbListener aSingleAppUser = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {

            itearationOverAppUsersCounter = itearationOverAppUsersCounter + 1;
            String tmp =data.child("email").getValue(String.class) ;

            if (tmp.equals(inputEmail)){
                newRequest.setApprovingUID(data.child("uid").getValue(String.class));
                newRequest.setApprovingName(data.child("display name").getValue(String.class));
                isApprovingUserExsists = true;

            }

            if (itearationOverAppUsersCounter == (num-1)){

                //we've ireateted Over all the registered users in the App. num-1 is because we have 1 dammy record of a readMe in this table
                if (isApprovingUserExsists){
                    isValidArenaName();
                }
                else {
                    Toast.makeText(getActivity(),"User isn't registered or wrong email",Toast.LENGTH_LONG).show();
                    myDialog.dismiss();
                    parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
                }

            }


        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do this after reading the user active requests
        OnGetDataFromFirebaseDbListener userRequests = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            String currentUser = parentActivity.getmAuth().getCurrentUser().getUid();
            if (num == 0){
                // this user never invited anyone, so there can't be any duplicate request
                Toast.makeText(getActivity(),"All is really OK",Toast.LENGTH_LONG).show();
                parentActivity.pushAndSetNewChildAtRequestsTable(newRequest);
                myDialog.dismiss();
                parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
            } else {
                // cheack all the user's request for a duplicate request
                for (DataSnapshot request :data.getChildren()) {
                    myRef = database.getReference().child("Request").child(currentUser).child(request.getKey());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            aSingleRequest.onDataListenerSuccess(dataSnapshot,num);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }


        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do this after reading a single request of the user active requests
        OnGetDataFromFirebaseDbListener aSingleRequest = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            itearteOverAllRequests = itearteOverAllRequests + 1;
            String tmpApprovingUID = data.child("approvingUID").getValue(String.class);
            String tmpArenaId = data.child("arenaID").getValue(String.class);
            if (tmpApprovingUID.equals(newRequest.getApprovingUID()) && tmpArenaId.equals(newRequest.getArenaID())){
                isValidRequest = false;
            }

            if (itearteOverAllRequests == num){
                // we've iterated over all the user requests
                if (isValidRequest){
                    Toast.makeText(getActivity(),"All is OK",Toast.LENGTH_LONG).show();
                    //TODO: Append the newRequest Object to the Request Table in the DB
                    parentActivity.pushAndSetNewChildAtRequestsTable(newRequest);
                    myDialog.dismiss();
                    parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
                } else {
                    Toast.makeText(getActivity(),"There's allready a pending invitation to that user",Toast.LENGTH_LONG).show();
                    myDialog.dismiss();
                    parentActivity.autoStartWithAnItemFromNavDrawer(parentActivity.getNavigationView(),R.id.nav_requests);
                }
            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
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
        loadingDialog = (LottieAnimationView) dialogView.findViewById(R.id.requestAnimationLoading);
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
                loadingDialog.setVisibility(View.INVISIBLE);
                loadingDialog.pauseAnimation();
                btPositive = ((AlertDialog) myDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btPositive.setClickable(true);
                btPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Lock the Button
                        btPositive.setClickable(false);
                        //reset inspection critirions
                        newRequest = new Request("1","null","null","null","null","null","null",2);
                        errorCode = 0;
                        isApprovingUserExsists = false;
                        itearationOverAppUsersCounter = 0;
                        isArenaNameValid = false;
                        isSuperUser = false;
                        itearationOverUserArenasCounter = 0;
                        itearteOverAllRequests = 0;
                        isValidRequest = true;

                        inputEmail = approvingEmail.getText().toString();
                        inputArena = arenaID.getText().toString();
                        if (inputEmail.equals(parentActivity.getmAuth().getCurrentUser().getEmail())){
                            Toast.makeText(getActivity(),"Don't invite yourself, that's sad...",Toast.LENGTH_LONG).show();
                        } else if (inputEmail.equals("")) {

                        } else if (inputArena.equals("")){

                        }else{
                            //Loading Animation
                            loadingDialog.setVisibility(View.VISIBLE);
                            loadingDialog.resumeAnimation();

                            newRequest.setRequestingUID(parentActivity.getmAuth().getCurrentUser().getUid());
                            newRequest.setRequestingName(parentActivity.getmAuth().getCurrentUser().getDisplayName());
                            isApprovingUserExsists();
                        }
                    }
                });
            }
        });
        myDialog.show();
    }

    private void isApprovingUserExsists() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("UsersLoginTime");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableUsersLoginTimeSnapShot.onDataListenerSuccess(dataSnapshot, dataSnapshot.getChildrenCount());
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

    private void isValidRequest() {
        String currentUser = parentActivity.getmAuth().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Request").child(currentUser);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRequests.onDataListenerSuccess(dataSnapshot,dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
