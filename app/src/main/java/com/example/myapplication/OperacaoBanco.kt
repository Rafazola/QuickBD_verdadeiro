package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.CancellationException

private const val TAG = "QuickDB"

// try/catch + Log.e em toda operação de banco (seção 3.4).
private suspend fun <T> comTratamento(acao: String, padrao: T, bloco: suspend () -> T): T =
    try {
        bloco()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(TAG, "erro ao $acao: ${e.message}", e)
        padrao
    }

// ---------- Tarefas ----------
suspend fun inserirTarefa(dao: TaskDao, tarefa: TaskItemData) =
    comTratamento("inserir tarefa", Unit) { dao.inserir(tarefa) }

suspend fun buscarTarefas(dao: TaskDao): List<TaskItemData> =
    comTratamento("buscar tarefas", emptyList()) { dao.buscarTodos() }

suspend fun buscarTarefaPorId(dao: TaskDao, id: Int): TaskItemData? =
    comTratamento("buscar tarefa $id", null) { dao.buscarPorId(id) }

suspend fun atualizarTarefa(dao: TaskDao, tarefa: TaskItemData) =
    comTratamento("atualizar tarefa", Unit) { dao.atualizar(tarefa) }

suspend fun deletarTarefa(dao: TaskDao, tarefa: TaskItemData) =
    comTratamento("deletar tarefa", Unit) { dao.deletar(tarefa) }

// ---------- Loja ----------
suspend fun buscarItensLoja(dao: ItemLojaDao): List<ItemLoja> =
    comTratamento("buscar itens da loja", emptyList()) { dao.buscarTodos() }

suspend fun atualizarItemLoja(dao: ItemLojaDao, item: ItemLoja) =
    comTratamento("atualizar item da loja", Unit) { dao.atualizar(item) }

suspend fun deletarItemLoja(dao: ItemLojaDao, item: ItemLoja) =
    comTratamento("deletar item da loja", Unit) { dao.deletar(item) }

// Primeira abertura: popula a loja com o catálogo inicial.
suspend fun garantirItensIniciaisLoja(dao: ItemLojaDao) =
    comTratamento("popular loja", Unit) {
        if (dao.contar() == 0) dao.inserirTodos(itensIniciaisLoja)
    }

suspend fun inserirItemLoja(dao: ItemLojaDao, item: ItemLoja) =
    comTratamento("inserir item da loja", Unit) { dao.inserir(item) }
