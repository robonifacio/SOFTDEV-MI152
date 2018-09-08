package apc.edu.ph.playsafe;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.github.mzule.fantasyslide.SideBar;
import com.github.mzule.fantasyslide.SimpleFantasyListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private TextView nameTextView;
    private CircleImageView imageView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    DatabaseReference rootRef,demoRef;
    public String profile_pic;
    private Spinner bns, categories;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerArrowDrawable indicator = new DrawerArrowDrawable(this);
        indicator.setColor(Color.WHITE);
        getSupportActionBar().setHomeAsUpIndicator(indicator);
        setListener();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (((ViewGroup) drawerView).getChildAt(1).getId() == R.id.leftSideBar) {
                    indicator.setProgress(slideOffset);
                }
            }
        });

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.textcenter_layout);
        TextView title=(TextView)findViewById(getResources().getIdentifier("tvTitle", "id", getPackageName()));
        title.setText("ACCOUNT");

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        imageView = (CircleImageView) findViewById(R.id.fbimage);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Users");


        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String image_url = "http://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large";
            String fword = name;
            int i = fword.indexOf(' ');
            String word = name.substring(0, i);
            String uid = user.getUid();

            demoRef.child(name).child("name").setValue(name);
            demoRef.child(name).child("email").setValue(email);
            demoRef.child(name).child("profile_image").setValue(image_url);
            demoRef.child(name).child("username").setValue("@" + word);
            rootRef.child(name).child("Bought").setValue(0);
            rootRef.child(name).child("Sold").setValue(0);


            nameTextView.setText(name);
            Glide.with(this)
                    .load(image_url)
                    .into(imageView);
        } else {
            goLoginScreen();
        }

        addItemsOnSpinner2();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToggle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void setListener() {
        final TextView tipView = (TextView) findViewById(R.id.tipView);
        SideBar leftSideBar = (SideBar) findViewById(R.id.leftSideBar);
        leftSideBar.setFantasyListener(new SimpleFantasyListener() {
            @Override
            public boolean onHover(@Nullable View view, int index) {
                tipView.setVisibility(View.VISIBLE);
                if (view instanceof TextView) {
                    tipView.setText(String.format("%s at %d", ((TextView) view).getText().toString(), index));
                } else if (view != null && view.getId() == R.id.userInfo) {
                    tipView.setText(String.format("Personal Center at %d", index));
                } else {
                    tipView.setText(null);
                }
                return false;

            }

            @Override
            public boolean onSelect(View view, int index) {
                tipView.setVisibility(View.INVISIBLE);
                if(index==1){

                }
                if(index==2){

                }
                if(index==3){
                    Intent transIntent = new Intent(MainActivity.this, MyTransactionActivity.class);
                    startActivity(transIntent);
                }
                if(index==4){
                    Intent scanIntent = new Intent(MainActivity.this, ScannerActivity.class);
                    startActivity(scanIntent);
                }
                if(index==5){
                    logout();
                }
                return false;
            }

            @Override
            public void onCancel() {
                tipView.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void onClick(View view) {
        if (view instanceof TextView) {
            String title = ((TextView) view).getText().toString();
            if (title.startsWith("week")) {
                Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            } else {

            }
        } else if (view.getId() == R.id.userInfo) {

        }
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        categories = (Spinner) findViewById(R.id.categories);
        List<String> list = new ArrayList<String>();
        list.add("Mobile Phones and Tablets");
        list.add("Computers");
        list.add("Consumer Electronics");
        list.add("Home and Furniture");
        list.add("Clothing and Accessories");
        list.add("Books, Sports, and Hobbies");
        list.add("Cars and Automotives");
        list.add("Motorcycles and Scooters");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categories.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        bns = (Spinner) findViewById(R.id.buyorsell);

        String[] buyands = new String[]{
                "Buying",
                "Selling"
        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,buyands);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        bns.setAdapter(spinnerArrayAdapter);
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        bns = (Spinner) findViewById(R.id.buyorsell);
        categories = (Spinner) findViewById(R.id.categories);
        final EditText mEdit = (EditText) findViewById(R.id.textInputEditText);
        btnSubmit = (Button) findViewById(R.id.button);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String check = String.valueOf(bns.getSelectedItem());
                if(check.equals("Buying")) {
                    Intent intent = new Intent(getApplicationContext(), BuyingActivity.class);
                    String catt = String.valueOf(categories.getSelectedItem());
                    intent.putExtra("Category", catt);
                    intent.putExtra("HM", mEdit.getText().toString());
                    startActivity(intent);
                }
                if(check.equals("Selling")) {
                    Intent intent = new Intent(getApplicationContext(), SellingActivity.class);
                    String catt = String.valueOf(categories.getSelectedItem());
                    intent.putExtra("Category", catt);
                    intent.putExtra("HM", mEdit.getText().toString());
                    startActivity(intent);
                }


            }

        });
    }
}