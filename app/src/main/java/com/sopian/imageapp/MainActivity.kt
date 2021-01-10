package com.sopian.imageapp

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.sopian.imageapp.core.utils.ConnectivityReceiver
import com.sopian.imageapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackbar = Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_SHORT)
            snackbar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackbar?.setAction("DISMISS") {
                snackbar?.dismiss()
            }
            snackbar?.show()
        } else {
            snackbar?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snackbar = null
        unregisterReceiver(ConnectivityReceiver())
    }
}