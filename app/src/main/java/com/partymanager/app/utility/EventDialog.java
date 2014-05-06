package com.partymanager.app.utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.partymanager.R;

import java.util.Calendar;

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

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                //String data = Integer.toString(date.getDayOfMonth()) + "/" +  Integer.toString(date.getMonth() + 1) + "/" +  Integer.toString(date.getYear());

                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();



                DatePickerDialog dialog = new DatePickerDialog(context,
                        new mDateSetListener(), today.year, today.month, today.monthDay);
                dialog.show();

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

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            Log.e("testDAta", Integer.toString(year) + " " +Integer.toString(monthOfYear) + " " +Integer.toString(dayOfMonth)  );


        }
    }

}
