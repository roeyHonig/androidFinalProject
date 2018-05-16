package honig.roey.student.roeysigninapp.arena;

import java.util.ArrayList;

public class Chart {
    ArrayList<PointDataSet> chart;


    public Chart(){}

    public Chart(ArrayList<PointDataSet> chart) {
        this.chart = chart;
    }

    public void add (PointDataSet pointDataSet){
        this.chart.add(pointDataSet);
    }
}
