# Contexto e Arquitetura: Tradutor PJ Mobile (APK Offline)

Este documento registra as decisões de engenharia, arquitetura, dependências e padrões adotados para compilação do APK Android do **Tradutor PJ Mobile**.

---

## 📌 1. Visão Geral da Solução

- **Objetivo**: Aplicativo Android moderno capaz de realizar tradução de textos com redes neurais sem depender de conexão de internet ou servidores remotos (100% Offline / Modo Avião).
- **Abordagem Adotada**: **Android Nativo Moderno (Kotlin + Jetpack Compose + Material 3)** integrado ao **Google ML Kit Translate On-Device**.
- **Por que esta abordagem?**:
  - Evita falhas de compilação cruzada no Buildozer (`python-for-android`), que não possui suporte nem receitas oficiais para empacotar o motor C++ do `CTranslate2` / OpenNMT em processadores ARM Android.
  - Oferece suporte oficial com aceleração de hardware nativa (GPU/NPU) no celular.
  - Compilação rápida e 100% confiável via Gradle e GitHub Actions (tempo de build: ~40 segundos).

---

## 🏗️ 2. Estrutura do Projeto

```text
c:\AgPJ\pyAg\python\
├── android-app/                              # Projeto Android Nativo
│   ├── app/
│   │   ├── build.gradle.kts                 # Dependências (Compose + ML Kit)
│   │   └── src/main/java/com/example/tradutorpj/
│   │       ├── MainActivity.kt              # Entrada da Activity
│   │       ├── Navigation.kt                # Controlador de rotas
│   │       ├── NavigationKeys.kt            # Definição das chaves NavKey
│   │       └── ui/main/
│   │           └── MainScreen.kt            # Interface de tradução offline com ML Kit
│   ├── gradle/libs.versions.toml            # Catálogo de versões centralizado
│   ├── gradlew / gradlew.bat                # Gradle Wrapper
│   └── build.gradle.kts                     # Configuração raiz do Gradle
│
├── .github/workflows/
│   └── build_apk.yml                        # Pipeline de CI/CD para compilação na nuvem
│
├── COMO_GERAR_APK.md                        # Guia simplificado para o usuário
└── CONTEXTO_APP_APK.md                      # Este arquivo de contexto técnico
```

---

## 🧩 3. Tecnologias e Dependências Principais

1. **Linguagem & UI**:
   - **Kotlin**: 2.3.x (Java 17 target).
   - **Jetpack Compose + Material 3**: Design limpo, responsivo, com suporte a temas e microinterações.
   - **Navigation 3**: Sistema de rotas do Jetpack Compose baseado em `NavKey`.

2. **Motor de IA Offline**:
   - `com.google.mlkit:translate:17.0.3`
   - O Google ML Kit armazena os modelos treinados diretamente na memória privada do app (`on-device`).
   - Idiomas configurados por padrão: Português (`pt`), Inglês (`en`), Espanhol (`es`), Francês (`fr`), Alemão (`de`), Italiano (`it`).
   - Recursos: Inversão rápida de idiomas, cópia de texto com um toque e limpeza.

---

## ⚙️ 4. Fluxo de Compilação do APK

### Localmente (Windows/Linux/Mac):
Dentro da pasta `android-app`:
```bash
./gradlew assembleDebug
```
O `.apk` é gerado em:
`android-app/app/build/outputs/apk/debug/app-debug.apk`

### Na Nuvem (GitHub Actions):
Arquivo: `.github/workflows/build_apk.yml`
- Executa em `ubuntu-latest`.
- Configura Java 17 via Temurin.
- Executa `./gradlew assembleDebug --no-daemon`.
- Publica o artefato **`TradutorPJ-Android-APK`** para download direto na aba **Actions**.

---

## 🔒 5. Regras para Futuras Alterações
- Sempre manter a assinatura do callback de navegação tipada como `(NavKey) -> Unit` ao expor rotas em componentes Compose.
- Manter compatibilidade com `minSdk = 24` (Android 7.0+) e `targetSdk = 36`.
- Ao adicionar novos idiomas, incluir as respectivas constantes da classe `com.google.mlkit.nl.translate.TranslateLanguage` na lista `IDIOMAS_DISPONIVEIS` em `MainScreen.kt`.
