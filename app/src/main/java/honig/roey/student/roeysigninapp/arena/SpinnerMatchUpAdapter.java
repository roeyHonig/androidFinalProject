package honig.roey.student.roeysigninapp.arena;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mklimek.circleinitialsview.CircleInitialsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honig.roey.student.roeysigninapp.R;

public class SpinnerMatchUpAdapter extends BaseAdapter {

    private ArrayList<SpinnerMatchUp> data;
    private Context context;

    public SpinnerMatchUpAdapter(ArrayList<SpinnerMatchUp> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // gathers the object in the i-th position from the ArrayList<SpinnerMatchUp>
        SpinnerMatchUp spinnerMatchUp = data.get(i);

        //get a reference to the LayoutInflator
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.match_up_content_for_spinner, viewGroup,false);

        TextView p1Name = v.findViewById(R.id.p1FullNameSpinner);
        ImageView p1ProfileImage = v.findViewById(R.id.p1ImageView);
        CircleInitialsView p1CircleView = v.findViewById(R.id.p1circleView);

        TextView p2Name = v.findViewById(R.id.p2FullNameSpinner);
        ImageView p2ProfileImage = v.findViewById(R.id.p2ImageView);
        CircleInitialsView p2CircleView = v.findViewById(R.id.p2circleView);

        TextView textViewVS = v.findViewById(R.id.textViewVsSpinner);

        if (i != 0) {
            p1Name.setText(spinnerMatchUp.getP1Name());
            p2Name.setText(spinnerMatchUp.getP2Name());

            if (spinnerMatchUp.getP1ImageUrl().equals("circleView")) {
                // p1 doesn't have a profile image
                p1CircleView.setText(spinnerMatchUp.getP1Name());
                p1CircleView.setVisibility(View.VISIBLE);
                p1ProfileImage.setVisibility(View.GONE);
            } else {
                //p1 has a profile image
                Picasso.get().load(spinnerMatchUp.getP1ImageUrl()).into(p1ProfileImage);
                p1CircleView.setVisibility(View.GONE);
                p1ProfileImage.setVisibility(View.VISIBLE);
            }

            if (spinnerMatchUp.getP2ImageUrl().equals("circleView")) {
                // p2 doesn't have a profile image
                p2CircleView.setText(spinnerMatchUp.getP2Name());
                p2CircleView.setVisibility(View.VISIBLE);
                p2ProfileImage.setVisibility(View.GONE);
            } else {
                //p2 has a profile image
                Picasso.get().load(spinnerMatchUp.getP2ImageUrl()).into(p2ProfileImage);
                p2CircleView.setVisibility(View.GONE);
                p2ProfileImage.setVisibility(View.VISIBLE);
            }

        } else {
            // this is the 1st position, meaning we just want to present the word "Show Data for All Players"
            p1Name.setText("Show Data for All Players");
            p1CircleView.setVisibility(View.GONE);
            p1ProfileImage.setVisibility(View.GONE);
            textViewVS.setVisibility(View.GONE);
            p2Name.setVisibility(View.GONE);
            p2CircleView.setVisibility(View.GONE);
            p2ProfileImage.setVisibility(View.GONE);
        }







        return v;
    }
}
