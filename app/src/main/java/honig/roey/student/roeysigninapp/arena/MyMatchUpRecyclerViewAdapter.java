package honig.roey.student.roeysigninapp.arena;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.arena.ArenaFragment.OnListFragmentInteractionListener;
import honig.roey.student.roeysigninapp.arena.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.UserStat;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMatchUpRecyclerViewAdapter extends RecyclerView.Adapter<MyMatchUpRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<UserStat> mValues1;
    private final ArrayList<UserStat> mValues2;
    private final ArrayList<String> matchupKey;
    private final OnListFragmentInteractionListener mListener;

    public MyMatchUpRecyclerViewAdapter(ArrayList<UserStat> mValues1 ,ArrayList<UserStat> mValues2 ,ArrayList<String> matchupKey , OnListFragmentInteractionListener listener) {
        this.mValues1 = mValues1;
        this.mValues2 = mValues2;
        this.matchupKey = matchupKey;
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
        holder.mItem = matchupKey.get(position);
        holder.player1.setText(mValues1.get(position).getFullName());
        holder.player2.setText(mValues2.get(position).getFullName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    // the item in question here, is a specific metchup between 2 players of the Arena
                    // mItem is a String containing the unique push() key representing this matchup
                    // this is what we pass back to the activity
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView player1;
        public final TextView player2;
        public ArrayList<UserStat> mItem1;
        public ArrayList<UserStat> mItem2;
        public String mItem;


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
