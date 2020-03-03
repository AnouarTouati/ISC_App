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


public class PostLevelAdapter extends ArrayAdapter<String> {

    private int resourceLayout;
    private Context mContext;
    private  ImageView departmentCheck;
    private TextView departmentName;
    private String myDepartment;
    private  LinearLayout departmentLL;

    public PostLevelAdapter(Context context, int resource, List<String> items) {
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
            PLAViewHolder holder = createViewHolderFrom(view);
            view.setTag(holder);
        }

        final PLAViewHolder holder = (PLAViewHolder) view.getTag();

        myDepartment = getItem(position);

        if(myDepartment != null) {
            holder.text.setText(myDepartment);
        }
        holder.departmentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.icon.getDrawable()!=null){
                    holder.text.setText(Html.fromHtml("<font color=\"#000000\">"+ holder.text.getText() +"</font>"));
                    holder.icon.setImageDrawable(null);
                    PostLevelActivity.includedDepartments.remove(holder.text.getText().toString());
                }else{
                    holder.text.setText(Html.fromHtml("<font color=\"#1976D2\">"+ holder.text.getText() +"</font>"));
                    holder.icon.setImageResource(R.drawable.ic_check_24dp);
                    PostLevelActivity.includedDepartments.add(holder.text.getText().toString());
                }
            }
        });

        return view;
    }


    private PLAViewHolder createViewHolderFrom(View view) {
        departmentName = view.findViewById(R.id.departmentName);
        departmentCheck = view.findViewById(R.id.departmentCheck);
        departmentLL = view.findViewById(R.id.departmentLL);
        return new PLAViewHolder(departmentName, departmentCheck, departmentLL);
    }
}

class PLAViewHolder {
    final TextView text;
    final ImageView icon;
    final LinearLayout departmentLL;

    PLAViewHolder(TextView text, ImageView icon, LinearLayout departmentLL) {
        this.text = text;
        this.icon = icon;
        this.departmentLL = departmentLL;
    }
}

