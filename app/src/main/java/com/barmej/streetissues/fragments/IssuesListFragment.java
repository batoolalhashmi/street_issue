package com.barmej.streetissues.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barmej.streetissues.entity.Issue;
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
    private LinearLayoutManager mLinearLayoutManager;
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issues_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecycleViewIssues = view.findViewById(R.id.recycler_view_issue);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecycleViewIssues.setLayoutManager(mLinearLayoutManager);
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
                    if(savedRecyclerLayoutState!=null){
                        mLinearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
                    }
                }
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT,
                mLinearLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
    }

    @Override
    public void onIssueClick(Issue issue) {
        Intent intent = new Intent(getContext(), IssueDetailsActivity.class);
        intent.putExtra(IssueDetailsActivity.ISSUE_DATA, issue);
        startActivity(intent);

    }
}
