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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.net.URI;
import java.util.ArrayList;

import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.RingsPerUser;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RingFragment.OnListFragmentInteractionListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid = "RRe3GGpTI6SeMb82413bJ4NPoA52";

    private GoogleSignInClient mGoogleSignInClient;
    private NavigationView navigationView;
    private Menu menu;
    private MenuItem nav_Log_Off;
    private ImageView imageViewUserProfile;
    private TextView navHeaderTitle;
    private RingFragment ringFragment = new RingFragment();
    private LoadingAnimationFragment loadingAnimationFragment = new LoadingAnimationFragment();
    boolean isRedirectedFromLoginActivity = false;

    Handler handler = new Handler();
    Runnable switchToRings = new Runnable() {
        @Override
        public void run() {
            readFromFireBaseRealTimeDataBase2("tableOfRingsPerUser", uid);

        }
    };

    public NavigationView getNavigationView() {
        return navigationView;
    }

    // todo i was expermienting in this activity with reqadin and writing to the firebase realtime databas
    // todo these 2 fields or variabls should be here, there here because i need them for retriving data from the firebase DB
    // todo the counter for example is a very important for making sure we've finished reading the releavent DB data
    // todo and it is safe to update the UI or do other stuff
    //todo tmp for example is the ArrayList used to collect the data from the firebase DB and constrct the correspanding class
    //todo which will then be send to the fragment as an argument
    private long counter=0; // a counter to makes sure all the rings are retrived from the DB. only then we can update the UI
    ArrayList<String> tmp = new ArrayList<String>();
    private ArrayList<Integer> tmpNumOfPlayersInSpecificRing= new ArrayList<Integer>();
    private long currentUserNumOfArenas;

    OnGetDataFromFirebaseDbListener tableOfRingsPerUser = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data,long num) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            currentUserNumOfArenas = data.getChildrenCount();
            tmp.clear();
            tmpNumOfPlayersInSpecificRing.clear();
            for (int i = 0; i < currentUserNumOfArenas ; i++) {

                myRef = database.getReference().child("tableOfRings").child((String) data.child("r"+i).getValue());
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int tmpCount =0;
                        for (int j = 0; j < 5; j++) {
                            if (dataSnapshot.child("p"+j).getValue().equals("")) {

                            } else {
                                tmpCount++;
                            }
                        }

                        // collect how many players in each Arena
                        tmpNumOfPlayersInSpecificRing.add((Integer) tmpCount);
                        // collect what's the name of each Arena
                        tmp.add((String)dataSnapshot.child("name").getValue());
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
    OnGetDataFromFirebaseDbListener tableOfRings = new OnGetDataFromFirebaseDbListener() {
        @Override
        public void onDataListenerStart() {

        }

        @Override
        public void onDataListenerSuccess(DataSnapshot data, long num) {
            counter = counter +1;
            //TODO this is stuipid thers's no need to cheack the DB every time we press Rings in the NAV menu
            //TODO: switch to spliting the orignal function to getting this information and
            //TODO: switching to the Rings Frag
            //TODO: now, i'm not so sure, i mean what happend when the user adds a new Ring, would we want a navigation back to Rings?,
            // TODO: and off course to read the DB again
            if (counter == num){
                RingsPerUser currentUserRings = new RingsPerUser(tmp);
                Bundle ringFragmentArgsBundle = new Bundle();
                //ringFragmentArgsBundle.putStringArrayList("arg1",currentUserRings.getUserRings());

                String[] trytoput = new String[(int)num];
                int[] trytoputNumOfPLayers = new int[(int)num];
                for (int i = 0; i < (int)num; i++) {
                    trytoput[i] = currentUserRings.getUserRings().get(i);
                    trytoputNumOfPLayers[i] = tmpNumOfPlayersInSpecificRing.get(i);
                }
                ringFragmentArgsBundle.putStringArray("arg1",trytoput);
                ringFragmentArgsBundle.putIntArray("arg2",trytoputNumOfPLayers);


                ringFragment.setArguments(ringFragmentArgsBundle);
                counter = 0; // reset the counter back to 0 so we can this process will hapend ever time we hit Rings in Nav Menu
                // DB data has been retrived -> safe tu update the UI
                switchToFragment(R.id.appFragContainer, ringFragment);
            }

        }

        @Override
        public void onDataListenerFailed(DatabaseError databaseError) {

        }
    };


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
                    startActivity(new Intent(NavDrawer.this, MainActivity.class));
                } else {
                    // Set customized Nav Menu
                    loadProfileImage(mAuth.getCurrentUser().getPhotoUrl(),imageViewUserProfile);
                    loadUserFullName(mAuth.getCurrentUser().getDisplayName().toString(),navHeaderTitle);
                    // get the token
                    //uid = mAuth.getCurrentUser().getUid();
                }
            }
        };

        isRedirectedFromLoginActivity = getIntent().getBooleanExtra("arg1",false);
        if (isRedirectedFromLoginActivity){
            uid = mAuth.getCurrentUser().getUid();
        }


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        nav_Log_Off = menu.findItem(R.id.nav_Log_Off);
        imageViewUserProfile = navigationView.getHeaderView(0).findViewById(R.id.ImageViewUserProfile);
        navHeaderTitle = navigationView.getHeaderView(0).findViewById(R.id.navHeaderTitle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();

                //exampleWriteToFireBaseRealTimeDataBase();
               // exampleWriteToFireBaseRealTimeDataBase2();
               // exampleWriteToFireBaseRealTimeDataBase3();
               // readFromFireBaseRealTimeDataBase2("tableOfRingsPerUser", "RRe3GGpTI6SeMb82413bJ4NPoA52");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_rings); // higlight the Rings Item in the Menu on StartUp
        navigationView.getMenu().performIdentifierAction(R.id.nav_rings, 0); // Perform the Action Associated with the Rings Menu Item


        //nav_Log_Off = findViewById(R.id.nav_Log_Off);
        // Set the mAuth Object & a listener to check for state change (there is \ isn't a USER?)



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            // TODO: handle a Null pointer exception, that is - "No Rings"

            // present loading animation
            switchToFragment(R.id.appFragContainer,loadingAnimationFragment);
            // Scan DB and present Rings
            if (isRedirectedFromLoginActivity){
                handler.postDelayed(switchToRings,1000);
            }





        } else if (id == R.id.nav_gallery) {

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
        Picasso.get().load(String.valueOf(uri)).into(imageView);
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
    public void onListFragmentInteraction(String item) {
        Toast.makeText(this,item,Toast.LENGTH_LONG).show();
    }



    public String pushAndSetNewChildAtRingsTable(String name, boolean isPublic){
        // write the JSON to the FireBase DataBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tableOfRings");
        String tempKey = myRef.push().getKey();
        // create a JSON
        RingGlobal tempArena = new RingGlobal(tempKey,name,2,isPublic, mAuth.getCurrentUser().getUid(), "", "", "", "", "");
        // set the JSON
        myRef.child(tempKey).setValue(tempArena);

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
                    //2nd Par "num" == 0, why?
                    // doesn't matter for now. could be any long type number
                    tableOfRingsPerUser.onDataListenerSuccess(dataSnapshot,0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //onDataListenerFailed(databaseError);
            }
        });


    }


    /*
    // todo these are methods which allow sharing information outside the eventlistenr of reading the DB - addListenerForSingleValueEvent
    // ****************************************************************************************
    @Override
    public void onDataListenerStart(long numOfRings) {
        counter = counter +1;
        if (counter == numOfRings ){
            Toast.makeText(this,tmp.get(3),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataListenerSuccess(DataSnapshot data) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        for (int i = 0; i < data.getChildrenCount() ; i++) {
            myRef = database.getReference().child("tableOfRings").child((String) data.child("r"+i).getValue());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tmp.add((String)dataSnapshot.child("name").getValue());
                    onDataListenerStart(data.getChildrenCount());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        RingsPerUser ringsPerThisSignedUser = new RingsPerUser(tmp);
       // Toast.makeText(this,"there are "+data.getChildrenCount()+" rings",Toast.LENGTH_LONG).show();
       // Toast.makeText(this,ringID,Toast.LENGTH_LONG).show();
        //Toast.makeText(this,tmp.get(0),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataListenerFailed(DatabaseError databaseError) {
        Toast.makeText(this,"Wrong",Toast.LENGTH_LONG).show();
    }

    //*************************************************************************************************************
    */

    //TODO: delete this
    public void fromFragment(String mas){
        Toast.makeText(NavDrawer.this,mas,Toast.LENGTH_LONG).show();
    }

}
