package app.repostit.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.repostit.R;
import app.repostit.entity.ImageData;
import app.repostit.listener.OnImageClickListener;
import app.repostit.widget.CustomTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.ViewHolder> {

    List<ImageData> data;
    OnImageClickListener onImageClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profilepic;
        CustomTextView profileName;
        RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);
            profilepic = (ImageView) view.findViewById(R.id.profile_image);
            profileName = (CustomTextView) view.findViewById(R.id.profileName);

            recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            /*try {
                recyclerView.addItemDecoration(new EqualSpacingItemDecoration(R.dimen.dp_1), GRID);
            }catch (Exception e){

            }*/

        }
    }

    public AdapterFeed(List<ImageData> data, OnImageClickListener onImageClickListener) {
        this.data = data;
        this.onImageClickListener = onImageClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ImageData imageData = data.get(position);
        holder.profileName.setText(imageData.profileName);
        Picasso.get().load(imageData.profileUrl).fit().fit().into(holder.profilepic);

        AdapterImages adapterImages = new AdapterImages(ImageData.getAllFromCategory(imageData.profileId), this.onImageClickListener);
        holder.recyclerView.setAdapter(adapterImages);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}