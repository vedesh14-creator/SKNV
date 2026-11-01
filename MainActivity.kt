package com.rolex.smokefree30

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val Bg = Color(0xFF090A0F)
private val Panel = Color(0xFF171922)
private val Red = Color(0xFFFF3434)
private val Green = Color(0xFF43E477)
private val Muted = Color(0xFFB8BBC7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("smokefree", MODE_PRIVATE)
        val saved = prefs.getString("quit_date", null)
        val initial = runCatching { saved?.let(LocalDate::parse) }.getOrNull()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                background = Bg, surface = Panel, primary = Green,
                secondary = Red, onBackground = Color.White, onSurface = Color.White
            )) {
                SmokeFreeApp(initial ?: LocalDate.now()) { date ->
                    prefs.edit().putString("quit_date", date.toString()).apply()
                }
            }
        }
    }
}

@Composable
fun SmokeFreeApp(initialDate: LocalDate, saveDate: (LocalDate) -> Unit) {
    var quitDate by remember { mutableStateOf(initialDate) }
    var cigarettesPerDay by remember { mutableStateOf(3f) }
    var costPerCigarette by remember { mutableStateOf(18f) }
    var showDateHelp by remember { mutableStateOf(false) }
    val days = (ChronoUnit.DAYS.between(quitDate, LocalDate.now()).toInt() + 1).coerceAtLeast(1)
    val progress = (days.coerceAtMost(30) / 30f)
    val savedMoney = ((days - 1).coerceAtLeast(0) * cigarettesPerDay * costPerCigarette).toInt()
    val milestones = listOf(7, 14, 21, 30)

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Bg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("🚭 SMOKE-FREE 30", color = Red, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text("One day at a time. Your healthier future starts here.", color = Muted, fontSize = 14.sp)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("YOUR SMOKE-FREE STREAK", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$days", color = Green, fontSize = 54.sp, fontWeight = FontWeight.Black)
                        Text(" days", color = Color.White, fontSize = 22.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp), color = Green, trackColor = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    Text("${(progress * 100).toInt()}% of your first 30-day challenge", color = Muted)
                    TextButton(onClick = { showDateHelp = true }) { Text("Change quit date") }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🏆 MILESTONES", color = Red, fontWeight = FontWeight.Bold)
                    milestones.forEach { m ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$m days")
                            Text(if (days >= m) "UNLOCKED ✓" else "Keep going", color = if (days >= m) Green else Muted)
                        }
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("💰 MONEY NOT SPENT", color = Red, fontWeight = FontWeight.Bold)
                    Text("₹$savedMoney", color = Green, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("Estimated from the cigarettes you would have smoked.", color = Muted, fontSize = 12.sp)
                    Text("Cigarettes per day: ${cigarettesPerDay.toInt()}")
                    Slider(value = cigarettesPerDay, onValueChange = { cigarettesPerDay = it }, valueRange = 1f..5f)
                    Text("Cost per cigarette: ₹${costPerCigarette.toInt()}")
                    Slider(value = costPerCigarette, onValueChange = { costPerCigarette = it }, valueRange = 5f..40f)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🫁 BODY RECOVERY TIMELINE", color = Red, fontWeight = FontWeight.Bold)
                    listOf(
                        "20 minutes" to "Heart rate begins moving toward normal.",
                        "12 hours" to "Carbon monoxide level in blood drops toward normal.",
                        "2–3 days" to "Nicotine is generally cleared; withdrawal may be strongest.",
                        "2–12 weeks" to "Circulation and lung function can improve.",
                        "1–9 months" to "Coughing and shortness of breath often decrease."
                    ).forEach { (time, detail) ->
                        Text(time, color = Green, fontWeight = FontWeight.Bold)
                        Text(detail, color = Muted, fontSize = 13.sp)
                        HorizontalDivider(color = Color.DarkGray)
                    }
                    Text("Recovery varies. These are typical milestones, not a guarantee.", color = Muted, fontSize = 11.sp)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🆘 CRAVING? TRY THE 5 Ds", color = Red, fontWeight = FontWeight.Bold)
                    listOf("Delay for 5–10 minutes", "Deep breathe slowly", "Drink a glass of water", "Distract yourself with a short walk", "Discuss it with someone supportive").forEachIndexed { i, s ->
                        Text("${i + 1}. $s", color = Color.White)
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("🔥 DAILY REMINDER", color = Red, fontWeight = FontWeight.Bold)
                    Text("Not even one puff. A craving passes; your progress is worth protecting.", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text("If you slip, don't give up. Stop again straight away and seek support if needed.", color = Muted, fontSize = 12.sp)
                    Text("India Tobacco Quitline: 1800-11-2356", color = Green, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    if (showDateHelp) {
        AlertDialog(
            onDismissRequest = { showDateHelp = false },
            title = { Text("Set your quit date") },
            text = { Text("Your streak starts from the date you last smoked. To keep this simple, use today's date only if you quit today. For an earlier quit date, tell us the date and we can add a date picker in the next version.") },
            confirmButton = { TextButton(onClick = { showDateHelp = false }) { Text("Got it") } }
        )
    }
}
