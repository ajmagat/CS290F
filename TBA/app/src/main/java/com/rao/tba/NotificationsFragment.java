package com.rao.tba;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    // List to hold notifications
    private List<Notification> mNotifications;
    private NotificationListAdapter mNotificationsAdapter;
    private OnListFragmentInteractionListener mListener;


    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance(int sectionNumber, List<Notification> n) {
        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setNotificationList(n);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void setNotificationList(List<Notification> n) {
        mNotifications = n;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Set the adapter
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            RecyclerView recyclerView = (RecyclerView) rootView;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new NotificationListAdapter(mNotifications, mListener, getContext()));
            mNotificationsAdapter = (NotificationListAdapter) recyclerView.getAdapter();
        }

        return rootView;
    }


    /**
     * @brief Accessor for mNotificationsAdapter
     * @return mNotificationsAdapter
     */
    public NotificationListAdapter getNotificationsAdapter() {
        return mNotificationsAdapter;
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Notification item, int pos, NotificationListAdapter adapter, List<Notification> values, boolean delete);
    }
    public void changeButtonVisibility() {
       // Button bikeNotificationButton = (Button) getView().findViewById(R.id.bikeNotificationButton);
        //bikeNotificationButton.setVisibility(View.VISIBLE);
    }
}
