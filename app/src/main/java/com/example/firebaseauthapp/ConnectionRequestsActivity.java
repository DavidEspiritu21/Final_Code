package com.example.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthapp.models.ConnectionRequest;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConnectionRequestsActivity extends AppCompatActivity {

    private RecyclerView requestsRecyclerView;
    private ConnectionRequestsAdapter adapter;
    private List<ConnectionRequest> connectionRequests = new ArrayList<>();
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_requests);

        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestoreHelper.getInstance().getUser(currentUser.getUid(), task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    com.example.firebaseauthapp.models.User user = task.getResult().toObject(com.example.firebaseauthapp.models.User.class);
                    if (user != null && "patient".equals(user.getRole())) {
                        patientId = user.getPatientId();
                        loadConnectionRequests();
                    }
                }
            });
        }
    }

    private void loadConnectionRequests() {
        FirebaseFirestoreHelper.getInstance().getPendingRequests(patientId, task -> {
            if (task.isSuccessful()) {
                connectionRequests.clear();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    ConnectionRequest request = doc.toObject(ConnectionRequest.class);
                    if (request != null) {
                        request.setRequestId(doc.getId());
                        connectionRequests.add(request);
                    }
                }
                adapter = new ConnectionRequestsAdapter(connectionRequests);
                requestsRecyclerView.setAdapter(adapter);
            }
        });
    }

    private class ConnectionRequestsAdapter extends RecyclerView.Adapter<ConnectionRequestsAdapter.ViewHolder> {

        private List<ConnectionRequest> requests;

        public ConnectionRequestsAdapter(List<ConnectionRequest> requests) {
            this.requests = requests;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_connection_request, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ConnectionRequest request = requests.get(position);
            holder.guardianNameTextView.setText(request.getGuardianName());
            holder.requestDateTextView.setText("Requested: " + new java.util.Date(request.getCreatedAt()));

            holder.acceptButton.setOnClickListener(v -> acceptRequest(request));
            holder.rejectButton.setOnClickListener(v -> rejectRequest(request));
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView guardianNameTextView, requestDateTextView;
            Button acceptButton, rejectButton;

            public ViewHolder(View itemView) {
                super(itemView);
                guardianNameTextView = itemView.findViewById(R.id.guardianNameTextView);
                requestDateTextView = itemView.findViewById(R.id.requestDateTextView);
                acceptButton = itemView.findViewById(R.id.acceptButton);
                rejectButton = itemView.findViewById(R.id.rejectButton);
            }
        }
    }

    private void acceptRequest(ConnectionRequest request) {
        FirebaseFirestoreHelper.getInstance().acceptConnectionRequest(
                request.getRequestId(),
                aVoid -> {
                    Toast.makeText(this, "Connection request accepted!", Toast.LENGTH_SHORT).show();
                    loadConnectionRequests(); // Reload the list
                },
                e -> Toast.makeText(this, "Error accepting request", Toast.LENGTH_SHORT).show()
        );
    }

    private void rejectRequest(ConnectionRequest request) {
        FirebaseFirestoreHelper.getInstance().updateConnectionRequestStatus(
                request.getRequestId(), "rejected",
                aVoid -> {
                    Toast.makeText(this, "Connection request rejected", Toast.LENGTH_SHORT).show();
                    loadConnectionRequests(); // Reload the list
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
