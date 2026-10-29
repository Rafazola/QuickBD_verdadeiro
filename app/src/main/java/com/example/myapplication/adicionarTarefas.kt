package com.example.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Cores (ajuste conforme seu tema do Figma) ----------
private val AddTaskCream = Color(0xFFFFFCF0)
private val AddTaskTeal = Color(0xFF1FBB9A)
private val AddTaskCoinYellow = Color(0xFFFFC83D)
private val AddTaskCoinSoft = Color(0xFFFFF6D6)
private val AddTaskCoinOrange = Color(0xFFF5A623)
private val AddTaskTextDark = Color(0xFF2B2B2B)
private val AddTaskTextGray = Color(0xFF8A8A8A)
private val AddTaskBorderGray = Color(0xFFE6E2D6)

private val addTaskWeekDays = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

private data class AddTaskNavItem(val label: String, val icon: ImageVector)

private val addTaskNavItems = listOf(
    AddTaskNavItem("Início", Icons.Filled.Home),
    AddTaskNavItem("Calendário", Icons.Filled.DateRange),
    AddTaskNavItem("Tarefas", Icons.AutoMirrored.Filled.List),
    AddTaskNavItem("Loja", Icons.Filled.ShoppingCart),
    AddTaskNavItem("Perfil", Icons.Filled.Person),
)

// ---------- Tela ----------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTaskScreen(
    coins: Int = 150,
    onBack: () -> Unit = {},
    onSave: (description: String, days: Set<String>, reward: Int) -> Unit = { _, _, _ -> },
    onNavigate: (index: Int) -> Unit = {},
) {
    var description by rememberSaveable { mutableStateOf("") }
    var selectedDays by rememberSaveable { mutableStateOf(setOf("Seg", "Qua", "Sex")) }
    var reward by rememberSaveable { mutableIntStateOf(20) }

    Scaffold(
        containerColor = AddTaskCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Adicionar Tarefa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AddTaskTextDark,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = AddTaskTextDark)
                    }
                },
                actions = {
                    AddTaskCoinChip(coins = coins)
                    Spacer(Modifier.size(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AddTaskCream),
            )
        },
        bottomBar = {
            AddTaskBottomBar(selectedIndex = 2, onSelect = onNavigate)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // --- Descrição ---
            AddTaskSectionLabel("DESCREVA A TAREFA")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Praticar violão por 20 minutos", color = AddTaskTextGray, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AddTaskTeal,
                    unfocusedBorderColor = AddTaskBorderGray,
                    cursorColor = AddTaskTeal,
                ),
            )

            Spacer(Modifier.height(24.dp))

            // --- Dias ---
            AddTaskSectionLabel("SELECIONE OS DIAS")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                addTaskWeekDays.forEach { day ->
                    AddTaskDayChip(
                        label = day,
                        selected = day in selectedDays,
                        onClick = {
                            selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                        },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Recompensa ---
            AddTaskSectionLabel("RECOMPENSA")
            AddTaskRewardCard(
                reward = reward,
                onDecrease = { if (reward > 5) reward -= 5 },
                onIncrease = { reward += 5 },
            )

            Spacer(Modifier.height(32.dp))

            // --- Salvar ---
            Button(
                onClick = { onSave(description.trim(), selectedDays, reward) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AddTaskTeal, contentColor = Color.White),
            ) {
                Text("Salvar Tarefa", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ---------- Componentes ----------
@Composable
private fun AddTaskSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = AddTaskTextGray,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun AddTaskCoinChip(coins: Int) {
    Surface(
        shape = RoundedCornerShape(50),
        color = AddTaskCoinSoft,
        border = BorderStroke(1.dp, AddTaskCoinYellow),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            AddTaskCoinDot(size = 14.dp)
            Text(coins.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AddTaskTextDark)
        }
    }
}

@Composable
private fun AddTaskCoinDot(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(AddTaskCoinYellow)
            .border(1.dp, AddTaskCoinOrange, CircleShape),
    )
}

@Composable
private fun AddTaskDayChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) AddTaskTeal else Color.White,
        border = if (selected) null else BorderStroke(1.dp, AddTaskBorderGray),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else AddTaskTextDark,
        )
    }
}

@Composable
private fun AddTaskRewardCard(reward: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AddTaskCoinSoft,
        border = BorderStroke(1.5.dp, AddTaskCoinYellow),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AddTaskCoinOrange),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("+$reward Moedas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AddTaskTextDark)
                Text("Para gastar na loja", fontSize = 12.sp, color = AddTaskTextGray)
            }

            AddTaskStepButton("−", "Diminuir recompensa", onDecrease)
            Spacer(Modifier.size(8.dp))
            AddTaskStepButton("+", "Aumentar recompensa", onIncrease)
        }
    }
}

@Composable
private fun AddTaskStepButton(symbol: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, AddTaskCoinYellow, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AddTaskCoinOrange)
    }
}

@Composable
private fun AddTaskBottomBar(selectedIndex: Int, onSelect: (Int) -> Unit) {
    NavigationBar(containerColor = AddTaskCream, tonalElevation = 0.dp) {
        addTaskNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AddTaskTeal,
                    selectedTextColor = AddTaskTeal,
                    unselectedIconColor = AddTaskTextGray,
                    unselectedTextColor = AddTaskTextGray,
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddTaskScreenPreview() {
    AddTaskScreen()
}