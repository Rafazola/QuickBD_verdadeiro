package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private const val COR_PET = 0xFFFFD8A0
private const val COR_FUNDO_INICIO = 0xFF4FC3F7
private const val COR_FUNDO_FIM = 0xFFFFE082

// Botões de editar e excluir exibidos dentro de cada card da Loja.
@Composable
fun AcoesItem(onEditar: () -> Unit, onExcluir: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onEditar, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onExcluir, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFD9534F), modifier = Modifier.size(18.dp))
        }
    }
}

// Formulário de adicionar (inicial == null) ou editar (inicial != null) um item da Loja.
@Composable
fun ItemLojaDialog(
    inicial: ItemLoja?,
    onDismiss: () -> Unit,
    onSalvar: (ItemLoja) -> Unit
) {
    var nome by remember { mutableStateOf(inicial?.nome ?: "") }
    var preco by remember { mutableStateOf(inicial?.preco?.toString() ?: "") }
    var emoji by remember { mutableStateOf(inicial?.emoji ?: "") }
    var tipo by remember { mutableStateOf(inicial?.tipo ?: TIPO_PET) }

    val precoValido = preco.toIntOrNull() != null
    val podeSalvar = nome.isNotBlank() && precoValido

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (inicial == null) "Novo item" else "Editar item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nome, onValueChange = { nome = it },
                    label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = preco, onValueChange = { preco = it.filter(Char::isDigit) },
                    label = { Text("Preço (moedas)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emoji, onValueChange = { emoji = it },
                    label = { Text("Emoji") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(TIPO_PET to "Pet", TIPO_FUNDO to "Fundo").forEach { (valor, rotulo) ->
                        if (tipo == valor) {
                            Button(onClick = { tipo = valor }, modifier = Modifier.weight(1f)) { Text(rotulo) }
                        } else {
                            OutlinedButton(onClick = { tipo = valor }, modifier = Modifier.weight(1f)) { Text(rotulo) }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = podeSalvar,
                onClick = {
                    val manterCores = inicial != null && inicial.tipo == tipo
                    val corInicio = if (manterCores) inicial!!.corInicio else if (tipo == TIPO_PET) COR_PET else COR_FUNDO_INICIO
                    val corFim = if (manterCores) inicial!!.corFim else if (tipo == TIPO_PET) COR_PET else COR_FUNDO_FIM
                    val base = inicial ?: ItemLoja(nome = "", preco = 0, emoji = "", tipo = tipo, corInicio = corInicio)
                    onSalvar(
                        base.copy(
                            nome = nome.trim(),
                            preco = preco.toInt(),
                            emoji = emoji.trim().ifBlank { "⭐" },
                            tipo = tipo,
                            corInicio = corInicio,
                            corFim = corFim,
                            equipado = if (tipo == TIPO_PET) base.equipado else false
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun ConfirmarExclusaoDialog(item: ItemLoja, onDismiss: () -> Unit, onConfirmar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir item?") },
        text = { Text("\"${item.nome}\" será removido do banco.") },
        confirmButton = { TextButton(onClick = onConfirmar) { Text("Excluir", color = Color(0xFFD9534F)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
