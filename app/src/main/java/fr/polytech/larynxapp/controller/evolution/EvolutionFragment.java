package fr.polytech.larynxapp.controller.evolution;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EvolutionFragment extends Fragment {

    /**
     * The line chart where the shimmer values will be shown
     */
    private LineChart shimmerMpLineChart;


    /**
     * The list of record datas
     */
    private List<Record> records;

    /**
     * The startDate Button
     */
    private ImageButton startDateButton;


    /**
     * The startDate
     */
    private int startDateDay;
    private int startDateMonth;
    private int startDateYear;

    /**
     * The endDate
     */
    private int endDateDay;
    private int endDateMonth;
    private int endDateYear;


    private String[] dateValues;

    private ImageButton resetButton;

    /**
     * The datePicker
     */
    private DatePicker datePicker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_shimmer, container, false);    //Sets the view the the fragment


        records = new DBManager(getContext()).query();

        startDateButton = root.findViewById(R.id.startDate);
        resetButton = root.findViewById(R.id.resetDate);
        dateValues = dateValues();
        initDateButton();
        //******************************Creation of the shimmer's chart*****************************/
        final TextView shimmerTextView = root.findViewById(R.id.shimmer_text_view);
        shimmerTextView.setText("Shimmer");
        shimmerTextView.setTextSize(20f);


        shimmerMpLineChart = root.findViewById(R.id.shimmer_line_chart);
        setShimmerChart(shimmerMpLineChart);
        setShimmerChartData();
        return root;
    }

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

        LimitLine shimmerLl = new LimitLine(0.35f);
        shimmerLl.setLabel("Limite shimmer");
        shimmerLl.setLineColor(Color.RED);
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
     * Set the graphic feature of the line charts for the shimmer
     * @param chart the chart to be set
     */
    private void setShimmerChart(LineChart chart){

        YAxis yAxis = chart.getAxisLeft();                  //The line chart's y axis
        XAxis xAxis = chart.getXAxis();                     //The line chart's x axis
        chart.getAxisLeft().setEnabled(true);

        chart.getAxisRight().setEnabled(false);             //Disable the right axis

        //Set the y axis property
        yAxis.setAxisLineWidth(0.2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);
        yAxis.setTextSize(12f);

        //Set the x axis property
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);

        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(true);
        chart.setTouchEnabled(false);
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
        yAxis.setAxisMaximum(3f);
        yAxis.setTextSize(12f);
        PercentFormatter percentFormatter = new PercentFormatter();
        yAxis.setValueFormatter(percentFormatter);

        //Set the x axis property
        xAxis.setAxisLineWidth(2f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);

        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.setScaleEnabled(true);
        chart.setTouchEnabled(false);
    }

    public void initDateButton() {
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                records = new DBManager(getContext()).query();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                setStartDateYear(year);
                                setStartDateMonth(month+1);
                                setStartDateDay(day);
                                String dateDay = String.valueOf(day);
                                String dateMonth = String.valueOf(month+1);
                                if(day < 10){
                                    dateDay = "0" + dateDay;
                                }
                                if(month < 10){
                                    dateMonth = "0" + dateMonth;
                                }

                                String test = dateDay +"-" + dateMonth + "-" + year;

                                for(int i = 0; i < records.size(); i++){
                                    System.out.println("tmp");
                                    String tmp = records.get(i).getName().split(" ")[0];
                                    System.out.println(tmp);
                                    System.out.println("test");
                                    System.out.println(test);
                                    if(!test.equals(tmp)){
                                        records.remove(i);
                                        i--;
                                    }
                                }
                                System.out.println("～～～～～～～～～～");
                                for(int i = 0; i < records.size(); i++){
                                    System.out.println("records.get(i).getName()");
                                    System.out.println(records.get(i).getName());
                                }
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setShimmerChartData();
//                view.invalidate();
                for(int i = 0; i < records.size(); i ++){
                    System.out.println(records.get(i).getName());
                }
                System.out.println("view has been invalidate");
            }
        });

    }

    public int getStartDateYear() {
        return startDateYear;
    }

    public void setStartDateYear(int startDateYear) {
        this.startDateYear = startDateYear;
    }

    public int getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(int startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public int getStartDateDay() {
        return startDateDay;
    }

    public void setStartDateDay(int startDateDay) {
        this.startDateDay = startDateDay;
    }

    public int getEndDateDay() {
        return endDateDay;
    }

    public void setEndDateDay(int endDateDay) {
        this.endDateDay = endDateDay;
    }

    public int getEndDateMonth() {
        return endDateMonth;
    }

    public void setEndDateMonth(int endDateMonth) {
        this.endDateMonth = endDateMonth;
    }

    public int getEndDateYear() {
        return endDateYear;
    }

    public void setEndDateYear(int endDateYear) {
        this.endDateYear = endDateYear;
    }

    public String[] getDateValues() {
        return dateValues;
    }

    public void setDateValues(String[] dateValues) {
        this.dateValues = dateValues;
    }
}

