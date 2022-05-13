package com.example.androidrlocationapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.androidrlocationapp.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int FINE_LOCATION_REQUEST_CODE = 1000;

    //oggetto che fornisce l'ultima location del cliente
    private FusedLocationProviderClient locationClient;

    private com.google.android.gms.location.LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        prepareLocationServices();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //mi mostra la posizione dell'utente appena apro la mia app
        showMeTheUserCurrentLocation();
    }

    private void giveMePermissionToAccessLocation() {

        //oggetto ActivityCompat serve per richiedere l'accesso a determinati permessi
        //in questo caso serve per accedere alla location
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_REQUEST_CODE);

    }

    //risposta dell'utente sulla richiesta di accesso o meno
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //gestione della risposta a questa richiesta
        //vediamo se è stata fatta 1 sola richiesta vedendo se è uguale a 1 e
        //vediamo se il primo elemento dell'array è uguale al permesso "positivo"
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //posso accedere la location dell'utente
                showMeTheUserCurrentLocation();
            } else {
                //non ho il permesso di accedere alla location dell'utente
                Toast.makeText(this, "Utente ha rifiutato di dare l'accesso alla sua location ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showMeTheUserCurrentLocation() {


        //se l'utente non ci ha dato il permesso devo chiederlo ancora
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            giveMePermissionToAccessLocation();
            //l'utente ce lo ha finalmente dato, allora vado avanti
        } else {
        /*

            //cancella tutti i marker precedentemente creati dalle mappe precedenti
            mMap.clear();

            // se non c'è nessuna richiesta di location la creo
            if (mLocationRequest == null) {

                mLocationRequest = com.google.android.gms.location.LocationRequest.create();

                //se questa richiesta va a buon fine, chiedo info sulla location
                if (mLocationRequest != null) {

                    //ottengo determinati dati della location
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(5000);
                    mLocationRequest.setFastestInterval(1000);

                    //callback per ottenere la location
                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            //chiama il metodo stesso per sapere la location
                            showMeTheUserCurrentLocation();
                        }
                    };

                    //aggiorna i dati sulla location
                    locationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);

                }
            }

            */

            //codice per creare un sopra layer che gestisce meglio la posizione snellendo il codice
            //crea il bottone blu x centrare la mia app a seconda di dove sono io e della mia posizione
            mMap.setMyLocationEnabled(true);

            //oggetto che fornisce l'ultima location del cliente
            //voglio essere notificato quando ottengo la location dell'utente o se l'app fallisce nel farlo
            locationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //ci da il valore reale della location
                    Location location = task.getResult();

                    //se la location esiste passo longitudine e latitudine al mio oggetto per farli vedere e aggiorno la camera
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("My location"));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);

                        mMap.moveCamera(cameraUpdate);
                    } else {
                        Toast.makeText(MapsActivity.this, "Qualcosa è andato male, riprova ancora", Toast.LENGTH_SHORT).show();
                    }
                }

            }) ;

        }

    }

        //inizializzo il "locationCliente"
    private void prepareLocationServices() {
        //restituisce la location dell'utente
        locationClient = LocationServices.getFusedLocationProviderClient(this);
    }

}