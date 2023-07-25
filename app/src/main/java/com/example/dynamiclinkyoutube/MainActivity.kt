package com.example.dynamiclinkyoutube

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamiclinkyoutube.ui.theme.DynamicLinkYoutubeTheme
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class MainActivity : ComponentActivity() {
    private var isDeepLink: Boolean = false
    private var deepLinkData: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        deepLinkData = appLinkIntent.data

        if(deepLinkData != null){
            isDeepLink = true
        }

        setContent {
            DynamicLinkYoutubeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home"){
                        composable("home"){
                            Greeting("home")

                            LaunchedEffect(key1 = isDeepLink){
                                if(isDeepLink) {
                                    navController.navigate("dynamic_link_screen")
                                }
                            }
                        }

                        composable("dynamic_link_screen"){
//                            deepLinkData?.let{ uri ->
//                                DynamicLinkScreen()
//                            }
                            DynamicLinkScreen(
                                callback = {
                                    isDeepLink = false
                                    navController.navigate("home")
                                }
                            )
                        }
                    }

                }
            }
        }

        getDynamicLinks(intent)
    }

    private fun getDynamicLinks(intent: android.content.Intent){
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicData ->
                if(pendingDynamicData != null){
                    isDeepLink = true
                    Toast.makeText(
                        applicationContext,
                        "Dynamic Link called",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    Log.d("Dynamic Link", "pendingDynamicData is Null")
                }
            }
    }
}

@Composable
fun Greeting(name: String) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Hello $name!")

        Button(onClick = {
            uriHandler.openUri("https://dynamiclinkyoutubekenmaro.page.link/test")
        }) {
            Text("Open")
        }
    }
}

@Composable
fun DynamicLinkScreen(callback: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Hello dynamic link called!")

        Button(onClick = {
            callback()
        }) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DynamicLinkYoutubeTheme {
        Greeting("Android")
    }
}