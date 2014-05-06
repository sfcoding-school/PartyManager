package com.partymanager.app.utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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

    public EventDialog(Context context){
        this.context = context;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_domande);
        alto = (EditText) dialog.findViewById(R.id.editText2);
        risposta = (EditText) dialog.findViewById(R.id.editText_risposta);
        //date = (DatePicker) dialog.findViewById(R.id.datePicker);
        chiusura = (CheckBox) dialog.findViewById(R.id.cb_chiusura);
        close = (Button) dialog.findViewById(R.id.btn_close);
        //orario = (TimePicker) dialog.findViewById(R.id.timePicker);
        //orario.setIs24HourView(true);

        //dialog.setCancelable(false); //toglie anche il click onBack
        dialog.setCanceledOnTouchOutside(false);
    }

    public Dialog date(){

        dialog.setTitle("Scegli una data");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.GONE);
        //date.setVisibility(View.VISIBLE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = Integer.toString(date.getDayOfMonth()) + "/" +  Integer.toString(date.getMonth() + 1) + "/" +  Integer.toString(date.getYear());


                    }
                });

        return dialog;
    }

    public Dialog luogo(){
        dialog.setTitle("Scegli una luogo");

        risposta.setHint("Scrivi qui il luogo");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        //date.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return dialog;
    }

    public Dialog personalizzata(){

        alto.setVisibility(View.VISIBLE);
        risposta.setVisibility(View.VISIBLE);
        //date.setVisibility(View.GONE);
        dialog.setTitle("Personalizzata");

        alto.setHint("Scrivi una domanda");
        risposta.setHint("Scrivi qui la tua risposta");

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return dialog;
    }

}
