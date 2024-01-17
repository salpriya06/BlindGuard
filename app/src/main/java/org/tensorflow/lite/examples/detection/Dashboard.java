package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/*

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
*/
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements RecognitionListener
{
    GpsTracker gpsTracker;
    CardView card1,card2,card3;
    private TextView returnedText;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    TextToSpeech textToSpeech;

  /*  FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    int flg = 0;
    int i = 0;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;
*/
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        card1=findViewById(R.id.card1);
        card2=findViewById(R.id.card2);
        card3=findViewById(R.id.card3);

        returnedText=findViewById(R.id.textView1);
        progressBar=findViewById(R.id.progressBar1);


        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        progressBar.setIndeterminate(true);
        speech.startListening(recognizerIntent);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Dashboard.this,OCR.class);
                startActivity(intent);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Dashboard.this,DetectorActivity.class);
                startActivity(intent);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =Dashboard.this.getPackageManager().getLaunchIntentForPackage("org.tensorflow.lite.examples.classification");
                startActivity(intent);
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR) {
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        speech.startListening(recognizerIntent);
        //startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
    }
    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }
    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        returnedText.setText(text);
        //  textToSpeech.speak(returnedText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

        if(text.contains("open ocr")||text.contains("read document")||text.contains("scan document")||text.contains("read a document"))
        {
            Intent intent =new Intent(Dashboard.this,OCR.class);
            startActivity(intent);
        }
        if(text.contains("detect object"))
        {
            Intent intent1=new Intent(Dashboard.this,DetectorActivity.class);
            startActivity(intent1);
           // Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            // Intent intent =new Intent(MainActivity.this,Activity1.class);
            //startActivity(intent);
        }
        if(text.contains("where i am")||text.contains("what is my location"))
        {
            Toast.makeText(this, "fetching", Toast.LENGTH_SHORT).show();
            /*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationGetter loc=new LocationGetter(Dashboard.this,101,locationManager);
            loc.getLocation();*/
            gpsTracker = new GpsTracker(Dashboard.this);
            if(gpsTracker.canGetLocation()){
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                Toast.makeText(Dashboard.this, "lat:"+latitude+"longitude:"+longitude, Toast.LENGTH_SHORT).show();
                getTheAddress(latitude,longitude);
            }else{
                gpsTracker.showSettingsAlert();
            }
        }
        if(text.contains("open detect currency")||text.contains("detect currency"))
        {

            Intent intent =Dashboard.this.getPackageManager().getLaunchIntentForPackage("org.tensorflow.lite.examples.classification");
            startActivity(intent);
        }
        speech.startListening(recognizerIntent);
    }
    private void getTheAddress(double latitude, double longitude) {
        List<Address> addresses;
        Geocoder geocoder; geocoder = new Geocoder(Dashboard.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.i("#address", city);
            Toast.makeText(Dashboard.this, ""+address, Toast.LENGTH_SHORT).show();
            textToSpeech.speak(address,TextToSpeech.QUEUE_FLUSH,null);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public  String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        speech.startListening(recognizerIntent);
        return message;
    }
/*
    public  void Location()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {

                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    Toast.makeText(Dashboard.this, "latlong:" + currentLatitude + "," + currentLongitude, Toast.LENGTH_SHORT).show();
                    String address = getCompleteAddressString();
                    textToSpeech.speak(address,TextToSpeech.QUEUE_FLUSH,null,null);
                } else {
                    Toast.makeText(Dashboard.this, "null", Toast.LENGTH_SHORT).show();

                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    String address=  getCompleteAddressString();
                    textToSpeech.speak(address,TextToSpeech.QUEUE_FLUSH,null,null);
                    Toast.makeText(Dashboard.this, "2nd:"+currentLatitude + "," + currentLongitude, Toast.LENGTH_SHORT).show();

                }
            }
        };
    }
    private void startLocationUpdates() {
        //  fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }



    private String getCompleteAddressString() {
        String strAdd = "";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        Log.i("prk:", "lat:"+currentLatitude+"");
        Log.i("prk:", "longi:"+currentLongitude+"");
        List<Address> addresses;

        try {
            addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            if (addresses.size() > 0)

            {
                strAdd = addresses.get(0).getAddressLine(0);
                String zip = addresses.get(0).getPostalCode();
                // String knownName = addresses.get(0).getFeatureName();


                strAdd=strAdd+","+zip;

            }


        } catch (Exception e) {

        }

        return strAdd;

    }
*/
}
