// PartAdapter.java
package mx.edu.logincontrolalmacen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mx.edu.logincontrolalmacen.api.Part;

public class PartAdapter extends ArrayAdapter<Part> {

    private List<Part> originalParts;
    private List<Part> filteredParts;

    public PartAdapter(Context context, List<Part> parts) {
        super(context, 0, parts);
        this.originalParts = new ArrayList<>(parts);
        this.filteredParts = new ArrayList<>(parts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Part part = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.part_list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.part_name);
        ImageView imageView = convertView.findViewById(R.id.part_image);

        nameTextView.setText(part.getName());
        Glide.with(getContext()).load(part.getImageUrl()).into(imageView);

        return convertView;
    }

    public void updateData(List<Part> parts) {
        originalParts.clear();
        originalParts.addAll(parts);
        filter("");  // Reset the filtered data to show all parts
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredParts.clear();
        if (query.isEmpty()) {
            filteredParts.addAll(originalParts);
        } else {
            for (Part part : originalParts) {
                if (part.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredParts.add(part);
                }
            }
        }
        clear();
        addAll(filteredParts);
        notifyDataSetChanged();
    }
}
