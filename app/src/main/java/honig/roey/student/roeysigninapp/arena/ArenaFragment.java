package honig.roey.student.roeysigninapp.arena;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.tables.MatchUp;
import honig.roey.student.roeysigninapp.tables.RingGlobal;
import honig.roey.student.roeysigninapp.tables.UserStat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArenaFragment extends Fragment {
    private ArrayList<UserStat> mValues1;
    private ArrayList<UserStat> mValues2;
    private ArrayList<String> matchupKey;
    private RingGlobal globalDataSet = new RingGlobal();
    private ArrayList<MatchUp> individualMatchUpsDataSet = new ArrayList<>();


    public ArenaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arena, container, false);

        // get Arguments
        if (getArguments() != null){
            globalDataSet.setKey(getArguments().getString("argKey"));
            globalDataSet.setName(getArguments().getString("argName"));
            globalDataSet.setPublicViewd(getArguments().getBoolean("argIsPublicViewd"));
            globalDataSet.setSuperUser(getArguments().getString("argSuperUser"));
            globalDataSet.setUserStats(getArguments().getParcelableArrayList("argUserStatArrayList"));
            globalDataSet.setNumPlayers(getArguments().getInt("argNumPlayers"));
            individualMatchUpsDataSet = getArguments().getParcelableArrayList("argMatchUpsDataArrayList");

            String tmpConcatation = "";

            for (int i = 0; i < individualMatchUpsDataSet.size(); i++) {
                tmpConcatation += individualMatchUpsDataSet.get(i).getPlayers().get(0).toString();
                tmpConcatation += "* Vs *";
                tmpConcatation += individualMatchUpsDataSet.get(i).getPlayers().get(1).toString();
                tmpConcatation += "//////";
            }


            // just a Test TextView To see i got the arguments out
            TextView tvBuffer =  view.findViewById(R.id.tvBuffer);

            tvBuffer.setText(globalDataSet.toShortString() + tmpConcatation);
        }


        return view;
    }

}
