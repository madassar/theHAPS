package com.example.maptest;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	 private GoogleMap googleMap;
	 private JsonParser parser;
	 private GeoPoint point;
	 private LocationManager locationManager;
	 private LocationListener locationListener;
	 private String provider;
	 private GoogleApiClient mGoogleApiClient;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		//locationListener = new GPSLocationListener();
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
		//Log.v("Tag", "http://172.16.99.190/api/all");
		//server=new ConnectServer("http://172.16.99.190/api/all");
		//server.start();
		//if(point!=null) {
		try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
             e.printStackTrace();
         }
	   /*}
		else
		{
			Toast.makeText(getBaseContext(), "Enable GPS",  Toast.LENGTH_SHORT).show();	
		}*/
		
	}
	
	
	  
	private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition position) {
                   LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                	setUpMapAndUpdateMap(boundsToQuery(bounds));
                }
            });
    //        Location loc=getMyLocation(this);
         //   if(loc!=null)
           //  Log.v("Location gps", String.valueOf(loc.getLatitude()+","+loc.getLongitude()));
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(32.1166, -96.7969)) .zoom(7.0f).build();
            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
            googleMap.moveCamera(camUpdate);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(new LatLng(32.1166, -96.7969)).title("Hello world"));
            //server.setGoogleMap(googleMap);     
            if(googleMap != null)
            {
            	LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;	
            	setUpMapAndUpdateMap(boundsToQuery(bounds));
            }
            	//setUpMapAndUpdateMap("http://172.16.99.190/api/all");
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
	
	
	

	public String boundsToQuery(LatLngBounds bounds )
	{
		//LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        //fetchData(bounds);
       LatLng ne= bounds.northeast;
      
       LatLng sw = bounds.southwest;
       //"http://172.16.99.190/api/points?latl=ne.latitude&lath=sw.latitude&lonl=sw.longitude&lonh=ne.longitude"
       String Query=String.valueOf( "http://172.16.99.190/api/points?latl="+String.valueOf(ne.latitude)
                                           +"&lath="+String.valueOf(sw.latitude)
                                           +"&lonl="+String.valueOf(sw.longitude) 
                                           +"&lonh="+String.valueOf(ne.longitude));
       
       Log.v("Query", Query);   
    	Log.v("Updating","udating");
    	//googleMap.clear();
    	return Query;
	}
	
	public void setUpMapAndUpdateMap(String _query)
	{
	
	  final String query=_query;
		new Thread(new Runnable() {
            public void run() {
                try {
                	getMarkers(query);
                } catch (IOException e) {
                   // Log.e(LOG_TAG, "Cannot retrive cities", e);
                    return;
                }
            }
        }).start();	
	}
	public void getMarkers(String query) throws IOException
	{
		URL url=new URL(query);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
	    connection.setRequestMethod("GET");
		parser = new JsonParser(connection.getInputStream());
		parser.parse();
		
		 runOnUiThread(new Runnable() {
	            public void run() {
	                
	                    createMarkers(parser);
	               
	            }
	        });
	}
	
	public void createMarkers(JsonParser parser) 
	{
		googleMap.clear();
      if(!parser.getLatitude().isEmpty()&&!parser.getLatitude().isEmpty()) 
		Log.v("Markers Count",String.valueOf(parser.getLatitude().size()) );
	  if(!parser.getLatitude().isEmpty()&&!parser.getLatitude().isEmpty())
		for(int i=0;i<parser.getLatitude().size();i++)
		{
			Double lat=(Double)parser.getLatitude().get(i);
			Double lon=(Double)parser.getLongitude().get(i);
			String localname=(String)parser.getLocalname().get(i);
			int color=(int)parser.getColors().get(i);
			//BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);


			googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(lat,lon))
					.title(localname)
					.icon(BitmapDescriptorFactory.fromResource(color)));
		//	googleMap.icon(icon);
	        
		}
	}
	
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        initilizeMap();
	    }
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
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
   /**		
	private class GPSLocationListener implements LocationListener 
	{
	  @Override
	  public void onLocationChanged(Location location) {
	    if (location != null) {
	      GeoPoint point = new GeoPoint(
	          (int) (location.getLatitude() * 1E6), 
	          (int) (location.getLongitude() * 1E6), 0);
	      
	      Toast.makeText(getBaseContext(), 
	          "Latitude: " + location.getLatitude() + 
	          " Longitude: " + location.getLongitude(), 
	          Toast.LENGTH_SHORT).show();
	      
	    }
	  }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	}
   **/
}
