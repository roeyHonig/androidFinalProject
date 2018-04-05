package honig.roey.student.roeysigninapp;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class PrograssBarAnimation extends Animation {
    private ProgressBar progressBar;
    private float from;
    private float to;

    public PrograssBarAnimation(ProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        double b = 0.2;
        float valueLin = this.from + (this.to - this.from) * interpolatedTime;  // Linear
        float valueSqr = (float) (Math.pow(interpolatedTime,b) * (this.to - this.from) + this.from) ; // faster at the begining
        this.progressBar.setProgress((int) (0.5 *(valueLin+valueSqr)));
    }
}
