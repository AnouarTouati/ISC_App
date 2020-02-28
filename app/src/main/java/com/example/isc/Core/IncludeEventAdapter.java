package com.example.isc.Core;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.isc.R;

import java.util.List;


public class IncludeEventAdapter extends ArrayAdapter<String> {

    private int resourceLayout;
    private Context mContext;
    ImageView eventCheck;
    TextView eventName;
    String myEvent;
    LinearLayout eventLL;

    public IncludeEventAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(resourceLayout, null);
            IEAViewHolder holder = createViewHolderFrom(view);
            view.setTag(holder);
        }

        final IEAViewHolder holder = (IEAViewHolder) view.getTag();

        myEvent = getItem(position);

        if(myEvent != null) {
            holder.text.setText(myEvent);
        }
        holder.event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.icon.getDrawable()!=null){
                    holder.text.setText(Html.fromHtml("<font color=\"#000000\">"+ holder.text.getText() +"</font>"));
                    holder.icon.setImageDrawable(null);
                    IncludeEventActivity.includedEvents.remove(holder.text.getText().toString());
                }else{
                    holder.text.setText(Html.fromHtml("<font color=\"#1976D2\">"+ holder.text.getText() +"</font>"));
                    holder.icon.setImageResource(R.drawable.ic_check_24dp);
                    IncludeEventActivity.includedEvents.add(holder.text.getText().toString());
                }
            }
        });

        return view;
    }


    private IEAViewHolder createViewHolderFrom(View view) {
        eventName = view.findViewById(R.id.eventName);
        eventCheck = view.findViewById(R.id.eventCheck);
        eventLL = view.findViewById(R.id.eventLL);
        return new IEAViewHolder(eventName, eventCheck, eventLL);
    }
}

class IEAViewHolder {
    final TextView text;
    final ImageView icon;
    final LinearLayout event;

    IEAViewHolder(TextView text, ImageView icon, LinearLayout event) {
        this.text = text;
        this.icon = icon;
        this.event = event;
    }
}

