package org.mxitz.mxconectado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Opciones extends Activity implements OnClickListener {

	private TextView tvTu, tvPoscicion, tvTitulo1, tvTitulo2;
	private LocationManager locManager;
	private String[] coordenadas = new String[4992];
	private ListView lista;
	public SoundPool sp;
	public int flujodemusica = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opciones);
		
		sp = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		flujodemusica = sp.load(this, R.raw.voz, 1);

		tvTitulo2 = (TextView) findViewById(R.id.textView2);
		tvPoscicion = (TextView) findViewById(R.id.textView4);
		
		this.lista=(ListView)findViewById(R.id.left_drawer);
		
		tvTitulo1 = (TextView) findViewById(R.id.textView);
		
		tvTu = (TextView) findViewById(R.id.textView3);

		tvTitulo1.setText("Mas cercano:");
		tvTitulo2.setText("");
		tvTu.setText("Buscando posicion...");
		tvPoscicion.setText("");

		 String arreglo[] = getResources().getStringArray(R.array.nav_options);
		 ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo);
		 lista.setAdapter(adaptador);
		 lista.setOnItemClickListener( new AdapterView.OnItemClickListener() {
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 if (position == 0){
			 Intent intent=new Intent(Opciones.this, Mapa_gps.class);
			Opciones.this.startActivity(intent);
		 }else if(position == 1){
			 Intent intent=new Intent(Opciones.this, MainActivity.class);
				Opciones.this.startActivity(intent);
		 }else if(position == 2){
			 Intent intent=new Intent(Opciones.this,AcercaDe.class);
			 Opciones.this.startActivity(intent);
		 }else if(position == 3){
			 System.exit(0);
		 }
		 finish();
		
		 }
		 });

		if (wifi());
		localizacion();
		leeArchivo();
	}

	private String tuPosicion(double lat, double lon) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addreses = geocoder.getFromLocation(lat, lon, 1);
			// System.out.println(addreses.get(0).getFeatureName());
			return "Calle: " + addreses.get(0).getThoroughfare() + "\n en: "
					+ addreses.get(0).getLocality();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Buscando Ubicacion";
	}

	private double calculaDistancia(double lat1, double lon1, double lat2,
			double lon2) {
		double k = 6371;

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = k * c;
		double resultado = d * 1000;

		return resultado;
	}

	private double resultado(double milat, double mitlong) {
		double[] puntos = new double[2496];
		double minDis = 0.0;
		int contador = 0;
		for (int i = 0; i < 2496; i++) {
			puntos[contador] = calculaDistancia(milat, mitlong,
					Double.parseDouble(coordenadas[i]),
					Double.parseDouble(coordenadas[i + 1]));
			contador++;
			i++;
		}

		shellSort(puntos);
//		for (int i = 0; i < puntos.length; i++) {
//			System.out.println(puntos[i]);
//		}
		int c = 0;
		int lon = puntos.length;
		while (c < lon) {
			if (puntos[c] != 0) {
				minDis = puntos[c];
				break;
			}
			c++;
		}

		return minDis;
	}

	public void shellSort(double[] matrix) {
		for (int increment = matrix.length / 2; increment > 0; increment = (increment == 2 ? 1
				: (int) Math.round(increment / 2.2))) {
			for (int i = increment; i < matrix.length; i++) {
				for (int j = i; j >= increment
						&& matrix[j - increment] > matrix[j]; j -= increment) {
					double temp = matrix[j];
					matrix[j] = matrix[j - increment];
					matrix[j - increment] = temp;
				}
			}
		}
	}

	private void leeArchivo() {
		String[] latLon;
		String linea;

		InputStream flujoEntrada = getResources().openRawResource(
				R.raw.coordenadas);
		InputStreamReader lectorFlujo = new InputStreamReader(flujoEntrada);
		BufferedReader buffer = new BufferedReader(lectorFlujo, 8192);
		try {
			linea = buffer.readLine();
			int contador = 0;
			while (linea != null) {
				latLon = linea.split("\t");
				for (int i = 0; i < latLon.length; i++) {
					latLon = linea.split("\t");
					coordenadas[contador] = latLon[0];
					contador++;
					coordenadas[contador] = latLon[1];
					linea = buffer.readLine();
					contador++;
				}
				linea = buffer.readLine();
			}
			buffer.close();
			lectorFlujo.close();
			flujoEntrada.close();
		} catch (Exception ex) {

		}
	}

	public void mensaje(String m) {
		Toast.makeText(this, m, Toast.LENGTH_LONG).show();
	}

	private boolean wifi() {
		WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!wifiMan.isWifiEnabled()) {
			//mensaje("Activa tus datos para una mejor experiencia");
			return true;
		}
		return false;
	}

	private void localizacion() {
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new MyLocationListener();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000,0, locListener);
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// String coordernadas=""+location.getLongitude();
			if (!wifi()) {
				tvTu.setText(tuPosicion(location.getLatitude(),
						location.getLongitude()));
				double lat = location.getLatitude();
				double lang = location.getLongitude();
				if (location.getLatitude() + "".compareTo("") == 0) {
					tvPoscicion.setText("Buscando posicion");
				} else {
					String pos = resultado(lat, lang) + "";
					tvPoscicion.setText(Math.rint(Double.parseDouble(pos)*100)/100+" metros");
					tvTitulo2.setText("Mas cercano:");
					tvTitulo1.setText("Tú:");
				}
			} else {
				double lat = location.getLatitude();
				double lang = location.getLongitude();
				if (location.getLatitude() + "".compareTo("") == 0) {
					tvTu.setText("Buscando posicion");
				} else {
					String pos = resultado(lat, lang) + "";
					tvTu.setText(Math.rint(Double.parseDouble(pos)*100)/100+" metros");
				}
			}
			double lat = location.getLatitude();
			double lang = location.getLongitude();
			String pos = resultado(lat, lang) + "";
			
			if(Math.rint(Double.parseDouble(pos)*100)/100<50.0){
				sp.play(flujodemusica, 1, 1, 0, 0, 1);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			tvTitulo1.setText("Activa tu Gps");
			tvTitulo2.setText("");
			tvTu.setText("");
			tvPoscicion.setText("");
		}

		@Override
		public void onProviderEnabled(String provider) {
			tvTitulo1.setText("Mas cercano:");
			tvTu.setText("Buscando tu posicion:...");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onClick(View v) {
	}
}
