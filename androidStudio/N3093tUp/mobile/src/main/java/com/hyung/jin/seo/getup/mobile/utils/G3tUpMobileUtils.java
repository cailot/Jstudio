package com.hyung.jin.seo.getup.mobile.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by jinseo on 2016. 4. 16..
 */
public class G3tUpMobileUtils
{
    /**
     * Check whether today belongs to pre-defined days in Preferences
     * @param list enabled days in Preferences
     * @return true if alarm needs today; otherwise false
     */
    public static boolean isDayForAlarm(String[] list)
    {
        if((list==null) || (list.length==0)) return false;
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String day = "";
        switch(dayOfWeek)
        {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return Arrays.asList(list).contains(day);
    }

    /**
     * Get alarm time.
     * If time is already past, get the alarm time on next day
     * @return
     */
    public static Calendar getAlarmTime(int hour, int minute) {
        GregorianCalendar current = new GregorianCalendar();
        GregorianCalendar alarm = new GregorianCalendar();
        alarm.set(Calendar.YEAR, current.get(Calendar.YEAR));
        alarm.set(Calendar.MONTH,
                current.get(Calendar.MONTH));
        alarm.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        alarm.set(Calendar.HOUR_OF_DAY, hour);
        alarm.set(Calendar.MINUTE, minute);

        // alarm time already passes the current time, then start from tomorrow
        if(alarm.getTimeInMillis() < current.getTimeInMillis()){
            alarm.set(Calendar.DAY_OF_MONTH, alarm.get(Calendar.DAY_OF_MONTH)+1);
        }
        return alarm;
    }
}
