package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Loja : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LojaRoute()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StoreScreenPreview() {
    StoreScreen()
}

data class StorePetUIModel(
    val name: String,
    val price: Int,
    val emoji: String,
    val circleColor: Color,
    val isEquipped: Boolean = false
)

data class StoreBackgroundUIModel(
    val name: String,
    val price: Int,
    val emoji: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

// Catálogo inicial: gravado no Room na primeira abertura (e usado no Preview).
val itensIniciaisLoja = listOf(
    ItemLoja(nome = "Cãozinho Mel", preco = 0, emoji = "🐶", tipo = TIPO_PET, corInicio = 0xFFFFD8A0, equipado = true),
    ItemLoja(nome = "Gatinho Mingau", preco = 350, emoji = "🐱", tipo = TIPO_PET, corInicio = 0xFFF8D7E0),
    ItemLoja(nome = "Ursinho Teddy", preco = 500, emoji = "🐻", tipo = TIPO_PET, corInicio = 0xFFF5C6A5),
    ItemLoja(nome = "Dragão Faísca", preco = 750, emoji = "🐲", tipo = TIPO_PET, corInicio = 0xFFDDF2D1),
    ItemLoja(nome = "Praia Ensolarada", preco = 150, emoji = "🏝️", tipo = TIPO_FUNDO, corInicio = 0xFF4FC3F7, corFim = 0xFFFFE082),
    ItemLoja(nome = "Quarto Gamer", preco = 250, emoji = "🎮", tipo = TIPO_FUNDO, corInicio = 0xFF6D4C41, corFim = 0xFFBF8F6B),
    ItemLoja(nome = "Espaço Sideral", preco = 400, emoji = "🪐", tipo = TIPO_FUNDO, corInicio = 0xFF1A1446, corFim = 0xFF8E2DE2),
    ItemLoja(nome = "Montanhas Geladas", preco = 550, emoji = "🏔️", tipo = TIPO_FUNDO, corInicio = 0xFFB3E5FC, corFim = 0xFFE1F5FE)
)

private fun ItemLoja.toPetUI() = StorePetUIModel(nome, preco, emoji, Color(corInicio), equipado)
private fun ItemLoja.toFundoUI() = StoreBackgroundUIModel(nome, preco, emoji, Color(corInicio), Color(corFim))

// Versão com banco: busca os itens no Room ao abrir a tela e grava ao equipar um pet.
@Composable
fun LojaRoute() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).itemLojaDao() }
    var itens by remember { mutableStateOf<List<ItemLoja>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            garantirItensIniciaisLoja(dao)
            itens = buscarItensLoja(dao)
        }
    }

    var itemEmEdicao by remember { mutableStateOf<ItemLoja?>(null) }   // item sendo editado
    var mostrarFormulario by remember { mutableStateOf(false) }        // abre o formulário (novo ou edição)
    var itemParaExcluir by remember { mutableStateOf<ItemLoja?>(null) }

    if (mostrarFormulario) {
        ItemLojaDialog(
            inicial = itemEmEdicao,
            onDismiss = { mostrarFormulario = false },
            onSalvar = { item ->
                mostrarFormulario = false
                CoroutineScope(Dispatchers.IO).launch {
                    if (item.id == 0) inserirItemLoja(dao, item) else atualizarItemLoja(dao, item)
                    itens = buscarItensLoja(dao)
                }
            }
        )
    }

    itemParaExcluir?.let { alvo ->
        ConfirmarExclusaoDialog(
            item = alvo,
            onDismiss = { itemParaExcluir = null },
            onConfirmar = {
                itemParaExcluir = null
                CoroutineScope(Dispatchers.IO).launch {
                    deletarItemLoja(dao, alvo)
                    itens = buscarItensLoja(dao)
                }
            }
        )
    }

    StoreScreen(
        itens = itens,
        onAdicionar = { itemEmEdicao = null; mostrarFormulario = true },
        onEditar = { itemEmEdicao = it; mostrarFormulario = true },
        onExcluir = { itemParaExcluir = it },
        onEquiparPet = { pet ->
            CoroutineScope(Dispatchers.IO).launch {
                itens.filter { it.tipo == TIPO_PET && it.equipado && it.id != pet.id }
                    .forEach { atualizarItemLoja(dao, it.copy(equipado = false)) }
                atualizarItemLoja(dao, pet.copy(equipado = true))
                itens = buscarItensLoja(dao)
            }
        }
    )
}

@Composable
fun StoreScreen(
    itens: List<ItemLoja> = itensIniciaisLoja,
    onEquiparPet: (ItemLoja) -> Unit = {},
    onAdicionar: () -> Unit = {},
    onEditar: (ItemLoja) -> Unit = {},
    onExcluir: (ItemLoja) -> Unit = {}
) {
    val pets = itens.filter { it.tipo == TIPO_PET }
    val fundos = itens.filter { it.tipo == TIPO_FUNDO }

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = { CustomBottomNavigation(currentRoute = "store") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdicionar, containerColor = PrimaryGreen, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar item")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StoreTopHeader(coins = 150) }

            item { StoreSectionTitle("PETS COMPANHEIROS") }
            pets.chunked(2).forEach { rowItems ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { pet ->
                            PetStoreCard(
                                pet = pet.toPetUI(),
                                modifier = Modifier.weight(1f),
                                onClick = { onEquiparPet(pet) },
                                onEditar = { onEditar(pet) },
                                onExcluir = { onExcluir(pet) }
                            )
                        }
                    }
                }
            }

            item { StoreSectionTitle("PAISAGENS DE FUNDO") }
            fundos.chunked(2).forEach { rowItems ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { bg ->
                            BackgroundStoreCard(
                                item = bg.toFundoUI(),
                                modifier = Modifier.weight(1f),
                                onEditar = { onEditar(bg) },
                                onExcluir = { onExcluir(bg) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun StoreTopHeader(coins: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Loja",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        CoinBadge(coins = coins)
    }
}

@Composable
fun StoreSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        color = DarkText,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun PriceChip(price: Int) {
    Surface(
        color = YellowBadge,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(GoldText, CircleShape)
            )
            Text(
                text = "$price",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }
    }
}

@Composable
fun PetStoreCard(
    pet: StorePetUIModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onEditar: () -> Unit = {},
    onExcluir: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(pet.circleColor)
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = pet.emoji, fontSize = 40.sp)
            }

            Text(
                text = pet.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (pet.isEquipped) {
                Surface(
                    color = SoftGreen,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Equipado",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            } else {
                PriceChip(price = pet.price)
            }

            AcoesItem(onEditar = onEditar, onExcluir = onExcluir)
        }
    }
}

@Composable
fun BackgroundStoreCard(
    item: StoreBackgroundUIModel,
    modifier: Modifier = Modifier,
    onEditar: () -> Unit = {},
    onExcluir: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(item.gradientStart, item.gradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 28.sp)
            }

            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            PriceChip(price = item.price)

            AcoesItem(onEditar = onEditar, onExcluir = onExcluir)
        }
    }
}