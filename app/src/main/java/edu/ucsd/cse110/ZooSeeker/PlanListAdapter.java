package edu.ucsd.cse110.ZooSeeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    private List<ExhibitListItem> planItems = Collections.emptyList();
    private List<String> distance = Collections.emptyList();


    public void setPlanListItems(List<ExhibitListItem> newPlanItems){
        this.planItems.clear();
        this.planItems = newPlanItems;
        notifyDataSetChanged();
    }

    public void setDistance(List<String> newDistance){
        this.distance.clear();
        this.distance = newDistance;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.plan_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanListAdapter.ViewHolder holder, int position) {
        holder.setTodoItem(planItems.get(position), "1 foot");
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView exhibitName;
        private ExhibitListItem planItem;
        private final TextView distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.exhibitName = itemView.findViewById(R.id.plan_item_text);
            this.distance = itemView.findViewById(R.id.distance_text);
        }

        public ExhibitListItem getPlanItem() {
            return planItem;
        }

        public void setTodoItem(ExhibitListItem planItem, String distance) {
            this.planItem = planItem;
            this.exhibitName.setText(planItem.exhibitName);
            //this.distance.setText(distance);
        }
    }
}
