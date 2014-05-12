package com.partymanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.partymanager.R;
import com.partymanager.helper.HelperConnessione;

public class EventDialog {

    Context context;
    Dialog dialog;
    EditText alto;
    EditText risposta;
    DatePicker date;
    Button close;
    CheckBox chiusura;
    TimePicker orario;

    private String idEvento;
    private static final int DIALOG_DATA = 1;
    private static final int DIALOG_ORARIO_E = 2;
    private static final int DIALOG_ORARIO_I = 3;
    private static final int DIALOG_LUOGO_I = 4;
    private static final int DIALOG_PERSONALLIZATA = 5;
    private static final int DIALOG_LUOGO_E = 6;
    private static final int DIALOG_SINO = 7;
    private Handler mResponseHandler;
    ProgressDialog progressDialog;

    public EventDialog(Context context, Handler reponseHandler, String idEvento) {
        this.context = context;
        this.mResponseHandler = reponseHandler;
        this.idEvento = idEvento;

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

                addDomanda(1, "Data Evento", idEvento, "data", temp);

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

                addDomanda(2, "Orario Evento", idEvento, "oraE", temp);
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

                addDomanda(3, "Orario Incontro", idEvento, "oraI", temp);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public Dialog luogoE() {
        dialog.setTitle("Luogo Evento");

        risposta.setText("");
        risposta.setHint("Scrivi qui il luogo");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("LUOGOSCELTO-E: ", risposta.getText().toString());

                if (!risposta.getText().toString().equals("")) {
                    addDomanda(4, "Luogo Evento", idEvento, "luogoE", risposta.getText().toString());
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public Dialog luogoI() {
        dialog.setTitle("Luogo Incontro");

        risposta.setText("");
        risposta.setHint("Scrivi qui il luogo");

        alto.setVisibility(View.GONE);
        risposta.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        orario.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("LUOGOSCELTO-I: ", risposta.getText().toString());

                if (!risposta.getText().toString().equals("")) {
                    addDomanda(4, "Luogo Incontro", idEvento, "luogoI", risposta.getText().toString());
                }

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
                    addDomanda(5, alto.getText().toString(), idEvento, null, risposta.getText().toString());
                }

                dialog.dismiss();

            }
        });

        return dialog;
    }

    public Dialog domanda_chiusa() {
    dialog.setTitle("Domanda SI/NO");

    alto.setVisibility(View.VISIBLE);
    risposta.setVisibility(View.GONE);
    date.setVisibility(View.GONE);
    orario.setVisibility(View.GONE);

    chiusura.setVisibility(View.GONE);
    alto.setHint("Scrivi qui la domanda");

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e("SI/NO: ", alto.getText().toString());
                if (!alto.getText().toString().equals("")) {
                    addDomanda(7, alto.getText().toString(), idEvento, null, "si");
                    dialog.dismiss();
                }
            }
        });

    return dialog;
}

    private void addDomanda(final int who, final String domanda, final String idEvento, final String template, final String risposta) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {

                InputMethodManager inputManager = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (((Activity) context).getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Creazione Domanda");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] name = {"domanda", "idEvento", "template", "risposta", "chiusa"};
                String chiusa = "0";
                /*
                if sono admin
                if cb.is selected
                chiusa = "1;
                 */
                String[] param = {domanda, idEvento, template, risposta, chiusa};

                Log.e("doinB inviati: ", domanda + " " + idEvento + " " + template + " " + risposta);

                String ris = HelperConnessione.httpPostConnection("http://androidpartymanager.herokuapp.com/addDomanda", name, param);

                Log.e("addDomanda-ris: ", ris);

                return ris;

            }

            @Override
            protected void onPostExecute(String ris) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (isInteger(ris)) {
                    Message m = new Message();
                    Bundle b = new Bundle();
                    switch (who) {
                        case DIALOG_DATA:
                            b.putInt("who", 1);
                            b.putString("data", risposta);
                            break;
                        case DIALOG_ORARIO_E:
                            b.putInt("who", 2);
                            b.putString("orario", risposta);
                            break;
                        case DIALOG_ORARIO_I:
                            b.putInt("who", 3);
                            b.putString("orario", risposta);
                            break;
                        case DIALOG_LUOGO_I:
                            b.putInt("who", 4);
                            b.putString("luogo", risposta);
                            break;
                        case DIALOG_LUOGO_E:
                            b.putInt("who", 6);
                            b.putString("luogo", risposta);
                            break;
                        case DIALOG_PERSONALLIZATA:
                            b.putInt("who", 5);
                            b.putString("pers-d", domanda);
                            b.putString("pers-r", risposta);
                            break;
                        case DIALOG_SINO:
                            b.putInt("who", 7);
                            b.putString("domanda", domanda);
                            break;
                    }
                    b.putBoolean("close", chiusura.isChecked());
                    m.setData(b);
                    mResponseHandler.sendMessage(m);
                }
            }
        }.execute(null, null, null);
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
