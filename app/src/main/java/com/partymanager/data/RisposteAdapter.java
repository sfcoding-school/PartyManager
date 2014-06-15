package com.partymanager.data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.partymanager.R;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.helper.HelperDataParser;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;

public class RisposteAdapter extends ArrayAdapter<DatiRisposte.Risposta> {

    private int num_pers_evento;

    public RisposteAdapter(Context context, ArrayList<DatiRisposte.Risposta> Risposta, int num_pers_evento) {
        super(context, R.layout.risposta_row, Risposta);
        this.num_pers_evento = num_pers_evento;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.risposta_row, parent, false);
        }

        TextView risp = (TextView) convertView.findViewById(R.id.txt_risposta2);

        String temp_risposta = DatiRisposte.ITEMS.get(position).risposta;
        if (DatiRisposte.ITEMS.get(position).template.equals("data")) {
            temp_risposta = HelperDataParser.getGiornoLettere(HelperDataParser.getCalFromString(temp_risposta)) + " " + temp_risposta;
        }

        TextView who = (TextView) convertView.findViewById(R.id.txt_who);


        StringBuilder sb = new StringBuilder();
        sb.append("");
        if (DatiRisposte.ITEMS.get(position).persone != null) {
            for (int i = 0; i < DatiRisposte.ITEMS.get(position).persone.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                if (DatiRisposte.ITEMS.get(position).persone.get(i).id_fb.equals(HelperFacebook.getFacebookId()))
                    sb.append("Io");
                else
                    sb.append(DatiRisposte.ITEMS.get(position).persone.get(i).nome.split(" ")[0]);
            }
        }
        Button vota = (Button) convertView.findViewById(R.id.button_voto);
        vota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Evento.vota(DatiRisposte.ITEMS.get(position).id, position);
            }
        });

        if (DatiRisposte.ITEMS.get(position).template.equals("sino")) {
            temp_risposta += " " + (100 * DatiRisposte.ITEMS.get(position).persone.size()) / num_pers_evento + "%";
            vota.setVisibility(View.GONE);
        }
        risp.setText(temp_risposta);

        who.setText(sb.toString());
        who.setTextColor(Color.BLACK);

        return convertView;
    }
}
