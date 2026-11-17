package ir.technologist.mokhtasat

import android.os.Bundle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.style.NeshanMapStyle
import java.util.Locale

private val Teal = Color(0xFF07877F)
private val Navy = Color(0xFF102D3A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MokhtasatMapScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MokhtasatMapScreen() {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var altitude by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var target by remember { mutableStateOf<LatLng?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapReady by remember { mutableStateOf(false) }
    var dark by remember { mutableStateOf(false) }
    var panelExpanded by remember { mutableStateOf(true) }

    MaterialTheme(
        colorScheme = if (dark) darkColorScheme(primary = Color(0xFF63D5C8), surface = Color(0xFF183B49), background = Navy)
        else lightColorScheme(primary = Teal, surface = Color.White, background = Color(0xFFF3F6F8))
    ) {
        Box(Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).also { map ->
                        map.moveCamera(LatLng(32.0, 53.0), 0f)
                        map.setZoom(5.5f, 0f)
                        map.mapStyle = NeshanMapStyle.STANDARD_DAY
                        mapView = map
                        mapReady = true
                    }
                },
                update = { map ->
                    val point = target
                    if (point != null) {
                        map.moveCamera(point, 0.6f)
                        map.setZoom(15.5f, 0.6f)
                    }
                }
            )

            // Fixed center marker: the camera is moved to the entered coordinate.
            if (target != null) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "مقصد انتخاب‌شده",
                    tint = Color(0xFFE34B4B),
                    modifier = Modifier.align(Alignment.Center).size(48.dp).offset(y = (-20).dp)
                )
            }

            Row(
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
                    .background(Navy.copy(alpha = 0.94f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("مختصات‌یاب تکنولوژیست", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("سامانه موقعیت‌یابی سازمانی", color = Color(0xFFB8D0D7), style = MaterialTheme.typography.labelSmall)
                }
                Row {
                    IconButton(onClick = { dark = !dark }) {
                        Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "تغییر پوسته", tint = Color.White)
                    }
                    IconButton(onClick = { panelExpanded = !panelExpanded }) {
                        Icon(if (panelExpanded) Icons.Default.ExpandMore else Icons.Default.Search, contentDescription = "نمایش جست‌وجو", tint = Color.White)
                    }
                }
            }

            if (!panelExpanded) {
                FilledIconButton(
                    onClick = { panelExpanded = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp).size(58.dp)
                ) { Icon(Icons.Default.AddLocationAlt, contentDescription = "ورود مختصات") }
            } else {
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(14.dp),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(if (target == null) "پیدا کردن موقعیت" else "مقصد انتخاب شد", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("عرض و طول جغرافیایی را وارد کنید", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = Teal.copy(alpha = 0.12f)) {
                                Text("GPS", color = Teal, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedTextField(
                            value = latitude,
                            onValueChange = { latitude = it; error = "" },
                            label = { Text("عرض جغرافیایی (Latitude)") },
                            placeholder = { Text("مثلاً 36.123456") },
                            leadingIcon = { Icon(Icons.Default.North, null, tint = Teal) },
                            trailingIcon = { if (latitude.isNotEmpty()) IconButton(onClick = { latitude = "" }) { Icon(Icons.Default.Close, "پاک کردن") } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true, shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = longitude,
                            onValueChange = { longitude = it; error = "" },
                            label = { Text("طول جغرافیایی (Longitude)") },
                            placeholder = { Text("مثلاً 57.123456") },
                            leadingIcon = { Icon(Icons.Default.East, null, tint = Teal) },
                            trailingIcon = { if (longitude.isNotEmpty()) IconButton(onClick = { longitude = "" }) { Icon(Icons.Default.Close, "پاک کردن") } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true, shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = altitude,
                            onValueChange = { altitude = it },
                            label = { Text("ارتفاع (اختیاری، متر)") },
                            leadingIcon = { Icon(Icons.Default.Terrain, null, tint = Teal) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true, shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val lat = latitude.trim().replace(',', '.').toDoubleOrNull()
                                val lon = longitude.trim().replace(',', '.').toDoubleOrNull()
                                if (lat == null || lon == null || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
                                    error = "عرض و طول جغرافیایی معتبر وارد کنید."
                                } else {
                                    error = ""
                                    target = LatLng(lat, lon)
                                    panelExpanded = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.LocationSearching, null)
                            Spacer(Modifier.width(9.dp))
                            Text("نمایش مقصد روی نقشه", fontWeight = FontWeight.Bold)
                        }
                        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        target?.let {
                            Text(
                                String.format(Locale.US, "مقصد: %.6f , %.6f", it.latitude, it.longitude),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        if (!mapReady) LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
            }

            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 18.dp, bottom = if (panelExpanded) 340.dp else 94.dp),
                shape = RoundedCornerShape(14.dp),
                color = Navy.copy(alpha = 0.92f)
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, null, tint = Color(0xFF63D5C8), modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("نقشه نشان", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
