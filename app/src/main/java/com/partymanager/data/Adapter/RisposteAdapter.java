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

import com.partymanager.EventSupport.EventAsync;
import com.partymanager.EventSupport.EventoHelper;
import com.partymanager.R;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.data.DatiRisposte;
import com.partymanager.helper.HelperDataParser;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;

public class RisposteAdapter extends ArrayAdapter<DatiRisposte.Risposta> {

    private int id_attributo;
    private boolean chiusa;
    private int arg2;
    private Context context;
    private int idEvento;

    public RisposteAdapter(int idEvento, Context context, ArrayList<DatiRisposte.Risposta> Risposta, int id_attributo, int arg2, boolean chiusa) {
        super(context, R.layout.risposta_row, Risposta);
        this.id_attributo = id_attributo;
        this.idEvento = idEvento;
        this.chiusa = chiusa;
        this.arg2 = arg2; /* posizione attributo su DatiAttributi */
        this.context = context;
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

        String temp_risposta = DatiRisposte.getPositionItem(position).risposta;

        if (DatiRisposte.template != null && DatiRisposte.template.equals("data")) {
            temp_risposta = HelperDataParser.getGiornoLettere(HelperDataParser.getCalFromString(temp_risposta)) + " " + temp_risposta;
        }

        TextView who = (TextView) convertView.findViewById(R.id.txt_who);
        final Button vota = (Button) convertView.findViewById(R.id.button_voto);
        vota.setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder();
        sb.append("");
        if (DatiRisposte.getPositionItem(position).persone != null) {
            for (int i = 0; i < DatiRisposte.getPositionItem(position).persone.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                if (DatiRisposte.getPositionItem(position).persone.get(i).id_fb.equals(HelperFacebook.getFacebookId(context))) {
                    sb.append("Io");
                    vota.setVisibility(View.GONE);
                } else
                    sb.append(DatiRisposte.getPositionItem(position).persone.get(i).nome.split(" ")[0]);
            }
        }

        if (chiusa) {
            vota.setVisibility(View.VISIBLE);
            vota.setText(context.getString(R.string.RisposteAdapter));
        }

        final ProgressBar pb_vota = (ProgressBar) convertView.findViewById(R.id.pb_vota);
        vota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chiusa) {
                    EventoHelper.modificaGrafica(true);
                } else {
                    EventAsync.vota(idEvento, RisposteAdapter.this, vota, DatiRisposte.getPositionItem(position).id, position, pb_vota);
                    vota.setVisibility(View.GONE);
                }
            }
        });

        if (DatiRisposte.template != null && DatiRisposte.template.equals("sino")) {
            vota.setVisibility(View.GONE);
            temp_risposta += " " + (100 * DatiRisposte.getPositionItem(position).persone.size()) / Evento.numUtenti + "%";
        }
        risp.setText(temp_risposta);

        who.setText(sb.toString());
        who.setTextColor(Color.BLACK);

        return convertView;
    }

    public int getArg2() {
        return arg2;
    }
}
