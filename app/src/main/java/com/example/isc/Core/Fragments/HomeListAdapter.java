package com.example.isc.Core.Fragments;

import android.content.Context;
import android.content.Intent;
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

import com.example.isc.Core.MyPost;
import com.example.isc.Core.ShowUserProfileActivity;
import com.example.isc.R;

import java.util.List;


public class HomeListAdapter extends ArrayAdapter<MyPost> {

    private int resourceLayout;
    private Context mContext;
    private MyPost post;
    private String text;
    private boolean textDisplayedAll;
    private TextView posterName, posterPosition, postedText, postEvents;
    private ImageView posterProfileImage, postedImage;
    private ImageButton postLevelButton, tagColleagueButton;
    private LinearLayout posterProfileLayout;

    public HomeListAdapter(Context context, int resource, List<MyPost> items) {
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
            HLAViewHolder holder = createViewHolderFrom(v);
            v.setTag(holder);
        }

        final HLAViewHolder holder = (HLAViewHolder) v.getTag();

        post = getItem(position);

        if (post != null) {
            if(post.getMyUser()!=null){
            if (posterName != null) {
                holder.posterName.setText(post.getMyUser().getFullName());
            }
            if (posterPosition != null) {
                holder.posterPosition.setText(post.getMyUser().getPositionAsString());
            }
            if (postedText != null) {
                if (post.getPostedText().length() > 100) {
                    text = post.getPostedText().substring(0, 99) + "... " + Html.fromHtml("<font font-weight=bold> See more </font>");
                    textDisplayedAll = false;
                    holder.postedText.setText(text);
                } else {
                    holder.postedText.setText(post.getPostedText());
                    textDisplayedAll = true;
                }
                holder.postedText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (textDisplayedAll) {
                            holder.postedText.setText(text);
                            textDisplayedAll = false;
                        } else {
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
                if(post.hasImage()){
                    if(post.getPostedImageBitmap()!=null){
                        holder.postedImage.setImageBitmap(post.getPostedImageBitmap());
                        holder.postedImage.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.postedImage.setImageResource(R.drawable.post_image_placeholder);
                    }
                }
                else{
                    holder.postedImage.setImageBitmap(null);
                    holder.postedImage.setVisibility(View.GONE);
                }

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
            if (postEvents != null) {
                postEvents.setText(post.getMyPostEvents());
            }
        } else {
                if(postedImage!=null){
                    if(post.hasImage()){
                        holder.postedImage.setImageResource(R.drawable.post_image_placeholder);
                        holder.postedImage.setVisibility(View.VISIBLE);
                    }else{
                        holder.postedImage.setImageBitmap(null);
                        holder.postedImage.setVisibility(View.GONE);
                    }
                }
            }
    }
        holder.posterProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] s = {post.getMyUser().getFullName(), post.getMyUser().getPositionAsString(),};
                Intent intent = new Intent(getContext(), ShowUserProfileActivity.class);
                intent.putExtra("user", s);
                getContext().startActivity(intent);
            }
        });
        return v;
    }
    private HLAViewHolder createViewHolderFrom(View view) {
        posterName = view.findViewById(R.id.posterName);
        posterPosition = view.findViewById(R.id.posterPosition);
        posterProfileImage = view.findViewById(R.id.posterProfileImage);
        postedText = view.findViewById(R.id.postedText);
        postedImage = view.findViewById(R.id.postedImage);
        postLevelButton = view.findViewById(R.id.postLevelButtonHomeAdapter);
        tagColleagueButton = view.findViewById(R.id.tagColleagueButtonHomeAdapter);
        posterProfileLayout = view.findViewById(R.id.posterProfileLayout);
        postEvents = view.findViewById(R.id.postEvents);

        return new HLAViewHolder(posterName, posterPosition, posterProfileImage,
                postedText, postedImage, postLevelButton, tagColleagueButton,
                posterProfileLayout, postEvents);
    }
}

class HLAViewHolder {
    final TextView posterName, posterPosition, postedText, postEvents;
    final ImageView posterProfileImage, postedImage;
    final ImageButton postLevelButton, tagColleagueButton;
    final LinearLayout posterProfileLayout;

    HLAViewHolder(TextView posterName, TextView posterPosition, ImageView posterProfileImage,
                  TextView postedText, ImageView postedImage,
                  ImageButton postLevelButton, ImageButton tagColleagueButton,
                  LinearLayout posterProfileLayout, TextView postEvents) {
        this.posterName = posterName;
        this.posterPosition = posterPosition;
        this.postedText = postedText;
        this.posterProfileImage = posterProfileImage;
        this.postedImage = postedImage;
        this.postLevelButton = postLevelButton;
        this.tagColleagueButton = tagColleagueButton;
        this.posterProfileLayout = posterProfileLayout;
        this.postEvents = postEvents;
    }
}
