package csc472.depaul.edu.onlineeats;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

public class CartActivity extends AppCompatActivity {

    // RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    // Firebase
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mCartRef;

    private TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Configure cart Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get Firebase database instance, user, and cart reference
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mCartRef = mDatabase.getReference().child("carts/" + mUser.getUid());

        // Get other views
        total = findViewById(R.id.cart_sum);
        Button placeOrderButton = findViewById(R.id.cart_order_button);

        calculateTotal();
        getFirebaseData();

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Generate new order in Firebase with contents of cart
                        mDatabase.getReference().child("orders/" + mUser.getUid())
                                .push().setValue(dataSnapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // If successful, erase current user's shopping cart
                                mCartRef.removeValue();

                                // Start delivery map activity
                                Intent deliveryIntent = new Intent(getCartActivity(), LocationActivity.class);
                                startActivity(deliveryIntent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("CART", "placeOrder:onCancelled", databaseError.toException());
                    }
                });


            }
        });

    }
    private void calculateTotal() {

        // Iterate through cart items and calculate total price
        mCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float sum = 0;

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    CartItem cartItem = ds.getValue(CartItem.class);
                    sum = sum + (cartItem.getPrice() * cartItem.getQty());
                }

                total.setText(NumberFormat.getCurrencyInstance().format(sum));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("CART", "calculateSum:onCancelled", databaseError.toException());
            }
        });

    }
    private void getFirebaseData() {

        Query query = mCartRef;

        FirebaseRecyclerOptions<CartItem> items =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(query, new SnapshotParser<CartItem>() {
                            @NonNull
                            @Override
                            public CartItem parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new CartItem(snapshot.child("name").getValue().toString(),
                                        Float.parseFloat(snapshot.child("price").getValue().toString()),
                                        Integer.parseInt(snapshot.child("qty").getValue().toString()));
                            }
                        })
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<CartItem, CartItemViewHolder>(items) {
            @Override
            public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_item, parent, false);

                return new CartItemViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(CartItemViewHolder holder, final int position, final CartItem item) {
                holder.setName(item.getName());
                holder.setPrice(NumberFormat.getCurrencyInstance().format(item.getPrice()));
                holder.setQty("Qty: " + Integer.toString(item.getQty()));

                // Increment item quantity on click
                holder.getUp().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CartItem cartItem = new CartItem(item.getName(),item.getPrice(),item.getQty() + 1);
                        mCartRef.child(item.getName()).setValue(cartItem);
                    }
                });

                // Decrement item quantity on click
                holder.getDown().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.getQty() > 0) {
                            CartItem cartItem = new CartItem(item.getName(), item.getPrice(), item.getQty() - 1);
                            mCartRef.child(item.getName()).setValue(cartItem);
                        }
                    }
                });

            }

        };

        // Attach Firebase adapter to RecyclerView
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

    private CartActivity getCartActivity() { return this; }
}
