package fr.polytech.larynxapp.controller.Shimmer;

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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShimmerFragment extends Fragment {

    /**
     * The line chart where the shimmer values will be shown
     */
    private LineChart shimmerMpLineChart;


    /**
     * The list of record datas
     */
    private List<Record> records;

    /**
     * to store the data
     */
    private String[] dateValues;

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
     * @return Return a evolution'sview object
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Sets the view the the fragment
        final View root = inflater.inflate(R.layout.fragment_shimmer, container, false);

        //Read data from the database
        records = new DBManager(getContext()).query();

        //Initialization module
        dateValues = dateValues();

        for(String str : dateValues){
            System.out.println(str);
        }


        //Initialization of period for the chart
        startingDateLayout = root.findViewById(R.id.startingDate);
        endingDateLayout = root.findViewById(R.id.endingDate);
        textstartdate = root.findViewById(R.id.textstartdate);
        textenddate = root.findViewById(R.id.textenddate);


        setPeriodChart();

        //initDateButton();
        resetLayout = root.findViewById(R.id.reset_date_layout);
        resetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                records = new DBManager(getContext()).query();
                setShimmerChartData();

                textenddate.setText("--/--/----");
                textstartdate.setText("--/--/----");
            }
        });

        //******************************Creation of the shimmer's chart*****************************/
        final TextView shimmerTextView = root.findViewById(R.id.shimmer_text_view);
        shimmerTextView.setText("Analyse Shimmer");
        shimmerTextView.setTextSize(25f);
        shimmerMpLineChart = root.findViewById(R.id.shimmer_line_chart);
        setShimmerChart(shimmerMpLineChart);
        setShimmerChartData();
        return root;
    }

    /**
     * Set the data into the chart.
     */
    public void setShimmerChartData(){
        LineDataSet shimmerLineSet = new LineDataSet(shimmerDataValues(), "Shimmer");
        shimmerLineSet.setColor(Color.BLACK);
        shimmerLineSet.setLineWidth(2f);
        shimmerLineSet.setCircleColor(Color.BLACK);
        shimmerLineSet.setCircleRadius(5f);
        shimmerLineSet.setCircleHoleRadius(2.5f);
        shimmerLineSet.setValueTextSize(0f);
        ArrayList<ILineDataSet> shimmerDataSets = new ArrayList<>();
        shimmerDataSets.add((shimmerLineSet));

        ArrayList<Entry> point = new ArrayList<>();
        point.add(new Entry(0, 0.35f));
        LineDataSet pointLine = new LineDataSet(point,"Point");
        pointLine.setValueTextSize(0f);
        pointLine.setDrawCircles(false);
        shimmerDataSets.add(pointLine);

        // Ligne de limite du Shimmer
        LimitLine shimmerLl = new LimitLine(0.35f);
        shimmerLl.setLabel("Limite shimmer");
        shimmerLl.setLineColor(Color.RED);
        shimmerLl.enableDashedLine(10f, 10f, 0f);
        shimmerMpLineChart.getAxisLeft().addLimitLine(shimmerLl);

        XAxis shimmerXAxis = shimmerMpLineChart.getXAxis();
        shimmerXAxis.setGranularity(1f);
        shimmerXAxis.setSpaceMax(0.1f);
        shimmerXAxis.setSpaceMin(0.1f);
        shimmerXAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dateValues()));


        LineData shimmerData = new LineData(shimmerDataSets);
        shimmerMpLineChart.setData(shimmerData);
        shimmerMpLineChart.invalidate();
        shimmerMpLineChart.setDrawGridBackground(false);
    }

    /**
     * Initialisation of the shimmer data's arraylist
     * @return the shimmer data's arraylist
     */
    private ArrayList<Entry> shimmerDataValues(){
        ArrayList<Entry> dataVals = new ArrayList<>();
        for(int i = 0; i < records.size(); i++) {
            dataVals.add(new Entry(i, (float) records.get(i).getShimmer()));
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
     * Set the graphic feature of the line charts for the shimmer
     * @param chart the chart to be set
     */
    private void setShimmerChart(LineChart chart){


        YAxis yAxis = chart.getAxisLeft();                  //The line chart's y axis
        XAxis xAxis = chart.getXAxis();                     //The line chart's x axis
        chart.getAxisLeft().setEnabled(true);

        chart.getAxisRight().setEnabled(false);             //Disable the right axis

        //Set the y axis property
        yAxis.setAxisLineWidth(1.5f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(12f);
        yAxis.setSpaceTop(20f); // make the Y axis responsive

        //Set the x axis property
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setAxisMinimum(-1f); // begin the chart at -1 (without we can't see the first value label)
        xAxis.setLabelCount(4); // max number of visible labels on screen (without -> overlapping labels)

        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(true);
        chart.setTouchEnabled(true);
        chart.setScaleYEnabled(false);

    }

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

                                    if(tmpdate.isBefore(startdate)){
                                        records.remove(i);
                                        i--;
                                    }
                                }

                                setShimmerChartData();

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

                                setShimmerChartData();

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

