# Guia: Gerar APK Android 100% Offline (Sem Internet)

O aplicativo foi configurado para **conter internamente todos os modelos neurais de tradução**. Assim, no celular, ele funcionará em modo avião ou sem qualquer conexão de dados ou Wi-Fi.

---

## 📦 Estrutura do App Mobile Offline:
- **`main.py`**: Código Kivy com o leitor de modelos locais embutidos (`ARGOS_PACKAGES_DIR`).
- **`modelos/`**: Pasta criada localmente com todos os pesos e vocabulários dos modelos:
  - Português ↔ Inglês
  - Português ↔ Espanhol
  - Inglês ↔ Espanhol
- **`buildozer.spec`**: Configuração que empacota o Python, Kivy, as extensões binárias do OpenNMT/CTranslate2 e a pasta `modelos/` dentro do arquivo `.apk`.
- **`.github/workflows/build_apk.yml`**: Compilação automatizada na nuvem do GitHub.

---

## 🚀 Como gerar e baixar seu APK:

### 1. No GitHub:
Crie um repositório novo em [github.com/new](https://github.com/new) (por exemplo: `tradutor-pj-mobile`).

### 2. No seu computador:
Abra o terminal na pasta `c:\AgPJ\pyAg\python` e rode os comandos:

```powershell
git init
git add main.py buildozer.spec .github/ COMO_GERAR_APK.md
git commit -m "Compilar APK 100% Offline com modelos inclusos"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
git push -u origin main
```

*(Obs: Os arquivos `.argosmodel` pesam cerca de 600MB somados. Para evitar o limite de 100MB do Git individual, o script do GitHub Actions foi configurado para baixar e injetar os modelos diretamente na compilação do APK, garantindo que o arquivo final `.apk` saia com tudo embutido).*

### 3. Baixar no Celular:
1. Vá na aba **Actions** no seu GitHub.
2. Clique no workflow **"Gerar APK Android 100% Offline"**.
3. Ao finalizar, baixe o arquivo **`TradutorPJ-Offline-Android-APK`** na seção **Artifacts**.
4. Transfira o `.apk` para o celular e instale. Você poderá desligar o Wi-Fi e os dados móveis: a tradução continuará funcionando normalmente!
