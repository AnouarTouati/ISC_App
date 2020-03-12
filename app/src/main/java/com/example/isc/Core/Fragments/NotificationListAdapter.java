package com.example.isc.Core.Fragments;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.isc.Core.MyNotification;
import com.example.isc.R;

import java.util.List;


public class NotificationListAdapter extends ArrayAdapter<MyNotification> {

    private int resourceLayout;
    private Context mContext;
    private MyNotification myNotification;
    private TextView notificationTextView, notificationTime;
    private ImageView notificationProfileImage;

    public NotificationListAdapter(Context context, int resource, List<MyNotification> items) {
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
            NLAViewHolder holder = createViewHolderFrom(v);
            v.setTag(holder);
        }

        final NLAViewHolder holder = (NLAViewHolder) v.getTag();

        myNotification = getItem(position);

        if (myNotification != null) {
            if(myNotification.getMyUser()!=null){
            if (notificationProfileImage != null) {

                    holder.notificationProfileImage.setImageBitmap(myNotification.getMyUser().getProfileImageBitmap());
                    holder.notificationProfileImage.setDrawingCacheEnabled(true);

            }
            if (notificationTextView != null) {
                holder.notificationTextView.setText(
                        Html.fromHtml("<b>"+
                                myNotification.getMyUser().getFullName()+"</b>" + " " +
                        "<font color=\"#555555\">"+
                                myNotification.getMyUser().getPositionAsString()+"</font>"+ " " +
                        "<font color=\"#000000\">"+
                                myNotification.getNotificationText()+"</font>"));
            }
            if (notificationTime != null) {
                holder.notificationTime.setText(myNotification.getNotificationTime());
            }
        }
        }
        return v;
    }
    private NLAViewHolder createViewHolderFrom(View v) {
        notificationTextView = v.findViewById(R.id.notificationTextView);
        notificationProfileImage = v.findViewById(R.id.notificationProfileImage);
        notificationTime = v.findViewById(R.id.notificationTime);
        return new NLAViewHolder(notificationTextView, notificationProfileImage,
                                notificationTime);
    }
}

class NLAViewHolder {
    final TextView notificationTextView, notificationTime;
    final ImageView notificationProfileImage;

    NLAViewHolder(TextView notificationTextView, ImageView notificationProfileImage,
                  TextView notificationTime) {
        this.notificationTextView = notificationTextView;
        this.notificationProfileImage = notificationProfileImage;
        this.notificationTime = notificationTime;
    }
}