package honig.roey.student.roeysigninapp.arena;

import android.os.Parcel;
import android.os.Parcelable;

public class PointDataSet implements Parcelable {
    private float xValue;
    private float yValue;

    public PointDataSet(float xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    protected PointDataSet(Parcel in) {
        xValue = in.readFloat();
        yValue = in.readFloat();
    }

    public static final Creator<PointDataSet> CREATOR = new Creator<PointDataSet>() {
        @Override
        public PointDataSet createFromParcel(Parcel in) {
            return new PointDataSet(in);
        }

        @Override
        public PointDataSet[] newArray(int size) {
            return new PointDataSet[size];
        }
    };

    public float getxValue() {
        return xValue;
    }

    public void setxValue(float xValue) {
        this.xValue = xValue;
    }

    public float getyValue() {
        return yValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(xValue);
        parcel.writeFloat(yValue);
    }
}
