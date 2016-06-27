package xianjie.shen.firstlinecode.CoolWeather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xianjie.shen.firstlinecode.CoolWeather.service.AutoUpdateService;

/**
 * Created by shen on 2016/6/27.
 */
public class AutoUpdateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
