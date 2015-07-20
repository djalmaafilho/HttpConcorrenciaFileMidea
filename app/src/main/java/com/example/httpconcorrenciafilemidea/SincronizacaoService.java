package com.example.httpconcorrenciafilemidea;

import android.content.Intent;
import android.widget.Toast;

public class SincronizacaoService extends BaseService {

	@Override
	public void onCreate() {
		super.onCreate();
		
		Toast.makeText(getApplicationContext(), "Servico criado!!!", 
				Toast.LENGTH_LONG).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getServiceHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), "Servico iniciado!!!", 
						Toast.LENGTH_LONG).show();
			}
		}, 10000l);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Servico foi fechado", 
				Toast.LENGTH_LONG).show();
		super.onDestroy();
	}
}