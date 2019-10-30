package camera.filters.hdresult.myapplication123;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import android.widget.AnalogClock;
import android.widget.RemoteViews;
// import com.example.mac.codematicslauncher.R;

/**
 * Simple widget to show analog clock.
 */


public class WidgetProvider extends AppWidgetProvider {

    private Intent mUpdateService;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                AppWidgetManager.ACTION_APPWIDGET_DISABLED)) {
            mUpdateService = new Intent(); //ensure this isn't null to prevent NPE
            context.stopService(mUpdateService);
        } else {
            if (mUpdateService == null)
                mUpdateService = new Intent(context, UpdateService.class);
            context.startService(mUpdateService);
        }
    }

    public static class UpdateService extends Service {

        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

            private boolean isScreenOn = true;

            @Override
            public void onReceive(Context context, Intent intent) {

                String key = intent.getAction();
                if (key.equals(Intent.ACTION_SCREEN_ON)) {
                    isScreenOn = true;
                    updateWidget();
                } else if (key.equals(Intent.ACTION_SCREEN_OFF)) {
                    isScreenOn = false;
                } else if (isScreenOn) {
                    updateWidget();
                }
            }

        };

        private RemoteViews mRemoteViews;
        private AnalogClock mAnalogClock;

        @Override
        public void onCreate() {
            mRemoteViews = new RemoteViews(getPackageName(),
                    R.layout.analog_appwidget);
            mRemoteViews.setOnClickPendingIntent(
                    R.id.analog_appwidget,
                    PendingIntent.getActivity(this, 0,
                            Utils.getAlarmIntent(this), 0));

           mAnalogClock = new AnalogClock(getResources());

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(mReceiver, filter);
        }
        // osama jadoon

        @Override
        public void onDestroy() {
            unregisterReceiver(mReceiver);
        }

        @Override
        public void onStart(Intent intent, int startId) {
            updateWidget();
        }

        private void updateWidget() {
            mRemoteViews.setImageViewBitmap(R.id.analog_appwidget, mAnalogClock.draw());

            AppWidgetManager.getInstance(this).updateAppWidget(
                            new ComponentName(this, WidgetProvider.class), mRemoteViews);
        }


        private void upDateWidget()

        {
           // mRemoteViews.setImageViewBitmap(R.id.analog_appwidget, mAnalogClock.draw());
            AppWidgetManager.getInstance(this)
                    .updateAppWidget(new ComponentName(this, WidgetProvider.class),
                            mRemoteViews);

        }


        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

    }

}
