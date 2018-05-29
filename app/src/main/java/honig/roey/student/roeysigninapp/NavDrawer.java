package honig.roey.student.roeysigninapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mklimek.circleinitialsview.CircleInitialsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honig.roey.student.roeysigninapp.arena.ArenaFragment;
import honig.roey.student.roeysigninapp.arena.MatchUpsFragment;
import honig.roey.student.roeysigninapp.dummy.DummyContent;
import honig.roey.student.roeysigninapp.requests.RequestFragment;
import honig.roey.student.roeysigninapp.tables.MatchUp;
import honig.roey.student.roeysigninapp.tables.Request;
import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.UserStat;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RingFragment.OnListFragmentInteractionListener ,
        PlayerStatFragment.OnListFragmentInteractionListener ,
        RequestFragment.OnListFragmentInteractionListener ,
        MatchUpsFragment.OnListFragmentInteractionListener ,
        ArenaFragment.OnListFragmentInteractionListener
{

    //TODO: Facebok Login, coll fragment transition animation, first time instructions snackbar, Logs, Chats, Push Notifications, Time (Games) History, confirmation email
    //TODO: circle view near sheare item indicating requests, change languge in Google SignIn Btn
    
    private boolean active = false; // Important To ask when doing things like changing the UI
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid = "RRe3GGpTI6SeMb82413bJ4NPoA52";
    private String fullNameoFTheCurrentSignedInUser;
    private String profileImage = "circleView"; // circleView means the user has no profile image and we present letters of hisname in circleView
    private GoogleSignInClient mGoogleSignInClient;
    private NavigationView navigationView;
    private Menu menu;
    private AlertDialog myDialog;
    private MenuItem nav_Log_Off;
    private ImageView imageViewUserProfile;
    private CircleInitialsView circleView;
    private TextView navHeaderTitle;
    private RingFragment ringFragment = new RingFragment();
    private RequestFragment requestFragment = new RequestFragment();
    private PlayerStatFragment playerStatFragment = new PlayerStatFragment(); // TODO: delete this, replaced ArenaFragment
    private ArenaFragment arenaFragment = new ArenaFragment();
    private MatchUpsFragment matchUpsFragment = new MatchUpsFragment();
    private LoadingAnimationFragment loadingAnimationFragment = new LoadingAnimationFragment();
    private int isRedirectedFromLoginActivity=0; // 1 - google Signin , 2 - email \ password Signin
    private long counter =0;
    private int clickedArenaIndex=0; // a field designed to keep track which arena was clicked, so we know the arena for which to colllect data in the DB
    private ArrayList<RingGlobal> userDataBaseData = new ArrayList<RingGlobal>();
    private ArrayList<Request> userRequests = new ArrayList<Request>();
    private ArrayList<Request> userInvites = new ArrayList<Request>();
    private ArrayList<Request> userAproves = new ArrayList<Request>();
    private long counterRequests =0;
    private long counterOfArenaIndivdualMatchups = 0;
    private ArrayList<MatchUp>  arenaMatchupsData = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable switchToRings = new Runnable() {
        @Override
        public void run() {
            // reset the counter back to 0 to enable this process every time we hit Rings in Nav Menu
            counter = 0;
            userDataBaseData.clear();
            readFromFireBaseRealTimeDataBase2("ArenasPerUser", uid);
        }
    };
    private Runnable switchToRequests = new Runnable() {
        @Override
        public void run() {
            // reset the counter back to 0 to enable this process every time we hit Rings in Nav Menu
            counterRequests = 0;
            userRequests.clear();
            userInvites.clear();
            userAproves.clear();
            readFromFireBaseRealTimeDataBase3("Request",uid,tableOfRequests,requestFragment);
        }
    };
    private Runnable switchToSpecificArena = new Runnable() {
        @Override
        public void run() {
            // reset the counter back to 0 to enable this process every time we enter a specific Arena
            //counterOfArenaIndivdualMatchups = 0;
            arenaMatchupsData.clear();
            readFromFireBaseRealTimeDataBaseMatchupsTable("IndividualArenas", userDataBaseData.get(clickedArenaIndex).getKey());
        }
    };
    private String arenaIdWhichWasClicked = ""; // this value will be init when the user reports a final match score in one of his rings
    private RingGlobal tutorialArena;
    private ArrayList<MatchUp> tutorialArenaIndividualMatchUps = new ArrayList<>();


    // Getters


    public String getArenaIdWhichWasClicked() {
        return arenaIdWhichWasClicked;
    }

    public void setArenaIdWhichWasClicked(String arenaIdWhichWasClicked) {
        this.arenaIdWhichWasClicked = arenaIdWhichWasClicked;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public String getUid() {
        return uid;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public String getFullNameoFTheCurrentSignedInUser() {
        return fullNameoFTheCurrentSignedInUser;
    }

    // Interface to call methods. these methods will recive as a parmeter: a FireBase Realtime DB SnapShot, and how many children it has
    // Inside these Interface we actually extract the data from the DB Snapshot
    // ***************************************************************************************************
    // Do This after reading the "ArenasPerUser" table in the DB
    OnGetDataFromFirebaseDbListener tableOfRingsPerUser = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data,long num) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            for (DataSnapshot record:data.getChildren()
                 ) {
                        String ringID = record.getKey();
                        myRef = database.getReference().child("Arenas").child(ringID);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //2nd Par "data.getChildrenCount()": how many rings does the user have
                                // we need this number to make sure we've iteareted over all of them before updating the UI
                                tableOfRings.onDataListenerSuccess(dataSnapshot,(long) data.getChildrenCount());
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do This after reading the "Arenas table" in the DB
    OnGetDataFromFirebaseDbListener tableOfRings = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            counter = counter +1;
            boolean tmpIsPublicViewd = true;
            String tmpSuperUser="";
            String tmpKey="";
            String tmpName="";
            String tmpUid;
            String tmpFullName;
            String tmpProfileImage;
            long tmpLos;
            long tmpDrw;
            long tmpWin;
            long tmpGoalsFor;
            long tmpGoalsAgainst;
            long tmpWinningStrike;
            ArrayList<UserStat> tmpArrayListUserStat = new ArrayList<UserStat>();

            for (DataSnapshot recordInArenasTable: data.getChildren()
                 ) {
                        // simple fields in the DB
                        switch (recordInArenasTable.getKey()){
                            case "isPublicViewd":
                                tmpIsPublicViewd = recordInArenasTable.getValue(Boolean.class).booleanValue();
                                break;
                            case "superUser":
                                tmpSuperUser = recordInArenasTable.getValue(String.class);
                                break;
                            case "key":
                                tmpKey = recordInArenasTable.getValue(String.class);
                                break;
                            case "name":
                                tmpName = recordInArenasTable.getValue(String.class);
                                break;
                            default:
                                break;
                        }
                        // complex fields in the DB - these fields are JSON Object withinthemself
                        if (recordInArenasTable.hasChildren()){
                            tmpUid = recordInArenasTable.child("uid").getValue(String.class);
                            tmpFullName = recordInArenasTable.child("fullName").getValue(String.class);
                            tmpProfileImage = recordInArenasTable.child("profileImage").getValue(String.class);
                            tmpLos = recordInArenasTable.child("los").getValue(Long.class);
                            tmpDrw = recordInArenasTable.child("drw").getValue(Long.class);
                            tmpWin = recordInArenasTable.child("win").getValue(Long.class);
                            tmpGoalsFor = recordInArenasTable.child("goalsFor").getValue(Long.class);
                            tmpGoalsAgainst = recordInArenasTable.child("goalsAgainst").getValue(Long.class);
                            tmpWinningStrike = recordInArenasTable.child("winningStrike").getValue(Long.class);
                            tmpArrayListUserStat.add(new UserStat(tmpUid,tmpFullName, tmpProfileImage,tmpLos,tmpDrw,tmpWin, tmpGoalsFor, tmpGoalsAgainst, tmpWinningStrike));
                        }
            }
            userDataBaseData.add(new RingGlobal(tmpKey,tmpName, tmpIsPublicViewd,tmpSuperUser,tmpArrayListUserStat));


            if (counter == num){
                // we've itereated over every Arena
                Bundle ringFragmentArgsBundle = new Bundle();
                ArrayList<String> namesOfTheUserArenas = new ArrayList<String>();
                ArrayList<String> numberOfPlayersInEveryUserArenas = new ArrayList<String>();
                ArrayList<String> idOfTheUserArenas = new ArrayList<String>();
                ArrayList<String> superUserOfTheUserArenas = new ArrayList<String>();

                userDataBaseData.add(0,tutorialArena);

                for (RingGlobal arena: userDataBaseData
                     ) {
                    namesOfTheUserArenas.add(arena.getName());
                    numberOfPlayersInEveryUserArenas.add(String.valueOf(arena.getNumPlayers()));
                    idOfTheUserArenas.add(arena.getKey());
                    superUserOfTheUserArenas.add(arena.getSuperUser());
                }
                ringFragmentArgsBundle.putStringArrayList("arg1", namesOfTheUserArenas);
                ringFragmentArgsBundle.putStringArrayList("arg2", numberOfPlayersInEveryUserArenas);
                ringFragmentArgsBundle.putStringArrayList("arg3", idOfTheUserArenas);
                ringFragmentArgsBundle.putStringArrayList("arg4", superUserOfTheUserArenas);
                ringFragmentArgsBundle.putInt(RingFragment.ARG_COLUMN_COUNT, 2);
                ringFragmentArgsBundle.putString("currentUserUID", mAuth.getCurrentUser().getUid());

                ringFragment.setArguments(ringFragmentArgsBundle);



                // :-) :-) :-) DB data has been retrieved -> safe to update the UI

                if (active /*Is Activity active?*/) {
                    switchToFragment(R.id.appFragContainer, ringFragment);
                }
            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do This after reading the "Requets" table in the DB
    OnGetDataFromFirebaseDbListener tableOfRequests = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data,long num) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            for (DataSnapshot record:data.getChildren()
                    ) {
                String requestID = record.getKey();
                myRef = database.getReference().child("Request").child(uid).child(requestID);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //2nd Par "data.getChildrenCount()": how many requests does the user have
                        // we need this number to make sure we've iteareted over all of them before updating the UI
                        singleRequest.onDataListenerSuccess(dataSnapshot,(long) data.getChildrenCount());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do This after reading a single Requet record of the current userDB
    OnGetDataFromFirebaseDbListener singleRequest = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            String tmpKey="";
            String tmpRequestingUID="";
            String tmpRequestingName="";
            String tmpApprovingUID="";
            String tmpApprovingName="";
            String tmpArenaName="";
            String tmpArenaID = "";
            int tmpStatus = 0;

            for (DataSnapshot aSingleFieldInRequestRecord: data.getChildren()
                 ) {

                // simple fields in the DB
                switch (aSingleFieldInRequestRecord.getKey()){
                    case "approvingUID":
                        tmpApprovingUID = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "approvingName":
                        tmpApprovingName = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "arenaName":
                        tmpArenaName = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "arenaID":
                        tmpArenaID = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "key":
                        tmpKey = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "requestingUID":
                        tmpRequestingUID = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "requestingName":
                        tmpRequestingName = aSingleFieldInRequestRecord.getValue(String.class);
                        break;
                    case "status":
                        tmpStatus = aSingleFieldInRequestRecord.getValue(Integer.class);
                        break;
                    default:
                        break;
                }

            }
            // Decide what kind of a request this is
            if (mAuth.getCurrentUser().getUid().equals(tmpRequestingUID)){
                // Requests or Invites
                counterRequests = counterRequests + 1;
                userInvites.add(new Request(tmpKey,tmpRequestingUID,tmpRequestingName,tmpApprovingUID,tmpApprovingName, tmpArenaName,tmpArenaID,tmpStatus));
            } else {
                // Approves
                counterRequests = counterRequests + 1;
                userAproves.add(new Request(tmpKey,tmpRequestingUID,tmpRequestingName,tmpApprovingUID,tmpApprovingName, tmpArenaName,tmpArenaID,tmpStatus));
            }

            if (counterRequests == num){
                // We've iterated on every request and classifed it
                Bundle requestFragmentArgsBundle = new Bundle();
                requestFragmentArgsBundle.putParcelableArrayList("arg1",userAproves);
                requestFragmentArgsBundle.putParcelableArrayList("arg2",userInvites);
                requestFragment.setArguments(requestFragmentArgsBundle);

                if (active /*Is Activity active?*/) {
                    switchToFragment(R.id.appFragContainer, requestFragment);
                }

            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do This after reading the matchups of a specific Arena
    OnGetDataFromFirebaseDbListener arenaMatchUps = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            for (DataSnapshot record:data.getChildren()
                    ) {
                String matchupID = record.getKey();
                myRef = database.getReference().child("IndividualArenas").child(data.getKey()).child(matchupID);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //2nd Par num:
                        // we need this number to make sure we've iteareted over all of them before updating the UI
                        singleArenaMatchUp.onDataListenerSuccess(dataSnapshot,num);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };
    // Do This after reading a single matchup
    OnGetDataFromFirebaseDbListener singleArenaMatchUp = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            //counterOfArenaIndivdualMatchups++;
            String tempMatchupkey = data.getKey();
            ArrayList<UserStat> tempPlayer = new ArrayList<>();
            for (DataSnapshot record:data.getChildren()) {

                tempPlayer.add(record.getValue(UserStat.class));
            }
            arenaMatchupsData.add(new MatchUp(tempMatchupkey, tempPlayer));

            if (arenaMatchupsData.size() == num){
                // we've iterated over all the arena's matchUps
                // update the UI
                // switch to the Arena Fragment
                // don't forget to put all the arguments
                //toastfromwithin(arenaMatchupsData.get(5).getPlayers().get(0).getFullName());
                //toastfromwithin(arenaMatchupsData.get(5).getKey());
                //toastfromwithin(""+arenaMatchupsData.size());
                Bundle playerStatFragmentArgsBundle = new Bundle();

                // Arguments of the Global Arena
                playerStatFragmentArgsBundle.putString("argKey",userDataBaseData.get(clickedArenaIndex).getKey());
                playerStatFragmentArgsBundle.putString("argName",userDataBaseData.get(clickedArenaIndex).getName());
                playerStatFragmentArgsBundle.putInt("argNumPlayers",userDataBaseData.get(clickedArenaIndex).getNumPlayers());
                playerStatFragmentArgsBundle.putBoolean("argIsPublicViewd",userDataBaseData.get(clickedArenaIndex).isPublicViewd());
                playerStatFragmentArgsBundle.putString("argSuperUser",userDataBaseData.get(clickedArenaIndex).getSuperUser());
                playerStatFragmentArgsBundle.putParcelableArrayList("argUserStatArrayList",userDataBaseData.get(clickedArenaIndex).getUserStats());

                // Arguments of the individuaal MatchUps in the Arena
                playerStatFragmentArgsBundle.putParcelableArrayList("argMatchUpsDataArrayList",arenaMatchupsData);

                //Argument for RecycelerView num of columbs
                playerStatFragmentArgsBundle.putInt(ArenaFragment.ARG_COLUMN_COUNT,1);


                arenaFragment.setArguments(playerStatFragmentArgsBundle);
                if (active /*Is Activty active*/) {
                    switchToFragment(R.id.appFragContainer, arenaFragment);
                }

            }





        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };


    // ***************************************************************************************************

    private void toastfromwithin(String massage) {
        Toast.makeText(this,massage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);




        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    // No User signed In -> redirect to login
                    mAuth.removeAuthStateListener(mAuthListener);
                    startActivity(new Intent(NavDrawer.this, MainActivity.class));
                } else {

                    logTheTimeOfUserSigningIntoTheApp();



                    // Set customized Nav Menu
                    //TODO: this should be the correct one
                    loadProfileImage(mAuth.getCurrentUser().getPhotoUrl(),imageViewUserProfile);
                    //loadProfileImage(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Creative-Tail-People-man.svg/128px-Creative-Tail-People-man.svg.png"),imageViewUserProfile);
                    //TODO: this is the correct
                    loadUserFullName(mAuth.getCurrentUser().getDisplayName().toString(),navHeaderTitle);
                    //loadUserFullName("no problems",navHeaderTitle);
                    // get the token
                    //uid = mAuth.getCurrentUser().getUid();
                    autoStartWithArenaNavDrawer(navigationView);
                }
            }
        };

        isRedirectedFromLoginActivity = getIntent().getIntExtra("arg1",0);
        if (isRedirectedFromLoginActivity == 1 || isRedirectedFromLoginActivity == 2){
            uid = mAuth.getCurrentUser().getUid();
            fullNameoFTheCurrentSignedInUser = mAuth.getCurrentUser().getDisplayName();



        }




        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        nav_Log_Off = menu.findItem(R.id.nav_Log_Off);
        imageViewUserProfile = navigationView.getHeaderView(0).findViewById(R.id.ImageViewUserProfile);
        circleView = navigationView.getHeaderView(0).findViewById(R.id.circleView);
        navHeaderTitle = navigationView.getHeaderView(0).findViewById(R.id.navHeaderTitle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();

                //pushAndSetNewChildAtRequestsTable(new Request("1", "RRe3GGpTI6SeMb82413bJ4NPoA52","Roey Honig", "eCRG8HIaoFf4Z9D4v4OscRhV1JC3" ,"Roey Regev","fifa with friends","-LActIbj-cgoOI3_Zr-m" ,1));
                //openDialogToChangeInviteOrRequestStatus();
                //toastfromwithin(getArenaIdWhichWasClicked());

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





        //nav_Log_Off = findViewById(R.id.nav_Log_Off);
        // Set the mAuth Object & a listener to check for state change (there is \ isn't a USER?)



    }

    private void logTheTimeOfUserSigningIntoTheApp() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UsersLoginTime");
        myRef.child(mAuth.getCurrentUser().getUid()).child("email").setValue(mAuth.getCurrentUser().getEmail());
        myRef.child(mAuth.getCurrentUser().getUid()).child("time").setValue(System.currentTimeMillis());
        myRef.child(mAuth.getCurrentUser().getUid()).child("display name").setValue(mAuth.getCurrentUser().getDisplayName());
        myRef.child(mAuth.getCurrentUser().getUid()).child("uid").setValue(mAuth.getCurrentUser().getUid());
    }

    public void autoStartWithArenaNavDrawer(NavigationView navigationView) {
        navigationView.setCheckedItem(R.id.nav_rings); // higlight the Rings Item in the Menu on StartUp
        navigationView.getMenu().performIdentifierAction(R.id.nav_rings, 0); // Perform the Action Associated with the Rings Menu Item
    }

    public void autoStartWithAnItemFromNavDrawer(NavigationView navigationView, int id) {
        navigationView.setCheckedItem(id); // higlight the Rings Item in the Menu on StartUp
        navigationView.getMenu().performIdentifierAction(id, 0); // Perform the Action Associated with the Rings Menu Item
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        mAuth.addAuthStateListener(mAuthListener);
        //TODO: ok, so this is nice and good when we paused the app, from the Rings
        // TODO: and we want to go back to the app (onStart) and not just see the animation forever
        //TODO: but also read the DB and see the rings
        //TODO: but what should happend if we paused from a diffrent fragment?
        //autoStartWithArenaNavDrawer(navigationView);
        //handler.postDelayed(switchToRings,1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // setup the Tutorail Arena Example Data
        ArrayList<UserStat> tutorialGlobalArenaUsers = new ArrayList<>();
        tutorialGlobalArenaUsers.add(new UserStat("0MtddcfO5oRQy4BkzUw5a3hsuyP2", "Ace Ventura", "https://lh5.googleusercontent.com/-2MwuEAFsU0Y/AAAAAAAAAAI/AAAAAAAAAAc/E3_hhhhgpUw/s96-c/photo.jpg", 1L, 0L, 5L, 16L, 10L, 4004L));
        tutorialGlobalArenaUsers.add(new UserStat("JileexqbDdeo340uUrvF5zMSrev2", "Lois Lane", "https://lh6.googleusercontent.com/-T7nznGE5hpw/AAAAAAAAAAI/AAAAAAAAAAc/3W8C-rc_Bzo/s96-c/photo.jpg", 4L, 1L, 4L, 15L, 13L, 3000L));
        tutorialGlobalArenaUsers.add(new UserStat("d18ohRXf26ZL9rTKKQ32DHF43oy1", "Austin Powers", "https://lh6.googleusercontent.com/-K0v_vMni0GU/AAAAAAAAAAI/AAAAAAAAAAc/PxJt1Fx8iKA/s96-c/photo.jpg", 3L, 0L, 2L, 5L, 8L, 1001L));
        tutorialGlobalArenaUsers.add(new UserStat("eF0FxX3jVcW2l421angIAhnH78J2", "Bruce Wayne", "https://lh4.googleusercontent.com/-qxy33kbohfw/AAAAAAAAAAI/AAAAAAAAAAc/isGqxZb7Al0/s96-c/photo.jpg", 7L, 1L, 4L, 16L, 21L, 2002L));
        tutorialArena = new RingGlobal("-LDXFee4x0SpnmdXI7xa", "Tutorial", true, "d18ohRXf26ZL9rTKKQ32DHF43oy1", tutorialGlobalArenaUsers);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(switchToRings);
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        active  = false;
    }

    //TODO: maybe delete this all togeter - I don't want ant backPressed Navigation
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed(); // I don't want any backPressed Navigation
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.nav_drawer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rings) {
            // Handle the recycler view of the user's rings
            switchToFragment(R.id.appFragContainer,loadingAnimationFragment); // present loading animation
            // Scan DB and present Rings
            if (isRedirectedFromLoginActivity == 1 || isRedirectedFromLoginActivity == 2  || mAuth.getCurrentUser() != null){
                uid = mAuth.getCurrentUser().getUid();
                fullNameoFTheCurrentSignedInUser = mAuth.getCurrentUser().getDisplayName();
                handler.postDelayed(switchToRings,1000);
            }


        } else if (id == R.id.nav_requests) {
            // Handle recycler view of the user's requests to join Arena
            switchToFragment(R.id.appFragContainer,loadingAnimationFragment); // present loading animation
            // Scan DB and present Requests
            handler.postDelayed(switchToRequests,1000);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_Log_Off) {


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(NavDrawer.this, gso);
            mGoogleSignInClient.signOut();
            mAuth.signOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadProfileImage(Uri uri, ImageView imageView){
        //ToDo make beutifull
        switch (isRedirectedFromLoginActivity){
            case 1:
                // Google SignIn
                Picasso.get().load(String.valueOf(uri)).into(imageView);
                imageViewUserProfile.setVisibility(View.VISIBLE);
                circleView.setVisibility(View.GONE);
                profileImage = uri.toString();
                //loadProfileImage(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Creative-Tail-People-man.svg/128px-Creative-Tail-People-man.svg.png"),imageViewUserProfile);
                break;
            case 2:
                // Email / Password SignIn
                imageViewUserProfile.setVisibility(View.GONE);
                circleView.setText(mAuth.getCurrentUser().getDisplayName().toString());
                circleView.setTextSize(50);
                circleView.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }

    }

    private void loadUserFullName (String name,TextView textView){
        textView.setText(name);

    }

    private void switchToFragment(int id,android.support.v4.app.Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id,fragment).commit();
    }

    @Override
    // Do When pressing an Item from the Arenas list
    public void onListFragmentInteraction(String item) {

        //Toast.makeText(this,item, Toast.LENGTH_LONG).show();

        if (item.equals("")) {
            // add new arena item was pressed

            RingFragment ringFragment = (RingFragment) getSupportFragmentManager().findFragmentById(R.id.appFragContainer);
            ringFragment.openDialogBox();
        } else {
            // an Arena was pressed
            for (RingGlobal arena: userDataBaseData
                    ) {
                if (arena.getKey().equals(item)){
                    clickedArenaIndex = userDataBaseData.indexOf(arena);
                    if (userDataBaseData.get(clickedArenaIndex).getNumPlayers() > 1) {
                        // there is more then 1 player in the arena
                        // go to the arena data
                        switchToFragment(R.id.appFragContainer,loadingAnimationFragment); // present loading animation
                        // Scan DB for individual Matchups of this arena
                        handler.postDelayed(switchToSpecificArena,1000);
                        break;
                    }
                }
            }



        }



    }

    public void pushAndSetNewChildAtRequestsTable(Request jsonObject){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Request");
        String tempKey = myRef.child(mAuth.getCurrentUser().getUid()).push().getKey();
        // change json field key to match the new pushed key
        jsonObject.setKey(tempKey);
        // set the JSON
        myRef.child(jsonObject.getRequestingUID()).child(tempKey).setValue(jsonObject);
        myRef.child(jsonObject.getApprovingUID()).child(tempKey).setValue(jsonObject);
        return;
    }

    public String pushAndSetNewChildAtArenasTable(String nameOfAreana, String uid, String fullName){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Arenas");
        String tempKey = myRef.push().getKey();
        // create a JSON
        ArrayList<UserStat> userStats = new ArrayList<UserStat>();
        userStats.add(new UserStat(uid,fullName,profileImage,0,0,0,0,0,0));
        RingGlobal tempArena = new RingGlobal(tempKey,nameOfAreana, true,uid,userStats);
        // set the JSON
        myRef.child(tempKey).child("key").setValue(tempArena.getKey());
        myRef.child(tempKey).child("name").setValue(tempArena.getName());
        myRef.child(tempKey).child("numPlayers").setValue(tempArena.getNumPlayers());
        myRef.child(tempKey).child("isPublicViewd").setValue(tempArena.isPublicViewd());
        myRef.child(tempKey).child("superUser").setValue(tempArena.getSuperUser());
        myRef.child(tempKey).child(uid).setValue(userStats.get(0));

        return tempKey;
    }

    public void addAnotherPlayerInArenasTable(String idOfAreana, String uid, String fullName){
        UserStat newPlayer = new UserStat(uid,fullName, profileImage, 0,0,0,0,0,0);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Arenas");
        myRef.child(idOfAreana).child(uid).setValue(newPlayer);
        myRef.child(idOfAreana).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int newNumOfPlayers = dataSnapshot.child("numPlayers").getValue(Integer.class);
                newNumOfPlayers = newNumOfPlayers + 1;
                myRef.child(idOfAreana).child("numPlayers").setValue(newNumOfPlayers);

                for (DataSnapshot item: dataSnapshot.getChildren())
                {
                    if (item.hasChildren() && !item.getKey().equals(uid)){
                        // item is a player in the Arena and not the player we've just added
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("IndividualArenas").child(idOfAreana);
                        String tempKey = myRef.push().getKey();
                        // set the new player and vs the player against him
                        myRef.child(tempKey).child(uid).setValue(new UserStat(uid, fullName, profileImage,0,0,0,0,0,0));
                        myRef.child(tempKey).child(item.getKey()).setValue(new UserStat(item.getKey(), item.child("fullName").getValue(String.class), item.child("profileImage").getValue(String.class),0,0,0,0,0,0));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pushAndSetNewChildAtArenasPerUserTable(String uid, String newArenaId){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ArenasPerUser");
        myRef.child(uid).child(newArenaId).setValue(newArenaId);
    }

    private void readFromFireBaseRealTimeDataBase2(String tableName, String UID){
        // reads from the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(tableName).child(UID);



            myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Does this user have Arenas?
                    if (dataSnapshot.hasChildren()){
                        //2nd Par "num" == 0, why?
                        // doesn't matter for now. could be any long type number
                        tableOfRingsPerUser.onDataListenerSuccess(dataSnapshot,0);
                    } else {
                        // No Arenas - update UI accordinglly - add just the Tutorial Example



                        // setup the Tutorail Arena Example Data
                        ArrayList<UserStat> tutorialGlobalArenaUsers = new ArrayList<>();
                        tutorialGlobalArenaUsers.add(new UserStat("0MtddcfO5oRQy4BkzUw5a3hsuyP2", "Ace Ventura", "https://lh5.googleusercontent.com/-2MwuEAFsU0Y/AAAAAAAAAAI/AAAAAAAAAAc/E3_hhhhgpUw/s96-c/photo.jpg", 1L, 0L, 5L, 16L, 10L, 4004L));
                        tutorialGlobalArenaUsers.add(new UserStat("JileexqbDdeo340uUrvF5zMSrev2", "Lois Lane", "https://lh6.googleusercontent.com/-T7nznGE5hpw/AAAAAAAAAAI/AAAAAAAAAAc/3W8C-rc_Bzo/s96-c/photo.jpg", 4L, 1L, 4L, 15L, 13L, 3000L));
                        tutorialGlobalArenaUsers.add(new UserStat("d18ohRXf26ZL9rTKKQ32DHF43oy1", "Austin Powers", "https://lh6.googleusercontent.com/-K0v_vMni0GU/AAAAAAAAAAI/AAAAAAAAAAc/PxJt1Fx8iKA/s96-c/photo.jpg", 3L, 0L, 2L, 5L, 8L, 1001L));
                        tutorialGlobalArenaUsers.add(new UserStat("eF0FxX3jVcW2l421angIAhnH78J2", "Bruce Wayne", "https://lh4.googleusercontent.com/-qxy33kbohfw/AAAAAAAAAAI/AAAAAAAAAAc/isGqxZb7Al0/s96-c/photo.jpg", 7L, 1L, 4L, 16L, 21L, 2002L));
                        tutorialArena = new RingGlobal("-LDXFee4x0SpnmdXI7xa", "Tutorial", true, "d18ohRXf26ZL9rTKKQ32DHF43oy1", tutorialGlobalArenaUsers);



                        Bundle ringFragmentArgsBundle = new Bundle();
                        ArrayList<String> namesOfTheUserArenas = new ArrayList<String>();
                        ArrayList<String> numberOfPlayersInEveryUserArenas = new ArrayList<String>();
                        ArrayList<String> idOfTheUserArenas = new ArrayList<String>();
                        ArrayList<String> superUserOfTheUserArenas = new ArrayList<String>();

                        userDataBaseData.add(0, tutorialArena);

                        for (RingGlobal arena: userDataBaseData
                                ) {
                            namesOfTheUserArenas.add(arena.getName());
                            numberOfPlayersInEveryUserArenas.add(String.valueOf(arena.getNumPlayers()));
                            idOfTheUserArenas.add(arena.getKey());
                            superUserOfTheUserArenas.add(arena.getSuperUser());
                        }
                        ringFragmentArgsBundle.putStringArrayList("arg1", namesOfTheUserArenas);
                        ringFragmentArgsBundle.putStringArrayList("arg2", numberOfPlayersInEveryUserArenas);
                        ringFragmentArgsBundle.putStringArrayList("arg3", idOfTheUserArenas);
                        ringFragmentArgsBundle.putStringArrayList("arg4", superUserOfTheUserArenas);
                        ringFragmentArgsBundle.putInt(RingFragment.ARG_COLUMN_COUNT, 2);
                        ringFragmentArgsBundle.putString("currentUserUID", mAuth.getCurrentUser().getUid());

                        ringFragment.setArguments(ringFragmentArgsBundle);


                        if (active /*Is Activty active*/) {
                            switchToFragment(R.id.appFragContainer, ringFragment);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //onDataListenerFailed(databaseError);

                }
            });







    }

    private void readFromFireBaseRealTimeDataBaseMatchupsTable(String tableName, String arenaID){
        // reads from the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(tableName).child(arenaID);



        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Does this arena have indiviudal matchups?
                if (dataSnapshot.hasChildren()){

                    arenaMatchUps.onDataListenerSuccess(dataSnapshot,dataSnapshot.getChildrenCount());
                } else {
                    // No Matchups - update UI accordinglly
                    // TODO: i don't think we need to switch to anything if there's no matchups in this arena
                    arenaFragment.setArguments(null);
                    if (active /*Is Activty active*/) {
                        switchToFragment(R.id.appFragContainer, arenaFragment);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //onDataListenerFailed(databaseError);

            }
        });







    }

    private void readFromFireBaseRealTimeDataBase3(String tableName, String UID, OnGetDataFromFirebaseDbListener listener,android.support.v4.app.Fragment fragment ){
        // reads from the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(tableName);
        if (UID != null){
             myRef = database.getReference().child(tableName).child(UID);
        }

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Does this user have any Records in this DB table?
                if (dataSnapshot.hasChildren()){
                    listener.onDataListenerSuccess(dataSnapshot,dataSnapshot.getChildrenCount());

                } else if(fragment != null && active) {
                    // No Records - update UI accordinglly
                    fragment.setArguments(null);
                    switchToFragment(R.id.appFragContainer, fragment);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //onDataListenerFailed(databaseError);

            }
        });







    }

    //TODO: delete
    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        // this is clickable from the Arena fragment (PlayerStatFragment)
    }


    @Override
    // Do When pressing an Item from the Request list
    public void onListFragmentInteraction(int flag, Request request) {
                openDialogToChangeInviteOrRequestStatus(flag, request);
    }

    public void openDialogToChangeInviteOrRequestStatus(int flag, Request request){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.update_request_status,null);
        final TextView massage = dialogView.findViewById(R.id.tvInviteOrRequestMassage);
        final RadioButton rbPending = dialogView.findViewById(R.id.rbPending);
        final RadioButton rbApproved = dialogView.findViewById(R.id.rbApproved);
        final RadioButton rbDenied = dialogView.findViewById(R.id.rbDenied);
        final RadioButton rbCancel = dialogView.findViewById(R.id.rbCancel);
        final RadioButton rbDontCancel = dialogView.findViewById(R.id.rbDontCancel);



        if (flag == 1){
            // Responding to an Invite Item
            rbDontCancel.setChecked(true);
            rbPending.setVisibility(View.GONE);
            rbApproved.setVisibility(View.GONE);
            rbDenied.setVisibility(View.GONE);
            massage.setText("You've invited " + request.getApprovingName() +
                    " to join Arena " + request.getArenaName() + ". Would you like to Cancel the invitation?");
        } else {
            // Responding to a Request Item
            rbPending.setChecked(true);
            rbCancel.setVisibility(View.GONE);
            rbDontCancel.setVisibility(View.GONE);
            massage.setText(request.getRequestingName() + " has invited you to join Arena " + request.getArenaName());
        }


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (rbApproved.isChecked()){
                    // TODO: present loading animation
                    changeRequestStatustTo(request,"Approved");
                    removeRequest(request);
                    pushAndSetNewChildAtArenasPerUserTable(request.getApprovingUID(),request.getArenaID());
                    // Adding the newPlayer to the individual rings is done from within the following method
                    addAnotherPlayerInArenasTable(request.getArenaID(),request.getApprovingUID(),request.getApprovingName());

                    //TODO: Log Massage("you joined arena")
                    Toast.makeText(NavDrawer.this,"Approved",Toast.LENGTH_LONG).show();

                } else if (rbDenied.isChecked()){
                    // TODO: present loading animation
                    Toast.makeText(NavDrawer.this,"Denied",Toast.LENGTH_LONG).show();
                    changeRequestStatustTo(request,"Denied");
                    removeRequest(request);
                    //TODO: Log Massage("you denied joining arena")
                } else if (rbPending.isChecked()) {
                     Toast.makeText(NavDrawer.this,"Pending",Toast.LENGTH_LONG).show();
                    changeRequestStatustTo(request,"Pending Approval");
                } else if (rbCancel.isChecked()) {
                    //TODO: change status, remove from request table of both requesting and approving uid's
                    //TODO: log massage ("you've canceled your invite") for both users records in the logs table
                    Toast.makeText(NavDrawer.this,"will be canceled",Toast.LENGTH_LONG).show();
                    changeRequestStatustTo(request,"Canceled");
                    removeRequest(request);
                } else {
                    Toast.makeText(NavDrawer.this,"Unchanged",Toast.LENGTH_LONG).show();
                }
            }
        })
                .setNegativeButton("Abourt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(NavDrawer.this,"Later then...",Toast.LENGTH_LONG).show();
                    }
                });

        builder.setView(dialogView);
        myDialog = builder.create();
        myDialog.show();


    }



    private void removeRequest(Request request) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Request");
        myRef.child(request.getRequestingUID()).child(request.getKey()).removeValue();
        myRef.child(request.getApprovingUID()).child(request.getKey()).removeValue();
    }

    public void changeRequestStatustTo(Request request,String newStatus) {
        int newStatusCode;
        switch (newStatus){
            case "Approved":
                newStatusCode = 0;
                break;
            case "Denied":
                newStatusCode = 1;
                break;
            case "Pending Approval":
                newStatusCode = 2;
                break;
            case "Canceled":
                newStatusCode = 3;
                break;
            default:
                newStatusCode = 5;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Request");
        myRef.child(request.getApprovingUID()).child(request.getKey()).child("status").setValue(newStatusCode);
        myRef.child(request.getRequestingUID()).child(request.getKey()).child("status").setValue(newStatusCode);
    }

    // Executed when a matchUp Item (2 players matchup in a specific Arena) is pressed
    @Override
    public void onMatchUpListInteraction(String specific2PlayersMatchUpKey) {
        //TODO: present data relating to the specific2PlayersMatchUpKey
        //Toast.makeText(this,specific2PlayersMatchUpKey + " says hello from the main activity",Toast.LENGTH_LONG).show();
        ArenaFragment arenaFragment = (ArenaFragment) getSupportFragmentManager().findFragmentById(R.id.appFragContainer);
        arenaFragment.onMatchUpListInteraction(specific2PlayersMatchUpKey);
    }
}
