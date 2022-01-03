package fr.polytech.larynxapp.controller.history;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.polytech.larynxapp.R;
import fr.polytech.larynxapp.model.Record;
import fr.polytech.larynxapp.model.database.DBManager;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FloatingActionButton floatingButton;

    /**
     * The UI list of the data
     */
    private ListView listview;

    /**
     * The list of record datas
     */
    private List<Record> records;

    private boolean informationShowable;

    /**
     * @param inflater Used to load the xml layout file as View
     * @param container A container component
     * @param savedInstanceState Used to save activity
     * @return Return a history's view object
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);      //Sets the view for the fragment
        initMap();

        // let know the different between a short an a long click
        informationShowable = true;

        //********************************Creation of the line chart*******************************/
        final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if(!records.isEmpty() ) {
            LineDataSet lineDataSet = new LineDataSet(dataValues(records.get(0)), records.get(0).getName());
            setLineData(lineDataSet);
            dataSets.add((lineDataSet));
            final LineData data = new LineData(dataSets);

        }


        //floating action button
        floatingButton = root.findViewById(R.id.floating_button);
        //floatingButton.setImageResource(R.drawable.ic_send);


        //***********************************Creation of the list**********************************/
        listview = root.findViewById(R.id.listViewRecords);
        final ListAdapter adapter = new ListAdapter(getActivity().getApplicationContext(),R.layout.liste_view_item, records);
        listview.setAdapter(adapter);

        listview.setSelector(R.color.transparent);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                showInformationDialog(position);
            }
        }
        );

        //the delete function
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, final long id) {
                informationShowable = false;
                AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                adb.setTitle("Supprimer l'enregistrement");
                adb.setMessage("Etes-vous sûr de vouloir supprimer cet enregistrement ?");
                adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        informationShowable = true;
                    }
                });
                adb.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Record record = records.get(position);
                        records.remove(record);
                        adapter.notifyDataSetChanged();
                        if(new DBManager(getContext()).deleteByName(record.getName())){
                            AlertDialog.Builder adb1=new AlertDialog.Builder(getContext());
                            adb1.setMessage("L'enregistrement a été supprimé avec succès.");
                            adb1.setNegativeButton("OK", null);
                            informationShowable = true;
                            adb1.show();
                        }

                    }});
                adb.show();

                listview.invalidate();
                return false;
            }
        });
        return root;
    }

    /**
     * Sets the data set's graphical parameters
     * @param lineDataSet the data set to configure
     */
    private void setLineData(LineDataSet lineDataSet){
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleHoleRadius(2.5f);
        lineDataSet.setValueTextSize(0f);
    }
    /**
     * Sets the data that will be shown in the chart
     * @param recordIn the record that contain shimmer and jitter data
     * @return the array list that will be shown
     */
    private ArrayList<Entry> dataValues(Record recordIn){
        ArrayList<Entry> dataVals = new ArrayList<>();
        dataVals.add(new Entry((float)recordIn.getJitter()*100, (float)recordIn.getShimmer()*100));
        return dataVals;
    }

    /**
     *  Initialisation of the data's map
     */
    private void initMap(){
        records = new DBManager(getContext()).query();
    }


    private void showInformationDialog(int position){
        if(informationShowable){
            final Dialog dialog = new Dialog(HistoryFragment.this.getContext());

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(true);

            dialog.setContentView(R.layout.custom_dialog_info);

            Record record = records.get(position);

            TextView textdate = dialog.findViewById(R.id.datetextvaluedialog);
            textdate.setText(record.getCommomName());

            View shimmerview = dialog.findViewById(R.id.shimmercolorview);
            TextView shimmervalue = dialog.findViewById(R.id.shimmerdialogvalue);
            shimmervalue.setText(((Double)round(record.getShimmer(),2)).toString());

            if(record.getShimmer()<0.3){
                shimmerview.setBackgroundColor(Color.GREEN);
            }else if(record.getShimmer()>= 0.3 && record.getShimmer()<= 0.4){
                shimmerview.setBackgroundColor(Color.YELLOW);
            }else{
                shimmerview.setBackgroundColor(Color.RED);
            }




            View jitterview = dialog.findViewById(R.id.jittercolorview);
            TextView jittervalue = dialog.findViewById(R.id.jitterdialogvalue);
            jittervalue.setText(((Double)round(record.getJitter(),2)).toString()+"%");

            if(record.getJitter()<1.5){
                jitterview.setBackgroundColor(Color.GREEN);
            }else if(record.getJitter()>= 1.5 && record.getJitter()<= 2.5){
                jitterview.setBackgroundColor(Color.YELLOW);
            }else{
                jitterview.setBackgroundColor(Color.RED);
            }

            Button cancelbttn = dialog.findViewById(R.id.cancel_button);
            cancelbttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
