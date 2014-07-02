package com.partymanager.data.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.partymanager.R;
import com.partymanager.data.DatiEventi;
import com.partymanager.helper.HelperDataParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventAdapter extends ArrayAdapter<DatiEventi.Evento> {

    SimpleDateFormat dfgiornoN;
    SimpleDateFormat dfgiornoS;
    SimpleDateFormat dfMese;

    public EventAdapter(Context context, ArrayList<DatiEventi.Evento> Evento) {
        super(context, R.layout.event_list_row, Evento);
        dfgiornoN = new SimpleDateFormat("d", Locale.ITALIAN);
        dfgiornoS = new SimpleDateFormat("E", Locale.ITALIAN);
        dfMese = new SimpleDateFormat("MMM", Locale.ITALIAN);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DatiEventi.Evento evento = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_row, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView details = (TextView) convertView.findViewById(R.id.details);
        TextView count = (TextView) convertView.findViewById(R.id.count);
        TextView giornoN = (TextView) convertView.findViewById(R.id.giornoN);
        TextView giornoS = (TextView) convertView.findViewById(R.id.giornoS);
        TextView mese = (TextView) convertView.findViewById(R.id.mese);

        //TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);

        // Populate the data into the template view using the data object

        name.setText(evento.name);
        details.setText(evento.details);

        if (evento.date != null) {
            String gN = HelperDataParser.getGiornoNumerio(evento.date);
            giornoN.setText(gN);

            giornoS.setText(HelperDataParser.getGiornoLettere(evento.date));

            mese.setText(HelperDataParser.getMese(evento.date));

            long countDown = HelperDataParser.getCountDownDays(
                    Integer.parseInt(gN),
                    Integer.parseInt(HelperDataParser.getMeseN(evento.date)),
                    Integer.parseInt(HelperDataParser.getYear(evento.date)));
            if (countDown <= 0) {
                count.setText("");
                if (countDown == 0) {
                    count.setText("0");
                    count.setTextColor(Color.GREEN);
                }
            } else
                count.setText("-" + (int) countDown);

        } else {
            giornoN.setText("");
            giornoS.setText("");
            mese.setText("");
            count.setText("");
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
