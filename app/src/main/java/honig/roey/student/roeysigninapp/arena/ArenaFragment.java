package honig.roey.student.roeysigninapp.arena;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import honig.roey.student.roeysigninapp.R;
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


    public ArenaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arena, container, false);

        // get Arguments
        globalDataSet.setKey(getArguments().getString("argKey"));
        globalDataSet.setName(getArguments().getString("argName"));
        globalDataSet.setPublicViewd(getArguments().getBoolean("argIsPublicViewd"));
        globalDataSet.setSuperUser(getArguments().getString("argSuperUser"));
        globalDataSet.setUserStats(getArguments().getParcelableArrayList("argUserStatArrayList"));
        globalDataSet.setNumPlayers(getArguments().getInt("argNumPlayers"));

        //globalDataSet.setNumPlayers(globalDataSet.getUserStats().size());

        // just a Test TextView To see i got the arguments out
        TextView tvBuffer =  view.findViewById(R.id.tvBuffer);
        tvBuffer.setText("Arena Id is: "+ globalDataSet.getKey() +", "+
                "Arena's Name is: "+ globalDataSet.getName() +", "+
                "There are: "+ globalDataSet.getNumPlayers() +" Players in this Arena, "+
                "Is this Arena visiable to all App users? "+ globalDataSet.isPublicViewd() +", " +
                "and the super User is "+ globalDataSet.getSuperUser()
        );


        return view;
    }

}
