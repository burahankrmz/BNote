package com.ourorobos.bnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ourorobos.bnote.model.Notes;

import java.util.List;

class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<Notes> list;

    private OnCallBack onCallBack;
    public NoteAdapter() {
    }

    public NoteAdapter(Context context, List<Notes> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnCallBack(OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  NoteAdapter.ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getText());
        holder.deleteimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onButtonDeleteClick(list.get(position));
            }
        });
        holder.editimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onButtonEditClick(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageButton editimagebutton;
        private final ImageButton deleteimagebutton;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            editimagebutton = itemView.findViewById(R.id.editimagebutton);
            deleteimagebutton = itemView.findViewById(R.id.deleteimagebutton);

        }
    }

    public interface OnCallBack{
        void onButtonDeleteClick(Notes notes);
        void onButtonEditClick(Notes notes);

    }
}
