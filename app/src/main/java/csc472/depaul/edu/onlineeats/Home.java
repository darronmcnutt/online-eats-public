package csc472.depaul.edu.onlineeats;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();


        // Shopping cart floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(getHome(),CartActivity.class);
                startActivity(cartIntent);
            }
        });


        // Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set username (using email rather than display name)
        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(mUser.getEmail());

        // Configure Restaurant Menu
        mRecyclerView = (RecyclerView) findViewById(R.id.menu_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getFirebaseData();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(getHome(), CartActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent locationIntent = new Intent(getHome(), LocationActivity.class);
            startActivity(locationIntent);

        } else if (id == R.id.nav_log_out) {
            mAuth.signOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private final Home getHome() {
        return this;
    }

    private void getFirebaseData() {
        Query query = FirebaseDatabase.getInstance().getReference().child("items");

        FirebaseRecyclerOptions<RestaurantMenuItem> items =
                new FirebaseRecyclerOptions.Builder<RestaurantMenuItem>()
                        .setQuery(query, new SnapshotParser<RestaurantMenuItem>() {
                            @NonNull
                            @Override
                            public RestaurantMenuItem parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new RestaurantMenuItem(snapshot.child("name").getValue().toString(),
                                        snapshot.child("description").getValue().toString(),
                                        snapshot.child("category").getValue().toString(),
                                        snapshot.child("imageURL").getValue().toString(),
                                        Float.parseFloat(snapshot.child("price").getValue().toString()));
                            }
                        })
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<RestaurantMenuItem, MenuItemViewHolder>(items) {
            @Override
            public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new MenuItemViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(MenuItemViewHolder holder, final int position, final RestaurantMenuItem item) {
                holder.setName(item.getName());
                holder.setDescription(item.getDescription());
                holder.setCategory(item.getCategory());
                holder.setPrice(NumberFormat.getCurrencyInstance().format(item.getPrice()));
                Picasso.get().load(item.getImageURL())
                        .placeholder(R.drawable.forkknife)
                        .error(R.drawable.forkknife)
                        .resize(1000,600)
                        .centerCrop()
                        .into(holder.getImage());


                holder.getLayout().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Get reference to the user's shopping cart in Firebase
                        DatabaseReference mCartRef = mDatabase.getReference("carts/" + mUser.getUid());

                        // Check if item exists: if yes, update qty / if no, create item
                        ValueEventListener cartItemListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                                if (cartItem != null) {
                                    int newQty = cartItem.getQty() + 1;
                                    cartItem = new CartItem(cartItem.getName(), cartItem.getPrice(), newQty);
                                } else {
                                    cartItem = new CartItem(item.getName(),item.getPrice(),1);
                                }
                                dataSnapshot.getRef().setValue(cartItem);
                                Toast.makeText(Home.this, item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("CART", "loadCartItem:onCancelled", databaseError.toException());
                            }
                        };

                        mCartRef.child(item.getName()).addListenerForSingleValueEvent(cartItemListener);

                    }
                });
            }

        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

}
