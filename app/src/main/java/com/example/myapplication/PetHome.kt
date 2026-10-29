package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.myapplication.R
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect

class PetHome : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TaskRepository.init(applicationContext)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    PetHomeScreen(navController = navController)
                }
                composable("adicionar_tarefa") {
                    AddTaskScreen(
                        onBack = { navController.popBackStack() },
                        onSave = { description, days, reward ->
                            if (description.isNotBlank()) {
                                val ordem = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
                                val diasTexto = days.sortedBy { ordem.indexOf(it) }.joinToString(",")
                                TaskRepository.addTask(description, reward, diasTexto)
                            }
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = "detalhes_tarefa/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { entry ->
                    val id = entry.arguments?.getInt("id") ?: -1
                    TarefaDetalhesScreen(
                        taskId = id,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

// Cores
val CreamBg = Color(0xFFFFFDF7)
val CardWhite = Color(0xFFFFFFFF)
private val CorTrilhaProgresso = Color(0xFFE7E1D2)
val OrangeBadge = Color(0xFFFFA000)
val YellowCoinBg = Color(0xFFFFF5D6)
val YellowCoinBorder = Color(0xFFFFD54F)
val TealPrimary = Color(0xFF12C2A3)
private val CorVerdeCheck = Color(0xFF3FBF7F)
val TextDark = Color(0xFF2B3A42)
val TextGray = Color(0xFF8C9BA5)

@Composable
fun PetHomeScreen(navController: NavController) {
    // Observa as tarefas em tempo real; só mostra as que ainda faltam fazer
    val taskList by TaskRepository.tasks.collectAsState()
    val pendingTasks = taskList.filter { !it.isCompleted }

    // Ao abrir a tela, busca as tarefas no banco (Room)
    LaunchedEffect(Unit) { TaskRepository.carregar() }

    Scaffold(
        containerColor = CreamBg,
        bottomBar = { CustomBottomNavigation(currentRoute = "home") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { TopHeaderSection() }
            item { PetHeroSection() }
            item { TaskSectionHeader(navController = navController) }

            items(pendingTasks, key = { it.id }) { task ->
                TaskItem(
                    title = task.title,
                    reward = "+${task.reward}",
                    onClick = { navController.navigate("detalhes_tarefa/${task.id}") }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TopHeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD1DC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(OrangeBadge)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "NÍVEL 2",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(YellowCoinBg)
                    .border(1.dp, YellowCoinBorder, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(OrangeBadge)
                )
                Text(
                    text = "150",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = TextGray
                )
            }
        }
    }
}

@Composable
fun PetHeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Pet e cenário",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\"Vamos fazer as tarefas de hoje?\"",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TaskSectionHeader(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TAREFAS A FAZER",
            color = TextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )

        FloatingActionButton(
            onClick = {
                navController.navigate("adicionar_tarefa")
            },
            containerColor = TealPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar Tarefa"
            )
        }
    }
}

@Composable
fun TaskItem(title: String, reward: String, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFE0E0E0), CircleShape)
                )
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(YellowCoinBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = reward,
                    color = OrangeBadge,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(OrangeBadge)
                )
            }
        }
            BotaoAtualizarTarefa(
                onClick = onClick,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
            )
        }
    }
}
@Composable
private fun CheckboxCircular(marcado: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (marcado) CorVerdeCheck else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (marcado) Color.Transparent else CorTrilhaProgresso,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (marcado) {
            Text(
                text = "✓",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}