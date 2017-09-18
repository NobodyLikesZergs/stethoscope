package edu.phystech.stethoscope.ui.history;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Person;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.PersonViewHolder> {

    private List<Person> persons = new ArrayList<>();
    private OnClickListener cardOnClickListener;
    private OnClickListener onSelectionClickListener;
    private List<Integer> selectionList = new ArrayList<>();

    public interface OnClickListener {
        void onClick(List<Integer> selectionList, long id);
    }

    public PersonRecyclerViewAdapter(OnClickListener onClickListener, OnClickListener onSelectionClickListener) {
        this.cardOnClickListener = onClickListener;
        this.onSelectionClickListener = onSelectionClickListener;
    }

    public List<Person> getSelectedPersons() {
        List<Person> res = new ArrayList<>();
        for (int i: selectionList) {
            res.add(persons.get(i));
        }
        return res;
    }

    public void removeSelected() {
        persons.removeAll(getSelectedPersons());
        selectionList = new ArrayList<>();
        if (onSelectionClickListener != null) {
            onSelectionClickListener.onClick(selectionList, -1);
        }
        notifyDataSetChanged();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_card_view,
                parent, false);
        return new PersonViewHolder(v);
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
        notifyDataSetChanged();
    }

    public void unselectAll() {
        selectionList = new ArrayList<>();
        if (onSelectionClickListener != null) {
            onSelectionClickListener.onClick(selectionList, -1);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, final int position) {
        holder.name.setText(persons.get(position).getFirstName() + " " + persons.get(position).getLastName());
        if (selectionList.contains(position)) {
            holder.selection.setImageResource(R.drawable.selected);
        } else {
            holder.selection.setImageResource(R.drawable.not_selected);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardOnClickListener != null) {
                    cardOnClickListener.onClick(selectionList, persons.get(position).getId());
                }
            }
        });
        holder.selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelection(holder, position);
                if (onSelectionClickListener != null) {
                    onSelectionClickListener.onClick(selectionList, persons.get(position).getId());
                }
            }
        });
    }

    public List<Long> getSelectedIdList() {
        List<Long> result = new ArrayList<>();
        for (Integer selectedPos: selectionList) {
            result.add(persons.get(selectedPos).getId());
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
        return persons.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {

        View cardView;
        TextView name;
        ImageView selection;

        public PersonViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            name = (TextView) itemView.findViewById(R.id.person_name_text_view);
            selection = (ImageView) itemView.findViewById(R.id.selection);
        }
    }
}
