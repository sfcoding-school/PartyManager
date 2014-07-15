package com.partymanager.EventSupport;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.partymanager.R;
import com.partymanager.data.DatiAttributi;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class EventDomanda {

    Context context;
    Dialog dialog;
    EditText alto;
    EditText risposta;
    DatePicker date;
    Button close;
    CheckBox chiusura;
    TimePicker orario;
    Spinner sp;

    private int idEvento;
    public static Handler mResponseHandler;
    private boolean first;
    ArrayList<String> list;

    public EventDomanda(final Context context, Handler reponseHandler, int idEvento, String adminEvento) {
        this.context = context;
        mResponseHandler = reponseHandler;
        this.idEvento = idEvento;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_domanda_2);
        alto = (EditText) dialog.findViewById(R.id.editText2);
        risposta = (EditText) dialog.findViewById(R.id.editText_risposta);
        date = (DatePicker) dialog.findViewById(R.id.datePicker);
        chiusura = (CheckBox) dialog.findViewById(R.id.cb_chiusura);
        close = (Button) dialog.findViewById(R.id.btn_close);
        orario = (TimePicker) dialog.findViewById(R.id.timePicker);

        if (!HelperFacebook.getFacebookId(context).equals(adminEvento)) {
            chiusura.setVisibility(View.GONE);
        } else {
            chiusura.setVisibility(View.VISIBLE);
        }

        dialog.setCanceledOnTouchOutside(true);

        sp = (Spinner) dialog.findViewById(R.id.spinner);
        renderSpinner();
        first = false;

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (!first) {
                    String Text = sp.getSelectedItem().toString().replace(" ", "").replace("/", "");
                    which(Text, arg2);
                } else {
                    first = true;
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public enum Template {
        DataEvento,
        OrarioEvento,
        OrarioIncontro,
        LuogoEvento,
        LuogoIncontro,
        Personalizzata,
        DomandaSINO,
    }

    public void which(String selectItem, int pos) {
        chiusura.setChecked(false);
        Template quale = Template.valueOf(selectItem);

        switch (quale) {
            case Personalizzata:
                personalizzata();
                break;
            case LuogoIncontro:
                luogoI();
                break;
            case LuogoEvento:
                luogoE();
                break;
            case DataEvento:
                date();
                break;
            case OrarioIncontro:
                orarioI();
                break;
            case OrarioEvento:
                orarioE();
                break;
            case DomandaSINO:
                domanda_chiusa();
                break;
        }


        if (pos == -1 || list.size() != 6) {
            for (int i = 0; i < list.size(); i++) {
                if ((list.get(i).replace(" ", "").replace("/", "")).equals(selectItem)) {
                    pos = i;
                    break;
                }
            }
        }

        sp.setSelection(pos);
    }

    public void renderSpinner() {

        String[] template = DatiAttributi.getTemplate();

        list = new ArrayList<String>();

        list.add(context.getString(R.string.pers));
        list.add(context.getString(R.string.dmndChiusa));
        list.add(context.getString(R.string.orarioI));

        if (template[2] == null)
            list.add(context.getString(R.string.luogoI));

        if (template[1] == null)
            list.add(context.getString(R.string.luogoE));

        if (template[0] == null)
            list.add(context.getString(R.string.dataE));

        if (template[3] == null)
            list.add(context.getString(R.string.orarioE));

        ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);

        sp.setAdapter(testAdapter);
    }

    public Dialog returnD() {
        return dialog;
    }

    // <editor-fold defaultstate="collapsed" desc="Dialog Data">
    public void date() {

        dialog.setTitle(R.string.dataE);

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.GONE);
        date.setVisibility(View.VISIBLE);
        orario.setVisibility(View.GONE);

        EventoHelper.setMinDate(date, new GregorianCalendar().getTimeInMillis() - 1000);


        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String temp = Integer.toString(date.getDayOfMonth()) + "/" + Integer.toString(date.getMonth() + 1) + "/" + Integer.toString(date.getYear());
                Log.e("DATASCELTA: ", temp);

                EventAsync.addDomanda(chiusura.isChecked(), context, 1, "Data Evento", idEvento, "data", temp);

                dialog.dismiss();
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dialog Orario Evento">
    public void orarioE() {

        dialog.setTitle(R.string.orarioE);

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.VISIBLE);

        orario.setIs24HourView(true);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String temp = Integer.toString(orario.getCurrentHour()) + ":" + Integer.toString(orario.getCurrentMinute());
                Log.e("ORARIOSCELTO: ", temp);

                EventAsync.addDomanda(chiusura.isChecked(), context, 2, "Orario Evento", idEvento, "oraE", temp);
                dialog.dismiss();
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dialog Orario Incontro">
    public void orarioI() {

        dialog.setTitle(R.string.orarioI);

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.VISIBLE);

        orario.setIs24HourView(true);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String temp = Integer.toString(orario.getCurrentHour()) + ":" + Integer.toString(orario.getCurrentMinute());
                Log.e("ORARIOSCELTO: ", temp);

                EventAsync.addDomanda(chiusura.isChecked(), context, 3, "Orario Incontro", idEvento, "oraI", temp);
                dialog.dismiss();
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dialog Luogo Evento">
    public void luogoE() {
        dialog.setTitle(R.string.luogoE);

        risposta.setText("");
        risposta.setHint(R.string.luogoEHint);

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("LUOGOSCELTO-E: ", risposta.getText().toString());

                if (!risposta.getText().toString().equals("")) {
                    EventAsync.addDomanda(chiusura.isChecked(), context, 6, "Luogo Evento", idEvento, "luogoE", risposta.getText().toString());
                }
                dialog.dismiss();
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dialog Luogo Incontro">
    public void luogoI() {
        dialog.setTitle(R.string.luogoI);

        risposta.setText("");
        risposta.setHint(R.string.luogoEHint);

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("LUOGOSCELTO-I: ", risposta.getText().toString());

                if (!risposta.getText().toString().equals("")) {
                    EventAsync.addDomanda(chiusura.isChecked(), context, 4, "Luogo Incontro", idEvento, "luogoI", risposta.getText().toString());
                }

                dialog.dismiss();
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dialog Personalizzata">
    public void personalizzata() {

        alto.setVisibility(View.VISIBLE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        dialog.setTitle(R.string.pers);

        alto.setText("");
        risposta.setText("");
        alto.setHint(R.string.persDH);
        risposta.setHint(R.string.persRH);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("PERSONALIZZATA-DOMANDA: ", alto.getText().toString());
                Log.e("PERSONALIZZATA-RISPOSTA: ", risposta.getText().toString());

                if (!alto.getText().toString().equals("")) {
                    EventAsync.addDomanda(chiusura.isChecked(), context, 5, alto.getText().toString(), idEvento, "", risposta.getText().toString());
                }

                dialog.dismiss();

            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dialog Domanda Chiusa">
    public void domanda_chiusa() {
        dialog.setTitle(R.string.dmndChiusa);

        alto.setVisibility(View.VISIBLE);
        risposta.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        alto.setHint(R.string.persDH);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e("SI/NO: ", alto.getText().toString());
                if (!alto.getText().toString().equals("")) {
                    EventAsync.addDomanda(chiusura.isChecked(), context, 7, alto.getText().toString(), idEvento, "sino", "si");
                    dialog.dismiss();
                }
            }
        });
    }
    // </editor-fold>
}
