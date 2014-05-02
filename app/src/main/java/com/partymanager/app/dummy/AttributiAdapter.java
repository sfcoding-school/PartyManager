package com.partymanager.app.dummy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.partymanager.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ulisse on 5/2/14.
 */

public class AttributiAdapter extends ArrayAdapter<DatiAttributi.Attributo> {

    public AttributiAdapter(Context context, ArrayList<DatiAttributi.Attributo> Attributo) {
        super(context, R.layout.attributi, Attributo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DatiAttributi.Attributo Attributo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attributi, parent, false);
        }

        // Lookup view for data population
        TextView domanda = (TextView) convertView.findViewById(R.id.txt_domanda);
        TextView risposta = (TextView) convertView.findViewById(R.id.txt_risposta);


        domanda.setText(Attributo.domanda);
        risposta.setText(Attributo.risposta);

        risposta.setTranslationX(30);

        return convertView;
    }
}
