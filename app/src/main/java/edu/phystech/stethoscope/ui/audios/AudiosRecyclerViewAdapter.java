package edu.phystech.stethoscope.ui.audios;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Audio;

public class AudiosRecyclerViewAdapter extends
        RecyclerView.Adapter<AudiosRecyclerViewAdapter.PersonViewHolder> {

    private List<Audio> audios = new ArrayList<>();
    private List<Integer> selectionList = new ArrayList<>();

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.audios_card_view,
                parent, false);
        return new PersonViewHolder(v);
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
        notifyDataSetChanged();
    }

    public List<Audio> getSelectedAudios() {
        List<Audio> res = new ArrayList<>();
        for (int i: selectionList) {
            res.add(audios.get(i));
        }
        return res;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, final int position) {
        holder.name.setText("Сердце   Точка " + (audios.get(position).getPoint()+1) +
                "   Запись " + audios.get(position).getNumber());
        holder.selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionList.contains(position)) {
                    selectionList.remove(Integer.valueOf(position));
                    holder.selection.setImageResource(R.drawable.not_selected);
                } else {
                    selectionList.add(position);
                    holder.selection.setImageResource(R.drawable.selected);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView selection;

        public PersonViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_name_text_view);
            selection = (ImageView) itemView.findViewById(R.id.selection);
        }
    }
}
