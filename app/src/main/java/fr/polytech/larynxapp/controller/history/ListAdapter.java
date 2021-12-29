package fr.polytech.larynxapp.controller.history;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.polytech.larynxapp.R;
import fr.polytech.larynxapp.model.Record;

public class ListAdapter extends ArrayAdapter<Record> {

    private int resourceLayout;
    private Context mContext;

    public ListAdapter(Context context, int resource, List<Record> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

         Record p = getItem(position);



        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.datetextvalue);
            View stv = (View) v.findViewById(R.id.shimmercolorview);
            View jtv = (View) v.findViewById(R.id.jittercolorview);



            if (tt1 != null) {
                tt1.setText(p.getCommomName());
            }

            if(p.getShimmer()<0.3){
                stv.setBackgroundColor(Color.GREEN);
            }else if(p.getShimmer()>= 0.3 && p.getShimmer()<= 0.4){
                stv.setBackgroundColor(Color.YELLOW);
            }else{
                stv.setBackgroundColor(Color.RED);
            }

            if(p.getJitter()<1.5){
                jtv.setBackgroundColor(Color.GREEN);
            }else if(p.getJitter()>= 1.5 && p.getJitter()<= 2.5){
                jtv.setBackgroundColor(Color.YELLOW);
            }else{
                jtv.setBackgroundColor(Color.RED);
            }

        }

        return v;
    }

}
