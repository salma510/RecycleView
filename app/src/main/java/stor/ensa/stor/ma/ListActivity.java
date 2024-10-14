package stor.ensa.stor.ma;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import stor.ensa.stor.ma.adapter.StarAdapter;
import stor.ensa.stor.ma.beans.Star;
import stor.ensa.stor.ma.service.StarService;

public class ListActivity extends AppCompatActivity {

    private List<Star> stars;
    private RecyclerView recyclerView;
    private StarAdapter starAdapter = null;
    private StarService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialisation de la Toolbar
       @SuppressLint({"MissingInflatedId", "LocalSuppress"})
       Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stars = new ArrayList<>();
        service = StarService.getInstance();
        init();

        // Configuration de la RecyclerView
        recyclerView = findViewById(R.id.recycle_view);
        starAdapter = new StarAdapter(this, service.findAll());
        recyclerView.setAdapter(starAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "Stars in adapter: " + service.findAll().size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (starAdapter != null){
                    starAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.share){
            String txt = "Stars";
            String mimeType = "text/plain";
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle("Stars")
                    .setText(txt)
                    .startChooser();
        }
        return super.onOptionsItemSelected(item);
    }


    public void init() {
        // Ajout de données au service
        service.create(new Star("Manal Benchlikha", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwDDNq-LwC-JH_MO3W5dtzBw1kwl5KIRO_mQ&s", 3.5f));
        service.create(new Star("ELgrande TOTO", "https://www.h24info.ma/wp-content/uploads/2023/10/EL-grande-Toto.jpg", 3));
        service.create(new Star("Sahar Seddiki", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaAwK7fwjCyYuNijkuwiVSSUAoeyO2eRh_0A&s", 5));
        service.create(new Star("Majdouline idrissi", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyBCUo0HnK46cnsHgcMc7xq7IosoD4al0ykg&s", 1));
        service.create(new Star("louise bouroin", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRtbUmqFtiHdbkZlPwP8B_mC0es21DqFNi7Dw&s", 5));
        service.create(new Star("Billie Eilish", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTWVs9hfyd120v73N7pf2SZP5s4RIvTwEELiQ&s", 1));

        Log.d(TAG, "Stars created: " + service.findAll().size());
    }
}
