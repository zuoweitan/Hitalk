package com.zuowei.utils.common;

import com.vivifram.second.hitalk.bean.TimeInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zuowei on 16-8-2.
 */
public class DateUtils {
    private static final long INTERVAL_IN_MILLISECONDS = 30000L;

    public static String getTimestampString(Date date) {
        String tamp = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean isZh = var2.startsWith("zh");
        long time = date.getTime();
        if(isSameDay(time)) {
            if(isZh) {
                tamp = "aa hh:mm";
            } else {
                tamp = "hh:mm aa";
            }
        } else if(isYesterday(time)) {
            if(!isZh) {
                return "Yesterday " + (new SimpleDateFormat("hh:mm aa", Locale.ENGLISH)).format(date);
            }
            tamp = "昨天aa hh:mm";
        } else if(isZh) {
            tamp = "M月d日aa hh:mm";
        } else {
            tamp = "MMM dd hh:mm aa";
        }

        return isZh?(new SimpleDateFormat(tamp, Locale.CHINESE)).format(date)
                :(new SimpleDateFormat(tamp, Locale.ENGLISH)).format(date);
    }

    private static boolean isSameDay(long time) {
        TimeInfo todayStartAndEndTime = getTodayStartAndEndTime();
        return time > todayStartAndEndTime.getStartTime() && time < todayStartAndEndTime.getEndTime();
    }

    private static boolean isYesterday(long time) {
        TimeInfo yesterdayStartAndEndTime = getYesterdayStartAndEndTime();
        return time > yesterdayStartAndEndTime.getStartTime() && time < yesterdayStartAndEndTime.getEndTime();
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Date startDate = start.getTime();
        long startTime = startDate.getTime();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        Date endDate = end.getTime();
        long endTime = endDate.getTime();
        TimeInfo todayTimeInfo = new TimeInfo();
        todayTimeInfo.setStartTime(startTime);
        todayTimeInfo.setEndTime(endTime);
        return todayTimeInfo;
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Date startDate = start.getTime();
        long startTime = startDate.getTime();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_MONTH, -1);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        Date endDate = end.getTime();
        long endTime = endDate.getTime();
        TimeInfo yesterdayTimeInfo = new TimeInfo();
        yesterdayTimeInfo.setStartTime(startTime);
        yesterdayTimeInfo.setEndTime(endTime);
        return yesterdayTimeInfo;
    }

    public static boolean isCloseEnough(long current, long pre) {
        long diff = current - pre;
        if(diff < 0L) {
            diff = - diff;
        }
        return diff < INTERVAL_IN_MILLISECONDS;
    }
}
