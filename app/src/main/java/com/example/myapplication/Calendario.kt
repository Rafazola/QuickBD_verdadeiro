package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.jvm.java

class Calendario : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarScreen()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}
val BackgroundColor = Color(0xFFFFFDF6)
val PrimaryGreen = Color(0xFF1ABC9C)
val SoftGreen = Color(0xFFE8F8F5)
val YellowBadge = Color(0xFFFFF3CD)
val GoldText = Color(0xFFD4AC0D)
val DarkText = Color(0xFF2C3E50)
val GrayText = Color(0xFF95A5A6)
val GrayLight = Color(0xFFECF0F1)

data class TaskUIModel(
    val title: String,
    val reward: Int,
    val isCompleted: Boolean
)

val sampleTasks = listOf(
    TaskUIModel("Escovar os dentes de manhã-", 10, isCompleted = true),
    TaskUIModel("Fazer lição de Ciências", 30, isCompleted = false),
    TaskUIModel("Arrumar a cama", 15, isCompleted = false)
)

@Composable
fun CalendarScreen() {
    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { TopHeader(coins = 150) }
            item { CalendarCard() }
            item { TaskHeader(dateTitle = "Terça, 23 de Outubro") }

            items(sampleTasks) { task ->
                TaskItemCard(task = task)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
@Composable
fun TopHeader(coins: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Calendário",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        CoinBadge(coins = coins)
    }
}

@Composable
fun CoinBadge(coins: Int) {
    Surface(
        color = YellowBadge,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(GoldText, CircleShape)
            )
            Text(
                text = "$coins",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
    }
}

@Composable
fun CalendarCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês Anterior", tint = GrayText)
                }
                Text(
                    text = "Outubro 2026",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkText
                )
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo Mês", tint = GrayText)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val daysOfWeek = listOf("D", "S", "T", "Q", "Q", "S", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontWeight = FontWeight.Bold,
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val calendarDays = listOf(
                listOf(26, 27, 28, 29, 30, 31, 1),
                listOf(2, 3, 4, 5, 6, 7, 8),
                listOf(9, 10, 11, 12, 13, 14, 15),
                listOf(16, 17, 18, 19, 20, 21, 22),
                listOf(23, 24, 25, 26, 27, 28, 29)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                calendarDays.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { day ->
                            DayCell(day = day)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int) {
    val isPreviousMonth = day > 25 && day <= 31
    val isSelected = day == 23
    val isSoftHighlighted = day in listOf(3, 6, 12)

    val bgColor = when {
        isSelected -> PrimaryGreen
        isSoftHighlighted -> SoftGreen
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        isPreviousMonth -> GrayText.copy(alpha = 0.4f)
        else -> DarkText
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor)
    ) {
        Text(
            text = "$day",
            fontSize = 13.sp,
            fontWeight = if (isSelected || isSoftHighlighted) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
@Composable
fun TaskHeader(dateTitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        FloatingActionButton(
            onClick = {},
            containerColor = PrimaryGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
        }
    }
}

@Composable
fun TaskItemCard(task: TaskUIModel) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (task.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(PrimaryGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(2.dp, GrayLight, CircleShape)
                    )
                }

                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) GrayText else DarkText,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            Surface(
                color = YellowBadge,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "+${task.reward}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldText
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(GoldText, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {

    val context = LocalContext.current

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, PetHome::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Início",
                    tint = GrayText
                )
            },
            label = {
                Text(
                    text = "Início",
                    color = GrayText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Calendário",
                    tint = PrimaryGreen
                )
            },
            label = {
                Text(
                    text = "Calendário",
                    color = PrimaryGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = "Tarefas",
                    tint = GrayText
                )
            },
            label = {
                Text(
                    text = "Tarefas",
                    color = GrayText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, Loja::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Loja",
                    tint = GrayText
                )
            },
            label = {
                Text(
                    text = "Loja",
                    color = GrayText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, Perfil::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = GrayText
                )
            },
            label = {
                Text(
                    text = "Perfil",
                    color = GrayText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}
data class NavItem(val label: String, val icon: ImageVector, val isSelected: Boolean)