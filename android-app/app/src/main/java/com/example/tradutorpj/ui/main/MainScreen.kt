package com.example.tradutorpj.ui.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

data class IdiomaOpcao(val nome: String, val codigo: String)

val IDIOMAS_DISPONIVEIS = listOf(
    IdiomaOpcao("Português", TranslateLanguage.PORTUGUESE),
    IdiomaOpcao("Inglês", TranslateLanguage.ENGLISH),
    IdiomaOpcao("Espanhol", TranslateLanguage.SPANISH),
    IdiomaOpcao("Francês", TranslateLanguage.FRENCH),
    IdiomaOpcao("Alemão", TranslateLanguage.GERMAN),
    IdiomaOpcao("Italiano", TranslateLanguage.ITALIAN)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (Any) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var textoOrigem by remember { mutableStateOf("") }
    var textoTraduzido by remember { mutableStateOf("") }
    var idiomaOrigem by remember { mutableStateOf(IDIOMAS_DISPONIVEIS[0]) } // Português
    var idiomaDestino by remember { mutableStateOf(IDIOMAS_DISPONIVEIS[1]) } // Inglês
    var statusTraducao by remember { mutableStateOf("Pronto para traduzir") }
    var carregando by remember { mutableStateOf(false) }

    var expandedOrigem by remember { mutableStateOf(false) }
    var expandedDestino by remember { mutableStateOf(false) }

    var translator: Translator? by remember { mutableStateOf(null) }

    fun traduzirTexto() {
        if (textoOrigem.isBlank()) {
            Toast.makeText(context, "Digite um texto para traduzir", Toast.LENGTH_SHORT).show()
            return
        }

        carregando = true
        statusTraducao = "Preparando modelo offline..."

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(idiomaOrigem.codigo)
            .setTargetLanguage(idiomaDestino.codigo)
            .build()

        val client = Translation.getClient(options)
        translator?.close()
        translator = client

        val conditions = DownloadConditions.Builder().build()
        client.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                statusTraducao = "Traduzindo localmente (offline)..."
                client.translate(textoOrigem)
                    .addOnSuccessListener { resultado ->
                        textoTraduzido = resultado
                        statusTraducao = "Tradução concluída com sucesso! ✓"
                        carregando = false
                    }
                    .addOnFailureListener { error ->
                        statusTraducao = "Erro ao traduzir: ${error.localizedMessage}"
                        carregando = false
                    }
            }
            .addOnFailureListener { error ->
                statusTraducao = "Erro ao carregar modelo: ${error.localizedMessage}"
                carregando = false
            }
    }

    fun inverterIdiomas() {
        val temp = idiomaOrigem
        idiomaOrigem = idiomaDestino
        idiomaDestino = temp
        val tempTexto = textoOrigem
        textoOrigem = textoTraduzido
        textoTraduzido = tempTexto
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabeçalho Premium
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌐 Tradutor PJ Mobile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "100% Offline • Neural On-Device",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Seletor de Idiomas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Idioma Origem
            ExposedDropdownMenuBox(
                expanded = expandedOrigem,
                onExpandedChange = { expandedOrigem = !expandedOrigem },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = idiomaOrigem.nome,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("De") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrigem) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedOrigem,
                    onDismissRequest = { expandedOrigem = false }
                ) {
                    IDIOMAS_DISPONIVEIS.forEach { idioma ->
                        DropdownMenuItem(
                            text = { Text(idioma.nome) },
                            onClick = {
                                idiomaOrigem = idioma
                                expandedOrigem = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { inverterIdiomas() },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(text = "⇄", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            // Idioma Destino
            ExposedDropdownMenuBox(
                expanded = expandedDestino,
                onExpandedChange = { expandedDestino = !expandedDestino },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = idiomaDestino.nome,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Para") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestino) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedDestino,
                    onDismissRequest = { expandedDestino = false }
                ) {
                    IDIOMAS_DISPONIVEIS.forEach { idioma ->
                        DropdownMenuItem(
                            text = { Text(idioma.nome) },
                            onClick = {
                                idiomaDestino = idioma
                                expandedDestino = false
                            }
                        )
                    }
                }
            }
        }

        // Entrada de Texto
        OutlinedTextField(
            value = textoOrigem,
            onValueChange = { textoOrigem = it },
            label = { Text("Texto para traduzir") },
            placeholder = { Text("Digite ou cole o texto aqui...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(12.dp)
        )

        // Botão Traduzir
        Button(
            onClick = { traduzirTexto() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !carregando
        ) {
            if (carregando) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processando...")
            } else {
                Text("Traduzir Agora", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Status
        Text(
            text = statusTraducao,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline
        )

        // Resultado da Tradução
        OutlinedTextField(
            value = textoTraduzido,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tradução") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp)
        )

        // Botões de Ação Auxiliar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (textoTraduzido.isNotBlank()) {
                        clipboardManager.setText(AnnotatedString(textoTraduzido))
                        Toast.makeText(context, "Copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📋 Copiar")
            }

            OutlinedButton(
                onClick = {
                    textoOrigem = ""
                    textoTraduzido = ""
                    statusTraducao = "Campos limpos"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🗑️ Limpar")
            }
        }
    }
}
