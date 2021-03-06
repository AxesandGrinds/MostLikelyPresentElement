package com.app.ej.ms.CSVRawFileReader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.app.ej.ms.R;

public class ItemArrayAdapterSpectrum extends ArrayAdapter<String[]> {
	private List<String[]> scoreList = new ArrayList<String[]>();

    static class ItemViewHolder {
        TextView wavelength;
        TextView intensity;
    }

    public ItemArrayAdapterSpectrum(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

	@Override
	public void add(String[] object) {
		scoreList.add(object);
		super.add(object);
	}

    @Override
	public int getCount() {
		return this.scoreList.size();
	}

    @Override
	public String[] getItem(int index) {
		return this.scoreList.get(index);
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
        ItemViewHolder viewHolder;
		if (row == null) {

			LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.item_layout_spectrum, parent, false);
            viewHolder              = new ItemViewHolder();

            viewHolder.wavelength   = (TextView) row.findViewById(R.id.wavelengthSpectrum);
            viewHolder.intensity    = (TextView) row.findViewById(R.id.intensitySpectrum);
            row.setTag(viewHolder);

		} else {
            viewHolder = (ItemViewHolder)row.getTag();
        }

        String[] stat = getItem(position);
        viewHolder.wavelength.setText(stat[0]);
        viewHolder.intensity.setText(stat[1]);
		return row;
	}
}
