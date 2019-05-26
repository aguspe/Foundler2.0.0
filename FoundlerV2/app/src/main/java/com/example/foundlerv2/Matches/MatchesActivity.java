package com.example.foundlerv2.Matches;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.foundlerv2.LoginActivity;
import com.example.foundlerv2.MainActivity;
import com.example.foundlerv2.R;
import com.example.foundlerv2.StartUpActivity;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdaptor;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private Button mMatchesBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdaptor = new MatchesAdaptor(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdaptor);

        MatchesObject obj = new MatchesObject("aaa");
        resultMatches.add(obj);
        resultMatches.add(obj);
        resultMatches.add(obj);
        resultMatches.add(obj);
        mMatchesAdaptor.notifyDataSetChanged();

        mMatchesBack = (Button) findViewById(R.id.button4);
        mMatchesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }
}
