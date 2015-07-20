
package com.example.httpconcorrenciafilemidea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements OnClickListener{
	
	Handler handler ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
	}
	
	protected void ativarViews(final int[] views){
		if(!isFinishing()){
			handler.post(new Runnable() {
				@Override
				public void run() {
					for(int view: views){
						findViewById(view).setEnabled(true);
					}
				}
			});
		}
	}
	
	protected void desativarViews(final int[] views){
		if(!isFinishing()){
			handler.post(new Runnable() {
				@Override
				public void run() {
					for(int view: views){
						findViewById(view).setEnabled(false);
					}
				}
			});
		}
	}
	
	protected void exibeToast(final String mensagem){
		if(!isFinishing()){
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getBaseContext(), mensagem, Toast.LENGTH_LONG).show();
				}
			});
		}
	}
	
	protected void executarRunnable(Runnable r){
		if(!isFinishing()){
			handler.post(r);
		}
	}
	
	protected void desativarHandler(){
		handler.removeCallbacksAndMessages(null);
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	public void abrirNovaTela(Class<? extends Activity> tela){
		startActivity(new Intent(this, tela));
	}
	
	public View recuperarViewAPartirDeLayout(int resId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(resId, null);
	}
}