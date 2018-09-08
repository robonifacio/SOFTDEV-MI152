package apc.edu.ph.playsafe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView zXingScannerView;
    private DatabaseReference databaseReference;
    int scoreTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        Toast.makeText(getApplicationContext(), "Transaction Number: " + result.getText() + " = Success",Toast.LENGTH_SHORT).show();
        databaseReference.child("Transaction(s)").child("Transaction Number").child(result.getText()).child("Status").setValue("Success");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String name = user.getDisplayName();
                final String carid = dataSnapshot.child(name).child("Sold").getValue(String.class);
                scoreTeam = Integer.valueOf(carid);
                scoreTeam = scoreTeam + 1;

                databaseReference.child(name).child("Sold").setValue(scoreTeam);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        zXingScannerView.resumeCameraPreview(this);
        Intent mainIntent = new Intent(ScannerActivity.this, MainActivity.class);
        startActivity(mainIntent);

    }
}