package honig.roey.student.roeysigninapp.arena;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Chart implements Parcelable {
    ArrayList<PointDataSet> chart;


    public Chart(){}

    public Chart(ArrayList<PointDataSet> chart) {
        this.chart = chart;
    }

    protected Chart(Parcel in) {
    }

    public static final Creator<Chart> CREATOR = new Creator<Chart>() {
        @Override
        public Chart createFromParcel(Parcel in) {
            return new Chart(in);
        }

        @Override
        public Chart[] newArray(int size) {
            return new Chart[size];
        }
    };

    public void add (PointDataSet pointDataSet){
        this.chart.add(pointDataSet);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
