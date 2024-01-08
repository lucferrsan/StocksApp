import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Variação de Preço - PETR4.SA'),
        ),
        body: FutureBuilder(
          future: fetchStockData(),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return Center(child: CircularProgressIndicator());
            } else if (snapshot.hasError) {
              return Center(child: Text('Erro ao carregar dados'));
            } else {
              List<StockData> stockDataList = snapshot.data;
              return StockDataTable(stockDataList: stockDataList);
            }
          },
        ),
      ),
    );
  }
}

class StockData {
  final DateTime timestamp;
  final double open;
  final double low;
  final double high;
  final double close;
  final int volume;

  StockData({
    required this.timestamp,
    required this.open,
    required this.low,
    required this.high,
    required this.close,
    required this.volume,
  });
}

class StockDataTable extends StatelessWidget {
  final List<StockData> stockDataList;

  StockDataTable({Key? key, required this.stockDataList}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: DataTable(
        columns: [
          DataColumn(label: Text('Data')),
          DataColumn(label: Text('Abertura')),
          DataColumn(label: Text('Mínima')),
          DataColumn(label: Text('Máxima')),
          DataColumn(label: Text('Fechamento')),
          DataColumn(label: Text('Volume')),
        ],
        rows: stockDataList.map((stockData) {
          return DataRow(cells: [
            DataCell(Text(stockData.timestamp.toLocal().toString())),
            DataCell(Text(stockData.open.toString())),
            DataCell(Text(stockData.low.toString())),
            DataCell(Text(stockData.high.toString())),
            DataCell(Text(stockData.close.toString())),
            DataCell(Text(stockData.volume.toString())),
          ]);
        }).toList(),
      ),
    );
  }
}

Future<List<StockData>> fetchStockData() async {
  final response = await http.get(Uri.parse('https://query2.finance.yahoo.com/v8/finance/chart/PETR4.SA'));

  if (response.statusCode == 200) {
    final Map<String, dynamic> data = json.decode(response.body);
    final List<dynamic> timestamps = data['chart']['result'][0]['timestamp'];
    final List<dynamic> opens = data['chart']['result'][0]['indicators']['quote'][0]['open'];
    final List<dynamic> lows = data['chart']['result'][0]['indicators']['quote'][0]['low'];
    final List<dynamic> highs = data['chart']['result'][0]['indicators']['quote'][0]['high'];
    final List<dynamic> closes = data['chart']['result'][0]['indicators']['quote'][0]['close'];
    final List<dynamic> volumes = data['chart']['result'][0]['indicators']['quote'][0]['volume'];

    List<StockData> stockDataList = [];

    for (int i = 0; i < timestamps.length; i++) {
      DateTime timestamp = DateTime.fromMillisecondsSinceEpoch(timestamps[i] * 1000);
      double open = opens[i].toDouble();
      double low = lows[i].toDouble();
      double high = highs[i].toDouble();
      double close = closes[i].toDouble();
      int volume = volumes[i].toInt();

      stockDataList.add(
        StockData(
          timestamp: timestamp,
          open: open,
          low: low,
          high: high,
          close: close,
          volume: volume,
        ),
      );
    }

    return stockDataList;
  } else {
    throw Exception('Erro ao carregar dados');
  }
}
