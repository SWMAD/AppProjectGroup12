package dk.au.mad21spring.spacenewsapplication.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.au.mad21spring.assignment2.au611785.Database.CityDTO;
import dk.au.mad21spring.assignment2.au611785.Database.Repository;
import dk.au.mad21spring.assignment2.au611785.R;

public class ForegroundService extends Service {

}