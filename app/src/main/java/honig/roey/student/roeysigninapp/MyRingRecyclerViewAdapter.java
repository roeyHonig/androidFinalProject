package honig.roey.student.roeysigninapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honig.roey.student.roeysigninapp.RingFragment.OnListFragmentInteractionListener;
import honig.roey.student.roeysigninapp.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRingRecyclerViewAdapter extends RecyclerView.Adapter<MyRingRecyclerViewAdapter.ViewHolder> {

    private final String[] mValues; // the Data from a DataBase , like the String[] of the user's rings
    private final OnListFragmentInteractionListener mListener; // a Listener for interaction with the list, specifcully clicking on a list item
                                                                // as can be seen in the "onBindViewHolder" methood
    // Constractor for the adapter
    // when called to action, in our RingFragment, it will be passed (as an argument) the data which is the String[]
    // of rings names
    // also an instance of the OnListFragmentInteractionListener interface is the 2nd argument
    public MyRingRecyclerViewAdapter(String[] items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ring, parent, false);
        // constracts and returns a single Ring Object (1 item from the list of rings)
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // set the content
        holder.mContentView.setText(mValues[position]);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    // look at the NavDraer Activity - which holds the fragment, this adapter class belongs too -
                    // you will see there, that there is a methood called "onListFragmentInteraction"
                    // this is a method waiting to be excuted. it will excute now:
                    mListener.onListFragmentInteraction(holder.mContentView.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }


    // This is the Object Class of our each individual Ring
    // The fields are Android views \ widgets , like the TextView which has the Ring's Name
    // But waut, we these are just the views, the content (the actual Ring Name for example) will come later,
    // in the "onBindViewHolder" method
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public String mItem;
        // Constractor - this takes the View which is the layout of a single Ring Item in the list
        // This View can have multiple TextView , buttons and what ever we want
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.ringName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
