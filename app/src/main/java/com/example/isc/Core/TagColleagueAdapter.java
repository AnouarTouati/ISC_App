package com.example.isc.Core;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.isc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TagColleagueAdapter extends ArrayAdapter<MyUser> {

    private int resourceLayout;
    private Context mContext;
    ImageView colleagueProfileImage, colleagueCheck;
    TextView colleagueName, colleaguePosition;
    MyUser myColleague;
    List<MyUser> list = null;
    ArrayList<MyUser> arrayList;

    public TagColleagueAdapter(Context context, int resource, List<MyUser> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.list = items;
        this.arrayList = new ArrayList<MyUser>();
        this.arrayList.addAll(list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(resourceLayout, null);
            TCAViewHolder holder = createViewHolderFrom(view);
            view.setTag(holder);
        }

        final TCAViewHolder holder = (TCAViewHolder) view.getTag();

        myColleague = getItem(position);

        if(myColleague != null) {
            holder.colleagueName.setText(myColleague.getFullName());
            holder.colleaguePosition.setText(myColleague.getPosition());
            holder.colleagueProfileImage.setImageBitmap(myColleague.getProfileImageBitmap());
        }

        holder.colleagueName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.colleagueCheck.getDrawable()!=null){
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#000000\">"+
                                                    holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageDrawable(null);
                    TagColleagueActivity.taggedColleagues.remove(holder.colleagueName.getText().toString());
                }else{
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                                                    holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                                                        holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageResource(R.drawable.ic_check_24dp);
                    TagColleagueActivity.taggedColleagues.add(holder.colleagueName.getText().toString());
                }
            }
        });
        holder.colleaguePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.colleagueCheck.getDrawable()!=null){
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageDrawable(null);
                    TagColleagueActivity.taggedColleagues.remove(holder.colleagueName.getText().toString());
                }else{
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                            holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageResource(R.drawable.ic_check_24dp);
                    TagColleagueActivity.taggedColleagues.add(holder.colleagueName.getText().toString());
                }
            }
        });
        holder.colleagueProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.colleagueCheck.getDrawable()!=null){
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageDrawable(null);
                    TagColleagueActivity.taggedColleagues.remove(holder.colleagueName.getText().toString());
                }else{
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                            holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageResource(R.drawable.ic_check_24dp);
                    TagColleagueActivity.taggedColleagues.add(holder.colleagueName.getText().toString());
                }
            }
        });
        holder.colleagueCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.colleagueCheck.getDrawable()!=null){
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#000000\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageDrawable(null);
                    TagColleagueActivity.taggedColleagues.remove(holder.colleagueName.getText().toString());
                }else{
                    holder.colleagueName.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                            holder.colleagueName.getText() +"</font>"));
                    holder.colleaguePosition.setText(Html.fromHtml("<font color=\"#1976D2\">"+
                            holder.colleaguePosition.getText() +"</font>"));
                    holder.colleagueCheck.setImageResource(R.drawable.ic_check_24dp);
                    TagColleagueActivity.taggedColleagues.add(holder.colleagueName.getText().toString());
                }
            }
        });
        return view;
    }

    private TCAViewHolder createViewHolderFrom(View view) {
        colleagueName = view.findViewById(R.id.colleagueName);
        colleaguePosition = view.findViewById(R.id.colleaguePosition);
        colleagueProfileImage = view.findViewById(R.id.colleagueProfileImage);
        colleagueCheck = view.findViewById(R.id.colleagueCheck);
        return new TCAViewHolder(colleagueName, colleaguePosition, colleagueProfileImage, colleagueCheck);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(arrayList);
        } else {
            for (MyUser myUser : arrayList) {
                if (myUser.getFullName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(myUser);
                }
            }
        }
        notifyDataSetChanged();
    }

}

class TCAViewHolder {
    final TextView colleagueName, colleaguePosition;
    final ImageView colleagueProfileImage, colleagueCheck;

    TCAViewHolder(TextView colleagueName, TextView colleaguePosition, ImageView colleagueProfileImage, ImageView colleagueCheck) {
        this.colleagueName = colleagueName;
        this.colleaguePosition = colleaguePosition;
        this.colleagueProfileImage = colleagueProfileImage;
        this.colleagueCheck = colleagueCheck;
    }
}

