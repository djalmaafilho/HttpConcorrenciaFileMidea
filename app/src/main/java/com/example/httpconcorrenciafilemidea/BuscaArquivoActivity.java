
package com.example.httpconcorrenciafilemidea;

import java.lang.Thread.State;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BuscaArquivoActivity extends BaseActivity implements OnClickListener{
	
	private BaixaArquivoThread t;
	private Task task;
	private boolean servicoChamado;
	private BroadcastReceiver bc;
	private String[] parametros = new String[] {"http://qualiti.com.br/Content/images/logo.png",
			"http://qualiti.com.br/Content/images/banners/qualiti_camp_banner.jpg",
			"http://qualiti.com.br/Content/images/logo.png",
			"http://qualiti.com.br/Content/images/banners/qualiti_camp_banner.jpg",
			"http://qualiti.com.br/Content/images/logo.png",
			"http://qualiti.com.br/Content/images/banners/qualiti_camp_banner.jpg"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		Button aux = (Button)findViewById(R.id.buttonAsync);
		aux.setOnClickListener(this);
		aux = (Button)findViewById(R.id.buttonThread);
		aux.setOnClickListener(this);
		aux = (Button)findViewById(R.id.buttonServicoFoto);
		aux.setOnClickListener(this);
		
		bc = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object dado = intent.getSerializableExtra("dado");
				if(dado != null){
					if(dado instanceof String){
						updateTexto((String)dado);
					}else if(dado instanceof Status){
						Status ds = (Status)dado;
						if(ds.equals(Status.FINISHED)){
							ativarBotoes();
						}
					}
				}
			}
		};
	}
	
	@Override
	protected void onStart() {
		try {
			LocalBroadcastManager.getInstance(getBaseContext()).
			registerReceiver(bc, new IntentFilter("filtro_aplicacao"));
		} catch (Exception e) {
			
		}
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		try {
			LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(bc);
		} catch (Exception e) {
			
		}
		super.onStop();
	}
	
	private void ativarBotoes(){
		ativarViews(new int[]{R.id.buttonAsync,R.id.buttonThread});
	}
	
	private void desativarBotoes(){
		desativarViews(new int[]{R.id.buttonAsync,R.id.buttonThread});
	}
	
	private void initTexto(){
		executarRunnable(new Runnable() {
			@Override
			public void run() {
				TextView txt = (TextView)findViewById(R.id.textView1);
				txt.setText("");
			}
		});
	}
	private void updateTexto(final String mensagem){
		executarRunnable(new Runnable() {
			@Override
			public void run() {
				TextView txt = (TextView)findViewById(R.id.textView1);
				txt.setText(txt.getText().toString()+"\n"+mensagem);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.buttonAsync){
			baixarAsync();
		}else if(v.getId() == R.id.buttonThread){
			baixarThread();
		}else if(v.getId() == R.id.buttonServicoFoto){
			if(!servicoChamado){
				servicoChamado = true;
				BaseAplicacao.getInstancia().iniciarServico(BaseAplicacao.FOTO_SERVICO);
				((Button)v).setText("Parar Serviço!!!");
			}else{
				servicoChamado = false;
				BaseAplicacao.getInstancia().pararServico(BaseAplicacao.FOTO_SERVICO);
				((Button)v).setText("Iniciar Serviço!!!");
			}
		}
	}

	private void baixarThread() {
		desativarBotoes();
		initTexto();
		t = new BaixaArquivoThread(parametros);
		t.executar();
	}

	private void baixarAsync() {
		desativarBotoes();
		initTexto();
		task = new Task();
		task.execute(parametros);
	}
	
	@Override
	public void onBackPressed() {
		if(task != null && !task.getStatus().equals(Status.FINISHED) && !task.isCancelled()){
			updateTexto("Cancelando task...");
			task.cancel(true);
		}else if(t!= null && !t.getState().equals(State.TERMINATED) && !t.isCancelada()){
			updateTexto("Cancelando Thread...");
			t.cancelar();
		}else{
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_fotos) {
			abrirNovaTela(GaleriaActivity.class);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}