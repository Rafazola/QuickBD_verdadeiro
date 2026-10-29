package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TIPO_PET = "pet"
const val TIPO_FUNDO = "fundo"

// Entidade 1: a antiga data class TaskItemData (era só memória) agora é uma tabela.
@Entity(tableName = "tarefas")
data class TaskItemData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val reward: Int,
    val isCompleted: Boolean = false,
    val days: String = ""          // ex.: "Seg,Qua,Sex"
)

// Entidade 2: itens da Loja (substitui StorePetUIModel / StoreBackgroundUIModel como fonte de dados).
// As cores são guardadas como Long ARGB (ex.: 0xFFFFD8A0).
@Entity(tableName = "itens_loja")
data class ItemLoja(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val preco: Int,
    val emoji: String,
    val tipo: String,              // TIPO_PET ou TIPO_FUNDO
    val corInicio: Long,
    val corFim: Long = corInicio,
    val equipado: Boolean = false
)
