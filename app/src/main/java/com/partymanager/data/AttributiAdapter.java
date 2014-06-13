package com.partymanager.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.partymanager.R;

import java.util.ArrayList;

public class AttributiAdapter extends ArrayAdapter<DatiAttributi.Attributo> {

    public AttributiAdapter(Context context, ArrayList<DatiAttributi.Attributo> Attributo) {
        super(context, R.layout.attributi_row, Attributo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DatiAttributi.Attributo attr = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attributi_row, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.txt_domanda);
        TextView details = (TextView) convertView.findViewById(R.id.txt_risposta);

        name.setText(attr.domanda);
        details.setText(attr.risposta);

        return convertView;
    }
}
