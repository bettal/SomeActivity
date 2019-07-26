package com.example.someactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    final int secondActivityResultEvent = 9876;
    private TextView contentTextView, textTemp, viewTemperature, textHumid, viewHumidity;
    private SensorManager sensorManager;
    private String txtKey ,humidityKey ,temperatureKey, textContent;
    private Sensor sensorTemperature, sensorHumidity;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtKey = getString(R.string.textKey);
        humidityKey = getString(R.string.humidityKey);
        temperatureKey = getString(R.string.temperatureKey);
        textContent = getString(R.string.textContent);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentTextView = findViewById(R.id.content);
        contentTextView.setText(textContent);

        textTemp = findViewById(R.id.textTemp);
        viewTemperature = findViewById(R.id.temp);
        textHumid = findViewById(R.id.textHumid);
        viewHumidity = findViewById(R.id.humd);



        FloatingActionButton fab = findViewById(R.id.fab);
        // Обработка нажатия на плавающую кнопку
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Здесь вылетит Snackbar
                Snackbar.make(view, getString(R.string.floatingActionButton), Snackbar.LENGTH_LONG).show();
            }
        });

        Button buttonStartService = findViewById(R.id.buttonStartService);
        buttonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, MainService.class));
            }
        });



        // Менеджер датчиков
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Датчик температуры
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        //Датчик давления (TYPE_ABSOLUTE_HUMIDITY - отсутсвует)
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Размещение меню в action bar
        // если он присутствует.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Управление касаниями на action bar.
        // Action bar будет автоматически управлять нажатиями на Home/Up кнопку
        // Вы это можете указать в родительской activity в файле манифеста.


        Intent intent1 = new Intent(this,InputActivity.class);
        startActivityForResult(intent1, secondActivityResultEvent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){

            int viewHumidityVisible = data.getIntExtra(humidityKey, 1);
            int viewTemperatureVisible = data.getIntExtra(temperatureKey,1);
            String text = data.getStringExtra (txtKey);
            //showToast (text);
            //Выведем текс из параметров, если не пусто
            if (!text.isEmpty()) {
                contentTextView.setText(text);
            }
            //Устанавливаем видимость вывода датчиков
           if (viewTemperatureVisible == 0) {
                textTemp.setVisibility(View.GONE);
                viewTemperature.setVisibility(View.GONE);
            }else{
               textTemp.setVisibility(View.VISIBLE);
               viewTemperature.setVisibility(View.VISIBLE);
           }

            if (viewHumidityVisible == 0) {
                textHumid.setVisibility(View.GONE);
                viewHumidity.setVisibility(View.GONE);
            }else{
                textHumid.setVisibility(View.VISIBLE);
                viewHumidity.setVisibility(View.VISIBLE);

            }


        }else {showToast (getText(R.string.wrong));}

    }

    // Регистрируем слушатели датчиков
    @Override
    protected void onResume() {
        super.onResume();
        //Listener temperature
        sensorManager.registerListener(listenerTemperature, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        //Listener Humidity
        sensorManager.registerListener(listenerHumidity, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
    }



    // Если приложение свернуто, то не будем тратить энергию на получение информации по датчикам
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerTemperature, sensorTemperature);
        sensorManager.unregisterListener(listenerHumidity, sensorHumidity);

    }

    //Вывод датчика температуры
    private void showTemperatureSensors(SensorEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(R.string.temperatureSensorValue).append(event.values[0])
                .append("\n");
        textTemp.setText(stringBuilder);
    }

    //Вывод датчика давления
    private void showHumiditySensors(SensorEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(R.string.humiditySensorValue).append(event.values[0])
                .append("\n");
        textHumid.setText(stringBuilder);
    }

    //Слушатель датчика температуры
    private final SensorEventListener listenerTemperature = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            showTemperatureSensors(event);
        }
    };

    //Слушатель датчика давления
    private final SensorEventListener listenerHumidity = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            showHumiditySensors(event);
        }
    };
    private void showToast(CharSequence toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

}


