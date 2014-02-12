package com.latarce.kintos14;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Gps extends MapActivity implements OnClickListener{

	private static final int DEFAULT_ZOOM_LEVEL = 18;
	private static final int DEFAULT_ZOOM_LEVEL_SPDL = 12;
	
	private Button btCenter;
	private Button btSatelite;
	private Button btSPDLCenter;
	private Button btSearch;
	private TextView txtLocation;
	private LocationManager mLocationManager;
	private MyLocationListener mLocationListener;
	private MapView mapView;
	private Location sanPedroLocation;
	private Location myLoc;
	private MapController mapControl;
	private MyLocationOverlay me = null;
	private Geocoder geocoder;
	
	private String myPositionTextInfo;
	
	private Drawable marker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps);
		
		//BOTONES
		btCenter = (Button)this.findViewById(R.id.btCenter);
		btCenter.setOnClickListener(this);
		btSPDLCenter = (Button)this.findViewById(R.id.btSPDLCenter);
		btSPDLCenter.setOnClickListener(this);
		btSatelite = (ToggleButton)this.findViewById(R.id.btSatelite);
		btSatelite.setOnClickListener(this);
		btSearch = (Button)this.findViewById(R.id.btSearch);
		btSearch.setOnClickListener(this);
		
		//TEXTO
		txtLocation = (TextView) this.findViewById(R.id.tvGPS);
		//searchText = (EditText) findViewById(R.id.searchText);
		
		//COORDENADAS PUEBLO
		sanPedroLocation = new Location("GPS_PROVIDER");
		sanPedroLocation.setLatitude(41.735688);
		sanPedroLocation.setLongitude(-5.326543);
		
		//LOCALIZADOR		
    	//Obtenemos una referencia al LocationManager
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		//Obtenemos la última posición conocida (Si el proveedor está actualmente deshabilitado obtenemos un null pointer)
		myLoc = mLocationManager.getLastKnownLocation(Context.LOCATION_SERVICE);
		
    	//Mostramos la última posición conocida
		if (myLoc != null)
			mostrarPosicion();
    	
    	//Nos registramos para recibir actualizaciones de la posición
		mLocationListener = new MyLocationListener();
		
		//Actualizar la posición cada 2 ó 30 segundos, con una distancia minima de 0 metros
		requestLocation();
		
		//GEOCODER
		geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		
		
		//MAPA
		mapView = (MapView) findViewById(R.id.myMapView);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		btSatelite.setPressed(false);
		
		//Control del mapa
		mapControl = mapView.getController();
		
		//Seleccion del icono de puntero de brújula que se dibujará sobre el mapa
		Bitmap pointer = BitmapFactory.decodeResource(getResources(), R.drawable.pointer);
		//Seleccion del icono del pueblo que se dibujará sobre el mapa
		marker = getResources().getDrawable(R.drawable.escudo);
		//Se centra el icono del pueblo sobre el punto
	    marker.setBounds(marker.getIntrinsicWidth()/-2, marker.getIntrinsicHeight()/-2, marker.getIntrinsicWidth()/2, marker.getIntrinsicHeight()/2);
	    
	    //Añadir posiciones
	    mapView.getOverlays().add(new SitesOverlay(marker));
	    me=new CurrentLocationOverlay(this, mapView, pointer);
	    mapView.getOverlays().add(me);
	}

	
	@Override
	protected void onResume(){
		me.enableMyLocation();
		me.enableCompass();
		requestLocation();
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		me.disableMyLocation();
		me.disableCompass();
		mLocationManager.removeUpdates(mLocationListener);
		super.onPause();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	//BUSQUEDA - Se llama cuando se obtienen las coordenadas de la localidad buscada por el usuario
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
            if(resultCode == 1){
        		//mapControl.setCenter(data);
        		mapControl.setZoom(15);
                mapView.invalidate();                 
            }               
        }else{
            Toast.makeText(this, "No se ha encontrado la localidad", Toast.LENGTH_SHORT).show();
        }
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btCenter:
				animarPosicionActual();
				break;
			case R.id.btSPDLCenter:
				animarPosicionSPDL();
				break;
			case R.id.btSatelite:
		        if(mapView.isSatellite())
		        	mapView.setSatellite(false);
		        else
		        	mapView.setSatellite(true);
				break;
			case R.id.btSearch:
				//Abrir Activity con cuadro de texto
				//Intent i = new Intent(this, Search.class);
				//startActivityForResult(i,1);
				break;
			case R.id.btSavePosition:
				//Guardar la posición
				/*
				editor = positionSaved.edit();
				editor.putLong("lat", (int)Math.rint(myLoc.getLatitude()*1000000)/1000000);
				editor.putLong("lon", (int)Math.rint(myLoc.getLongitude()*1000000)/1000000);
				editor.commit();
				//Mostrar posición
			    sanPedroLocation.setLatitude((double)positionSaved.getLong("lat", 0));
				sanPedroLocation.setLongitude((double)positionSaved.getLong("lon", 0));
				mapView.getOverlays().remove(0);
				mapView.getOverlays().add(new SitesOverlay(marker));
				break;
				*/
			
		}
	}
	
	private class MyLocationListener implements LocationListener{
		public void onLocationChanged(Location loc) {
			myLoc = loc;
			mostrarPosicion();
		}
		public void onProviderDisabled(String provider) {
			// TODO
		}
		public void onProviderEnabled(String provider) {
			// TODO
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO
		}
	}
	
	private void animarPosicionActual() {
	    if(myLoc != null){
			GeoPoint geoPoint = new GeoPoint(
					(int) (myLoc.getLatitude()* 1000000),
					(int) (myLoc.getLongitude()* 1000000));
			mapControl.setZoom(DEFAULT_ZOOM_LEVEL);
			mapControl.animateTo(geoPoint);
	    }
		else{
			txtLocation.setText("Posición actual no disponible" );
		}
	}
	
	private void animarPosicionSPDL() {
		mapControl.setZoom(DEFAULT_ZOOM_LEVEL_SPDL);
		mapControl.animateTo(new GeoPoint((int)(sanPedroLocation.getLatitude()*1000000), (int)(sanPedroLocation.getLongitude()*1000000)));
	}
	
	private void mostrarPosicion() {
	    if(myLoc != null){
	    	List<Address> addresses = new ArrayList<Address>();
	    	int altitud = (int)myLoc.getAltitude();
	    	String str_altitud = "\nAltitud: ";
	    	    	
		    //DATOS
	    	if (altitud != 0){
	    		str_altitud = str_altitud + altitud;
	    	}else{
	    		str_altitud = str_altitud + "N/A";
	    	}
	    	myPositionTextInfo = "A " + Math.rint((myLoc.distanceTo(sanPedroLocation)/1000)*100)/100 + "km de SPdL"
	    						+ "\nLatitud: " + Math.rint(myLoc.getLatitude()*1000000)/1000000
								+ "\nLongitud: " + Math.rint(myLoc.getLongitude()*1000000)/1000000
								+ str_altitud
								+ "\nPrecision: " + (int)myLoc.getAccuracy() + " m"
								+ "\nProveedor: " + myLoc.getProvider();
	    	
		    //GEOCODER
		    try{
		    	addresses = geocoder.getFromLocation(myLoc.getLatitude(), myLoc.getLongitude(), 1);
		    	myPositionTextInfo = addresses.get(0).getAddressLine(0)
		    						 + "\n" + addresses.get(0).getAddressLine(1)
		    						 + "\n" + addresses.get(0).getAddressLine(2)
		    						 + "\n" + myPositionTextInfo;
		    }catch(Exception e) {
		    	e.printStackTrace();
		    }
	    }
	    else{
	    	myPositionTextInfo = "Posición actual no disponible";   	
	    }
	    txtLocation.setText(myPositionTextInfo);
	    txtLocation.setMovementMethod(new ScrollingMovementMethod());
	}

	private void requestLocation(){
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, mLocationListener);
		}
		else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, mLocationListener);
		}
		else{
			Toast.makeText(getBaseContext(), "Necesita activar el GPS o la localización por redes inalámbricas", Toast.LENGTH_LONG).show();
			finish();			
		}
	}
	
	
	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
	    private List<OverlayItem> items = new ArrayList<OverlayItem>();
	    
	    public SitesOverlay(Drawable marker) {
			super(marker);
			boundCenterBottom(marker);
			items.add(new OverlayItem( new GeoPoint((int)(sanPedroLocation.getLatitude()*1000000), (int)(sanPedroLocation.getLongitude()*1000000)),
			                           "San Pedro de Latarce", 
			                           "47851 - San Pedro de Latarce (Valladolid)"));
			populate();
	    }
	    
	    @Override
	    protected OverlayItem createItem(int i) {
	    	return(items.get(i));
	    }
	    
	    @Override
	    protected boolean onTap(int i) {
	    	Toast.makeText(Gps.this,
	                      items.get(i).getSnippet(),
	                      Toast.LENGTH_SHORT).show();
	    	return(true);
	    }
	    
	    @Override
	    public int size() {
	    	return(items.size());
	    }

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {	
			super.draw(canvas, mapView, shadow);
		} 
	  }
	
	
	//Se extiende la clase MyLocationOverlay para sobreescribir el metodo drawCompass y poner nuestra propia brújula
	public class CurrentLocationOverlay extends MyLocationOverlay{

		private Bitmap pointer;
		public CurrentLocationOverlay(Context context, MapView mapView, Bitmap pointer) {
			super(context, mapView);
			this.pointer = pointer;
		}

		@Override
		protected boolean dispatchTap(){
			Toast.makeText(Gps.this,
					myPositionTextInfo,
					Toast.LENGTH_LONG).show();
			return true;
		}

		@Override
		protected void drawCompass(Canvas canvas, float bearing) {
			Point screenPts = new Point(0,0);
			//Trasladar mi GeoPoint a pixels de la pantalla
			if (myLoc != null){
				screenPts = mapView.getProjection().toPixels(
		    		new GeoPoint(
		    				(int) (myLoc.getLatitude()* 1000000),
		    				(int) (myLoc.getLongitude()* 1000000)
		    				),
		    		null);
			}
		    float rotationAngle = bearing + 360f;
		    Matrix rotation = new Matrix();
		    rotation.preRotate(rotationAngle, pointer.getWidth()/2.0f, pointer.getHeight()/2.0f);
		    rotation.postTranslate(screenPts.x - pointer.getWidth()/2, screenPts.y - pointer.getHeight()/2);
		    canvas.drawBitmap(pointer, rotation, null);
		    //Llamar si se quiere la imagen de la brújula por defecto:
		    //super.drawCompass(canvas, bearing);
		}
	}
}