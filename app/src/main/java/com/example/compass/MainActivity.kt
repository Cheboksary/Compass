package com.example.compass

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.compass.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding:ActivityMainBinding
    //compass fields
    private lateinit var degreeTextView: TextView
    private lateinit var dinamicView: ImageView
    var manager: SensorManager?=null
    var currentDegree: Int = 0

    //location fields
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var locationTextView: TextView

    val REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //compass fields
        degreeTextView = binding.upperTextView
        dinamicView = binding.dinamicView
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //check permission
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
        else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback(){

            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations[p0.locations.size-1] //last location
                locationTextView = binding.locationTextView
                locationTextView.text = location.latitude.toString()+"/"+location.longitude.toString()
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        locationRequest.smallestDisplacement = 5F
    }

    override fun onResume() {
        super.onResume()
        manager?.registerListener(this, manager?.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME)
    }
    override fun onPause() {
        super.onPause()
        manager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val degree:Int = event?.values?.get(0)?.toInt()!!
        degreeTextView.text = degree.toString()
        val rotateAnimation = RotateAnimation(currentDegree.toFloat(),(-degree).toFloat(),Animation.RELATIVE_TO_SELF,0.5f,
            Animation.RELATIVE_TO_SELF,0.5f)
        rotateAnimation.duration = 210
        rotateAnimation.fillAfter = true
        currentDegree = -degree
        dinamicView.startAnimation(rotateAnimation)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}