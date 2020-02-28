package com.example.isc.Core;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.isc.R;

import java.util.List;


public class ShowUserProfilePostsListAdapter extends ArrayAdapter<MyPost> {

    private int resourceLayout;
    private Context mContext;
    private MyPost post;
    private String text;
    private boolean textDisplayedAll;
    private TextView posterName, posterPosition, postedText;
    private ImageView posterProfileImage, postedImage;
    private ImageButton postLevelButton, tagColleagueButton;
    private LinearLayout posterProfileLayout;

    public ShowUserProfilePostsListAdapter(Context context, int resource, List<MyPost> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
            SUPAViewHolder holder = createViewHolderFrom(v);
            v.setTag(holder);
        }

        final SUPAViewHolder holder = (SUPAViewHolder) v.getTag();

        post = getItem(position);

        if (post != null) {
            if (posterName != null) {
                holder.posterName.setText(post.getMyUser().getFullName());
            }
            if (posterPosition != null) {
                holder.posterPosition.setText(post.getMyUser().getPosition());
            }
            if (postedText != null) {
                if(post.getPostedText().length()>100){
                    text = post.getPostedText().substring(0, 99) +"... "+ Html.fromHtml("<font font-weight=bold> See more </font>");
                    textDisplayedAll = false;
                    holder.postedText.setText(text);
                }else{
                    holder.postedText.setText(post.getPostedText());
                    textDisplayedAll = true;
                }
                holder.postedText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textDisplayedAll){
                            holder.postedText.setText(text);
                            textDisplayedAll = false;
                        }else{
                            holder.postedText.setText(post.getPostedText());
                            textDisplayedAll = true;
                        }
                    }
                });
            }
            if (posterProfileImage != null) {
                holder.posterProfileImage.setImageBitmap(post.getMyUser().getProfileImageBitmap());
                holder.posterProfileImage.setDrawingCacheEnabled(true);
            }
            if (postedImage != null) {
                holder.postedImage.setImageBitmap(post.getPostedImageBitmap());
                holder.postedImage.setDrawingCacheEnabled(true);
            }
            if (postLevelButton != null) {
                holder.postLevelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pl = "Not specified";
                        if (post.getMyPostLevel() != null) {
                            pl = post.getMyPostLevel();
                        }
                        new AlertDialog.Builder(getContext())
                                .setTitle("Your post is visible to the departments of:")
                                .setMessage(pl)
                                .setPositiveButton("OK", null)
                                .setNegativeButton(null, null)
                                .show();
                    }
                });
            }
            if (tagColleagueButton != null) {
                holder.tagColleagueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tc = "Not specified";
                        if (post.getMyPostTagColleague() != null) {
                            tc = post.getMyPostTagColleague();
                        }
                        new AlertDialog.Builder(getContext())
                                .setTitle("Your post is visible to the departments of:")
                                .setMessage(tc)
                                .setPositiveButton("OK", null)
                                .setNegativeButton(null, null)
                                .show();
                    }
                });
            }
        }
        return v;
    }
    private SUPAViewHolder createViewHolderFrom(View view) {
        posterName = view.findViewById(R.id.posterName);
        posterPosition = view.findViewById(R.id.posterPosition);
        posterProfileImage = view.findViewById(R.id.posterProfileImage);
        postedText = view.findViewById(R.id.postedText);
        postedImage = view.findViewById(R.id.postedImage);
        postLevelButton = view.findViewById(R.id.postLevelButtonHomeAdapter);
        tagColleagueButton = view.findViewById(R.id.tagColleagueButtonHomeAdapter);
        posterProfileLayout = view.findViewById(R.id.posterProfileLayout);

        return new SUPAViewHolder(posterName, posterPosition, posterProfileImage,
                postedText, postedImage, postLevelButton, tagColleagueButton,
                posterProfileLayout);
    }
}

class SUPAViewHolder {
    final TextView posterName, posterPosition, postedText;
    final ImageView posterProfileImage, postedImage;
    final ImageButton postLevelButton, tagColleagueButton;
    final LinearLayout posterProfileLayout;

    SUPAViewHolder(TextView posterName, TextView posterPosition, ImageView posterProfileImage,
                  TextView postedText, ImageView postedImage,
                  ImageButton postLevelButton, ImageButton tagColleagueButton,
                  LinearLayout posterProfileLayout) {
        this.posterName = posterName;
        this.posterPosition = posterPosition;
        this.postedText = postedText;
        this.posterProfileImage = posterProfileImage;
        this.postedImage = postedImage;
        this.postLevelButton = postLevelButton;
        this.tagColleagueButton = tagColleagueButton;
        this.posterProfileLayout = posterProfileLayout;
    }
}
