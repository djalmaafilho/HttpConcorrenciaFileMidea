
package com.example.httpconcorrenciafilemidea;



import java.lang.Thread.State;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BuscaArquivoActivity extends BaseActivity implements OnClickListener{

    //Acesso a um servico que desejo recupera a instancia como referencia para manipular seus metodos.
    private BoundService mService;
    private boolean mBound;

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
        loadButton(R.id.buttonAsync);
        loadButton(R.id.buttonThread);
        loadButton(R.id.buttonServicoFoto);
        loadButton(R.id.buttonBindServico);

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

    public void loadButton(int id) {
        Button bt = (Button)findViewById(id);
        bt.setOnClickListener(BuscaArquivoActivity.this);
    }

	@Override
	protected void onStart() {
		try {
			LocalBroadcastManager.getInstance(getBaseContext()).
			registerReceiver(bc, new IntentFilter("filtro_aplicacao"));

            //Passando a conexao como parametro para o metodo bindservice.
            //Aqui o acesso ao servico sera disponibilizado.
            Intent intent = new Intent(this, BoundService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        } catch (Exception e) {
			e.printStackTrace();
		}
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		try {
			LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(bc);
            unbindService(mConnection);
		} catch (Exception e) {

		}
        super.onStop();
	}

    private void initBotoes(int[] btIds){

    }

	private void ativarBotoes(){
		ativarViews(new int[]{R.id.buttonAsync,R.id.buttonThread, R.id.buttonBindServico});
	}
	
	private void desativarBotoes(){
		desativarViews(new int[]{R.id.buttonAsync,R.id.buttonThread, R.id.buttonBindServico});
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
        }else if(v.getId() == R.id.buttonBindServico){
            if (mBound) {
                mService.mensagem("Activity Conectada ao serviço com sucesso!!!");
            }else{
                Toast.makeText(this, "Serviço não conectado", Toast.LENGTH_SHORT).show();
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

    //Conexao com um servico , vai permitir capturar a instancia do servico em uma variavel de referencia.
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BoundService.LocalBinder binder = (BoundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            ((Button)findViewById(R.id.buttonBindServico)).setText("Testar Bind Serviço!!!");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}