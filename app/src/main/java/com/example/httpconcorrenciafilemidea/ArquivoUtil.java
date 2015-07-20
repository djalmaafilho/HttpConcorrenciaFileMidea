package com.example.httpconcorrenciafilemidea;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class ArquivoUtil {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final String NOME_PASTA = "HttpConcMediFile";
	public static final String SUB_PASTA_DIM = "DIM";
	private static File pastaDim;

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type, String path) {
		return Uri.fromFile(getOutputMediaFile(type, path));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type, String path) {

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			Log.i("DJPS", "Path: " + path);
			mediaFile = new File(path + File.separator + "IMG_" + timeStamp
					+ ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(path + File.separator + "VID_" + timeStamp
					+ ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	public static void carregarDiretoriosAplicacao() throws IOException {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// 2.1
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), NOME_PASTA);

		Log.i("DJPS", mediaStorageDir.getAbsolutePath());

		// 2.2
		// File mediaStorageDir = new
		// File(Environment.getExternalStoragePublicDirectory(
		// Environment.DIRECTORY_PICTURES), NOME_PASTA);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdir()) {
				Log.d(NOME_PASTA, "Falha ao criar o diretorio " + NOME_PASTA);
			}
		}

		pastaDim = new File(mediaStorageDir.getAbsolutePath(), SUB_PASTA_DIM);

		if (!pastaDim.exists()) {
			pastaDim.mkdir();
			if (!pastaDim.exists()) {
				Log.d(SUB_PASTA_DIM, "Falha ao criar o diretorio "
						+ SUB_PASTA_DIM);
			}
		}
	}

	public static String recuperarPathPastaDim() throws IOException {
		if(pastaDim == null){
			carregarDiretoriosAplicacao();
		}
		return pastaDim.getAbsolutePath();
	}
}