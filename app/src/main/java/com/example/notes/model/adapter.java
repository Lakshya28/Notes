package com.example.notes.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.notedetails;

import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.viewholder> {
    List<String>titles;
    List<String>contents;
    public adapter(List<String>title,List<String>content){
        this.titles=title;
        this.contents=content;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.note_view_layout,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, final int position) {

        holder.content.setText(contents.get(position));
        holder.title.setText(titles.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(view.getContext(), notedetails.class);
                i.putExtra("title",titles.get(position));
                i.putExtra("content",contents.get(position));
                view.getContext().startActivity(i);

                //Toast.makeText(view.getContext(), "The item is clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView content,title;

         public viewholder(@NonNull View itemView) {
            super(itemView);
            content=itemView.findViewById(R.id.note_text);
            title=itemView.findViewById(R.id.note_title);
        }
    }
}
