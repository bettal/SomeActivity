package com.example.someactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;

// Читатель источника данных на основе курсора
// Этот класс был вынесен из NoteDataSource, чтобы разгрузить его
public class CityDataReader implements Closeable {

    private Cursor cursor;              // Курсор: фактически это подготовенный запрос,
                                        // но сами данные считываются только по необходимости

    private final SQLiteDatabase database;

    private final String[] notesAllColumn = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CITY,
            DatabaseHelper.COLUMN_COUNTRY
    };

    public CityDataReader(SQLiteDatabase database) {
        this.database = database;
    }

    // Подготовить к чтению таблицу
    public void open() {
        query();
        cursor.moveToFirst();
    }

    public void close() {
        cursor.close();
    }

    // Перечитать таблицу
    public void refresh() {
        int position = cursor.getPosition();
        query();
        cursor.moveToPosition(position);
    }

    // создание запроса
    private void query() {
        cursor = database.query(DatabaseHelper.TABLE_CITY,
                notesAllColumn, null, null,
                null, null, null);
    }

    // прочитать данные по определенной позиции
    public City getPosition(int position) {
        cursor.moveToPosition(position);
        return cursorToCity();
    }

    // получить количество строк в таблице
    public int getCount() {
        return cursor.getCount();
    }

    // преобразователь курсора в объект
    private City cursorToCity() {
        City city = new City();
        city.setId(cursor.getLong(0));
        city.setCity(cursor.getString(1));
        city.setCountry(cursor.getString(2));
        return city;
    }
}
