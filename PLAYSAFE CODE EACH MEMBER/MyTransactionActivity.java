package apc.edu.ph.playsafe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyTransactionActivity extends AppCompatActivity implements OnItemClick{

    //Firebase
    DatabaseReference mUserDatabase, transactiondata;

    //List Transaction
    ArrayList<String> tnumberList;
    ArrayList<String> buyernList;
    ArrayList<String> sellernList;
    ArrayList<String> priceList;
    TransactionAdapter transactionAdapter;
    RecyclerView mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_transaction);
        mResultList = (RecyclerView) findViewById(R.id.transaction_list);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        mResultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        tnumberList = new ArrayList<>();
        buyernList = new ArrayList<>();
        sellernList = new ArrayList<>();
        priceList = new ArrayList<>();

        mUserDatabase = FirebaseDatabase.getInstance().getReference();

        transactiondata = mUserDatabase.child("Transaction(s)");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String user = firebaseUser.getDisplayName();

        transactiondata.child("Buying").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String uid = snapshot.getKey();
                    String number = snapshot.child("Transaction Number").getValue(String.class);
                    String buyer = snapshot.child("Buyer Name").getValue(String.class);
                    String seller = snapshot.child("Seller Name").getValue(String.class);
                    String price = snapshot.child("Price").getValue(String.class);

                    tnumberList.add(number);
                    buyernList.add(buyer);
                    sellernList.add(seller);
                    priceList.add("PHP " + price);
                    counter++;

                    if(counter == 15)
                        break;
                }
                transactionAdapter = new TransactionAdapter(MyTransactionActivity.this, tnumberList, buyernList, sellernList, priceList);
                mResultList.setAdapter(transactionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        transactiondata.child("Selling").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String uid = snapshot.getKey();
                    String number = snapshot.child("Transaction Number").getValue(String.class);
                    String buyer = snapshot.child("Buyer Name").getValue(String.class);
                    String seller = snapshot.child("Seller Name").getValue(String.class);
                    String price = snapshot.child("Price").getValue(String.class);

                    tnumberList.add(number);
                    buyernList.add(buyer);
                    sellernList.add(seller);
                    priceList.add("PHP " + price);
                    counter++;

                    if(counter == 15)
                        break;
                }
                transactionAdapter = new TransactionAdapter(MyTransactionActivity.this, tnumberList, buyernList, sellernList, priceList);
                mResultList.setAdapter(transactionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onClick(String value){

    }
}
