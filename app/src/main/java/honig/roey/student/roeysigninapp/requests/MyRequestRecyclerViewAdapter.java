package honig.roey.student.roeysigninapp.requests;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.requests.RequestFragment.OnListFragmentInteractionListener;
import honig.roey.student.roeysigninapp.requests.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Request> mRequestValues;
    private final OnListFragmentInteractionListener mListener;
    private final int mflag; // 1  - Invites   2 - requests

    public MyRequestRecyclerViewAdapter(ArrayList<Request> items, OnListFragmentInteractionListener listener, int flag) {
        mRequestValues = items;
        mListener = listener;
        mflag = flag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.mItem = mValuesInvite.get(position);
        holder.mItem = position;
        holder.mItemFlag = mflag;
        if (mflag == 1){
            // Invite Item
            holder.mItemTitle.setText("You've invited " + mRequestValues.get(position).getApprovingName() +
                    " to join Arena " + mRequestValues.get(position).getArenaName());
            holder.mItemStatus.setText("Status: " + mRequestValues.get(position).getStatus());
        } else {
            // Request Item
            holder.mItemTitle.setText(mRequestValues.get(position).getRequestingName() + " has invited you to join Arena " + mRequestValues.get(position).getArenaName());
            holder.mItemStatus.setText("Status: " + mRequestValues.get(position).getStatus());
        }




        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequestValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemTitle;
        public final TextView mItemStatus;
        // TODO: this is what we pass to the parent activity when we click on it, change it to someting valubale if needed
        public int mItem;
        public int mItemFlag;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemTitle = (TextView) view.findViewById(R.id.item_title);
            mItemStatus = (TextView) view.findViewById(R.id.item_status);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItemTitle.getText() + "'";
        }
    }
}
