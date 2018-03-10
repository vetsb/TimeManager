package ru.dmitriylebyodkin.timemanager;

import android.app.Application;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by dmitr on 08.03.2018.
 */

public class App extends Application {
    private static final String TAG = "myLogs";
    public static String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Откбярь", "Ноябрь", "Декабрь"};
    public static String[] monthsR = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};

    public static String[] weekDays = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    public static String[] weekDaysShort = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    public static String getMonthByNumber(int position) {
        return Arrays.asList(months).get(position);
    }

    public static String getMonthRByNumber(int position) {
        return Arrays.asList(monthsR).get(position);
    }

    public static String getWeekDayByNumber(int position) {
        return Arrays.asList(weekDays).get(position-1);
    }

    public static String getWeekDayShortByNumber(int position) {
        return Arrays.asList(weekDaysShort).get(position-1);
    }

    public static String formatMoney(int sum) {
        final NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        return currencyInstance.format(sum);
    }

    public static String formatDate(int timestamp) {
        int currentTimestamp = (int) (System.currentTimeMillis()/1000L);
        int mod = currentTimestamp - timestamp;
        String dateText = "Только что";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp*1000L);

        if (mod <= 60*60 && mod > 60) {
            dateText = String.valueOf(mod / 60);

            if ((mod / 60) == 11) {
                dateText += " минут";
            } else {
                switch ((mod / 60) % 10) {
                    case 1:
                        dateText += " минута";
                        break;
                    case 2:
                    case 3:
                    case 4:
                        dateText += " минуты";
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 0:
                        dateText += " минут";
                        break;
                }
            }

            dateText += " назад";
        }

        if (mod > 60*60 && mod <= 60*60*24) {
            dateText = String.valueOf(mod / (60*60));

            switch (mod / (60*60)) {
                case 1:
                    dateText += " час";
                    break;
                case 2:
                case 3:
                case 4:
                    dateText += " часа";
                    break;
                default:
                    dateText += " часов";
                    break;
            }

            dateText += " назад";
        }

        if (mod > 60*60*24 && mod <= 60*60*24*2) {
            dateText = "Вчера в " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
        }

        if (mod > 60*60*24*2) {
            dateText = calendar.get(Calendar.DAY_OF_MONTH) + " " + App.getMonthRByNumber(calendar.get(Calendar.MONTH)) + " в " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
        }

        return dateText;
    }

    public static String formatDateWithoutTime(int timestamp) {
        int currentTimestamp = (int) (System.currentTimeMillis()/1000L);
        int mod = currentTimestamp - timestamp;
        String dateText = "Только что";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp*1000L);

        if (mod > 60*60 && mod < 60*60*24) {
            dateText = "Сегодня";
        }

        if (mod > 60*60*24 && mod <= 60*60*24*2) {
            dateText = "Вчера";
        }

        if (mod > 60*60*24*2) {
            dateText = calendar.get(Calendar.DAY_OF_MONTH) + " " + App.getMonthRByNumber(calendar.get(Calendar.MONTH));
        }

        return dateText;
    }

    public static String formatWord(int number, String[] titles) {
        int[] cases = new int[] {2, 0, 1, 1, 1, 2};
        return titles[ (number%100>4 && number%100<20)? 2 : cases[(number%10<5)?number%10:5] ];
    }

    public static String formatSeconds(int seconds) {
        String text = "";

        if (seconds < 60) {
            text = seconds + " " + formatWord(seconds, new String[] {"секунду", "секунды", "секунд"});
        }

        if (seconds >= 60 && seconds < 60*60) {
            text = seconds/60 + " " + formatWord(seconds/60, new String[] {"минуту", "минуты", "минут"});
        }

        if (seconds >= 60*60 && seconds < 60*60*24) {
            text = seconds/60/60 + " " + formatWord(seconds/60, new String[] {"час", "часа", "часов"});
        }

        if (seconds >= 60*60*24 && seconds < 60*60*24*30) {
            text = seconds/60/60/24 + " " + formatWord(seconds/60, new String[] {"день", "дня", "дней"});
        }

        if (seconds >= 60*60*24*30) {
            text = seconds/60/60/24/30 + " " + formatWord(seconds/60, new String[] {"месяц", "месяца", "месяцев"});
        }

        return text;
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getTimeZone("Europe/Moscow");
    }
}
