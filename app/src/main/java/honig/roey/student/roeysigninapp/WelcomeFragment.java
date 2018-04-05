package honig.roey.student.roeysigninapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {


    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_screen, container, false);
        long duration = 1000;

        ProgressBar progressBar0 = view.findViewById(R.id.progressBar0);
        ProgressBar progressBar1 = view.findViewById(R.id.progressBar1);
        ProgressBar progressBar2 = view.findViewById(R.id.progressBar2);
        ProgressBar progressBar3 = view.findViewById(R.id.progressBar3);

        animatePrograss(progressBar0,100,0,60,duration);
        animatePrograss(progressBar1,100,0,80,duration);
        animatePrograss(progressBar2,100,0,90,duration);
        animatePrograss(progressBar3,100,0,100,duration);

        return view;
    }

    private void animatePrograss(ProgressBar progressBar, int max, int from, int to, long duration) {
        progressBar.setMax(max);
        progressBar.setProgress(from);
        PrograssBarAnimation prograssBarAnimation = new PrograssBarAnimation(progressBar,from, to);
        prograssBarAnimation.setDuration(duration);
        progressBar.startAnimation(prograssBarAnimation);
    }

}
