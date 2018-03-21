package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by dmitr on 08.03.2018.
 */

@Entity
public class Task {
    public static final String[] UNITS = new String[] {
            "секунды",
            "минуты",
            "часы",
    };

    public static final String[] DIFFICULTIES = new String[] {
            "Низкий",
            "Средний",
            "Высокий"
    };

    public static final String[] LABELS = new String[] {
            "В пути",
            "Еда",
            "Интернет",
            "Личное",
            "Обучение",
            "Перерыв",
            "Работа",
            "Работа по дому",
            "Сон",
            "ТВ",
            "Тренировки",
            "Чтение"
    };

//    public static List<Label> labelList = new ArrayList<>();

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private int planTime;
    private int unit;
    private String description;
    private int difficulty;
    private int label;
    private int timestampStart;
    private int timestampDeadline;
    private int createdAt = (int) (System.currentTimeMillis()/1000L);
    private int updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlanTime() {
        return planTime;
    }

    public void setPlanTime(int planTime) {
        this.planTime = planTime;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(int timestampStart) {
        this.timestampStart = timestampStart;
    }

    public int getTimestampDeadline() {
        return timestampDeadline;
    }

    public void setTimestampDeadline(int timestampDeadline) {
        this.timestampDeadline = timestampDeadline;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getPlanSeconds() {
        switch (this.getUnit()) {
            case 0:
                return getPlanTime();
            case 1:
                return getPlanTime()*60;
            case 2:
                return getPlanTime()*60*60;
        }

        return 0;
    }

//    public static List<Label> getLabelList() {
//        labelList = new ArrayList<>();
//        labelList.add(new Label(1, R.drawable.work, "Работа"));
//        labelList.add(new Label(2, R.drawable.home, "Дом"));
//        labelList.add(new Label(3, R.drawable.family, "Семья"));
//
//        return labelList;
//    }
//
//    public static String getLabelTitle(int position) {
//        return labelList.get(position).getTitle();
//    }
}
