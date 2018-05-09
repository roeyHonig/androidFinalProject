package honig.roey.student.roeysigninapp.arena;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.arena.MatchUpsFragment.OnListFragmentInteractionListener;
import honig.roey.student.roeysigninapp.arena.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.MatchUp;
import honig.roey.student.roeysigninapp.tables.UserStat;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMatchUpRecyclerViewAdapter extends RecyclerView.Adapter<MyMatchUpRecyclerViewAdapter.ViewHolder> {


    private final ArenaFragment.OnListFragmentInteractionListener mListener;
    private ArrayList<MatchUp> individualMatchUpsDataSet = new ArrayList<>();

    public MyMatchUpRecyclerViewAdapter(ArrayList<MatchUp> individualMatchUpsDataSet, ArenaFragment.OnListFragmentInteractionListener listener) {
        this.individualMatchUpsDataSet = individualMatchUpsDataSet;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_matchup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.player1.setText(individualMatchUpsDataSet.get(position).getPlayers().get(0).getFullName());
        holder.player2.setText(individualMatchUpsDataSet.get(position).getPlayers().get(1).getFullName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    // A String, containing the unique push() key representing this matchup
                    // is what we pass back to the activity
                    mListener.onMatchUpListInteraction(individualMatchUpsDataSet.get(position).getKey());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return individualMatchUpsDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView player1;
        public final TextView player2;
        public ArrayList<UserStat> mItem1;
        public ArrayList<UserStat> mItem2;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            this.player1 = (TextView) view.findViewById(R.id.player1);
            this.player2 = (TextView) view.findViewById(R.id.player2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" +
                    this.player1.getText().toString() +
                    " Vs " +
                    this.player2.getText().toString() + "'";
        }
    }
}
