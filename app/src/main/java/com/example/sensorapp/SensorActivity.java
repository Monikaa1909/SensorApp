package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private List<Sensor> sensorList;

    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private int sensorCounter = 0;

    private static final String SENSOR_APP_TAG = "SENSOR_APP_TAG";
    private final List<Integer> favourSensors = Arrays.asList(Sensor.TYPE_AMBIENT_TEMPERATURE, Sensor.TYPE_LIGHT);
    public static final int SENSOR_DETAILS_ACTIVITY_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
//        setHasOptionsMenu(true);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorList.forEach(sensor -> {
            Log.d(SENSOR_APP_TAG, "Sensor name:" + sensor.getName() + "  Sensor vendor:" + sensor.getVendor() + "    Sensor max range:" + sensor.getMaximumRange());
        });

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String string = getString(R.string.sensors_count, sensorList.size());
        getSupportActionBar().setSubtitle(string);
        return true;
    }

    private class SensorHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
//        private final TextView typeTextView;

        private ImageView iconImageView;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));

            nameTextView = itemView.findViewById(R.id.sensor_name);
//            typeTextView = itemView.findViewById(R.id.sensor_type);
//            iconImageView = itemView.findViewById(R.id.list_item_icon);
        }

        public void bind(Sensor sensor) {
            nameTextView.setText(sensor.getName());
//            iconImageView.setImageResource(R.drawable.ic_baseline_child_care_24);

            View itemContainer = itemView.findViewById(R.id.list_item_sensor);
            if (favourSensors.contains(sensor.getType())) {
                itemContainer.setBackgroundColor(getResources().getColor(R.color.purple_200));
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivityForResult(intent, SENSOR_DETAILS_ACTIVITY_REQUEST_CODE);
                });
            }
        }

    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private List<Sensor> sensorList;

        public SensorAdapter(List<Sensor> sensorList) {
            this.sensorList = sensorList;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new SensorHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensorList.size();
        }
    }

}