package com.partymanager.data.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.partymanager.R;
import com.partymanager.data.DatiAttributi;

import java.util.ArrayList;

public class AttributiAdapter extends ArrayAdapter<DatiAttributi.Attributo> {

    private int num_pers_evento;

    public AttributiAdapter(Context context, ArrayList<DatiAttributi.Attributo> Attributo, int num_pers_evento) {
        super(context, R.layout.attributi_row, Attributo);
        this.num_pers_evento = num_pers_evento;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DatiAttributi.Attributo attr = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

            if (attr.template != null && (attr.template.equals("data") ||
                    attr.template.equals("luogoE") ||
                    attr.template.equals("luogoI") ||
                    attr.template.equals("oraE"))) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_null, parent, false);
            } else {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.attributi_row, parent, false);


        TextView name = (TextView) convertView.findViewById(R.id.txt_domanda);
        TextView details = (TextView) convertView.findViewById(R.id.txt_risposta);

        name.setText(attr.domanda);

        StringBuilder temp = new StringBuilder();
        temp.append(attr.risposta);
        if (!attr.risposta.equals("")) {
        /* la percentuale di persone che hanno votato quella domanda, rispetto alle persone che hanno risposto alla domanda */
            temp.append(" (" + 100 * attr.numr / attr.numd + "% ha votato questa risposta, ");

        /* il numero di persone che hanno risposto alla domanda rispetto alle persone totali dell'evento */
            temp.append(attr.numd + "/" + num_pers_evento + " hanno risposto)");
        }

        details.setText(temp);
            }


        return convertView;
    }
}
