package com.partymanager.app.utility;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.partymanager.R;
import com.partymanager.app.helper.HelperDataParser;
import com.partymanager.gcm.Helper_Notifiche;

public class EventDialog {

    Context context;
    Dialog dialog;
    EditText alto;
    EditText risposta;
    DatePicker date;
    Button close;
    CheckBox chiusura;

    public EventDialog(Context context){
        this.context = context;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_domande);
        alto = (EditText) dialog.findViewById(R.id.editText2);
        risposta = (EditText) dialog.findViewById(R.id.editText_risposta);
        date = (DatePicker) dialog.findViewById(R.id.datePicker);
        chiusura = (CheckBox) dialog.findViewById(R.id.cb_chiusura);
        close = (Button) dialog.findViewById(R.id.btn_close);
    }

    public Dialog date(){

        dialog.setTitle("Scegli una data");

        alto.setVisibility(View.GONE);

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

        alto.setVisibility(View.GONE);
        date.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return dialog;
    }

    public Dialog personalizzata(){

        date.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return dialog;
    }

}
