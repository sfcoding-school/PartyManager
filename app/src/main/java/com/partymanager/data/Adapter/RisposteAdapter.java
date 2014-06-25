package com.partymanager.data.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.partymanager.EventSupport.EventoHelper;
import com.partymanager.R;
import com.partymanager.data.DatiRisposte;
import com.partymanager.helper.HelperDataParser;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;

public class RisposteAdapter extends ArrayAdapter<DatiRisposte.Risposta> {

    private int num_pers_evento;
    private int id_attributo;
    private int idEvento;

    public RisposteAdapter(int idEvento, Context context, ArrayList<DatiRisposte.Risposta> Risposta, int num_pers_evento, int id_attributo) {
        super(context, R.layout.risposta_row, Risposta);
        this.num_pers_evento = num_pers_evento;
        this.id_attributo = id_attributo;
        this.idEvento = idEvento;
    }

    public int getId() {
        return id_attributo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.risposta_row, parent, false);
        }

        TextView risp = (TextView) convertView.findViewById(R.id.txt_risposta2);

        String temp_risposta = DatiRisposte.getItemPosition(position).risposta;

        if (DatiRisposte.template != null && DatiRisposte.template.equals("data")) {
            temp_risposta = HelperDataParser.getGiornoLettere(HelperDataParser.getCalFromString(temp_risposta)) + " " + temp_risposta;
        }


        TextView who = (TextView) convertView.findViewById(R.id.txt_who);


        StringBuilder sb = new StringBuilder();
        sb.append("");
        if (DatiRisposte.getItemPosition(position).persone != null) {
            for (int i = 0; i < DatiRisposte.getItemPosition(position).persone.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                if (DatiRisposte.getItemPosition(position).persone.get(i).id_fb.equals(HelperFacebook.getFacebookId()))
                    sb.append("Io");
                else
                    sb.append(DatiRisposte.getItemPosition(position).persone.get(i).nome.split(" ")[0]);
            }
        }
        final Button vota = (Button) convertView.findViewById(R.id.button_voto);
        vota.setVisibility(View.VISIBLE);
        final ProgressBar pb_vota = (ProgressBar) convertView.findViewById(R.id.pb_vota);
        vota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventoHelper.vota(idEvento, RisposteAdapter.this, vota, DatiRisposte.getItemPosition(position).id, position, pb_vota);
                vota.setVisibility(View.GONE);
            }
        });

        if (DatiRisposte.template != null && DatiRisposte.template.equals("sino")) {
            vota.setVisibility(View.GONE);
            temp_risposta += " " + (100 * DatiRisposte.getItemPosition(position).persone.size()) / num_pers_evento + "%";
        }
        risp.setText(temp_risposta);

        who.setText(sb.toString());
        who.setTextColor(Color.BLACK);

        return convertView;
    }
}
