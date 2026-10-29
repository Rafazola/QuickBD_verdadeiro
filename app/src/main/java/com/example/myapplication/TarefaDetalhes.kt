package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CorExcluir = Color(0xFFD9534F)

const val EXTRA_TASK_ID = "task_id"

// Abre a tela de Detalhes (atualizar / excluir) a partir de qualquer lista de tarefas.
class TarefaDetalhesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TaskRepository.init(applicationContext)
        val id = intent.getIntExtra(EXTRA_TASK_ID, -1)
        setContent {
            MaterialTheme {
                TarefaDetalhesScreen(taskId = id, onBack = { finish() })
            }
        }
    }
}

// Botão visível "Atualizar tarefa", usado dentro dos cards das listas.
@Composable
fun BotaoAtualizarTarefa(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, TealPrimary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
    ) {
        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text("Atualizar tarefa", fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// Recebe só o ID (vindo da navegação) e busca a tarefa no banco (TaskDao.buscarPorId).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefaDetalhesScreen(taskId: Int, onBack: () -> Unit) {
    var tarefa by remember { mutableStateOf<TaskItemData?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var titulo by remember { mutableStateOf("") }
    var recompensa by remember { mutableIntStateOf(0) }
    var confirmarExclusao by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        val encontrada = TaskRepository.buscarPorId(taskId)
        tarefa = encontrada
        if (encontrada != null) {
            titulo = encontrada.title
            recompensa = encontrada.reward
        }
        carregando = false
    }

    Scaffold(
        containerColor = CreamBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalhes da Tarefa", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamBg)
            )
        }
    ) { padding ->
        val atual = tarefa
        when {
            carregando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealPrimary)
            }

            atual == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Tarefa não encontrada", color = TextGray)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (atual.isCompleted) "✓ Concluída" else "Pendente",
                            color = if (atual.isCompleted) TealPrimary else OrangeBadge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (atual.days.isBlank()) "Dias: não definidos"
                            else "Dias: ${atual.days.replace(",", ", ")}",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Descrição") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recompensa", color = TextDark, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { if (recompensa > 5) recompensa -= 5 }) { Text("−") }
                        Text("$recompensa", fontWeight = FontWeight.Bold, color = OrangeBadge, fontSize = 18.sp)
                        OutlinedButton(onClick = { recompensa += 5 }) { Text("+") }
                    }
                }

                Button(
                    onClick = {
                        TaskRepository.updateTask(atual.copy(title = titulo.trim(), reward = recompensa))
                        onBack()
                    },
                    enabled = titulo.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = Color.White)
                ) { Text("Salvar alterações", fontWeight = FontWeight.Bold) }

                OutlinedButton(
                    onClick = {
                        TaskRepository.updateTask(atual.copy(isCompleted = !atual.isCompleted))
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) { Text(if (atual.isCompleted) "Reabrir tarefa" else "Marcar como concluída") }

                OutlinedButton(
                    onClick = { confirmarExclusao = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CorExcluir)
                ) { Text("Excluir tarefa", fontWeight = FontWeight.Bold) }

                if (confirmarExclusao) {
                    AlertDialog(
                        onDismissRequest = { confirmarExclusao = false },
                        title = { Text("Excluir tarefa?") },
                        text = { Text("\"${atual.title}\" será removida do banco.") },
                        confirmButton = {
                            TextButton(onClick = {
                                confirmarExclusao = false
                                TaskRepository.deleteTask(atual)
                                onBack()
                            }) { Text("Excluir", color = CorExcluir) }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
                        }
                    )
                }
            }
        }
    }
}
