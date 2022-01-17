package fr.polytech.larynxapp.controller.jitter;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import fr.polytech.larynxapp.R;
import fr.polytech.larynxapp.model.Record;
import fr.polytech.larynxapp.model.database.DBManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JitterFragment extends Fragment {

    /**
     * The line chart where the jitter values will be shown
     */
    private LineChart jitterMpLineChart;

    /**
     * The list of record datas
     */
    private List<Record> records;

    /**
     * the reset Layout
     */
    private LinearLayout resetLayout;


    private LinearLayout startingDateLayout;
    private LinearLayout endingDateLayout;

    private LocalDate startdate;
    private LocalDate endingdate;

    private TextView textstartdate;
    private TextView textenddate;

    /**
     * @param inflater Used to load the xml layout file as View
     * @param container A container component
     * @param savedInstanceState Used to save activity
     * @return Return a Jitter's view object
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_jitter,container, false);    //Sets the view the the fragment


        records = new DBManager(getContext()).query();

        //Initialization of period for the chart
        startingDateLayout = root.findViewById(R.id.startingDate);
        endingDateLayout = root.findViewById(R.id.endingDate);
        textstartdate = root.findViewById(R.id.textstartdate);
        textenddate = root.findViewById(R.id.textenddate);

        setPeriodChart();

        resetLayout = root.findViewById(R.id.reset_date_layout);
        resetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                records = new DBManager(getContext()).query();
                setJitterChartData();

                startdate = null;
                endingdate = null;

                textenddate.setText("--/--/----");
                textstartdate.setText("--/--/----");
            }
        });
        //******************************Creation of the jitter's chart******************************/
        final TextView jitterTextView = root.findViewById(R.id.jitter_text_view);
        jitterTextView.setText("Analyse Jitter");
        jitterTextView.setTextSize(25f);

        jitterMpLineChart = root.findViewById(R.id.jitter_line_chart);
        setJitterChart(jitterMpLineChart);
        setJitterChartData();
        return root;
    }

    /**
     * set the jitter's data to the chart
     */
    public void setJitterChartData(){
        LineDataSet jitterLineSet = new LineDataSet(jitterDataValues(), "Jitter");
        jitterLineSet.setColor(Color.BLACK);
        jitterLineSet.setLineWidth(2f);
        jitterLineSet.setCircleColor(Color.BLACK);
        jitterLineSet.setCircleRadius(5f);
        jitterLineSet.setCircleHoleRadius(2.5f);
        jitterLineSet.setValueTextSize(0f);
        ArrayList<ILineDataSet> jitterDataSets = new ArrayList<>();
        jitterDataSets.add((jitterLineSet));

        ArrayList<Entry> point = new ArrayList<>();
        point.add(new Entry(0, 2.04f));
        LineDataSet pointLine = new LineDataSet(point,"Point");
        pointLine.setDrawCircles(false);
        pointLine.setValueTextSize(0f);
        jitterDataSets.add(pointLine);

        LimitLine jitterLl = new LimitLine(2.04f);
        jitterLl.setLabel("Limite jitter");
        jitterLl.setLineColor(Color.RED);
        jitterLl.enableDashedLine(10f, 10f, 0f);
        jitterMpLineChart.getAxisLeft().addLimitLine(jitterLl);

        XAxis jitterXAxis = jitterMpLineChart.getXAxis();
        jitterXAxis.setGranularity(1f);
        jitterXAxis.setSpaceMax(0.1f);
        jitterXAxis.setSpaceMin(0.1f);
        jitterXAxis.setAxisMinimum(-1f); // begin the chart at -1 (without we can't see the first value label)
        jitterXAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dateValues()));

        LineData jitterData = new LineData(jitterDataSets);
        jitterMpLineChart.setData(jitterData);
        jitterMpLineChart.invalidate();
        jitterMpLineChart.setDrawGridBackground(false);
    }

    /**
     * Initialisation of the jitter data's arraylist
     * @return the jitter data's arraylist
     */
    private ArrayList<Entry> jitterDataValues(){
        ArrayList<Entry> dataVals = new ArrayList<>();
        for(int i = 0; i < records.size(); i++) {
            dataVals.add(new Entry(i, (float) records.get(i).getJitter()));
        }
        return dataVals;
    }

    /**
     * Initialisation of the dates arraylist
     * @return the dates arraylist
     */
    private String[] dateValues(){
        ArrayList<String> dates = new ArrayList<>();
        for(int i = 0; i < records.size(); i++)
        {
            String strippedName = records.get(i).getName().replace("-", " ");
            String[] dateTimes = strippedName.split(" ");
            dates.add(i ,dateTimes[0] + "-" + dateTimes[1] + "-" + dateTimes[2]);
        }
        return dates.toArray(new String[0]);
    }


    /**
     * Set the graphic feature of the line charts for the jitter
     * @param chart the chart to be set
     */
    private void setJitterChart(LineChart chart){

        YAxis yAxis = chart.getAxisLeft();                  //The line chart's y axis
        XAxis xAxis = chart.getXAxis();                     //The line chart's x axis

        chart.getAxisRight().setEnabled(false);             //Disable the right axis

        //Set the y axis property
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setAxisMinimum(0f);
        yAxis.setSpaceTop(20f); // make the Y axis responsive
        yAxis.setTextSize(12f);
        PercentFormatter percentFormatter = new PercentFormatter();
        yAxis.setValueFormatter(percentFormatter);

        //Set the x axis property
        xAxis.setAxisLineWidth(2f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setLabelCount(4); // max number of visible labels on screen (without -> overlapping labels)

        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

    }

    /**
     * init the select date buttons
     */
    public void setPeriodChart(){

        startingDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String dateDay = String.valueOf(day);
                                String dateMonth = String.valueOf(month+1);
                                if(day < 10){
                                    dateDay = "0" + dateDay;
                                }
                                if(month < 10){
                                    dateMonth = "0" + dateMonth;
                                }


                                //Process the file name
                                String test = dateDay +"-" + dateMonth + "-" + year;

                                textstartdate.setText(dateDay+"/"+dateMonth+"/"+year);

                                startdate = LocalDate.parse(test, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                for(int i = 0; i < records.size(); i++){
                                    String tmp = records.get(i).getName().split(" ")[0];
                                    LocalDate tmpdate = LocalDate.parse(tmp, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                    if(tmpdate.isAfter(endingdate)){
                                        records.remove(i);
                                        i--;
                                    }
                                }

                                setJitterChartData();

                                if(records.size()==0){
                                    Toast.makeText(getContext(),"Aucun enregistrement lié à cette période ou période choisie incorrecte",Toast.LENGTH_LONG).show();
                                }
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });



        endingDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String dateDay = String.valueOf(day);
                                String dateMonth = String.valueOf(month+1);
                                if(day < 10){
                                    dateDay = "0" + dateDay;
                                }
                                if(month < 10){
                                    dateMonth = "0" + dateMonth;
                                }


                                //Process the file name
                                String test = dateDay +"-" + dateMonth + "-" + year;
                                textenddate.setText(dateDay+"/"+dateMonth+"/"+year);

                                endingdate = LocalDate.parse(test, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                for(int i = 0; i < records.size(); i++){
                                    String tmp = records.get(i).getName().split(" ")[0];
                                    LocalDate tmpdate = LocalDate.parse(tmp, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                    if(tmpdate.isAfter(endingdate)){
                                        records.remove(i);
                                        i--;
                                    }
                                }

                                setJitterChartData();

                                if(records.size()==0){
                                    Toast.makeText(getContext(),"Aucun enregistrement lié à cette période ou période choisie incorrecte",Toast.LENGTH_LONG).show();
                                }

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();

            }
        });




    }
}

