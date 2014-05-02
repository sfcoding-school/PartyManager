package com.partymanager.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.partymanager.R;
import com.partymanager.app.dummy.AttributiAdapter;
import com.partymanager.app.dummy.DataProvide;
import com.partymanager.app.dummy.DatiAttributi;
import com.partymanager.app.dummy.DatiEventi;
import com.partymanager.app.dummy.EventAdapter;
import com.partymanager.app.helper.helperFacebook;

public class Evento extends Fragment implements AbsListView.OnItemClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String mParam1; //Lista amici
    private String mParam2; //nome evento
    private String mParam3; //id evento
    private ImageButton bnt_friends;


    public static Evento newInstance(String param1, String param2, String param3) {
        Evento fragment = new Evento();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);



        return fragment;
    }

    public Evento() {

    }
    AttributiAdapter aAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);

            Log.e("Evento TEST: ", mParam1 + " " + mParam2 + " " + mParam3);
        }

        aAdapter = new AttributiAdapter (getActivity(), DatiAttributi.ITEMS);

        //if (mParam3 != null)
        DataProvide.getAttributi(getActivity(), "1");

    }

    //TEST
    ListView listView;
    View prova;
    String[] values = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"};
    int mLastFirstVisibleItem = 0;
    int mLastLastVisibleItem = 0;
    //END TEST

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evento, container, false);

/*
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mParam2);
        }
*/
        //TEST
        prova = view.findViewById(R.id.stickyheader);
        prova.setVisibility(View.INVISIBLE);

        listView = (ListView) view.findViewById(R.id.listview_attributi);
        bnt_friends = (ImageButton) view.findViewById(R.id.imgButton_amici);


        // Assign adapter to ListView
        listView.setAdapter(aAdapter);
/*
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == 0)
                    Log.i("a", "scrolling stopped...");

                if (view.getId() == listView.getId()) {
                    final int currentFirstVisibleItem = listView.getFirstVisiblePosition();
                    final int currentLAstVisibileItem = listView.getLastVisiblePosition();
                    if (currentFirstVisibleItem == 1) {
                        prova.setVisibility(View.VISIBLE);

                    } else {
                        if (currentFirstVisibleItem > mLastFirstVisibleItem) {

                            //prova.setVisibility(View.VISIBLE);
                            if (prova.getVisibility() != View.GONE) {
                                TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -prova.getHeight());
                                anim.setDuration(500);
                                anim.setFillAfter(false);
                                prova.startAnimation(anim);
                                prova.setVisibility(View.GONE);
                            }

                            Log.i("a", "scrolling verso giu...");
                        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                            /*if (prova.getVisibility() != View.GONE && currentFirstVisibleItem > 8) {
                                TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -prova.getHeight());
                                anim.setDuration(500);
                                anim.setFillAfter(false);
                                prova.startAnimation(anim);
                                prova.setVisibility(View.GONE);
                            }
                            if (currentLAstVisibileItem <= 11) {
                                prova.setVisibility(View.VISIBLE);
                            }*/
                            /*
                            if (prova.getVisibility() != View.VISIBLE) {
                                TranslateAnimation anim = new TranslateAnimation(0, 0, -prova.getHeight(), 0);
                                anim.setDuration(100);
                                anim.setFillAfter(false);
                                prova.startAnimation(anim);
                                prova.setVisibility(View.VISIBLE);
                            }
                            Log.i("a", "scrolling verso su...");
                        }
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                    mLastLastVisibleItem = currentLAstVisibileItem;

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }
        });

        bnt_friends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        //END TEST
        */
        return view;
    }

/*
    @Override
    public void onResume() {
        super.onResume();
    }
*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
