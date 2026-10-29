package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TaskRepository.init(applicationContext)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TarefasDoDiaScreen()
                }
            }
        }
    }
}

private val CorFundo = Color(0xFFFDF6E9)
private val CorCard = Color(0xFFFFFFFF)
private val CorMoeda = Color(0xFFF4B740)
private val CorTextoPrincipal = Color(0xFF3B3730)
private val CorTextoSecundario = Color(0xFF8A8578)
private val CorVerdeCheck = Color(0xFF3FBF7F)
private val CorAzulProgresso = Color(0xFF5AA9E6)
private val CorTrilhaProgresso = Color(0xFFE7E1D2)

@Composable
fun TarefasDoDiaScreen() {
    val taskList by TaskRepository.tasks.collectAsState()
    val context = LocalContext.current

    // Ao abrir a tela, busca as tarefas no banco (Room)
    LaunchedEffect(Unit) { TaskRepository.carregar() }

    val concluidas = taskList.count { it.isCompleted }
    val total = taskList.size

    Scaffold(
        containerColor = CorFundo,
        bottomBar = { TarefasBottomNavBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CorFundo)
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    TarefasHeader(moedas = 150)
                    Spacer(modifier = Modifier.height(20.dp))
                    ProgressoDoDia(concluidas = concluidas, total = total)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(taskList, key = { it.id }) { tarefa ->
                TarefaCard(
                    tarefa = tarefa,
                    onToggle = { TaskRepository.toggleTaskCompletion(tarefa.id) },
                    onAtualizar = {
                        context.startActivity(
                            Intent(context, TarefaDetalhesActivity::class.java)
                                .putExtra(EXTRA_TASK_ID, tarefa.id)
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun TarefasHeader(moedas: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tarefas do Dia",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CorTextoPrincipal
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFFFF3D6))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(CorMoeda)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$moedas ",
                fontWeight = FontWeight.Bold,
                color = CorTextoPrincipal
            )
        }
    }
}

@Composable
private fun ProgressoDoDia(concluidas: Int, total: Int) {
    val fracao = if (total > 0) concluidas.toFloat() / total.toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CorCard),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progresso do dia",
                    fontSize = 13.sp,
                    color = CorTextoSecundario,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$concluidas / $total concluídos",
                    fontSize = 13.sp,
                    color = CorTextoSecundario,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(CorTrilhaProgresso)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = fracao)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(CorAzulProgresso)
                )
            }
        }
    }
}

@Composable
private fun TarefaCard(
    tarefa: TaskItemData,
    onToggle: () -> Unit,
    onAtualizar: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = CorCard),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CheckboxCircular(marcado = tarefa.isCompleted)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = tarefa.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (tarefa.isCompleted) CorTextoSecundario else CorTextoPrincipal
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFFF3D6))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CorMoeda)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${tarefa.reward}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorTextoPrincipal
                    )
                }
            }
        }
        BotaoAtualizarTarefa(
            onClick = onAtualizar,
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

@Composable
private fun TarefasBottomNavBar() {

    val context = LocalContext.current

    NavigationBar(containerColor = CorCard) {
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
            label = { Text(text = "Início", fontSize = 11.sp) },
            colors = itemColors()
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, Calendario::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Calendário",
                    tint = GrayText
                )
            },
            label = { Text(text = "Calendário", fontSize = 11.sp) },
            colors = itemColors()
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = "Tarefas",
                    tint = PrimaryGreen
                )
            },
            label = { Text(text = "Tarefas", fontSize = 11.sp) },
            colors = itemColors()
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
            label = { Text(text = "Loja", fontSize = 11.sp) },
            colors = itemColors()
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
            label = { Text(text = "Perfil", fontSize = 11.sp) },
            colors = itemColors()
        )
    }
}

@Composable
private fun itemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = CorTextoPrincipal,
    selectedTextColor = CorTextoPrincipal,
    unselectedIconColor = CorTextoSecundario,
    unselectedTextColor = CorTextoSecundario,
    indicatorColor = Color(0xFFFFF3D6)
)

@Preview(showBackground = true)
@Composable
private fun TarefasDoDiaScreenPreview() {
    MaterialTheme {
        TarefasDoDiaScreen()
    }
}