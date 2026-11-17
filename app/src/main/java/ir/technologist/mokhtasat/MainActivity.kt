package ir.technologist.mokhtasat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CoordinateFinder() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinateFinder() {
    val context = LocalContext.current
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }
    var altitude by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var dark by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf(false) }
    val teal = Color(0xFF07877F)
    MaterialTheme(colorScheme = if (dark) darkColorScheme(primary = Color(0xFF63D5C8)) else lightColorScheme(primary = teal)) {
        Scaffold(topBar = {
            TopAppBar(title = { Column {
                Text("مختصات‌یاب تکنولوژیست", fontWeight = FontWeight.Bold)
                Text("TECHNOLOGIST", style = MaterialTheme.typography.labelSmall)
            } }, actions = { IconButton(onClick = { dark = !dark }) {
                Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "تغییر پوسته")
            } })
        }) { pad ->
            Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.MyLocation, null, tint = teal)
                        Text("مقصد کجاست؟", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("عرض و طول جغرافیایی را جداگانه وارد کنید.")
                    }
                }
                Card(shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(lat, { lat = it; result = false }, label = { Text("عرض جغرافیایی (Latitude)") }, placeholder = { Text("36.123456") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), trailingIcon = { if(lat.isNotEmpty()) IconButton(onClick={lat=""}) { Icon(Icons.Default.Close,null) } })
                        OutlinedTextField(lon, { lon = it; result = false }, label = { Text("طول جغرافیایی (Longitude)") }, placeholder = { Text("57.123456") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), trailingIcon = { if(lon.isNotEmpty()) IconButton(onClick={lon=""}) { Icon(Icons.Default.Close,null) } })
                        OutlinedTextField(altitude, { altitude = it }, label = { Text("ارتفاع (اختیاری، متر)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                        Button(onClick = {
                            val a = lat.replace(',', '.').toDoubleOrNull()
                            val b = lon.replace(',', '.').toDoubleOrNull()
                            if (a == null || b == null || a !in -90.0..90.0 || b !in -180.0..180.0) {
                                error = "لطفاً عرض و طول جغرافیایی معتبر وارد کنید."; result = false
                            } else { error = ""; result = true }
                        }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
                            Icon(Icons.Default.LocationSearching, null); Spacer(Modifier.width(8.dp)); Text("نمایش موقعیت روی نقشه")
                        }
                        if(error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
                    }
                }
                if(result) {
                    val a = lat.replace(',', '.').toDouble()
                    val b = lon.replace(',', '.').toDouble()
                    Card(shape = RoundedCornerShape(22.dp)) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("مختصات آماده است", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("GPS: " + String.format(Locale.US, "%.6f, %.6f", a, b))
                            Text("DMS: " + dms(a, true) + " | " + dms(b, false))
                            altitude.toDoubleOrNull()?.let { Text("ارتفاع واردشده: $it متر") }
                            Button(onClick = {
                                val uri = Uri.parse("geo:$a,$b?q=$a,$b")
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                                    .onFailure { error = "برنامه نقشه‌ای روی دستگاه پیدا نشد." }
                            }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Map, null); Spacer(Modifier.width(8.dp)); Text("باز کردن در نقشه")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun dms(v: Double, latitude: Boolean): String {
    val x = abs(v); val d = x.toInt(); val mf = (x-d)*60; val m = mf.toInt(); val s = (mf-m)*60
    val dir = if(latitude) if(v>=0) "N" else "S" else if(v>=0) "E" else "W"
    return String.format(Locale.US, "%d° %02d' %05.2f\" %s", d, m, s, dir)
}
