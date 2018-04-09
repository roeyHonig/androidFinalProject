package honig.roey.student.roeysigninapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by hackeru on 09/04/2018.
 */

public interface OnGetDataListener {
    public void onDataListenerStart();
    public void onDataListenerSuccess(DataSnapshot data);
    public void onDataListenerFailed(DatabaseError databaseError);
}
