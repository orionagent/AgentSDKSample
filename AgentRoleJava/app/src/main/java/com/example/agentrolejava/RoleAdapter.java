package com.example.agentrolejava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {
    private List<Role> roles;
    private OnRoleClickListener listener;

    public interface OnRoleClickListener {
        void onRoleClick(Role role);
    }

    public RoleAdapter(List<Role> roles, OnRoleClickListener listener) {
        this.roles = roles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_role, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        Role role = roles.get(position);
        holder.bind(role);
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    class RoleViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImageView;
        private TextView nameTextView;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRoleClick(roles.get(position));
                }
            });
        }

        public void bind(Role role) {
            nameTextView.setText(role.getName());
            avatarImageView.setImageResource(role.getAvatarRes());
        }
    }
} 