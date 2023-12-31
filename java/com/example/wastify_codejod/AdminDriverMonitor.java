package com.example.wastify_codejod;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminDriverMonitor extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<UserForLoc> userArrayList;
    MyLocAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_driver_monitor);

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting updates from driver");

        recyclerView=findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db=FirebaseFirestore.getInstance();
        userArrayList =new ArrayList<UserForLoc>();
        myAdapter= new MyLocAdapter(AdminDriverMonitor.this, userArrayList);

        recyclerView.setAdapter(myAdapter);
        EventChangeListener();
    }
    private void EventChangeListener() {
        db.collection("driver-locations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Fetching error",error.getMessage());
                            return;
                        }

                        assert value != null;
                        for(DocumentChange dc:value.getDocumentChanges()){
                            if(dc.getType()== DocumentChange.Type.ADDED ){
                                userArrayList.add(dc.getDocument().toObject(UserForLoc.class));
                            }
                            myAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }
}