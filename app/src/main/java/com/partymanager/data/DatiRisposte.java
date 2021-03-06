
package com.partymanager.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.partymanager.data.Adapter.RisposteAdapter;
import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperFacebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DatiRisposte {

    private static RisposteAdapter eAdapter;
    private static ArrayList<Risposta> ITEMS = new ArrayList<Risposta>();
    private static SparseArray<Risposta> MAP = new SparseArray<Risposta>();
    public static String template = null;
    private static Context context_global;
    private static int id_attr_global;

    public static RisposteAdapter init(Context context, int id_evento, int id_attr, int arg2, boolean chiusa) {
        context_global = context;
        eAdapter = new RisposteAdapter(id_evento, context, DatiRisposte.ITEMS, id_attr, arg2, chiusa);
        DataProvide.getRisposte(id_evento, id_attr, context);
        id_attr_global = id_attr;
        return eAdapter;
    }

    public static void removeAll(int id_evento, int id_attributo) {
        toJson(new ArrayList<Risposta>(ITEMS), id_evento, id_attributo);
        id_attr_global = -1;
        removeAll();
    }

    public static int getIdAttributo() {
        return id_attr_global;
    }

    public static void notifyDataChange() {
        if (eAdapter != null) eAdapter.notifyDataSetChanged();
    }

    public static void removeAll() {
        template = null;
        ITEMS.removeAll(ITEMS);
        MAP = new SparseArray<Risposta>();
        eAdapter.notifyDataSetChanged();
    }


    private static void toJson(final ArrayList<Risposta> ITEMS_temp, final int id_evento, final int id_attributo) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                JSONArray jsonArr = new JSONArray();
                try {
                    for (Risposta aITEMS_temp : ITEMS_temp) {
                        JSONObject pnObj = new JSONObject();
                        pnObj.put("id_risposta", aITEMS_temp.id);
                        pnObj.put("risposta", aITEMS_temp.risposta);
                        //pnObj.put("template", aITEMS_temp.template);

                        JSONArray userL = new JSONArray();
                        for (int j = 0; j < aITEMS_temp.persone.size(); j++) {
                            JSONObject pers = new JSONObject();
                            pers.put("id_user", aITEMS_temp.persone.get(j).id_fb);
                            pers.put("name", aITEMS_temp.persone.get(j).nome);
                            userL.put(pers);
                        }
                        pnObj.put("userList", userL);
                        jsonArr.put(pnObj);
                    }
                } catch (JSONException e) {
                    Log.e("DatiRisposte-toJson", "JSONException " + e);
                    return null;
                } catch (NullPointerException e) {
                    Log.e("DatiRisposte-toJson", "NullPointerException " + e);
                    return null;
                }
                return jsonArr;
            }

            @Override
            protected void onPostExecute(JSONArray js) {
                Log.e("DatiRisposte-toJson-onPost", js.toString());
                if (js != null && js.length() > 0) {
                    DataProvide.saveJson(js, "risposte_" + id_evento + "_" + id_attributo, context_global);
                } else {
                    Log.e("DatiRisposte-toJson", "Non ho salvato array vuoto");
                }
            }
        }.execute(null, null, null);
    }

    public static Risposta getPositionItem(int position) {
        return ITEMS.get(position);
    }

    public static int getLenght() {
        return ITEMS.size();
    }

    public static void addItem(Risposta item, String template, boolean controllo) {
        if (controllo) cercami();
        DatiRisposte.template = template;
        addItem(item);
    }

    public static void addItem(Risposta item, boolean controllo) {
        if (controllo) cercami();
        addItem(item);
    }

    public static void addItem(Risposta item) {
        ITEMS.add(item);
        MAP.put(item.id, item);
        cercaVotata();
        eAdapter.notifyDataSetChanged();
    }

    public static void addItemNoNotify(Risposta item, String template, boolean controllo) {
        DatiRisposte.template = template;
        addItemNoNotify(item, controllo);
        cercaVotata(false);
    }

    public static void addItemNoNotify(Risposta item, boolean controllo) {
        if (controllo) cercami();
        ITEMS.add(item);
        MAP.put(item.id, item);
        cercaVotata(false);
    }


    public static void ordina() {
        Collections.sort(ITEMS, comparator);
    }


    private static Comparator<Risposta> comparator = new Comparator<Risposta>() {
        @Override
        public int compare(Risposta item1, Risposta item2) {
            Integer numr1 = item1.persone.size();
            Integer numr2 = item2.persone.size();
            Integer id1 = item1.id;
            Integer id2 = item2.id;

            if (numr1.compareTo(numr2) > 0)
                return -1;
            else if (numr1.compareTo(numr2) == 0)
                return id2.compareTo(id1);
            else
                return 1;
        }
    };

    public static void removePositionItem(int pos, boolean notify) {
        int i = ITEMS.get(pos).id;
        ITEMS.remove(pos);
        MAP.remove(i);
        cercaVotata();
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void removePositionItem(int pos) {
        removePositionItem(pos, true);
    }

    public static void removeIdItem(int idRisposta, boolean notify) {
        ITEMS.remove(MAP.get(idRisposta));
        MAP.remove(idRisposta);
        cercaVotata();
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void removeIdItem(int idRisposta) {
        removeIdItem(idRisposta, true);
    }

    public static void modificaRisposta(int pos, String nuova) {
        ITEMS.get(pos).risposta = nuova;
        eAdapter.notifyDataSetChanged();
    }

    public static void addIdPersona(int idRisposta, String idUser, String name, boolean controllo, boolean notify) {
        if (controllo) cercami();
        MAP.get(idRisposta).addPersona(new Persona(idUser, name));
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void addIdPersona(int idRisposta, String idUser, String name, boolean controllo) {
        addIdPersona(idRisposta, idUser, name, controllo, true);
    }

    public static void addPositionPersona(int position, String idUser, String name, boolean controllo) {
        if (controllo) cercami();
        ITEMS.get(position).addPersona(new Persona(idUser, name));
        eAdapter.notifyDataSetChanged();
    }

    public static Risposta getIdItem(int idRisposta) {
        return MAP.get(idRisposta);
    }

    public static void cercaVotata(boolean notify) {
        int max_persone = -1, id_risposta = -1;
        String nuovaRisposta = null;

        for (int i = 0; i < DatiRisposte.ITEMS.size(); i++) {
            int temp = 0;
            for (int j = 0; DatiRisposte.ITEMS.get(i).persone != null && j < DatiRisposte.ITEMS.get(i).persone.size(); j++) {
                temp++;
            }
            if (temp > max_persone) {
                max_persone = temp;
                id_risposta = DatiRisposte.ITEMS.get(i).id;
                nuovaRisposta = DatiRisposte.ITEMS.get(i).risposta;
            }
        }

        DatiAttributi.setNuovaRisposta(id_attr_global, max_persone, String.valueOf(id_risposta), nuovaRisposta, notify);

    }

    public static void cercaVotata() {
        cercaVotata(true);
    }

    private static void cercami() {
        Boolean trovato = false;
        for (int i = 0; i < DatiRisposte.ITEMS.size() && !trovato; i++) {
            for (int j = 0; DatiRisposte.ITEMS.get(i).persone != null && j < DatiRisposte.ITEMS.get(i).persone.size() && !trovato; j++) {
                if (DatiRisposte.ITEMS.get(i).persone.get(j).id_fb.equals(HelperFacebook.getFacebookId(context_global))) {
                    DatiRisposte.ITEMS.get(i).persone.remove(j);
                    trovato = true;
                }
            }
        }
    }

    public static class Risposta {
        public int id;
        public String risposta;
        public List<Persona> persone;

        public Risposta(int id, String risposta, JSONArray userList) {
            this.id = id;
            this.risposta = risposta;
            this.persone = creaLista(userList);
        }

        public Risposta(int id, String risposta, List<Persona> userList) {
            this.id = id;
            this.risposta = risposta;
            this.persone = userList;
        }

        private List<Persona> creaLista(JSONArray userList) {
            List<Persona> list = new ArrayList<Persona>();
            for (int i = 0; i < userList.length(); i++) {
                try {
                    list.add(new Persona(userList.getJSONObject(i).getString("id_user"), userList.getJSONObject(i).getString("name")));

                } catch (JSONException e) {
                    Log.e("DatiRisposte-creaLista", "error creaLista " + e);
                    return new ArrayList<Persona>();
                }
            }
            return list;
        }

        public void addPersona(Persona item) {
            persone.add(item);
            cercaVotata();
            eAdapter.notifyDataSetChanged();
        }
    }

    public static class Persona {
        public String id_fb;
        public String nome;

        public Persona(String id_fb, String nome) {
            this.id_fb = id_fb;
            this.nome = nome;
        }
    }
}

