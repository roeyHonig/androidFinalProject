package honig.roey.student.roeysigninapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mklimek.circleinitialsview.CircleInitialsView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.net.URI;
import java.util.ArrayList;

import honig.roey.student.roeysigninapp.dummy.DummyContent;
import honig.roey.student.roeysigninapp.requests.RequestFragment;
import honig.roey.student.roeysigninapp.tables.Request;
import honig.roey.student.roeysigninapp.tables.RequestsPerUser;
import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.RingsPerUser;
import honig.roey.student.roeysigninapp.tables.UserStat;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RingFragment.OnListFragmentInteractionListener , PlayerStatFragment.OnListFragmentInteractionListener , RequestFragment.OnListFragmentInteractionListener{

    private boolean active = false; // Important To ask when doing things like changing the UI
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid = "RRe3GGpTI6SeMb82413bJ4NPoA52";
    private String fullNameoFTheCurrentSignedInUser;
    private GoogleSignInClient mGoogleSignInClient;
    private NavigationView navigationView;
    private Menu menu;
    private MenuItem nav_Log_Off;
    private ImageView imageViewUserProfile;
    private CircleInitialsView circleView;
    private TextView navHeaderTitle;
    private RingFragment ringFragment = new RingFragment();
    private RequestFragment requestFragment = new RequestFragment();
    private PlayerStatFragment playerStatFragment = new PlayerStatFragment();
    private LoadingAnimationFragment loadingAnimationFragment = new LoadingAnimationFragment();
    private int isRedirectedFromLoginActivity=0; // 1 - google Signin , 2 - email \ password Signin
    private long counter =0;
    private ArrayList<RingGlobal> userDataBaseData = new ArrayList<RingGlobal>();
    private ArrayList<Request> userRequests = new ArrayList<Request>();
    private ArrayList<Request> userInvites = new ArrayList<Request>();
    private ArrayList<Request> userAproves = new ArrayList<Request>();
    private long counterRequests =0;
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

    // Getters
    public String getUid() {
        return uid;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public String getFullNameoFTheCurrentSignedInUser() {
        return fullNameoFTheCurrentSignedInUser;
    }

    // Interface to call methods after reading the FireBase Realtime DB
    // ****************************************************************
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
            long tmpLos;
            long tmpDrw;
            long tmpWin;
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
                            tmpLos = recordInArenasTable.child("los").getValue(Long.class);
                            tmpDrw = recordInArenasTable.child("drw").getValue(Long.class);
                            tmpWin = recordInArenasTable.child("win").getValue(Long.class);
                            tmpArrayListUserStat.add(new UserStat(tmpUid,tmpFullName,tmpLos,tmpDrw,tmpWin));
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

    // ****************************************************************

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

                pushAndSetNewChildAtRequestsTable(new Request("1", "RRe3GGpTI6SeMb82413bJ4NPoA52","Roey Honig", "UV2tVsaP8GVhB4YU2o2iHCAfOum2" ,"Tal Efroni","fifa with friends","-LActIbj-cgoOI3_Zr-m" ,1));

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

    private void autoStartWithArenaNavDrawer(NavigationView navigationView) {
        navigationView.setCheckedItem(R.id.nav_rings); // higlight the Rings Item in the Menu on StartUp
        navigationView.getMenu().performIdentifierAction(R.id.nav_rings, 0); // Perform the Action Associated with the Rings Menu Item
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
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(switchToRings);
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
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
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
    // TODO: i think i need to add a flag to know from whic list the item was pressed
    public void onListFragmentInteraction(String item) {
        //TODO: item is the ID of the Arena the user clicked on, in the RingFragment
        //TODO: insted of Toast, switch to the PlayerStatFragment VIEW
        //Toast.makeText(this,"you've selected Arena with id: "+item,Toast.LENGTH_LONG).show();
        Bundle playerStatFragmentArgsBundle = new Bundle();

        int clickedArenaIndex=0;
        for (RingGlobal arena: userDataBaseData
             ) {
            if (arena.getKey().equals(item)){
                clickedArenaIndex = userDataBaseData.indexOf(arena);
            }
        }
        /*
        ArrayList<UserStat> tryme = new ArrayList<UserStat>();
        for (int i = 0; i < userDataBaseData.get(clickedArenaIndex).getNumPlayers()  ; i++) {
            tryme.add(userDataBaseData.get(clickedArenaIndex).getUserStats())
        }
        */
        playerStatFragmentArgsBundle.putString("argKey",userDataBaseData.get(clickedArenaIndex).getKey());
        playerStatFragmentArgsBundle.putString("argName",userDataBaseData.get(clickedArenaIndex).getName());
        playerStatFragmentArgsBundle.putInt("argNumPlayers",userDataBaseData.get(clickedArenaIndex).getNumPlayers());
        playerStatFragmentArgsBundle.putBoolean("argIsPublicViewd",userDataBaseData.get(clickedArenaIndex).isPublicViewd());
        playerStatFragmentArgsBundle.putParcelableArrayList("argUserStatArrayList",userDataBaseData.get(clickedArenaIndex).getUserStats());


        playerStatFragment.setArguments(playerStatFragmentArgsBundle);
       switchToFragment(R.id.appFragContainer, playerStatFragment);
    }


    public  void unMaskFieldRequestingUIDinObjectRequest(Request request, String name){
        return;
    }
    public  void unMaskFieldApprovingUIDinObjectRequest(Request request, String name){
        return;
    }
    public  void unMaskFieldArenaIDinObjectRequest(Request request, String name){
        return;
    }

    public void pushAndSetNewChildAtArenasTable(String nameOfAreana, String uid1 , String fullName1, String uid2 , String fullName2, String uid3, String fullName3){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Arenas");
        String tempKey = myRef.push().getKey();
        // create a JSON
        ArrayList<UserStat> userStats = new ArrayList<UserStat>();
        //userStats.add(new UserStat(uid1,fullName1,5,2,6));
        //userStats.add(new UserStat(uid2,fullName2,1,7,3));
        //userStats.add(new UserStat(uid3,fullName3,4,3,7));
        RingGlobal tempArena = new RingGlobal(tempKey,nameOfAreana, true,uid1,userStats);
        // set the JSON
        myRef.child(tempKey).child("key").setValue(tempArena.getKey());
        myRef.child(tempKey).child("name").setValue(tempArena.getName());
        myRef.child(tempKey).child("numPlayers").setValue(tempArena.getNumPlayers());
        myRef.child(tempKey).child("isPublicViewd").setValue(tempArena.isPublicViewd());
        myRef.child(tempKey).child("superUser").setValue(tempArena.getSuperUser());

        myRef.child(tempKey).child(uid1).setValue(userStats.get(0));
        myRef.child(tempKey).child(uid2).setValue(userStats.get(1));
        myRef.child(tempKey).child(uid3).setValue(userStats.get(2));



        return;

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
        userStats.add(new UserStat(uid,fullName,0,0,0));
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

    public String pushAndSetNewChildAtRingsTable(String name, boolean isPublic){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tableOfRings");
        String tempKey = myRef.push().getKey();
        // create a JSON
        //RingGlobal tempArena = new RingGlobal(tempKey,name,2,isPublic, mAuth.getCurrentUser().getUid(), "", "", "", "", "");
        // set the JSON
        //myRef.child(tempKey).setValue(tempArena);

        return tempKey;

    }

    public void pushAndSetNewChildAtRingsPerUserTable(int index, String newArenaId){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tableOfRingsPerUser");
        myRef.child(mAuth.getCurrentUser().getUid()).child("r"+index).setValue(newArenaId);
        /*
        myRef.child("RRe3GGpTI6SeMb82413bJ4NPoA52").child("numOfRings").setValue(2);
        myRef.child("RRe3GGpTI6SeMb82413bJ4NPoA52").child("r0").setValue("blabla");
        myRef.child("RRe3GGpTI6SeMb82413bJ4NPoA52").child("r1").setValue("blablagain");
        myRef.child("RRe3GGpTI6SeMb82413bJ4NPoA52").child("r2").setValue("blablagainAndAgain");
        */
    }

    public void pushAndSetNewChildAtArenasPerUserTable(String uid, String newArenaId){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ArenasPerUser");
        myRef.child(uid).child(newArenaId).setValue(newArenaId);
    }

    private void exampleWriteToFireBaseRealTimeDataBase3(){
        // write the JSON to the FireBase DataBase
        ArrayList<String> temp = new ArrayList<>();
        temp.add("-L9_sU7hfPxP5QuFSzS_");
        temp.add("-L9aZMxhoEZZ0_O7-YI0");
        temp.add("-L9aZQ8E9PxT1fiFQ3dk");

        RingsPerUser tempRingsPerUser = new RingsPerUser(temp);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tableOfRingsPerUser");
        for (int i = 0; i < tempRingsPerUser.getUserRings().size() ; i++) {
            myRef.child("UV2tVsaP8GVhB4YU2o2iHCAfOum2").child("r"+i).setValue(tempRingsPerUser.getUserRings().get(i));
        }
    }

    private void readFromFireBaseRealTimeDataBase(String tableName, String UID){
        // reads from the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(tableName);
        Query atempToQuery = myRef.orderByKey().equalTo(UID);
        DatabaseReference userSpecificRef = atempToQuery.getRef();

        userSpecificRef.addListenerForSingleValueEvent(new ValueEventListener() {

           // long c;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //c = dataSnapshot.getChildrenCount();
                //onDataListenerSuccess(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //onDataListenerFailed(databaseError);
            }
        });

      //  Toast.makeText(this,r0,Toast.LENGTH_LONG).show();

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
                        // No Arenas - update UI accordinglly
                        ringFragment.setArguments(null);
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


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        // this is clickable from the Arena fragment (PlayerStatFragment)
    }


    @Override
    public void onListFragmentInteraction(int item) {

    }
}
