package com.example.httpconcorrenciafilemidea;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

/**
 * Classe que representa um servico que roda em background com a 
 * aplicacao e nao na thread main. Para que as funcionalidades
 * das classes que herdarem deste servico tenham seus codigos
 * excutados em paralelo e preciso chamar o metodo getServiceHandler
 * e usar o metodo post do mesmo.
 * 
 * Como esta classe tambem e instancia de ISolicitante na ocorrencia 
 * do fechamento do servico, caso o mesmo tenha sido adicionado ao controler
 * de solicitações, sera feito o desregistro de sua solicitacao. 
 * @author Djalma
 *
 */
public class BaseService extends Service{
	private HandlerThread threadHAndler;
	private volatile Looper serviceLooper;
	private volatile Handler serviceHandler;
	
	/*
	 * Neste metodo esta sendo setada a configuracao para
	 * permitir que o servico rode em um processo paralelo a aplicacao.
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		threadHAndler = new HandlerThread(this.getClass().getSimpleName(), 
				Process.THREAD_PRIORITY_BACKGROUND);
		threadHAndler.start();
		serviceLooper = threadHAndler.getLooper();
		serviceHandler = new Handler(serviceLooper);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	/**
	 * Use este handler para rodar seus codigos em paralelo
	 * 
	 * Como no exemplo abaixo onde passamos um runable coma funcionalidade
	 * a ser executada em paralelo dentro do servico.
	 * 
	 * getServiceHandler().post(new Runnable() {
			@Override
			public void run() {
				//acao a ser executada
				stopSelf();
			}
		});
	 */
	public Handler getServiceHandler() {
		return serviceHandler;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(getBaseContext(), "Servico "+this.getClass().getSimpleName()+" Fechado!!!", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	public void colocarMensagemBarraNotificacao(String chamada, String cabecalho, String mensagem, long quando, int id){
		
		NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notificacao = new Notification(android.R.drawable.stat_notify_sync, chamada, quando);
		PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0, null, 0);
		notificacao.vibrate = new long[] {100,250,100,250};
		notificacao.setLatestEventInfo(getBaseContext(), cabecalho, mensagem, pi);
		manager.notify(id, notificacao);
	}
}