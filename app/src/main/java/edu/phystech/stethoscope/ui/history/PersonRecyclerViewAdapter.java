package edu.phystech.stethoscope.ui.history;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Person;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.PersonViewHolder> {

    private List<Person> persons = new ArrayList<>();

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

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.name.setText(persons.get(position).getFirstName() + " " + persons.get(position).getLastName());
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public PersonViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_name_text_view);
        }
    }
}
