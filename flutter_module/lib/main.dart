import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';

void main() {
  runApp(MyApp());
}

class ChartData {
  final int timestamp;
  final double open;

  ChartData({
    required this.timestamp,
    required this.open,
  });
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  List<ChartData> chartDataList = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Histórico'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: LineChart(
          LineChartData(
            titlesData: const FlTitlesData(
              leftTitles: SideTitles(showTitles: true),
              bottomTitles: SideTitles(showTitles: true),
            ),
            gridData: const FlGridData(
              showHorizontalLine: true,
              showVerticalLine: true,
            ),
            borderData: FlBorderData(
              show: true,
              border: Border.all(color: const Color(0xff37434d), width: 1),
            ),
            minX: 0,
            maxX: chartDataList.length.toDouble(),
            minY: chartDataList.isNotEmpty ? chartDataList.map((data) => data.open).reduce((a, b) => a < b ? a : b) : 0,
            maxY: chartDataList.isNotEmpty ? chartDataList.map((data) => data.open).reduce((a, b) => a > b ? a : b) : 100,
            lineBarsData: [
              LineChartBarData(
                spots: chartDataList.asMap().entries.map((entry) {
                  return FlSpot(entry.key.toDouble(), entry.value.open);
                }).toList(),
                isCurved: true,
                color: Colors.blue,
                belowBarData: BarAreaData(show: false),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
