package xianjie.shen.firstlinecode.CoolWeather.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by shen on 2016/6/27.
 */
public class TimerUtil
{
    public static void setTimerTask(Context context, long intervalByHour, Class<?> startObjectClass)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(context, startObjectClass);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent1, 0);
        long interval = intervalByHour * 60 * 60 * 1000;//8小时的毫秒数
        long triggerTime = SystemClock.elapsedRealtime() + interval;
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        Log.e(Constants.TAG_BP_COOLWEATHER, "setTimerTask start (" + intervalByHour + ")");
    }
}
