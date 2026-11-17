package ir.technologist.mokhtasat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition

private const val MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
private val Teal = Color(0xFF07877F)
private val Navy = Color(0xFF102D3A)
private val Ink = Color(0xFF183746)
private val Muted = Color(0xFF6B8189)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(applicationContext)
        setContent { MokhtasatApp() }
    }
}

@Composable
private fun MokhtasatApp() {
    var showMap by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = lightColorScheme(primary = Teal, surface = Color.White, background = Color(0xFFF3F6F8))) {
        if (showMap) {
            MokhtasatMapScreen(onBack = { showMap = false })
        } else {
            WelcomeScreen(onStart = { showMap = true })
        }
    }
}

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF0B2633), Color(0xFF124D55), Color(0xFF087E78)))
        )
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.size(116.dp).background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(34.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(82.dp).background(Color.White, RoundedCornerShape(26.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Teal, modifier = Modifier.size(48.dp))
                }
            }
            Spacer(Modifier.height(28.dp))
            Text("مختصات یاب", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Text(
                "موقعیت دقیق، سریع و ساده",
                color = Color(0xFFD3E8E8),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(34.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                FeaturePill(Icons.Default.PinDrop, "جست‌وجوی مختصات")
                FeaturePill(Icons.Default.Map, "نقشه داخلی")
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Navy)
            ) {
                Text("ورود به اپلیکیشن", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "طراحی و توسعه | تکنولوژیست",
                color = Color(0xFFE0F0EF),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FeaturePill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFFBCEBE4), modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun AppHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Navy, Color(0xFF12636A))), RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = Color(0xFFB9D6D9), style = MaterialTheme.typography.labelSmall)
        }
        Box(Modifier.size(42.dp).background(Color.White.copy(alpha = 0.13f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Explore, null, tint = Color(0xFFBCEBE4), modifier = Modifier.size(25.dp))
        }
    }
}

@Composable
private fun MokhtasatMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var altitude by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("نقشه برداری OpenStreetMap") }
    var target by remember { mutableStateOf<LatLng?>(null) }
    var panelExpanded by remember { mutableStateOf(true) }
    var downloading by remember { mutableStateOf(false) }
    var mapReady by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(Color(0xFFF3F6F8))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).also { view ->
                    view.onCreate(Bundle())
                    view.getMapAsync { map ->
                        map.setStyle(Style.Builder().fromUri(MAP_STYLE_URL)) {
                            map.uiSettings.isLogoEnabled = true
                            map.uiSettings.isAttributionEnabled = true
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(36.0, 57.0), 6.0))
                            mapReady = true
                        }
                    }
                }
            },
            update = { view ->
                target?.let { point ->
                    view.getMapAsync { map -> map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15.0)) }
                }
            }
        )

        if (target != null) {
            Icon(Icons.Default.LocationOn, "مقصد انتخاب‌شده", tint = Color(0xFFE34B4B),
                modifier = Modifier.align(Alignment.Center).size(48.dp).offset(y = (-22).dp))
        }

        Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(12.dp)) {
            AppHeader("مختصات یاب", "سامانه موقعیت‌یابی", onBack)
            Spacer(Modifier.height(8.dp))
            Surface(color = Navy.copy(alpha = 0.92f), shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(horizontal = 11.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Public, null, tint = Color(0xFFBCEBE4), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("نقشه آزاد · MapLibre", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (panelExpanded) {
            Card(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(26.dp),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("جست‌وجوی موقعیت", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink)
                            Text("مختصات را جداگانه وارد کنید", style = MaterialTheme.typography.bodySmall, color = Muted)
                        }
                        IconButton(onClick = { panelExpanded = false }) { Icon(Icons.Default.ExpandMore, "جمع کردن پنل", tint = Teal) }
                    }
                    OutlinedTextField(latitude, { latitude = it; error = "" }, Modifier.fillMaxWidth(),
                        label = { Text("عرض جغرافیایی") }, placeholder = { Text("36.123456") },
                        leadingIcon = { Icon(Icons.Default.North, null, tint = Teal) }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(longitude, { longitude = it; error = "" }, Modifier.fillMaxWidth(),
                        label = { Text("طول جغرافیایی") }, placeholder = { Text("57.123456") },
                        leadingIcon = { Icon(Icons.Default.East, null, tint = Teal) }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(altitude, { altitude = it }, Modifier.fillMaxWidth(),
                        label = { Text("ارتفاع (اختیاری، متر)") }, leadingIcon = { Icon(Icons.Default.Terrain, null, tint = Teal) },
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    Button(onClick = {
                        val lat = latitude.trim().replace(',', '.').toDoubleOrNull()
                        val lon = longitude.trim().replace(',', '.').toDoubleOrNull()
                        if (lat == null || lon == null || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
                            error = "عرض و طول جغرافیایی معتبر وارد کنید."
                        } else {
                            error = ""
                            target = LatLng(lat, lon)
                            panelExpanded = false
                        }
                    }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp)) {
                        Icon(Icons.Default.LocationSearching, null); Spacer(Modifier.width(8.dp))
                        Text("نمایش مقصد روی نقشه", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(enabled = target != null && !downloading, onClick = {
                        val point = target ?: return@OutlinedButton
                        downloading = true
                        info = "در حال ایجاد بسته آفلاین…"
                        val bounds = LatLngBounds.Builder()
                            .include(LatLng(point.latitude + 0.045, point.longitude - 0.045))
                            .include(LatLng(point.latitude - 0.045, point.longitude + 0.045)).build()
                        val definition = OfflineTilePyramidRegionDefinition(
                            MAP_STYLE_URL, bounds, 10.0, 15.0, context.resources.displayMetrics.density)
                        OfflineManager.getInstance(context).createOfflineRegion(
                            definition, ("mokhtasat-" + point.latitude + "-" + point.longitude).toByteArray(),
                            object : OfflineManager.CreateOfflineRegionCallback {
                                override fun onCreate(region: OfflineRegion) {
                                    region.setDownloadState(OfflineRegion.STATE_ACTIVE)
                                    downloading = false
                                    info = "دانلود نقشه آغاز شد؛ برای تکمیل آنلاین بمانید."
                                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                                }
                                override fun onError(errorMessage: String) {
                                    downloading = false
                                    info = "خطا در دانلود نقشه: " + errorMessage
                                }
                            })
                    }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                        if (downloading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.DownloadForOffline, null)
                        Spacer(Modifier.width(8.dp)); Text("دانلود محدوده برای استفاده آفلاین")
                    }
                    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
                    Text(info, style = MaterialTheme.typography.labelSmall, color = Muted)
                    Text("محدوده تقریبی ۱۰ × ۱۰ کیلومتر · زوم ۱۰ تا ۱۵", style = MaterialTheme.typography.labelSmall, color = Muted)
                    if (!mapReady) LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        } else {
            FloatingActionButton(onClick = { panelExpanded = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp),
                containerColor = Teal, contentColor = Color.White) { Icon(Icons.Default.Search, "جست‌وجو") }
        }
    }
}
