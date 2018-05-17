package honig.roey.student.roeysigninapp.arena;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ChartsCollection implements Parcelable {
    ArrayList<Chart> chartsCollection;

    public ChartsCollection(ArrayList<Chart> chartsCollection) {
        this.chartsCollection = chartsCollection;
    }

    protected ChartsCollection(Parcel in) {
    }

    public static final Creator<ChartsCollection> CREATOR = new Creator<ChartsCollection>() {
        @Override
        public ChartsCollection createFromParcel(Parcel in) {
            return new ChartsCollection(in);
        }

        @Override
        public ChartsCollection[] newArray(int size) {
            return new ChartsCollection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
