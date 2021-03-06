package com.letsgo.wifitrigger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.letsgo.wifitrigger.R;
import com.letsgo.wifitrigger.settings.SettingsHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfigureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfigureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigureFragment extends PreferenceFragment {
    private static final String LOG_TAG = ConfigureFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NETWORK = "network";
    private static final String ARG_IS_CONNECTED = "is_connected";

    private String mNetworkSSID;
    private boolean mIsConnected;

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param network network SSID.
     * @return A new instance of fragment ConfigureFragment.
     */
    public static ConfigureFragment newInstance(String network, boolean isConnected) {
        ConfigureFragment fragment = new ConfigureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NETWORK, network);
        args.putBoolean(ARG_IS_CONNECTED, isConnected);
        fragment.setArguments(args);
        return fragment;
    }

    public ConfigureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetworkSSID = getArguments().getString(ARG_NETWORK);
            mIsConnected = getArguments().getBoolean(ARG_IS_CONNECTED);
        }

        addPreferencesFromResource(R.xml.preferences_configure);

        createPreferenceHierarchy();
    }

    public void createPreferenceHierarchy() {
        Context context = this.getActivity();
        PreferenceScreen root = getPreferenceScreen();
        for(Preference preference : SettingsHelper.getPreferences(context, mNetworkSSID, mIsConnected))
            root.addPreference(preference);
    }//end method

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(LOG_TAG, this.getClass() + "::onCreateView");
        return inflater.inflate(R.layout.fragment_connected, container, false);
    }*/

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/

}
