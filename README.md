# StocksApp
## Aplicativo Nativo Android com módulo Flutter

Visão geral:

Este código Java define a MainActivity para um aplicativo Android que exibe informações e um gráfico de linha de uma ação específica.
Ele utiliza a API da Yahoo Finance para obter os dados de ações.
Inicialmente, este repositório tinha a pretensão de ser uma avaliação. Mas tentarei dar continuidade e tentar inovar em alguma coisas dentro do contexto envolvido.

###Update v.1.0.1
1. Melhorando o gráfico de exibição na tela inicial;
   - Exibição de marcador ou cursor de linha, como usado em apps de investimentos.
3. Começando a organizar a estrutura e limpando a bagunça;

   
A interface da atividade inclui:
Exibição de porcentagem de variação e preço atual da ação.
Botão para visualizar histórico da ação em uma tela Flutter separada.
Gráfico de linha que mostra a variação do preço da ação ao longo do tempo.
Botões para alternar entre diferentes períodos de tempo no gráfico.

##Instalação
1. Baixar este respositório em diretório local;
2. Abra o módulo flutter_module e num terminal com acesso ao diretório do módulo, rode o comando:
  "flutter build aar"
3. Feche o módulo flutter e abra o projeto Core;
4. Rode ou debug o projeto.
   
###Funcionalidades principais:

Inicializar componentes da interface:
Associa elementos da interface (TextViews, Button, Chart) aos seus IDs no layout XML.
Obter e exibir dados de ação:
Chama fetchAndUpdateStockData() para buscar dados da ação "PETR4.SA".
Processa os dados recebidos e exibe o preço atual e a porcentagem de variação.

###Inicializar Flutter Engine:
Cria um FlutterEngine para executar a tela Flutter de histórico.
Armazena o engine em cache para uso posterior.
Configurar botão de histórico:
Define um listener para o botão "Histórico", que inicia a tela Flutter.

###Buscar dados de gráfico:
Chama fetchData() para buscar dados da ação para o período de tempo selecionado.
Atualiza o gráfico de linha com os dados recebidos.
Configura os botões de período de tempo.

###Métodos principais:

fetchAndUpdateStockData():
Faz uma requisição à API para obter dados da ação.
Processa os dados recebidos e atualiza os TextViews de preço e variação.
processStockData():
Calcula a porcentagem de variação do preço.
Atualiza os TextViews com o preço e variação, ajustando a cor da variação.
setupRangeButtons():
Cria e configura os botões de período de tempo.
fetchData():
Faz uma requisição à API para obter dados de gráfico para o período de tempo especificado.
Atualiza o gráfico de linha com os dados recebidos.
updateChart():
Configura os dados, aparência e formatação do gráfico de linha. 
