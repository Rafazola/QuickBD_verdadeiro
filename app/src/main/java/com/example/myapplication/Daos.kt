package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    suspend fun inserir(tarefa: TaskItemData)

    @Query("SELECT * FROM tarefas ORDER BY id")
    suspend fun buscarTodos(): List<TaskItemData>

    // Consulta filtrada (seção 3.1) usada pela tela de Detalhes (seção 3.3)
    @Query("SELECT * FROM tarefas WHERE id = :id")
    suspend fun buscarPorId(id: Int): TaskItemData?

    @Update
    suspend fun atualizar(tarefa: TaskItemData)

    @Delete
    suspend fun deletar(tarefa: TaskItemData)
}

@Dao
interface ItemLojaDao {
    @Insert
    suspend fun inserir(item: ItemLoja)

    @Insert
    suspend fun inserirTodos(itens: List<ItemLoja>)

    @Query("SELECT * FROM itens_loja ORDER BY id")
    suspend fun buscarTodos(): List<ItemLoja>

    @Query("SELECT * FROM itens_loja WHERE id = :id")
    suspend fun buscarPorId(id: Int): ItemLoja?

    @Query("SELECT COUNT(*) FROM itens_loja")
    suspend fun contar(): Int

    @Update
    suspend fun atualizar(item: ItemLoja)

    @Delete
    suspend fun deletar(item: ItemLoja)
}
