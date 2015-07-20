package com.example.httpconcorrenciafilemidea;

import java.io.Serializable;

/**
 * Classe filha de Thread que executa dentro pool de Thread da aplicacao.
 * 
 * @author djalma
 * 
 */
public class BaseThread extends Thread {

	private boolean cancelada;

	public void cancelar() {
		cancelada = true;
	}

	public boolean isCancelada() {
		return cancelada;
	}

	/**
	 * Use o metodo executar para ter o comportamento esperado para a aplicacao.
	 */
	@Deprecated
	@Override
	public final void start() {
		executar();
	}

	/**
	 * Executa a Thread no pool de Treads da aplicacao.
	 */
	public void executar() {
		try {
			BaseAplicacao.getInstancia().getPoolThread().execute(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateStatus(Serializable dado) {
		BaseAplicacao.getInstancia().enviarLocalBroadcast(dado);
	}
}