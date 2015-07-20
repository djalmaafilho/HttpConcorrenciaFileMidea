package com.example.httpconcorrenciafilemidea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class FotoService extends Service implements PictureCallback {

	public static final int NOTIFICATION_ID = 1;  
	private NotificationManager mNotificationManager;  
	private NotificationCompat.Builder builder;
	private int contador = 1;
	private int MAXIMO = 10;
	private Camera c;
	private CameraPreview mPreview;
	private Handler handler;
	private Runnable r;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			if (checkCameraHardware(getBaseContext())) {
				c = getCameraInstance();
				if (c != null) {
					mPreview = new CameraPreview(this, c);

					handler = new Handler();
					r = new Runnable() {
						@Override
						public void run() {
							tirarFoto();
						}
					};
				} else {
					Log.i("DJPS","CAMERA N�O INICIADA");
				}
			} else {
				Log.i("DJPS","SEM CAMERA");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (c != null) {
			//c.startPreview();
			Log.i("DJPS", "Iniciando Preview Camera");
			handler.postDelayed(r, 5000);
		} else {
			Log.i("DJPS", "Sem camera n�o iniciado Preview");
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	void tirarFoto() {
		if(c != null){
            c.startPreview();
            c.takePicture(null, null, this);
			Log.i("DJPS", "pegando figura");
			sendNotification("pegando figura");
		}
	}
	
	
	@Override
	public void onDestroy() {
		try {
			Log.i("DJPS", "SERVICO PARADO");
			
			if(c!=null){
				handler.removeCallbacksAndMessages(null);
				c.stopPreview();
				c.release();
				c = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		//tenta limpar memoria
		System.gc();
		try {
			salvarArquivo(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		c.stopPreview();
		Log.i("DJPS", "Parando Preview Camera");

		c.startPreview();
		Log.i("DJPS", "Iniciando Preview Camera");
		
		//apenas para demostracao de forma de parar o servico
		if(contador == MAXIMO){
			handler.removeCallbacks(r);
			stopSelf();
		}else{
			contador ++;
			handler.postDelayed(r, 5000);
			Log.i("DJPS", "Agendando nova foto");
			sendNotification("Agendando nova foto");
		}
	}
	
	public void salvarArquivo(byte[] bytes) throws IOException{
		File pictureFile = ArquivoUtil.getOutputMediaFile(1,
				ArquivoUtil.recuperarPathPastaDim());
		if (pictureFile == null) {
			Toast.makeText(getBaseContext(), "Arquivo nao foi criado ",
					Toast.LENGTH_LONG).show();
			Log.d("DJPS",
					"Arquivo nao foi criado, cheque as permissoes storage: ");
			return;
		}

		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d("DJPS", "Arquivo nao encontrado: " + e.getMessage());
		} catch (IOException e) {
			Log.d("DJPS", "Erro ao tentar acessar o arquivo: " + e.getMessage());
		}
	}
	
	private void sendNotification(String msg) {  
		    mNotificationManager = (NotificationManager) 
		    		this.getSystemService( Context.NOTIFICATION_SERVICE);  
		  
		    PendingIntent contentIntent = 
		    		PendingIntent.getActivity(this, 0, new Intent(), 0);  
		  
		    NotificationCompat.Builder mBuilder =  
		        new NotificationCompat.Builder(this)  
		      .setSmallIcon(R.drawable.ic_launcher)
		      .setContentTitle("Foto Service")  
		      .setStyle(new NotificationCompat.BigTextStyle()  
		      .bigText(msg)) 
		      .setContentText(msg);
		  
		    mBuilder.setContentIntent(contentIntent);
		    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());  
	}
}