package com.example.socinetandroid.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DateFormatter {
    private static final Map<DayOfWeek, String> daysMap = new HashMap<>();

    static {
        daysMap.put(DayOfWeek.MONDAY, "T2");
        daysMap.put(DayOfWeek.TUESDAY, "T3");
        daysMap.put(DayOfWeek.WEDNESDAY, "T4");
        daysMap.put(DayOfWeek.THURSDAY, "T5");
        daysMap.put(DayOfWeek.FRIDAY, "T6");
        daysMap.put(DayOfWeek.SATURDAY, "T7");
        daysMap.put(DayOfWeek.SUNDAY, "CN");
    }

    public static String getTimeDifference(Date inputDate){
        Date currentDate = new Date();
        long timeDifference = currentDate.getTime() - inputDate.getTime();

        long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        long hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDifference);
        long daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifference);

        if (minutesDifference < 1) {
            return "vài giây trước";
        } else if (minutesDifference < 60) {
            return minutesDifference + " phút trước";
        } else if (hoursDifference < 24) {
            return hoursDifference + " giờ trước";
        } else if (daysDifference < 7) {
            return daysDifference + " ngày trước";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime localDateTime = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return localDateTime.format(formatter);
        }
    }

    public static String getDetailTime(Date inputDate){
        LocalDateTime dateTime = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDate inputLocalDate = dateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Kiểm tra nếu ngày là hôm nay
        if (inputLocalDate.isEqual(today)) {
            return dateTime.format(timeFormatter);
        }

        // Kiểm tra nếu ngày nằm trong tuần này
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        if (inputLocalDate.isAfter(startOfWeek.minusDays(1)) && inputLocalDate.isBefore(endOfWeek.plusDays(1))) {
            DayOfWeek dayOfWeek = inputLocalDate.getDayOfWeek();
            String dayName = daysMap.get(dayOfWeek);
            return dayName + " " + dateTime.format(timeFormatter);
        }

        // Nếu không phải hôm nay và không nằm trong tuần này
        return dateTime.format(dateFormatter);
    }
}
