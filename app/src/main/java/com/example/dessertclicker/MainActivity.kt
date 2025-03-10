package com.example.dessertclicker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dessertclicker.data.DessertUiState
import com.example.dessertclicker.ui.theme.DessertClickerTheme
import com.example.dessertclicker.ui.DessertViewModel

// Tag for logging.
private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        setContent {
            DessertClickerTheme {
                // Call the main app function that uses the ViewModel.
                DessertClickerApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }
}

/**
 * Share dessert sold information using an ACTION_SEND intent.
 */
private fun shareSoldDessertsInformation(intentContext: Context, dessertsSold: Int, revenue: Int) {
    val shareText = "I sold $dessertsSold desserts and earned $$revenue in revenue!"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    try {
        startActivity(intentContext, shareIntent, null)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(intentContext, "Sharing not available", Toast.LENGTH_LONG).show()
    }
}

/**
 * The main app function uses a ViewModel.
 * We wrap the ViewModel's onDessertClicked function in a lambda that ignores the passed Int.
 */
@Composable
private fun DessertClickerApp(
    viewModel: DessertViewModel = viewModel()
) {
    val uiState by viewModel.dessertUiState.collectAsState()
    // Wrap the call to match the (Int) -> Unit signature.
    DessertClickerContent(
        uiState = uiState,
        onDessertClicked = { _ -> viewModel.onDessertClicked() }
    )
}

/**
 * This function accepts the UI state and a callback that takes a dessert ID.
 */
@Composable
private fun DessertClickerContent(
    uiState: DessertUiState,
    onDessertClicked: (dessertId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            val intentContext = LocalContext.current
            AppBar(
                onShareButtonClicked = {
                    shareSoldDessertsInformation(
                        intentContext = intentContext,
                        dessertsSold = uiState.dessertsSold,
                        revenue = uiState.revenue
                    )
                }
            )
        }
    ) { contentPadding ->
        // Display two dessert images side by side.
        // Cupcake click maps to dessertId = 1; Donut click maps to dessertId = 2.
        DessertClickerScreen(
            revenue = uiState.revenue,
            dessertsSold = uiState.dessertsSold,
            onCupcakeClicked = { onDessertClicked(1) },
            onDonutClicked = { onDessertClicked(2) },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun AppBar(
    onShareButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dessert Clicker",
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h6
        )
        IconButton(
            onClick = onShareButtonClicked,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}

/**
 * Updated screen that shows two dessert images (cupcake and donut) side by side.
 * Cupcake is priced at $10 and donut remains at $5.
 * Ensure your project contains drawable resources named cupcake, donut, and bakery_back.
 */
@Composable
fun DessertClickerScreen(
    revenue: Int,
    dessertsSold: Int,
    onCupcakeClicked: () -> Unit,
    onDonutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background image.
        Image(
            painter = painterResource(R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cupcake column (priced at $10).
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onCupcakeClicked() }
                ) {
                    Image(
                        painter = painterResource(R.drawable.cupcake),
                        contentDescription = "Cupcake",
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$10")
                }
                // Donut column (priced at $5).
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onDonutClicked() }
                ) {
                    Image(
                        painter = painterResource(R.drawable.donut),
                        contentDescription = "Donut",
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$5")
                }
            }
            TransactionInfo(revenue = revenue, dessertsSold = dessertsSold)
        }
    }
}

@Composable
private fun TransactionInfo(
    revenue: Int,
    dessertsSold: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(Color.White)) {
        DessertsSoldInfo(dessertsSold)
        RevenueInfo(revenue)
    }
}

@Composable
private fun RevenueInfo(revenue: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Total Revenue",
            style = MaterialTheme.typography.h4
        )
        Text(
            text = "$$revenue",
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.h4
        )
    }
}

@Composable
private fun DessertsSoldInfo(dessertsSold: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Desserts Sold",
            style = MaterialTheme.typography.h6
        )
        Text(
            text = dessertsSold.toString(),
            style = MaterialTheme.typography.h6
        )
    }
}

/**
 * Preview of only the DessertClickerScreen.
 */
@Preview
@Composable
fun MyDessertClickerScreenPreview() {
    DessertClickerTheme {
        DessertClickerScreen(
            revenue = 100,
            dessertsSold = 20,
            onCupcakeClicked = {},
            onDonutClicked = {}
        )
    }
}
