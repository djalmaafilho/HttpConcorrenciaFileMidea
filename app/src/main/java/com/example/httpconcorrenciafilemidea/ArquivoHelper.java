package com.example.httpconcorrenciafilemidea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArquivoHelper {

	HttpURLConnection urlConn;
	
	public byte[] baixarArquivo(String urlArquivo) throws IOException{
		URL urlbase = new URL(urlArquivo);
		urlConn = (HttpURLConnection) urlbase.openConnection();
		
		urlConn.setConnectTimeout(10000);
		urlConn.setReadTimeout(10000);
		
		InputStream inputStream = urlConn.getInputStream();
		byte[] bytes = readBytes(inputStream);
		
		return bytes;
	}
	
	private byte[] readBytes(InputStream is) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] retorno = null;
		byte[] buf = new byte[1024];
		int len;

		try {
			while ((len = is.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			retorno = bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if(bos != null){
				bos.close();
			}
			if(is != null){
				is.close();
			}
		}
		
		return retorno;
	}
	
	public void fechar(){
		try {
			if(this.urlConn != null){
				this.urlConn.disconnect();
			}
			System.out.println("Conexão fechada!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}