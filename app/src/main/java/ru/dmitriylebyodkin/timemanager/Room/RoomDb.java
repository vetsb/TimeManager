package ru.dmitriylebyodkin.timemanager.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.dmitriylebyodkin.timemanager.Room.Dao.ExItemDao;
import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Dao.TaskDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;

/**
 * Created by dmitr on 08.03.2018.
 */

@Database(entities = { Task.class, Execution.class, ExItem.class}, version = 1, exportSchema = false)

@TypeConverters(RoomDb.Converter.class)
public abstract class RoomDb extends RoomDatabase {

    private static final String DB_NAME = "roomDatabase.db";
    private static volatile RoomDb instance;

    public static synchronized RoomDb getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static RoomDb create(final Context context) {
        return Room.databaseBuilder(
                context,
                RoomDb.class,
                DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public abstract TaskDao getTaskDao();

    public abstract ExecutionDao getExecutionDao();

    public abstract ExItemDao getExItemDao();

    public static class Converter {
        @TypeConverter
        public static ArrayList<String> fromString(String value) {
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public static String fromArrayLisr(ArrayList<String> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    }
}
