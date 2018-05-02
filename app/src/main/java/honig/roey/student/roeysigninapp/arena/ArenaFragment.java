package honig.roey.student.roeysigninapp.arena;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import honig.roey.student.roeysigninapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArenaFragment extends Fragment {


    public ArenaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arena, container, false);
    }

}
