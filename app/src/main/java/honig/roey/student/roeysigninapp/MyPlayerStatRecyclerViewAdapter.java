package honig.roey.student.roeysigninapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honig.roey.student.roeysigninapp.PlayerStatFragment.OnListFragmentInteractionListener;
import honig.roey.student.roeysigninapp.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.UserStat;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPlayerStatRecyclerViewAdapter extends RecyclerView.Adapter<MyPlayerStatRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<UserStat> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPlayerStatRecyclerViewAdapter(ArrayList<UserStat> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playerstat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position).getUid();
        holder.mIdView.setText(mValues.get(position).getFullName());
        holder.mContentView.setText(mValues.get(position).getPct()+"");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                    /* I think that for now i don't need an option for click
                    mListener.onListFragmentInteraction(holder.mItem);
                    */
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.userFullName);
            mContentView = (TextView) view.findViewById(R.id.userPCT);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
