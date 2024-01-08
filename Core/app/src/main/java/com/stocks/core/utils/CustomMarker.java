package com.stocks.core.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.stocks.core.utils.getOff;

public class CustomMarker extends MarkerView implements getOff {

    public CustomMarker(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Aqui você pode personalizar o conteúdo do marcador se necessário
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // Defina o deslocamento para alinhar o marcador com a linha de marcação ou cursor
        return new MPPointF(-(getWidth() / 5f), -getHeight());
    }

    @Override
    public int getXOffset(float xpos) {
        // Ajuste o valor negativo para mover o marcador para o lado esquerdo
        return 30;
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }

    @Override
    public void draw(Canvas canvas, float posx, float posy) {
        // Tamanho do triângulo
        float triangleSize = getWidth() / 5;

        // Coordenadas do triângulo
        float triangleStartX = posx - triangleSize; // Ajuste aqui para ficar dentro do gráfico
        float triangleEndX = posx;
        float triangleTopY = posy - getHeight() / 2;
        float triangleBottomY = posy + getHeight() / 2;

        Path path = new Path();
        path.moveTo(triangleEndX, triangleTopY);
        path.lineTo(triangleStartX, posy);
        path.lineTo(triangleEndX, triangleBottomY);
        path.close();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // Defina a cor desejada para o triângulo
        paint.setStyle(Paint.Style.FILL);

        // Desenhe o triângulo
        canvas.drawPath(path, paint);

        // Ajuste o tamanho do retângulo
        float rectangleWidth = triangleSize * 1.5f;

        // Desenhe o retângulo encostado no final do triângulo
        paint.setColor(Color.WHITE); // Defina a cor desejada para o retângulo
        canvas.drawRect(triangleEndX, triangleTopY, posx + getWidth() + rectangleWidth, triangleBottomY, paint);

        // Desenhe o texto dentro do triângulo
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Defina a cor do texto
        textPaint.setTextSize(45f); // Defina o tamanho do texto

        // Ajuste a posição do texto para centrá-lo no triângulo
        float textX = posx + triangleSize / 2.3f;
        float textY = posy + triangleSize / 2.5f;

        canvas.drawText("R$ 39,00", textX, textY, textPaint);
    }

}
