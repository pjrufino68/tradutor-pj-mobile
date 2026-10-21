# Guia: Como Gerar e Baixar o APK Android

O aplicativo **Tradutor PJ Mobile** utiliza arquitetura Android Nativa (Jetpack Compose + Material 3) com motor de tradução neural **100% On-Device (Google ML Kit)**, permitindo traduções rápidas e offline diretamente no aparelho.

---

## 📦 Como Gerar o APK

### Opção 1: Compilar Localmente no seu Computador
1. Abra o terminal na pasta `c:\AgPJ\pyAg\python\android-app`.
2. Execute o comando:
   ```powershell
   .\gradlew assembleDebug
   ```
3. O arquivo APK final estará pronto no caminho:
   `android-app\app\build\outputs\apk\debug\app-debug.apk`

---

### Opção 2: Gerar Automaticamente no GitHub Actions
Sempre que fizer alterações e enviar para o repositório (`git push origin main`), o GitHub Actions compila o APK automaticamente na nuvem:

1. Acesse o seu repositório:
   👉 **[https://github.com/pjrufino68/tradutor-pj-mobile/actions](https://github.com/pjrufino68/tradutor-pj-mobile/actions)**
2. Clique na execução mais recente do workflow **"Gerar APK Android 100% Offline (ML Kit)"**.
3. No final da página, na seção **Artifacts**, clique em **`TradutorPJ-Android-APK`** para baixar o arquivo zip com o instalador `.apk`.
4. Transfira para o seu celular e instale!

---

## 🌐 Idiomas Suportados
- Português ↔ Inglês
- Português ↔ Espanhol
- Português ↔ Francês
- Português ↔ Alemão
- Português ↔ Italiano
*(E qualquer combinação entre eles com o botão de inverter idiomas).*

Para mais detalhes técnicos e arquitetura, consulte o arquivo [CONTEXTO_APP_APK.md](CONTEXTO_APP_APK.md).
