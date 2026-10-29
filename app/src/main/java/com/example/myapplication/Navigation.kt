package com.example.myapplication

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavEntry(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val targetActivity: Class<*>?
)

val bottomNavEntries = listOf(
    BottomNavEntry(
        route = "home",
        icon = Icons.Default.Home,
        label = "Início",
        targetActivity = PetHome::class.java
    ),
    BottomNavEntry(
        route = "calendar",
        icon = Icons.Default.DateRange,
        label = "Calendário",
        targetActivity = Calendario::class.java
    ),
    BottomNavEntry(
        route = "tasks",
        icon = Icons.AutoMirrored.Filled.List,
        label = "Tarefas",
        targetActivity = MainActivity::class.java
    ),
    BottomNavEntry(
        route = "store",
        icon = Icons.Default.ShoppingCart,
        label = "Loja",
        targetActivity = Loja::class.java
    ),
    BottomNavEntry(
        route = "profile",
        icon = Icons.Default.Person,
        label = "Perfil",
        targetActivity = Perfil::class.java
    )
)

@Composable
fun CustomBottomNavigation(currentRoute: String = "home") {
    val context = LocalContext.current

    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavEntries.forEach { entry ->
                val isSelected = entry.route == currentRoute
                val color = if (isSelected) Color(0xFF12C2A3) else Color(0xFF8C9BA5)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (entry.route == currentRoute) return@clickable

                            entry.targetActivity?.let { target ->
                                val intent = Intent(context, target).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                context.startActivity(intent)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = entry.icon,
                        contentDescription = entry.label,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = entry.label,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
