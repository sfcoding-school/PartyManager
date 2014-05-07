package com.partymanager.app.utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.partymanager.R;

public class EventDialog {

    Context context;
    Dialog dialog;
    EditText alto;
    EditText risposta;
    EditText date;
    Button close;
    CheckBox chiusura;
    EditText orario;
    private Handler mResponseHandler;

    public EventDialog(Context context, Handler reponseHandler) {
        this.context = context;
        this.mResponseHandler = reponseHandler;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_domande);
        alto = (EditText) dialog.findViewById(R.id.editText2);
        risposta = (EditText) dialog.findViewById(R.id.editText_risposta);
        date = (EditText) dialog.findViewById(R.id.edt_data_evento);
        chiusura = (CheckBox) dialog.findViewById(R.id.cb_chiusura);
        close = (Button) dialog.findViewById(R.id.btn_close);
        orario = (EditText) dialog.findViewById(R.id.edt_orario_evento);


        //dialog.setCancelable(false); //toglie anche il click onBack

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCanceledOnTouchOutside(false);
    }

    public Dialog date() {

        dialog.setTitle("Scegli una data");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.GONE);
        date.setVisibility(View.VISIBLE);
        orario.setVisibility(View.VISIBLE);

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();
                DatePickerDialog dialog = new DatePickerDialog(context,
                        new mDateSetListener(), today.year, today.month, today.monthDay);
                dialog.show();

            }
        });

        orario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();
                TimePickerDialog dialog = new TimePickerDialog(context,
                        new mHourSetListener(), today.hour, today.minute, DateFormat.is24HourFormat(context));
                dialog.show();

            }
        });

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("DATASCELTA: ", date.getText().toString());
                Log.e("ORARIOSCELTO: ", orario.getText().toString());

                //TEST
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("what", date.getText().toString()); // for example
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

        risposta.setHint("Scrivi qui il luogo");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("LUOGOSCELTO: ", risposta.getText().toString());
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

        alto.setHint("Scrivi una domanda");
        risposta.setHint("Scrivi qui la tua risposta");

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("PERSONALIZZATA-DOMANDA: ", alto.getText().toString());
                Log.e("PERSONALIZZATA-RISPOSTA: ", risposta.getText().toString());
                dialog.dismiss();
            }
        });

        return dialog;
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            //Log.e("testDAta", Integer.toString(year) + " " + Integer.toString(monthOfYear) + " " + Integer.toString(dayOfMonth));

            date.setText(Integer.toString(dayOfMonth) + "/" + Integer.toString(monthOfYear + 1) + "/" + Integer.toString(year));
        }
    }

    class mHourSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(android.widget.TimePicker view,
                              int hourOfDay, int minute) {
            //Log.e("", "" + hourOfDay + ":" + minute);
            orario.setText("" + hourOfDay + ":" + minute);
        }
    }
}
