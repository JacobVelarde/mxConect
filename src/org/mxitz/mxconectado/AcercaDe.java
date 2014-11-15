package org.mxitz.mxconectado;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AcercaDe extends Activity {

	private ListView lista;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acerca_de);
		
		this.lista=(ListView)findViewById(R.id.left_drawer);
		
		String arreglo[] = getResources().getStringArray(R.array.nav_acercaDe);
		 ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,arreglo);
		 lista.setAdapter(adaptador);
		 lista.setOnItemClickListener( new AdapterView.OnItemClickListener() {
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			 if (position == 0){
			 Intent intent=new Intent(AcercaDe.this, Opciones.class);
			AcercaDe.this.startActivity(intent);
		 }else if(position == 1){
			 Intent intent=new Intent(AcercaDe.this, Mapa_gps.class);
				AcercaDe.this.startActivity(intent);
		 }else if(position == 2){
//			 Intent intent=new Intent(AcercaDe.this,Gui.class);
//			 Mapa_gps.this.startActivity(intent);
		 }else if(position == 3){
		 System.exit(0);
		 }
			 finish();
		 }
		 });
	}
	
	

}
