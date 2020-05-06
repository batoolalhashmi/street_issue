package com.barmej.streetissues.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barmej.entity.Issue;
import com.barmej.streetissues.activities.IssueDetailsActivity;
import com.barmej.streetissues.adapter.IssuesListAdapter;
import com.barmej.streetissues.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IssuesListFragment extends Fragment implements IssuesListAdapter.onIssueClickListener {

    private RecyclerView mRecycleViewIssues;
    private IssuesListAdapter mIssuesListAdapter;
    private ArrayList<Issue> mIssues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issues_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecycleViewIssues = view.findViewById(R.id.recycler_view_issue);
        mRecycleViewIssues.setLayoutManager(new LinearLayoutManager(getContext()));
        mIssues = new ArrayList<>();
        mIssuesListAdapter = new IssuesListAdapter(mIssues, IssuesListFragment.this);
        mRecycleViewIssues.setAdapter(mIssuesListAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("issues").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    mIssues.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        mIssues.add(documentSnapshot.toObject(Issue.class));
                    }
                    mIssuesListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onIssueClick(Issue issue) {
        Intent intent = new Intent(getContext(), IssueDetailsActivity.class);
        intent.putExtra(IssueDetailsActivity.ISSUE_DATA, issue);
        startActivity(intent);

    }
}
