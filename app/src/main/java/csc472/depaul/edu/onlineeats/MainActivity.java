package csc472.depaul.edu.onlineeats;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Buttons
    private Button btnSignIn, btnSignUp;

    private FirebaseDatabase mDatabase;

    // Firebase Authorization
    private FirebaseAuth mAuth;

    // Firebase User
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnSignIn).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If user is already signed in currentUser will be non-null
        currentUser = mAuth.getCurrentUser();

    }

    private final MainActivity getMainActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnSignUp) {
            Intent signUpIntent = new Intent(getMainActivity(), SignUpActivity.class);
            startActivity(signUpIntent);
        } else if (i == R.id.btnSignIn) {
            if (currentUser == null) {
                Intent loginIntent = new Intent(getMainActivity(), EmailPasswordLoginActivity.class);
                startActivity(loginIntent);
            } else {
                Intent homeIntent = new Intent(getMainActivity(), Home.class);
                startActivity(homeIntent);
            }
        }

    }
}
