package org.mxitz.mxconectado;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class Mapa_gps extends ActionBarActivity implements OnMarkerClickListener {
	private ListView lista;
	LocationManager locManager;
	private GoogleMap mMap;
	double lon,lat;
	private String[] coordenadas= new String[4992];
	private ProgressDialog _dialog;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_gps);
        
        this.lista=(ListView)findViewById(R.id.left_drawer);
        
        _dialog = new ProgressDialog(this);
        setUpMapIfNeeded();
        localizacion();
		leeArchivo();
        creaMarker();
	    mMap.setOnMarkerClickListener(this);
	    
	    String arreglo[] = getResources().getStringArray(R.array.nav_mapa);
		 ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,arreglo);
		 lista.setAdapter(adaptador);
		 lista.setOnItemClickListener( new AdapterView.OnItemClickListener() {
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			 if (position == 0){
			 Intent intent=new Intent(Mapa_gps.this, Opciones.class);
			Mapa_gps.this.startActivity(intent);
		 }else if(position == 1){
		
		 }else if(position == 2){
			 Intent intent=new Intent(Mapa_gps.this,AcercaDe.class);
			 
			 Mapa_gps.this.startActivity(intent);
			 
		 }else if(position == 3){
			 System.exit(0);
		 }
			 finish();
		 }
		 });
        
    }
       
    private void creaMarker(){
    	for(int i=0;i<=2496;i++){
    		//System.out.println(i);
    		mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(coordenadas[i]), Double.parseDouble(coordenadas[i+1]))).title("wi-fi"));
    		i++;
    	}
    }
    
    private void leeArchivo(){
		String[] latLon;
		String linea;

		InputStream flujoEntrada= getResources().openRawResource(R.raw.coordenadas);
		InputStreamReader lectorFlujo= new InputStreamReader(flujoEntrada);
		BufferedReader buffer= new BufferedReader(lectorFlujo, 8192);
		try{
			linea= buffer.readLine();
			int contador=0;
			while(linea!=null){
				latLon= linea.split("\t");
				for(int i=0;i<latLon.length;i++){
					latLon= linea.split("\t");
					coordenadas[contador]=latLon[0];
					contador++;
					coordenadas[contador]=latLon[1];
					linea= buffer.readLine();
					contador++;
				}
				linea= buffer.readLine();
			}
			buffer.close();
			lectorFlujo.close();
			flujoEntrada.close();
		}catch(Exception ex){
			
		}
	}

    private void setUpMapIfNeeded() {
    	   if (mMap == null) {
    	      mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    	      if (mMap != null) {

    	        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	        	
    	        mMap.setMyLocationEnabled(true);
    	      }
    	   }
    	}

    public void mensaje(String m){
    	Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
    }
    
    private void localizacion(){
		locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener=new MyLocationListener();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000, 0, locListener);
	}
    
    private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			//String coordernadas="Latitud: "+location.getLatitude()+" Longitud: "+location.getLongitude();
			//mensaje(coordernadas);
			 //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Un Marker"));
		}

		@Override
		public void onProviderDisabled(String arg0) {
			mensaje("Gps Desactivado");
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			mensaje("Gps Activado");
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mapa_gps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    

	@Override
	public boolean onMarkerClick(Marker v) {
		
		return false;
	}
}
