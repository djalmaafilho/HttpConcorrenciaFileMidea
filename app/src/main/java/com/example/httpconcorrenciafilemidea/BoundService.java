package com.example.httpconcorrenciafilemidea;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BoundService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder{
        BoundService getService(){
            return BoundService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
        return mBinder;
	}

    public void mensagem(String mensagem){
        Toast.makeText(this, "BOUNDSERVICE DIZ: "+mensagem, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onDestroy() {
		try {
			Log.i("DJPS", "BOUNDSERVICE PARADO");
            Toast.makeText(this, "BOUNDSERVICE PARADO", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
}