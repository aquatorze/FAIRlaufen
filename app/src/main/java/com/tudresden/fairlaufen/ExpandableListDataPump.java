package com.tudresden.fairlaufen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    static DatabaseHelper dbHelper;
    static HashMap<String, List<String>> getData(Context context) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.createDataBase();
        } catch (IOException ioe) {
        }
        SQLiteDatabase database = dbHelper.getDataBase();
        //SQLiteDatabase database = MapActivity.dbHelper.getDataBase();

        List<String> secondHand = new ArrayList<>();
        Cursor dbCursor1 = database.rawQuery("SELECT * FROM fairPlaces WHERE category like 'Second Hand'", null);
        int length = dbCursor1.getCount();
        dbCursor1.moveToFirst();
        for (int i = 0; i < length; i++) {
                String placeName = dbCursor1.getString(1);
                //System.out.println(placeName);
                secondHand.add(placeName);
                dbCursor1.moveToNext();
        }



        List<String> cosmetics = new ArrayList<>();
        Cursor dbCursor2 = database.rawQuery("SELECT * FROM fairPlaces WHERE category like 'Kosmetik'", null);
        dbCursor2.moveToFirst();
        for (int i = 0; i < dbCursor2.getCount(); i++) {
            String cosmeticName = dbCursor2.getString(1);
            //System.out.println(placeName);
            cosmetics.add(cosmeticName);
            dbCursor2.moveToNext();
        }

        List<String> clothing = new ArrayList<>();
        Cursor dbCursor3 = database.rawQuery("SELECT * FROM fairPlaces WHERE category like 'Bekleidung'", null);
        dbCursor3.moveToFirst();
        for (int i = 0; i < dbCursor3.getCount(); i++) {
            String clothingName = dbCursor3.getString(1);
            //System.out.println(placeName);
            clothing.add(clothingName);
            dbCursor3.moveToNext();
        }

        List<String> food = new ArrayList<>();
        Cursor dbCursor4 = database.rawQuery("SELECT * FROM fairPlaces WHERE category like 'Lebensmittel'", null);
        dbCursor4.moveToFirst();
        for (int i = 0; i < dbCursor4.getCount(); i++) {
            String foodName = dbCursor4.getString(1);
            //System.out.println(placeName);
            food.add(foodName);
            dbCursor4.moveToNext();
        }

        List<String> foodDrinks = new ArrayList<>();
        Cursor dbCursor5 = database.rawQuery("SELECT * FROM fairPlaces WHERE category like 'Essen & Trinken'", null);
        dbCursor5.moveToFirst();
        for (int i = 0; i < dbCursor5.getCount(); i++) {
            String foodDrinksName = dbCursor5.getString(1);
            //System.out.println(placeName);
            foodDrinks.add(foodDrinksName);
            dbCursor5.moveToNext();
        }

        List<String> others = new ArrayList<>();
        Cursor dbCursor6 = database.rawQuery("SELECT * FROM fairPlaces WHERE category like 'Sonstiges'", null);
        dbCursor6.moveToFirst();
        for (int i = 0; i < dbCursor6.getCount(); i++) {
            String othersName = dbCursor6.getString(1);
            //System.out.println(placeName);
            others.add(othersName);
            dbCursor6.moveToNext();
        }


        expandableListDetail.put("Second Hand", secondHand);
        expandableListDetail.put("Kosmetik", cosmetics);
        expandableListDetail.put("Bekleidung", clothing);
        expandableListDetail.put("Lebensmittel", food);
        expandableListDetail.put("Essen & Trinken", foodDrinks);
        expandableListDetail.put("Sonstiges", others);
        return expandableListDetail;
    }
}