package honig.roey.student.roeysigninapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface OnGetDataFromFirebaseDbListener {
    // this Interface has methods to be implemented in activities \ fragment where we view the DataBase
    // viewing the DataBase is done through asyncronus tasks so if we want to share information from the DB we read
    // into the activity that setup the eventListener (for reading the DB) we invoke some of these methoods
    public void onDataListenerStart();
    public void onDataListenerSuccess(DataSnapshot data, long num);
    public void onDataListenerFailed(DatabaseError databaseError);


}
