package com.example.httpconcorrenciafilemidea;

import java.io.File;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GaleriaActivity extends BaseActivity {
	private List<Foto> listaFoto;
	private File arquivo;
	private Bitmap bitmapDefault;
	private int indice;
	private ListView galeria;
	private ProgressDialog dialog;
	private Handler hand;
	private BaseAdapter baseAdapter;
	private CarregaGaleria carregaGaleria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ArquivoUtil.carregarDiretoriosAplicacao();
			arquivo = new File(ArquivoUtil.recuperarPathPastaDim());
			if (arquivo == null) {
				throw new Exception("Arquivo não existe!!");
			}
		} catch (Exception e) {
			exibeToast("Problema ao acessar pasta de fotos");
			finish();
			return;
		}
		setContentView(R.layout.lista_fotos);
		listaFoto = new ArrayList<Foto>();
		galeria = (ListView) findViewById(R.id.listViewFotos);
		dialog = ProgressDialog.show(this, "Galeria de Fotos", "carregando...",
				true);
		bitmapDefault = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		hand = new Handler();
		setAdapterGaleria();
		setarListenerGaleria();
		carregarGaleria();
	}

	public void setAdapterGaleria() {
		baseAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					convertView = recuperarViewAPartirDeLayout(R.layout.linha_foto);
				}
				ImageView img = (ImageView) convertView
						.findViewById(R.id.linhaImg);

				if (listaFoto.get(position).getBitmap() != null) {
					img.setImageBitmap(listaFoto.get(position).getBitmap());

					TextView txt = (TextView) convertView
							.findViewById(R.id.linhaTxt);
					txt.setText(listaFoto.get(position).getArquivo().getName());
				} else {
					img.setImageBitmap(bitmapDefault);
				}

				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public int getCount() {
				return listaFoto.size();
			}
		};

		galeria.setAdapter(baseAdapter);
	}

	public void setarListenerGaleria() {
		galeria.setOnItemClickListener(new OnItemClickListener() {
			ImageView imgViewAux;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int posicao, long arg3) {
				// caso a lista ainda nao esteja completamente carregada
				if (listaFoto.get(posicao) != null) {
					// limpar dados caso existam
					imgViewAux = null;
					imgViewAux = new ImageView(GaleriaActivity.this);
					imgViewAux.setImageBitmap(BitmapFactory
							.decodeFile(listaFoto.get(posicao).getArquivo()
									.getAbsolutePath()));
				}
			}
		});

		galeria.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int posicao, long arg3) {
				if (listaFoto.get(posicao) != null) {
					final int indice = posicao;
					AlertDialog.Builder alerta = new AlertDialog.Builder(
							GaleriaActivity.this);
					alerta.setTitle("DELETAR ARQUIVO");
					alerta.setMessage(listaFoto.get(indice).getArquivo()
							.getName());
					alerta.setPositiveButton("SIM", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							listaFoto.get(indice).getArquivo().delete();// deletar
																		// arquivo
							listaFoto.remove(indice);
							atualizaGalaria();
						}
					});

					alerta.setNegativeButton("NÃO", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

					alerta.show();
					return true;
				}
				return false;
			}
		});
	}

	/*
	 * inicia o processo de carregamento da galeria, caso existam fotos para
	 * serem carregadas da pasta de fotos da aplicacao
	 */
	private void carregarGaleria() {
		if (arquivo == null || arquivo.list().length == 0) {
			dialog.dismiss();
			dialog = null;
			System.gc();
			exibeToast("Pasta de fotos vazia");
			finish();
		} else {

			for (File arquivoAux : arquivo.listFiles()) {
				listaFoto.add(new Foto(arquivoAux, null));
			}
			carregaGaleria = new CarregaGaleria();
			carregaGaleria.executar();
		}
	}

	/*
	 * prepara a galeria para ser novamente carregada. apos esse processo
	 * deve-se carregar novamente a galeria
	 */
	private void resetarGaleria() {
		hand.removeCallbacksAndMessages(null);
		listaFoto.clear();
		zerarIndice();
		System.gc();
	}

	/*
	 * Executa em paralelo a busca pelos arquivos de fotos na pasta da aplicacao
	 * , e carrega lista de bitmaps para serem colocados nas posicoes da
	 * galeria. Apenas Threads autorizadas pela requisicao de atualizacao
	 * corrente podem realizar a acao.
	 */
	class CarregaGaleria extends BaseThread{
		boolean concluida = false;
		@Override
		public void run() {
			try {
				cancelarDialog();
				executarServico();
				
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				exibeToast("Carregamento de fotos concluido");
				concluida = true;
			}
		}
		
		void executarServico(){
			int indice = recuperarIndiceInsercao();
			Bitmap btm = null;
			while (!isCancelada() && indice < listaFoto.size()) {
				btm = BitmapFactory.decodeFile(listaFoto.get(indice)
						.getArquivo().getAbsolutePath());
				btm = Bitmap.createScaledBitmap(btm, 60, 60, false);
				try {
					inserirBitmap(btm, indice);
					atualizaGalaria();
					btm = null;
					System.gc();
				} catch (Exception e) {
					break;
				}
				
				indice = recuperarIndiceInsercao();
			}
		}
		public boolean isConcluida() {
			return concluida;
		}
	};

	private void cancelarDialog() {
		hand.post(new Runnable() {
			public void run() {
				if (dialog != null) {
					dialog.dismiss(); // parar barra de progresso
					dialog = null;
				}
				System.gc();
			}
		});
	}

	private void atualizaGalaria() {
		hand.post(new Runnable() {
			public void run() {
				baseAdapter.notifyDataSetChanged();
			}
		});
	}

	/*
	 * reinicia o valor do indice de insercao de bitmaps na lista de bitmaps
	 */
	private synchronized void zerarIndice() {
		this.indice = 0;
	}

	/*
	 * recupera a proxima posicao a ser inserida de um novo bitmap dentro da
	 * lista de bitmaps.
	 */
	private synchronized int recuperarIndiceInsercao() {
		int i = indice++;
		return i;
	}

	/*
	 * insere um novo bitmap, na posicao indicada, dentro lista de bitmaps
	 */
	private synchronized void inserirBitmap(Bitmap bitmap, int posicao)
			throws Exception {
		if (posicao < listaFoto.size()) {
			listaFoto.get(posicao).setBitmap(null);
			System.gc();
			listaFoto.get(posicao).setBitmap(bitmap);
		}
	}

	@Override
	public void onBackPressed() {
		if (carregaGaleria != null && !carregaGaleria.isConcluida() && !carregaGaleria.isCancelada()) {
			carregaGaleria.cancelar();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		resetarGaleria();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

	}
}