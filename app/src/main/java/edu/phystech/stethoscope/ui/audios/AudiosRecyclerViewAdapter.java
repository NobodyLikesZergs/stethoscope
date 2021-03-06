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
    private OnClickListener cardOnClickListener;

    public interface OnClickListener {
        void onClick(List<Integer> selectionList, long id);
    }

    public AudiosRecyclerViewAdapter(OnClickListener cardOnClickListener) {
        this.cardOnClickListener = cardOnClickListener;
    }

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

    public void unselectAll() {
        selectionList = new ArrayList<>();
        if (cardOnClickListener != null) {
            cardOnClickListener.onClick(selectionList, -1);
        }
        notifyDataSetChanged();
    }

    public void removeSelected() {
        audios.removeAll(getSelectedAudios());
        selectionList = new ArrayList<>();
        if (cardOnClickListener != null) {
            cardOnClickListener.onClick(selectionList, -1);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, final int position) {
        holder.name.setText("Сердце   Точка " + (audios.get(position).getPoint()+1) +
                "   Запись " + audios.get(position).getNumber());
        if (selectionList.contains(position)) {
            holder.selection.setImageResource(R.drawable.selected);
        } else {
            holder.selection.setImageResource(R.drawable.not_selected);
        }
        holder.wholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelection(holder, position);
                if (cardOnClickListener != null) {
                    cardOnClickListener.onClick(selectionList, audios.get(position).getId());
                }
            }
        });
    }

    public List<Long> getSelectedIdList() {
        List<Long> result = new ArrayList<>();
        for (Integer selectedPos: selectionList) {
            result.add(audios.get(selectedPos).getId());
        }
        return result;
    }

    private void handleSelection(PersonViewHolder holder, int position) {
        if (selectionList.contains(position)) {
            selectionList.remove(Integer.valueOf(position));
            holder.selection.setImageResource(R.drawable.not_selected);
        } else {
            selectionList.add(position);
            holder.selection.setImageResource(R.drawable.selected);
        }
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView selection;
        View wholeView;

        public PersonViewHolder(View itemView) {
            super(itemView);
            wholeView = itemView;
            name = (TextView) itemView.findViewById(R.id.person_name_text_view);
            selection = (ImageView) itemView.findViewById(R.id.selection);
        }
    }
}
