package com.example.httpconcorrenciafilemidea;

import android.os.AsyncTask.Status;

public class BaixaArquivoThread extends BaseThread{
	
	ArquivoHelper helper;
	String[] arquivos;
	
	public BaixaArquivoThread() {
		helper = new ArquivoHelper();
	}
	
	public BaixaArquivoThread(String... arquivos) {
		this();
		this.arquivos = arquivos;
	}
	 
	
	@Override
	public void cancelar() {
		updateStatus("Cancelamento da Thread chamado!!!");
		helper.fechar();
		super.cancelar();
	}
	
	@Override
	public void run() {
		super.run();
		try{
			updateStatus("Iniciando execução com Thread!!!");
			
			for (int i = 0; i < arquivos.length; i++) {
				if(!isCancelada()){
					updateStatus("baixando arquivo: "+arquivos[i]);
					helper.baixarArquivo(arquivos[i]);
				}else{
					updateStatus("Ação foi cancelada!!!");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(isCancelada()){
				updateStatus("Thread foi cancelada!!!");
			}
			updateStatus("Thread busca arquivos concluida!!");
			updateStatus(Status.FINISHED);
		}
	}
};