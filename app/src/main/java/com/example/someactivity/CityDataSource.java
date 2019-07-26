package com.example.someactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;

//  Источник данных, позволяет изменять данные в таблице
// Создает и содержит в себе читатель данных
public class CityDataSource implements Closeable {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private CityDataReader cityDataReader;

    public CityDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Открывает базу данных
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        // создать читателя и открыть его
        cityDataReader = new CityDataReader(database);
        cityDataReader.open();
    }

    // Закрыть базу данных
    public void close() {
        cityDataReader.close();
        dbHelper.close();
    }

    // Добавить новую запись
    public  City addCity(String city, String country) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CITY, city);
        values.put(DatabaseHelper.COLUMN_COUNTRY, country);
        // Добавление записи
        long insertId = database.insert(DatabaseHelper.TABLE_CITY, null,
                values);
        City newCity = new City();
        newCity.setCity(city);
        newCity.setCountry(country);
        newCity.setId(insertId);
        return newCity;
    }

    // Изменить запись
    public void editNote(City note, String description, String title) {
        ContentValues editedNote = new ContentValues();
        editedNote.put(DatabaseHelper.COLUMN_ID, note.getId());
        editedNote.put(DatabaseHelper.COLUMN_CITY, description);
        editedNote.put(DatabaseHelper.COLUMN_COUNTRY, title);
        // изменение записи
        database.update(DatabaseHelper.TABLE_CITY,
                editedNote,
                DatabaseHelper.COLUMN_ID + " = " + note.getId(),
                null);
    }

    // Удалить запись
    public void deleteNote(City note) {
        long id = note.getId();
        database.delete(DatabaseHelper.TABLE_CITY, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    // Очистить таблицу
    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_CITY, null, null);
    }

    // вернуть читателя (он потребуется в других местах)
    public CityDataReader getCityDataReader() {
        return cityDataReader;
    }
}
