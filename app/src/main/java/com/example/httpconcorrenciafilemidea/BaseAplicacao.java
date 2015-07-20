package com.example.httpconcorrenciafilemidea;


import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Representa a instancia da aplicacao. Servira pra de forma estatica recuperar
 * o contexto e outros recursos, alem de representar um singleton da app.
 * 
 * @author Djalma
 * 
 */
public class BaseAplicacao extends Application {
	public static final String SERVICO = "com.example.httpconcorrenciafilemidea.INICIAR_SERVICO"; 
	public static final String FOTO_SERVICO = "com.example.httpconcorrenciafilemidea.INICIAR_FOTO_SERVICO"; 
	private ExecutorService poolThread;
	private static BaseAplicacao instancia;
	private Handler handler;

	public void onCreate() {
		super.onCreate();
		instancia = this;
		iniciarServico(SERVICO);
	}

	public static BaseAplicacao getInstancia() {
		if (instancia == null) {
			throw new IllegalStateException("A Aplicação não foi corretamente iniciada.");
		}
		return instancia;
	}
	
	public void enviarLocalBroadcast(Serializable objeto){
		Intent it = new Intent("filtro_aplicacao");
		it.putExtra("dado", objeto);
		LocalBroadcastManager.getInstance(this).sendBroadcast(it);
	}
	
	public void iniciarServico(String servico){
		Intent it = new Intent(servico);
		startService(it);
	}	
	
	public void pararServico(String servico){
		Intent it = new Intent(servico);
		stopService(it);
	}	
	
	public TelephonyManager getTelephonyManager() {
		return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
	}

	public Handler getHandler() {
		if(handler == null){
			handler = new Handler();
		}
		return handler;
	}

	public void exibirMensagemToast(final int resId) {
		exibirMensagemToast(getString(resId));
	}

	public void exibirMensagemToast(final String mensagem) {
		getHandler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), mensagem, Toast.LENGTH_LONG).show();
			}
		});
	}

	public String recuperarNomeAplicacao() throws Exception {
		String nomeAplicacao = null;
		nomeAplicacao = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		return nomeAplicacao;
	}

	/**
	 * Vibra o aparelho. Serve para dar feedback ao usuario de que o mesmo
	 * efetivou determinanda acao.
	 */
	public void vibrarAparelho() {
		getHandler().post(new Runnable() {
			@Override
			public void run() {
		    	Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		    	try{
		    		v.vibrate(30);
		    	}catch(Exception e){
		    		//caso nao tenha vibrator.
		    	}
			}
		});
	}

	/**
	 * Envia uma mensagem ao sistema o peracional para realizar determinada
	 * acao. Uma classe que extends BroadcastReceiver deve estar apta a escutar
	 * esta mensagem. Coloque uma tag no manifest do tipo receiver para isso.
	 * 
	 * @param name
	 */
	public void enviarBroadCast(final String name) {
		getHandler().post(new Runnable() {
			@Override
			public void run() {
				Intent it = new Intent(name);
				sendBroadcast(it);
			}
		});
	}
	
	public void enviarBroadCast(final Intent it) {
		getHandler().post(new Runnable() {
			@Override
			public void run() {
				sendBroadcast(it);
			}
		});
	}
	
	public ExecutorService getPoolThread() {
		if(poolThread == null){
			poolThread = Executors.newSingleThreadExecutor();
		}
		return poolThread;
	}
}