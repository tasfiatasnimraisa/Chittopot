package com.example.chittopot;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class Viewholder extends RecyclerView.ViewHolder {
    PlayerView playerView;
    TextView videoTitle, videoType;
    SimpleExoPlayer player;

    public Viewholder(@NonNull View itemView) {
        super(itemView);
        playerView = itemView.findViewById(R.id.exoplayer_item);
        videoTitle = itemView.findViewById(R.id.videotitle);
        videoType = itemView.findViewById(R.id.videotype);
        player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
        playerView.setPlayer(player);
    }

    public void setVideo(String title, String vurl, String category) {
        videoTitle.setText(title);
        videoType.setText(category);

        // Create a MediaItem using the video URL

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(vurl));

        // Set the MediaItem to the player and prepare (don't play automatically)


        // Set the MediaItem to the player and start playing
        player.setMediaItem(mediaItem);
        player.prepare();

    }
    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
