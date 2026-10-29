package com.example.myapplication

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Continua sendo a fonte única para PetHome e TarefasDoDia (StateFlow),
// mas agora cada alteração é gravada no Room e a lista é relida do banco.
object TaskRepository {

    private var dao: TaskDao? = null

    private val _tasks = MutableStateFlow<List<TaskItemData>>(emptyList())
    val tasks: StateFlow<List<TaskItemData>> = _tasks.asStateFlow()

    // Chamar no onCreate de cada Activity (é idempotente).
    @Synchronized
    fun init(context: Context) {
        if (dao == null) dao = AppDatabase.getDatabase(context).taskDao()
    }

    // BUSCAR: lê do banco e publica no StateFlow. Chamada via LaunchedEffect(Unit) nas telas de lista.
    suspend fun carregar() = withContext(Dispatchers.IO) {
        val d = dao ?: return@withContext
        _tasks.value = buscarTarefas(d)
    }

    // BUSCAR POR ID: usada pela tela de Detalhes.
    suspend fun buscarPorId(id: Int): TaskItemData? = withContext(Dispatchers.IO) {
        dao?.let { buscarTarefaPorId(it, id) }
    }

    // INSERIR
    fun addTask(title: String, reward: Int, days: String = "") {
        val d = dao ?: return
        CoroutineScope(Dispatchers.IO).launch {
            inserirTarefa(d, TaskItemData(title = title, reward = reward, days = days))
            carregar()
        }
    }

    // ATUALIZAR (marcar/desmarcar como concluída)
    fun toggleTaskCompletion(taskId: Int) {
        val atual = _tasks.value.firstOrNull { it.id == taskId } ?: return
        updateTask(atual.copy(isCompleted = !atual.isCompleted))
    }

    // ATUALIZAR (edição completa)
    fun updateTask(task: TaskItemData) {
        val d = dao ?: return
        CoroutineScope(Dispatchers.IO).launch {
            atualizarTarefa(d, task)
            carregar()
        }
    }

    // REMOVER
    fun deleteTask(task: TaskItemData) {
        val d = dao ?: return
        CoroutineScope(Dispatchers.IO).launch {
            deletarTarefa(d, task)
            carregar()
        }
    }
}
