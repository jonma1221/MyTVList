package com.example.nahson.mytvlist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nahson.mytvlist.R;
import com.example.nahson.mytvlist.activity.SeasonActivity;
import com.example.nahson.mytvlist.model.SeasonNumber.SeasonNumber;
import com.example.nahson.mytvlist.model.TVID.Season;
import com.example.nahson.mytvlist.rest.ApiClient;
import com.example.nahson.mytvlist.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nah Son on 5/13/2016.
 */
public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder> {

    private final static String API_KEY = "56ad544b1b49a341e09a472c21bfcf9e";
    private static final String SEASON = "Season ";
    private static List<Season> seasonList;
    private static int rowLayout;
    private static Context context;

    public static class SeasonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linearLayout;
        TextView seasonTitle;
        ImageView posterPath;
        Intent intent;
        SeasonNumber seasonNumber;


        public SeasonViewHolder(View v) {
            super(v);
            linearLayout = (LinearLayout) v.findViewById(R.id.season_tv_layout);
            seasonTitle = (TextView) v.findViewById(R.id.season_title);
            posterPath = (ImageView) v.findViewById(R.id.season_poster_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Position: ", "" + position);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Season seasonN = seasonList.get(position);

            Call<SeasonNumber> call = apiInterface.getSeasonNumberInfo(TVAdapter.ID,
                    seasonN.getSeasonNumber(), API_KEY);
            call.enqueue(new Callback<SeasonNumber>() {
                @Override
                public void onResponse(Call<SeasonNumber> call, Response<SeasonNumber> response) {
                    seasonNumber = response.body();
                    Log.d("Name: ", seasonNumber.getName());
                    Log.d("Overview: ", seasonNumber.getOverview());
                    intent = new Intent(context, SeasonActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Poster", seasonNumber.getPosterPath());
                    intent.putExtra("Name", seasonNumber.getName());
                    intent.putExtra("Overview", seasonNumber.getOverview());
                    intent.putParcelableArrayListExtra("Episodes", (ArrayList) seasonNumber.getEpisodes());
                    context.startActivity(intent);
                }

                @Override
                public void onFailure(Call<SeasonNumber> call, Throwable t) {
                    Log.e("SeasonAdapter: ", t.getMessage());
                }
            });
        }
    }

    public SeasonAdapter(List<Season> seasonList, int rowLayout, Context context) {
        SeasonAdapter.seasonList = seasonList;
        SeasonAdapter.rowLayout = rowLayout;
        SeasonAdapter.context = context;
    }

    @Override
    public SeasonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final SeasonViewHolder viewHolder = new SeasonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SeasonViewHolder holder, int position) {
        holder.seasonTitle.setText(SEASON + seasonList.get(position).getSeasonNumber());
        Picasso.with(context)
                .load(seasonList.get(position).getPosterPath())
                .into(holder.posterPath);
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }
}