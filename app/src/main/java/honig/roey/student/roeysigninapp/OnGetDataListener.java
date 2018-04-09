package honig.roey.student.roeysigninapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by hackeru on 09/04/2018.
 */

public interface OnGetDataListener {
    // this Interface has methods to be implemented in activities \ fragment where we view the DataBase
    // viewing the DataBase is done through asyncronus tasks so if we want to share information from the DB we read
    // into the activity that setup the eventListener (for reading the DB) we invoke some of these methoods
    public void onDataListenerStart(long numOfRings);
    public void onDataListenerSuccess(DataSnapshot data);
    public void onDataListenerFailed(DatabaseError databaseError);
}
