package apc.edu.ph.playsafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SellingActivity extends AppCompatActivity implements OnItemClick {

    TextView CatText;
    EditText HM;

    //Firebase
    EditText mSearchField, wayb, price, description, sc;
    RecyclerView mResultList;
    DatabaseReference mUserDatabase, transactiondata;
    FirebaseUser firebaseUser;

    //Searcher
    ArrayList<String> nameList;
    ArrayList<String> profilePicList;
    ArrayList<String> usernameList;
    SearchAdapter searchAdapter;

    //Transaction
    RadioGroup pfee, smfee, shipfee;
    RadioButton radio1, radio2, radio3;
    Button startnow;

    //QR Code Generator
    private StorageReference mStorage;
    private Firebase mRef;
    int scoreTeam;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);

        getDataplease();

        mSearchField = (EditText) findViewById(R.id.search_field);
        mResultList = (RecyclerView) findViewById(R.id.result_list);
        wayb = (EditText) findViewById(R.id.wb);
        price = (EditText) findViewById(R.id.price);
        description = (EditText) findViewById(R.id.description);
        sc = (EditText) findViewById(R.id.sc);
        pfee = (RadioGroup) findViewById(R.id.playfeegroup);
        smfee = (RadioGroup) findViewById(R.id.shippinggroup);
        shipfee = (RadioGroup) findViewById(R.id.wpaygroup);
        startnow = (Button) findViewById(R.id.syt);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        mResultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        nameList = new ArrayList<>();
        profilePicList = new ArrayList<>();
        usernameList = new ArrayList<>();

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    setAdapter(s.toString());
                } else {
                    nameList.clear();
                    profilePicList.clear();
                    usernameList.clear();
                    mResultList.removeAllViews();
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorage = storage.getReferenceFromUrl("gs://playsafe-4e573.appspot.com/QR Codes");

        transactiondata = mUserDatabase.child("Transaction(s)");

        mRef = new Firebase("https://playsafe-4e573.firebaseio.com/");

        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                final String carid = dataSnapshot.child("Transaction Counter").child("value").getValue(String.class);

                startnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String created = firebaseUser.getDisplayName();

                        int radioId = pfee.getCheckedRadioButtonId();
                        int radioId2 = shipfee.getCheckedRadioButtonId();
                        int radioId1 = smfee.getCheckedRadioButtonId();

                        radio1 = findViewById(radioId);
                        radio2 = findViewById(radioId1);
                        radio3 = findViewById(radioId2);

                        String seller = mSearchField.getText().toString().trim();
                        String wb = wayb.getText().toString().trim();
                        String prices = price.getText().toString().trim();
                        String descrip = description.getText().toString().trim();
                        String shipcost = sc.getText().toString().trim();
                        scoreTeam = Integer.valueOf(carid);
                        scoreTeam = scoreTeam + 1;

                        mRef.child("Transaction Counter").child("value").setValue(scoreTeam);

                        if(seller.isEmpty()){
                            Toast.makeText(SellingActivity.this, "Please select buyer name", Toast.LENGTH_SHORT).show();
                        } else if (wb.isEmpty()){
                            Toast.makeText(SellingActivity.this, "Please enter what are you selling?", Toast.LENGTH_SHORT).show();
                        } else if (prices.isEmpty()){
                            Toast.makeText(SellingActivity.this, "Please enter the price", Toast.LENGTH_SHORT).show();
                        } else if (descrip.isEmpty()){
                            Toast.makeText(SellingActivity.this, "Please enter the description", Toast.LENGTH_SHORT).show();
                        } else if (shipcost.isEmpty()){
                            Toast.makeText(SellingActivity.this, "Please enter shipping cost", Toast.LENGTH_SHORT).show();
                        } else {
                            transactiondata.child("Selling").child(created).child(carid).child("Seller Name").setValue(created);
                            transactiondata.child("Selling").child(created).child(carid).child("Buyer Name").setValue(mSearchField.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("What are you selling?").setValue(wayb.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Price").setValue(price.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Description").setValue(description.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Who will pay for PlaySafe Fee?").setValue(radio1.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Shipping Method").setValue(radio2.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Who will pay for the shipping?").setValue(radio3.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Shipping Cost").setValue(sc.getText().toString());
                            transactiondata.child("Selling").child(created).child(carid).child("Transaction Number").setValue(carid);
                            transactiondata.child("Transaction Number").child(carid).child("Status").setValue("Pending");
                        }

                        try {
                            bitmap = TextToImageEncode(carid);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = mStorage.child(carid).putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    Toast.makeText(SellingActivity.this, "Transaction created successfully", Toast.LENGTH_SHORT).show();
                                    taskSnapshot.getMetadata();
                                }
                            });
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        Intent sellintent = new Intent(SellingActivity.this, MainActivity.class);
                        startActivity(sellintent);
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onClick(String value){

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String data = intent.getStringExtra("value");
            mSearchField.setText(data);
            nameList.clear();
            profilePicList.clear();
            usernameList.clear();
            mResultList.removeAllViews();
        }
    };

    public void playfee(View v){

        int radioId = pfee.getCheckedRadioButtonId();

        radio1 = findViewById(radioId);

    }

    public void shippingpay(View v1){

        int radioId1 = smfee.getCheckedRadioButtonId();

        radio2 = findViewById(radioId1);

    }
    public void whowillpayshipping(View v3){

        int radioId2 = shipfee.getCheckedRadioButtonId();

        radio3 = findViewById(radioId2);

    }

    private void setAdapter(final String searchedstring) {

        mUserDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nameList.clear();
                profilePicList.clear();
                usernameList.clear();
                mResultList.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String uid = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String profile_pic = snapshot.child("profile_image").getValue(String.class);
                    String user_name = snapshot.child("username").getValue(String.class);


                    if(name.toLowerCase().contains(searchedstring.toLowerCase())){
                        nameList.add(name);
                        profilePicList.add(profile_pic);
                        usernameList.add(user_name);
                        counter++;
                    }

                    if(counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(SellingActivity.this, nameList, profilePicList, usernameList);
                mResultList.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataplease() {
        Intent intent = getIntent();

        String cat = intent.getStringExtra("Category");
        String hm = intent.getStringExtra("HM");

        CatText = (TextView) findViewById(R.id.CatText);
        HM = (EditText) findViewById(R.id.price);

        CatText.setText(cat);
        HM.setText(hm);
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


}
