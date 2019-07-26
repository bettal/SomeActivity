package com.example.someactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class InputActivity extends AppCompatActivity {
    private EditText editText;
    private CheckBox checkBoxHumidity, checkBoxTemperature;
    private Button saveBtn, loadBtn;

    private CityDataSource cityDataSource;     // Источник данных
    private CityDataReader cityDataReader;      // Читатель данных
    private String preferenceName;    // имя файла настроек

    private String txtKey ,humidityKey ,temperatureKey, fileName;
    private static final String TAG = MainActivity.class.getSimpleName();

    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        initDataSource();

        txtKey = getString(R.string.textKey);
        humidityKey = getString(R.string.humidityKey);
        temperatureKey = getString(R.string.temperatureKey);
        fileName = getString(R.string.savedText);
        preferenceName = getString(R.string.preferenceFileName);

        editText = findViewById(R.id.editText);
        saveBtn = findViewById(R.id.buttonSave);
        loadBtn = findViewById(R.id.buttonLoad);
        checkBoxHumidity = findViewById(R.id.sensorHumidity);
        checkBoxTemperature = findViewById(R.id.sensorTemperature);

        Button clearButton = findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(clearListener);

        checkBoxTemperature.setChecked(true);
        checkBoxHumidity.setChecked(true);


        Button deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(deleteListener);
        setOnClickBtnBehaviour();
        try{
            SharedPreferences sharedPref = getSharedPreferences(preferenceName, MODE_PRIVATE);
            loadPreferences(sharedPref);
        }catch (Exception e){
            Log.e(TAG, getString(R.string.loadFileError), e);
        }
    }


    private void setOnClickBtnBehaviour() {
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SharedPreferences sharedPref = getSharedPreferences(preferenceName, MODE_PRIVATE);
                    loadPreferences(sharedPref);
                }catch (Exception e){
                    Log.e(TAG, getString(R.string.loadFileError), e);
                }

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    // получаем файл настроек по имени файла, хранящемуся в preferenceName
                    SharedPreferences sharedPref = getSharedPreferences(preferenceName, MODE_PRIVATE);
                    savePreferences(sharedPref);    // сохранить настройки

                }catch (Exception e){
                    Log.e(TAG, getString(R.string.saveFileError), e);
                }

                addElement();
                CityDataSource.addCity(editText.getText().toString(),getString(R.string.RU));
                dataUpdated();

                // Формируем посылку

                int flagHumidity =  (checkBoxHumidity.isChecked()) ? 1 : 0;
                int flagTemperature = (checkBoxTemperature.isChecked()) ? 1 : 0;
                String text = editText.getText().toString();

                Intent intent = new Intent(InputActivity.this, MainActivity.class);
                intent.putExtra(humidityKey, flagHumidity);
                intent.putExtra(temperatureKey, flagTemperature);
                intent.putExtra(txtKey, text);
                setResult(RESULT_OK, intent);// Отправляем посылку

                finish();

            }
        });


    }
    private void initDataSource() {
        cityDataSource = new CityDataSource(getApplicationContext());
        cityDataSource.open();
        cityDataReader = cityDataSource.getCityDataReader();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private final View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editText.setText(R.string.empty);
        }
    };


    private final View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(getApplicationContext().getFilesDir(), fileName);
            if (file.exists()) {
                file.delete();
                //showToast(getText(R.string.toast_file_deleted));
                clearListener.onClick(view);
            }
            else
                showToast(getText(R.string.toast_file_not_exist));
        }
    };

    private void showToast(CharSequence toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    // сохраняем настройки
    private void savePreferences(SharedPreferences sharedPref){

        String[] keys = {txtKey, temperatureKey,humidityKey};
        String[] values = {editText.getText().toString(), String.valueOf(checkBoxTemperature.isChecked()),String.valueOf(checkBoxHumidity.isChecked())};

        // для сохранения настроек надо воспользоваться классом Editor
        SharedPreferences.Editor editor = sharedPref.edit();

        // теперь в Editor установим значения
        for (int i=0;i<3;i++) {editor.putString(keys[i], values[i]);}

        // и сохраним файл настроек
        editor.apply();
    }

    private void loadPreferences(SharedPreferences sharedPref){
        String[] keys = {txtKey, temperatureKey,humidityKey};

        // для получения настроек нет необходимости в Editor, получаем их прямо из SharedPreferences
        editText.setText(sharedPref.getString(keys[0], getString(R.string.textContent)));
        checkBoxTemperature.setChecked(Boolean.valueOf(sharedPref.getString(keys[1], "true")));
        checkBoxHumidity.setChecked(Boolean.valueOf(sharedPref.getString(keys[2], "true")));

    }

    private void addElement() {

        CityDataSource.addCity(editText.getText().toString(),getString(R.string.RU));
        dataUpdated();
    }
    private void dataUpdated() {
        cityDataReader.refresh();
    }

}
