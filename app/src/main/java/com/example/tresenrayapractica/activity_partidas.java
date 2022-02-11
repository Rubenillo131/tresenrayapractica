package com.example.tresenrayapractica;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class activity_partidas extends AppCompatActivity {
    TextView datalist;
    SQLiteHelper sql;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partidas);
        datalist = findViewById(R.id.showdb1);
        sql = new SQLiteHelper(this);
        db = sql.getWritableDatabase();

    }

    public void search(Cursor cursor){
        cursor.moveToFirst();
        int r=cursor.getCount();
        int c=cursor.getColumnCount();
        String row="\n";
        for (int i = 0; i < r; i++) {
            row="\n";
            for(int j=0;j<c;j++){
                row=row+cursor.getString(j)+" ";
            }
            datalist.append(row);
            cursor.moveToNext();
        }
    }
    public void all(View view) {
        datalist.setText("");
        db=sql.getReadableDatabase();
        Cursor c=db.query("resultados", null, null, null, null, null,null);
        search(c);
    }
}