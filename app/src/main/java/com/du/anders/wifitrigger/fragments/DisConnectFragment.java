package com.du.anders.wifitrigger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import com.du.anders.wifitrigger.G;
import com.du.anders.wifitrigger.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisConnectFragment extends PreferenceFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NETWORK = "network";

    // TODO: Rename and change types of parameters
    private String mNetworkSSID;

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param network network SSID.
     * @return A new instance of fragment ConnectedFragment.
     */
    public static DisConnectFragment newInstance(String network) {
        DisConnectFragment fragment = new DisConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NETWORK, network);
        fragment.setArguments(args);
        return fragment;
    }

    public DisConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetworkSSID = getArguments().getString(ARG_NETWORK);
        }
        Log.i(G.LOG_TAG, this.getClass() + "::onCreate");
        this.setPreferenceScreen(createPreferenceHierarchy());
    }

    public PreferenceScreen createPreferenceHierarchy() {
        Context context = this.getActivity();
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(context);
        Resources res = getResources();

        // category 1 created programmatically
        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(res.getString(R.string.preference_disconnect_title));
        root.addPreference(category);

        SwitchPreference switch_sound = new SwitchPreference(context);
        switch_sound.setTitle(res.getString(R.string.preference_disconnect_sound_title));
        switch_sound.setKey(mNetworkSSID + G.KEY_DISCONNECT_SOUND_POSTFIX);
        switch_sound.setSummaryOn(res.getString(R.string.preference_disconnect_sound_summary_on));
        switch_sound.setSummaryOff(res.getString(R.string.preference_disconnect_sound_summary_off));
        category.addPreference(switch_sound);

        ListPreference list_vibrator = new ListPreference(context);
        list_vibrator.setTitle(res.getString(R.string.preference_vibrate_title));
        list_vibrator.setDialogTitle(res.getString(R.string.preference_vibrate_title));
        list_vibrator.setKey(mNetworkSSID + G.KEY_DISCONNECT_VIBRATE_POSTFIX);

        list_vibrator.setEntries(res.getStringArray(R.array.preferences_connected_vibrator_entries));
        list_vibrator.setEntryValues(res.getStringArray(R.array.preferences_connected_vibrator_values));
        list_vibrator.setDefaultValue(String.valueOf(G.PREFERENCE_CONNECTED_VIBRATE_NO_CHANGE));
        list_vibrator.setSummary("%s");//this will show the selected option as summary

        category.addPreference(list_vibrator);

        return root;
    }//end method

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(G.LOG_TAG, this.getClass() + "::onCreateView");
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
