package com.partymanager.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.partymanager.R;

public class EventDialog {

    Context context;
    Dialog dialog;
    EditText alto;
    EditText risposta;
    DatePicker date;
    Button close;
    CheckBox chiusura;
    TimePicker orario;

    private Handler mResponseHandler;

    public EventDialog(Context context, Handler reponseHandler) {
        this.context = context;
        this.mResponseHandler = reponseHandler;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_domande);
        alto = (EditText) dialog.findViewById(R.id.editText2);
        risposta = (EditText) dialog.findViewById(R.id.editText_risposta);
        date = (DatePicker) dialog.findViewById(R.id.datePicker);
        chiusura = (CheckBox) dialog.findViewById(R.id.cb_chiusura);
        close = (Button) dialog.findViewById(R.id.btn_close);
        orario = (TimePicker) dialog.findViewById(R.id.timePicker);

        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //per tutto schermo
        dialog.setCanceledOnTouchOutside(false);
    }

    public Dialog date() {

        dialog.setTitle("Scegli una data per l'Evento");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.GONE);
        date.setVisibility(View.VISIBLE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String temp = Integer.toString(date.getDayOfMonth()) + "/" + Integer.toString(date.getMonth() + 1) + "/" + Integer.toString(date.getYear());
                Log.e("DATASCELTA: ", temp);

                //TEST
                Message m = new Message();
                Bundle b = new Bundle();
                b.putInt("who", 1);
                b.putBoolean("close", chiusura.isChecked());
                b.putString("data", temp); // for example
                m.setData(b);

                mResponseHandler.sendMessage(m);
                //END TEST

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public Dialog orarioE() {

        dialog.setTitle("Orario Evento");

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

                //TEST
                Message m = new Message();
                Bundle b = new Bundle();
                b.putInt("who", 2);
                b.putBoolean("close", chiusura.isChecked());
                b.putString("orario", temp); // for example
                m.setData(b);

                mResponseHandler.sendMessage(m);
                //END TEST

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public Dialog orarioI() {

        dialog.setTitle("Orario Incontro");

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

                //TEST
                Message m = new Message();
                Bundle b = new Bundle();
                b.putInt("who", 3);
                b.putBoolean("close", chiusura.isChecked());
                b.putString("orario", temp); // for example
                m.setData(b);

                mResponseHandler.sendMessage(m);
                //END TEST

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public Dialog luogo() {
        dialog.setTitle("Scegli una luogo");

        risposta.setText("");
        risposta.setHint("Scrivi qui il luogo");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("LUOGOSCELTO: ", risposta.getText().toString());

                if (!risposta.getText().toString().equals("")) {
                    //TEST
                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putInt("who", 4);
                    b.putBoolean("close", chiusura.isChecked());
                    b.putString("luogo", risposta.getText().toString()); // for example
                    m.setData(b);

                    mResponseHandler.sendMessage(m);
                }
                //END TEST

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public Dialog personalizzata() {

        alto.setVisibility(View.VISIBLE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        dialog.setTitle("Personalizzata");

        alto.setText("");
        risposta.setText("");
        alto.setHint("Scrivi una domanda");
        risposta.setHint("Scrivi qui la tua risposta");

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("PERSONALIZZATA-DOMANDA: ", alto.getText().toString());
                Log.e("PERSONALIZZATA-RISPOSTA: ", risposta.getText().toString());

                if (!alto.getText().toString().equals("")) {
                    //TEST
                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putInt("who", 5);
                    b.putBoolean("close", chiusura.isChecked());
                    b.putString("pers-d", alto.getText().toString());
                    b.putString("pers-r", risposta.getText().toString());
                    m.setData(b);

                    mResponseHandler.sendMessage(m);
                }
                //END TEST

                dialog.dismiss();
            }
        });

        return dialog;
    }
}
