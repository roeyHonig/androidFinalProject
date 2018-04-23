package honig.roey.student.roeysigninapp.requests;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import honig.roey.student.roeysigninapp.R;
import honig.roey.student.roeysigninapp.requests.dummy.DummyContent;
import honig.roey.student.roeysigninapp.requests.dummy.DummyContent.DummyItem;
import honig.roey.student.roeysigninapp.tables.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RequestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private  int noRequestsVisibility;
    private  int noInvitesVisibility;
    private ArrayList<Request> userAproves;
    private ArrayList<Request> userInvites;
    private MyRequestRecyclerViewAdapter mInvitesAdapter;
    private MyRequestRecyclerViewAdapter mRequestsAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RequestFragment newInstance(int columnCount) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            userAproves=getArguments().getParcelableArrayList("arg1");
            userInvites = getArguments().getParcelableArrayList("arg2");




            //
            Toast.makeText(getActivity(),""+userAproves.get(0).getStatus(),Toast.LENGTH_LONG).show();

            if (userAproves.size()!=0){
                noRequestsVisibility = View.GONE;
            } else {
                noRequestsVisibility = View.VISIBLE;
            }

            if (userInvites.size()!=0){
                noInvitesVisibility = View.GONE;
            } else {
                noInvitesVisibility = View.VISIBLE;
            }

            /*
            Parcel roey = Parcel.obtain();
            userAproves.get(0).writeToParcel(roey,0);
            userAproves.add(Request.CREATOR.createFromParcel(roey));
            */
        } else {
            // No Requests of any type for current User - show the "No Arenea Yet" UI
            noRequestsVisibility = View.VISIBLE;
            noInvitesVisibility = View.VISIBLE;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set the View
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);
        // Set TextViews visibility - "No Requests \ Invites"
        TextView textViewNoInvites = view.findViewById(R.id.noInvites);
        textViewNoInvites.setVisibility(noInvitesVisibility);
        TextView textViewNoRequests = view.findViewById(R.id.noRequests);
        textViewNoRequests.setVisibility(noRequestsVisibility);

        // set recyclerViews
        RecyclerView recyclerViewInvitesList = view.findViewById(R.id.invitesList);
        RecyclerView recyclerViewRequestsList = view.findViewById(R.id.aprovalsList);


        // set Toggle buttons
        Button toggleButtonInvites = view.findViewById(R.id.toggleButtonInvites);
        Button toggleButtonRequests = view.findViewById(R.id.toggleButtonRequests);

        toggleButtonInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewInvitesList.setVisibility(View.VISIBLE);
                textViewNoInvites.setVisibility(noInvitesVisibility);

                recyclerViewRequestsList.setVisibility(View.GONE);
                textViewNoRequests.setVisibility(View.GONE);
            }
        });

        toggleButtonRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewInvitesList.setVisibility(View.GONE);
                textViewNoInvites.setVisibility(View.GONE);

                recyclerViewRequestsList.setVisibility(View.VISIBLE);
                textViewNoRequests.setVisibility(noRequestsVisibility);
            }
        });


        RecyclerView invitesRecyclerView = view.findViewById(R.id.invitesList);
        if (noInvitesVisibility == View.GONE){
            invitesRecyclerView.setVisibility(View.VISIBLE);
            // Set the adapter for recyclerView
            Context invitesRecyclerViewContext = invitesRecyclerView.getContext();
            if (mColumnCount <= 1) {
                invitesRecyclerView.setLayoutManager(new LinearLayoutManager(invitesRecyclerViewContext));
            } else {
                invitesRecyclerView.setLayoutManager(new GridLayoutManager(invitesRecyclerViewContext, mColumnCount));
            }
            mInvitesAdapter = new MyRequestRecyclerViewAdapter(userInvites,mListener,1);
            invitesRecyclerView.setAdapter(mInvitesAdapter);


        } else {
            // No invites
            invitesRecyclerView.setVisibility(View.GONE);
        }



        RecyclerView requestRecyclerView = view.findViewById(R.id.aprovalsList);
        if (noRequestsVisibility == View.GONE){
            requestRecyclerView.setVisibility(View.VISIBLE);
            // Set the adapter for recyclerView
            Context requestRecyclerViewContext = invitesRecyclerView.getContext();
            if (mColumnCount <= 1) {
                invitesRecyclerView.setLayoutManager(new LinearLayoutManager(requestRecyclerViewContext));
            } else {
                invitesRecyclerView.setLayoutManager(new GridLayoutManager(requestRecyclerViewContext, mColumnCount));
            }
            mRequestsAdapter = new MyRequestRecyclerViewAdapter(userAproves,mListener,2);
            requestRecyclerView.setAdapter(mRequestsAdapter);

        } else {
            // No invites
            requestRecyclerView.setVisibility(View.GONE);
        }










        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(int item);
    }
}
