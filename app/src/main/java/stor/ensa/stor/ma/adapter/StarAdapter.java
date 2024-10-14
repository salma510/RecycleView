package stor.ensa.stor.ma.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import stor.ensa.stor.ma.R;
import stor.ensa.stor.ma.beans.Star;
import stor.ensa.stor.ma.service.StarService;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarViewHolder> implements Filterable {

    private static final String TAG = "StarAdapter";

    private List<Star> stars; // Liste originale de stars
    private List<Star> starsFilter; // Liste filtrée de stars
    private Context context;
    private NewFilter mFilter;

    public StarAdapter(Context context, List<Star> stars) {
        this.context = context;
        this.stars = stars;
        this.starsFilter = new ArrayList<>(stars); // Initialiser starsFilter avec les stars originales
        this.mFilter = new NewFilter(this);
    }

    @NonNull
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.star_item, viewGroup, false);
        final StarViewHolder holder = new StarViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popup = LayoutInflater.from(context).inflate(R.layout.star_edit_item, null,
                        false);
                final ImageView img = popup.findViewById(R.id.img);
                final RatingBar bar = popup.findViewById(R.id.ratingBar);
                final TextView idss = popup.findViewById(R.id.idss);
                Bitmap bitmap =
                        ((BitmapDrawable)((ImageView)v.findViewById(R.id.img)).getDrawable()).getBitmap();
                img.setImageBitmap(bitmap);
                bar.setRating(((RatingBar)v.findViewById(R.id.stars)).getRating());
                idss.setText(((TextView)v.findViewById(R.id.ids)).getText().toString());
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Notez : ")
                        .setMessage("Donner une note entre 1 et 5 :")
                        .setView(popup)
                        .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                float s = bar.getRating();
                                int ids = Integer.parseInt(idss.getText().toString());
                                Star star = StarService.getInstance().findById(ids);
                                star.setStar(s);
                                StarService.getInstance().update(star);
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .create();
                dialog.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StarViewHolder holder, int position) {
        Log.d(TAG, "onBindView call ! " + position);
        Star star = starsFilter.get(position); // Récupérer l'étoile filtrée
        Glide.with(context)
                .asBitmap()
                .load(star.getImg())
                .apply(new RequestOptions().override(100, 100))
                .into(holder.img);
        holder.name.setText(star.getName().toUpperCase());
        holder.stars.setRating(star.getStar());
        holder.idss.setText(String.valueOf(star.getId())); // Utiliser String.valueOf() pour éviter les conversions explicites
    }

    @Override
    public int getItemCount() {
        return starsFilter.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public List<Star> getStars() {
        return stars; // Méthode pour accéder à la liste originale de stars
    }

    public List<Star> getStarsFilter() {
        return starsFilter; // Méthode pour accéder à la liste filtrée de stars
    }

    public void clearStarsFilter() {
        starsFilter.clear(); // Méthode pour vider la liste filtrée
    }

    public void addStarsFilter(Star star) {
        starsFilter.add(star); // Méthode pour ajouter une étoile à la liste filtrée
    }

    public class StarViewHolder extends RecyclerView.ViewHolder {
        TextView idss;
        ImageView img;
        TextView name;
        RatingBar stars;
        RelativeLayout parent;

        public StarViewHolder(@NonNull View itemView) {
            super(itemView);
            idss = itemView.findViewById(R.id.ids);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            stars = itemView.findViewById(R.id.stars);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    // Classe interne pour le filtrage
    class NewFilter extends Filter {
        private StarAdapter mAdapter;

        public NewFilter(StarAdapter adapter) {
            super();
            this.mAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Star> filteredList = new ArrayList<>();

            if (charSequence.length() == 0) {
                filteredList.addAll(mAdapter.getStars());
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Star star : mAdapter.getStars()) {
                    if (star.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(star);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mAdapter.clearStarsFilter(); // Vider la liste filtrée
            List<Star> stars = (List<Star>) filterResults.values; // Obtenir les résultats filtrés
            for (Star star : stars) {
                mAdapter.addStarsFilter(star); // Ajouter les résultats filtrés à la liste filtrée
            }
            mAdapter.notifyDataSetChanged(); // Notifier le changement
        }
    }
}
