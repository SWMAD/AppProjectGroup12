package dk.au.mad21spring.spacenewsapplication.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.au.mad21spring.spacenewsapplication.Database.Article;
import dk.au.mad21spring.spacenewsapplication.Database.Repository;
import dk.au.mad21spring.spacenewsapplication.R;

import static java.sql.Types.NULL;

public class ForegroundService extends Service {

    // Code regarding foreground service is inspired by code demo from class: " DemoServices"
    private static final String TAG="ForegroundService";
    public static final String SERVICE_CHANNEL = "serviceChannel";
    public static final int NOTIFICATION_ID = 55;

    private Repository repository;
    ExecutorService executorService;

    private boolean started = false;
    private int sleepTime = 10000;

    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        repository = Repository.getInstance(getApplication());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL, "Remember, you have saved an article", NotificationManager.IMPORTANCE_LOW);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, SERVICE_CHANNEL);

        notification = notificationBuilder
                .setContentTitle(getResources().getText(R.string.Service_welcome))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(NOTIFICATION_ID, notification);
        doBackgroundWork();

        return START_STICKY;
    }

    private void doBackgroundWork() {
        if(!started) {
            started = true;
            doSleepLoop();
        }
    }

    private void doSleepLoop() {
        final Handler h = new Handler(getMainLooper());

        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(sleepTime);
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Article readLaterArticle = repository.getReadLaterArticle();
                            if (readLaterArticle != null){
                                notification = notificationBuilder.setContentText(readLaterArticle.Title).setContentTitle(getResources().getText(R.string.Service_Remember)).build();
                                notificationManager.notify(NOTIFICATION_ID, notification);
                                Log.e(TAG, "Remember, you have saved an article: " + readLaterArticle.Title);
                            }
                            else{

                            }
                        }
                    });
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: EROOR", e);
                }

                if (started) {
                    doSleepLoop();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        started = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}