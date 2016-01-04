package com.example.hosin.bikesummon;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView email;
    private EditText username;
    private EditText phone;
    private RadioButton male;
    private RadioButton female;
    private RadioButton single;
    private RadioButton couple;
    private RadioButton secret;
    //private TextView name;

    HttpUtil httpUtil=new HttpUtil();
    private static Handler handler;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        username=(EditText)view.findViewById(R.id.profile_nickname);
        phone=(EditText)view.findViewById(R.id.profile_Phone);
        email=(TextView)view.findViewById(R.id.profile_Email);
        male=(RadioButton)view.findViewById(R.id.profile_male);
        female=(RadioButton)view.findViewById(R.id.profile_female);
        single=(RadioButton)view.findViewById(R.id.profile_single);
        couple=(RadioButton)view.findViewById(R.id.profile_couple);
        secret=(RadioButton)view.findViewById(R.id.profile_secret);
        SharedPreferences pref=getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        if(email==null){
            Log.d("null","null");

        }
        email.setText(pref.getString("email",""));
        username.setText(pref.getString("username", ""));
        //name.setText(pref.getString("name", ""));
        phone.setText(pref.getString("tel",""));
        if(pref.getString("sex",null).equals("M")) {
            male.setChecked(true);
            female.setChecked(false);
        }else if(pref.getString("sex",null).equals("F")){
            male.setChecked(false);
            female.setChecked(true);
        }else{
            male.setChecked(false);
            female.setChecked(false);
        }
        if(pref.getString("relationStatus",null).equals("S")){
            single.setChecked(true);
            couple.setChecked(false);
            secret.setChecked(false);
        }else if(pref.getString("relationStatus",null).equals("N")){
            couple.setChecked(true);
            single.setChecked(false);
            secret.setChecked(false);
        }else{
            secret.setChecked(true);
            single.setChecked(false);
            couple.setChecked(false);
        }

        return view;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
