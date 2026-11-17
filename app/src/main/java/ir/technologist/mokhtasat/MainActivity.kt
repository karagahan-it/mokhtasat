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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(applicationContext)
        setContent { MokhtasatMapScreen() }
    }
}

@Composable
private fun MokhtasatMapScreen() {
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

    MaterialTheme(colorScheme = lightColorScheme(primary = Teal, surface = Color.White, background = Color(0xFFF3F6F8))) {
        Box(Modifier.fillMaxSize()) {
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
            Row(
                Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(14.dp)
                    .background(Navy.copy(alpha = 0.96f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 15.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("مختصات‌یاب تکنولوژیست", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("MapLibre · OpenStreetMap", color = Color(0xFFB8D0D7), style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = { panelExpanded = !panelExpanded }) {
                    Icon(if (panelExpanded) Icons.Default.ExpandMore else Icons.Default.Search, null, tint = Color.White)
                }
            }
            if (panelExpanded) {
                Card(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(12.dp),
                    shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(12.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("پیدا کردن موقعیت", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        OutlinedTextField(latitude, { latitude = it; error = "" }, Modifier.fillMaxWidth(),
                            label = { Text("عرض جغرافیایی") }, placeholder = { Text("36.123456") },
                            leadingIcon = { Icon(Icons.Default.North, null) }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        OutlinedTextField(longitude, { longitude = it; error = "" }, Modifier.fillMaxWidth(),
                            label = { Text("طول جغرافیایی") }, placeholder = { Text("57.123456") },
                            leadingIcon = { Icon(Icons.Default.East, null) }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        OutlinedTextField(altitude, { altitude = it }, Modifier.fillMaxWidth(),
                            label = { Text("ارتفاع (اختیاری، متر)") }, leadingIcon = { Icon(Icons.Default.Terrain, null) },
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
                        Text(info, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("محدوده تقریبی ۱۰ × ۱۰ کیلومتر · زوم ۱۰ تا ۱۵", style = MaterialTheme.typography.labelSmall)
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
}
