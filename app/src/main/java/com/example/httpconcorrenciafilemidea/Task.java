package com.example.httpconcorrenciafilemidea;

import java.io.Serializable;

import android.os.AsyncTask;

public class Task extends AsyncTask<String, Integer, Integer>{
	private ArquivoHelper helper;
	
	public Task() {
		helper = new ArquivoHelper();
	}
	
	protected void onPreExecute() {
		updateStatus("Iniciando execução com AsyncTask!!!");
	};
	
	private void updateStatus(Serializable dado){
		BaseAplicacao.getInstancia().enviarLocalBroadcast(dado);
	}
	
	/**
	 * Executa a parte
	 */
	@Override
	protected Integer doInBackground(String... params) {
		Integer tam = -1;
		try {
			for(String arquivo: params){
				if(!isCancelled()){
					updateStatus("Baixando arquivo: "+arquivo);
					byte[] bytes = helper.baixarArquivo(arquivo);
					tam = bytes.length;
					publishProgress(tam);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			updateStatus("AssincTask Concluida!!!");
			updateStatus(Status.FINISHED);
		}
		return null;
	}
	
	/**
	 * Executa a atualização na tela para feedback ao usuario
	 */
	protected void onProgressUpdate(Integer... values) {
		updateStatus("Bytes baixados "+values[0]);
	};
	
	@Override
	protected void onCancelled() {
		updateStatus("Cancelamento da task chamado...");
		helper.fechar();
		super.onCancelled();
	}
	protected void onCancelled(Integer result) {
		updateStatus("Asinc Task Cancelada!!!");
	};
	
	
};