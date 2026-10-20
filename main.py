"""
Tradutor PJ Mobile - 100% Offline (sem necessidade de internet no celular)
Carrega os modelos neurais embutidos diretamente no aplicativo.
"""

import os
import sys
import threading
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.uix.textinput import TextInput
from kivy.uix.button import Button
from kivy.uix.spinner import Spinner
from kivy.core.clipboard import Clipboard
from kivy.clock import Clock

IDIOMAS = {
    "Português": "pt",
    "Inglês": "en",
    "Espanhol": "es"
}


def carregar_modelos_locais():
    """Garante que o Argos Translate leia os modelos incluídos dentro do próprio APK/app."""
    try:
        import argostranslate.package as pkg

        caminho_base = os.path.dirname(os.path.abspath(__file__))
        pasta_modelos = os.path.join(caminho_base, "modelos")

        # Se houver pasta modelos empacotada com o app, define o diretório de dados do Argos
        if os.path.exists(pasta_modelos):
            os.environ["ARGOS_PACKAGES_DIR"] = pasta_modelos
            os.environ["ARGOS_DEVICE_TYPE"] = "cpu"

        # Carrega os pacotes instalados localmente
        pkg.get_installed_packages()
    except Exception as e:
        print(f"Erro ao inicializar modelos: {e}")


class TradutorLayout(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        self.padding = 15
        self.spacing = 10

        # Cabeçalho
        lbl_titulo = Label(
            text="[b]Tradutor PJ[/b] (100% Offline)",
            markup=True,
            font_size='22sp',
            size_hint_y=None,
            height=40,
            color=(0.2, 0.6, 1.0, 1)
        )
        self.add_widget(lbl_titulo)

        # Barra de Idiomas
        lang_bar = BoxLayout(size_hint_y=None, height=45, spacing=8)

        self.spin_origem = Spinner(
            text='Português',
            values=('Português', 'Inglês', 'Espanhol'),
            size_hint_x=0.4
        )
        lang_bar.add_widget(self.spin_origem)

        btn_swap = Button(
            text='⇄',
            font_size='20sp',
            size_hint_x=0.2,
            background_color=(0.3, 0.4, 0.5, 1)
        )
        btn_swap.bind(on_press=self.inverter_idiomas)
        lang_bar.add_widget(btn_swap)

        self.spin_destino = Spinner(
            text='Inglês',
            values=('Português', 'Inglês', 'Espanhol'),
            size_hint_x=0.4
        )
        lang_bar.add_widget(self.spin_destino)

        self.add_widget(lang_bar)

        # Entrada de Texto
        lbl_in = Label(text="Texto original:", size_hint_y=None, height=25, halign='left')
        lbl_in.bind(size=lbl_in.setter('text_size'))
        self.add_widget(lbl_in)

        self.txt_entrada = TextInput(
            multiline=True,
            font_size='16sp',
            size_hint_y=0.35,
            hint_text='Digite o texto para traduzir...'
        )
        self.add_widget(self.txt_entrada)

        # Botões de Ação
        btn_bar = BoxLayout(size_hint_y=None, height=45, spacing=10)

        self.btn_limpar = Button(
            text="Limpar",
            size_hint_x=0.3,
            background_color=(0.5, 0.5, 0.5, 1)
        )
        self.btn_limpar.bind(on_press=self.limpar_texto)
        btn_bar.add_widget(self.btn_limpar)

        self.btn_traduzir = Button(
            text="Traduzir",
            size_hint_x=0.7,
            background_color=(0.1, 0.5, 0.9, 1),
            font_size='18sp'
        )
        self.btn_traduzir.bind(on_press=self.iniciar_traducao)
        btn_bar.add_widget(self.btn_traduzir)

        self.add_widget(btn_bar)

        # Saída de Texto
        lbl_out = Label(text="Tradução:", size_hint_y=None, height=25, halign='left')
        lbl_out.bind(size=lbl_out.setter('text_size'))
        self.add_widget(lbl_out)

        self.txt_saida = TextInput(
            multiline=True,
            font_size='16sp',
            size_hint_y=0.35,
            readonly=True
        )
        self.add_widget(self.txt_saida)

        # Rodapé com botão copiar e status
        footer = BoxLayout(size_hint_y=None, height=35, spacing=10)

        self.lbl_status = Label(
            text="Pronto para traduzir off-line.",
            font_size='13sp',
            halign='left',
            color=(0.7, 0.7, 0.7, 1)
        )
        self.lbl_status.bind(size=self.lbl_status.setter('text_size'))
        footer.add_widget(self.lbl_status)

        btn_copiar = Button(
            text="Copiar",
            size_hint_x=None,
            width=90,
            background_color=(0.2, 0.7, 0.4, 1)
        )
        btn_copiar.bind(on_press=self.copiar_texto)
        footer.add_widget(btn_copiar)

        self.add_widget(footer)

    def inverter_idiomas(self, instance):
        origem = self.spin_origem.text
        destino = self.spin_destino.text
        self.spin_origem.text = destino
        self.spin_destino.text = origem

        t_in = self.txt_entrada.text
        t_out = self.txt_saida.text
        if t_out:
            self.txt_entrada.text = t_out
            self.txt_saida.text = t_in

    def limpar_texto(self, instance):
        self.txt_entrada.text = ""
        self.txt_saida.text = ""
        self.lbl_status.text = "Campos limpos."

    def copiar_texto(self, instance):
        txt = self.txt_saida.text.strip()
        if txt:
            Clipboard.copy(txt)
            self.lbl_status.text = "Copiado para a área de transferência!"

    def iniciar_traducao(self, instance):
        origem_nome = self.spin_origem.text
        destino_nome = self.spin_destino.text

        if origem_nome == destino_nome:
            self.lbl_status.text = "Selecione idiomas diferentes."
            return

        texto = self.txt_entrada.text.strip()
        if not texto:
            self.lbl_status.text = "Digite algum texto."
            return

        from_code = IDIOMAS[origem_nome]
        to_code = IDIOMAS[destino_nome]

        self.btn_traduzir.disabled = True
        self.lbl_status.text = "Traduzindo off-line..."

        threading.Thread(
            target=self._traduzir_thread,
            args=(texto, from_code, to_code),
            daemon=True
        ).start()

    def _traduzir_thread(self, texto, from_code, to_code):
        try:
            import argostranslate.translate as tr
            resultado = tr.translate(texto, from_code, to_code)
            Clock.schedule_once(lambda dt: self._finalizar_traducao(resultado, None))
        except Exception as e:
            Clock.schedule_once(lambda dt: self._finalizar_traducao(None, str(e)))

    def _finalizar_traducao(self, resultado, erro):
        self.btn_traduzir.disabled = False
        if erro:
            self.lbl_status.text = "Erro ao traduzir."
            self.txt_saida.text = f"Erro: {erro}"
        else:
            self.lbl_status.text = "Tradução concluída (off-line)!"
            self.txt_saida.text = resultado


class TradutorPJApp(App):
    def build(self):
        self.title = "Tradutor PJ Offline"
        carregar_modelos_locais()
        return TradutorLayout()


if __name__ == "__main__":
    TradutorPJApp().run()
