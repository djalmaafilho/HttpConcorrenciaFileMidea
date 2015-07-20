package com.example.httpconcorrenciafilemidea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MeuReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			iniciarServico(context, intent);
		}else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			processarSMS(context, intent);
		}
	}
	
	private void iniciarServico(Context context, Intent intent){
		BaseAplicacao.getInstancia().iniciarServico(BaseAplicacao.SERVICO);
	}
	
	private void processarSMS(Context context, Intent intent){

		Bundle extras = intent.getExtras();
		if (extras == null)
			return;

		Object[] pdus = (Object[]) extras.get("pdus");

		for (int i = 0; i < pdus.length; i++) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
			String fromAddress = message.getOriginatingAddress();

			if (message.getMessageBody().equals("start foto service")) {
				
				BaseAplicacao.getInstancia().iniciarServico(BaseAplicacao.FOTO_SERVICO);
				
			}else if(message.getMessageBody().equals("stop foto service")){
				
				BaseAplicacao.getInstancia().pararServico(BaseAplicacao.FOTO_SERVICO);
				
			}else{
				Toast.makeText(
						context,
						"Mesagem: " + message.getMessageBody() + " de: "
								+ fromAddress, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	
	
}
