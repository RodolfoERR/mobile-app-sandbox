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

import java.util.List;

import mx.edu.logincontrolalmacen.api.Part;

public class PartAdapter extends ArrayAdapter<Part> {

    public PartAdapter(Context context, List<Part> parts) {
        super(context, 0, parts);
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
        clear();
        addAll(parts);
        notifyDataSetChanged();
    }
}
