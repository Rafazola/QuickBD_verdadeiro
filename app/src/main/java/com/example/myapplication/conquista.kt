package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ConquistasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TelaConquistasQuick(onVoltarClick = { finish() })
            }
        }
    }
}

// --- PALETA DE CORES EXCLUSIVA DO QUICK ---
val QuickBgColor = Color(0xFFFFFDF5)
val QuickCardBg = Color(0xFFFFFFFF)
val QuickPrimaryGreen = Color(0xFF24C59A)
val QuickGold = Color(0xFFFFB800)
val QuickTextDark = Color(0xFF2D3748)
val QuickTextMuted = Color(0xFFA0AEC0)
val QuickLockedBg = Color(0xFFEDF2F7)

// --- MODELOS DE DADOS DO QUICK ---
data class DestaqueQuick(
    val id: String,
    val valor: String,
    val titulo: String,
    val subtitulo: String,
    val corTema: Color
)

data class InsigniaQuick(
    val id: String,
    val titulo: String,
    val progressoAtual: Int,
    val metaTotal: Int,
    val estaDesbloqueada: Boolean,
    val recemConquistada: Boolean = false,
    val emojiInsignia: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaConquistasQuick(
    onVoltarClick: () -> Unit = {}
) {
    val destaques = remember {
        listOf(
            DestaqueQuick("1", "15", "Dias de Ofensiva", "Pet Super Feliz!", QuickGold),
            DestaqueQuick("2", "Nível 5", "Evolução do Pet", "Companheiro Leal", QuickPrimaryGreen),
            DestaqueQuick("3", "1.250", "Moedas Ganhas", "Total acumulado", Color(0xFFFF7A00))
        )
    }

    val insignias = remember {
        listOf(
            InsigniaQuick("1", "Habito de Ferro", 7, 7, true, recemConquistada = true, "🔥"),
            InsigniaQuick("2", "Mestre de Tarefas", 8, 10, true, recemConquistada = true, "⭐"),
            InsigniaQuick("3", "Guardião da Rotina", 10, 10, true, emojiInsignia = "🛡️"),
            InsigniaQuick("4", "Amigo dos Animais", 6, 10, true, emojiInsignia = "🐾"),
            InsigniaQuick("5", "Foco Total", 4, 5, true, emojiInsignia = "🎯"),
            InsigniaQuick("6", "Cofre Cheio", 6, 10, true, emojiInsignia = "💰"),
            InsigniaQuick("7", "Madrugador", 2, 5, false, emojiInsignia = "🌅"),
            InsigniaQuick("8", "Pet Lendário", 0, 1, false, emojiInsignia = "👑"),
            InsigniaQuick("9", "Desafio Mensal", 0, 1, false, emojiInsignia = "🏆")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Conquistas do Quick",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuickTextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = QuickTextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QuickBgColor)
            )
        },
        containerColor = QuickBgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // --- DESTAQUES / RECORDES DO PET ---
            Text(
                text = "Marcos da Sua Jornada",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = QuickTextDark,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(destaques) { destaque ->
                    CardDestaqueQuick(destaque = destaque)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- INSÍGNIAS E MEDALHAS ---
            Text(
                text = "Insígnias Conquistadas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = QuickTextDark,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // Grid customizada de insígnias em 3 colunas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                val linhasInsignias = insignias.chunked(3)
                for (linha in linhasInsignias) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (insignia in linha) {
                            ItemInsigniaQuick(
                                insignia = insignia,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (linha.size < 3) {
                            repeat(3 - linha.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- CARD DE DESTAQUE ---
@Composable
fun CardDestaqueQuick(destaque: DestaqueQuick) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = QuickCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(150.dp)
            .border(1.5.dp, QuickLockedBg, RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(destaque.corTema.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = destaque.valor,
                    color = destaque.corTema,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = destaque.titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = QuickTextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = destaque.subtitulo,
                fontSize = 11.sp,
                color = QuickTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- ITEM DA INSÍGNIA ---
@Composable
fun ItemInsigniaQuick(
    insignia: InsigniaQuick,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            // Moldura da Insígnia
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (insignia.estaDesbloqueada) QuickCardBg else QuickLockedBg
                    )
                    .border(
                        width = 2.dp,
                        color = if (insignia.estaDesbloqueada) QuickPrimaryGreen else Color(0xFFCBD5E0),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (insignia.estaDesbloqueada) {
                    Text(
                        text = insignia.emojiInsignia,
                        fontSize = 36.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueada",
                        tint = QuickTextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Tag de "NOVO" exclusiva
            if (insignia.recemConquistada && insignia.estaDesbloqueada) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(QuickGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "NOVO",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = insignia.titulo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (insignia.estaDesbloqueada) QuickTextDark else QuickTextMuted,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "${insignia.progressoAtual}/${insignia.metaTotal}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = QuickPrimaryGreen
        )
    }
}
