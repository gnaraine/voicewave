package main.java.xy.sourbet;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Drawing {

    private static Drawing instance;
    Canvas canvas;
    GraphicsContext graphicsContext;
    double yHeight;

    public Drawing(Canvas canvasPrimary) {
        this.canvas = canvasPrimary;
        this.yHeight = canvas.getHeight() * .5;
        graphicsContext = canvasPrimary.getGraphicsContext2D();
    }

    public static Drawing getInstance(Canvas canvasPrimary) {
        if (instance == null) {
            instance = new Drawing(canvasPrimary);
        }
        return instance;
    }

    public void draw(
            float[] samples,
            int amplitude,
            Double strokeWidth,
            Color[] colors, boolean randomColors) {

        graphicsContext.setLineWidth(strokeWidth);

        //first channel
        if (randomColors) {
            graphicsContext.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
        } else {
            graphicsContext.setStroke(colors[0]);
        }
        graphicsContext.beginPath();
        graphicsContext.moveTo(0, yHeight); //center y
        for (int i = 0; i < samples.length; i = i + 2) {
            float var = samples[i];
            graphicsContext.lineTo(i, yHeight + (var * amplitude));
        }
        graphicsContext.stroke();


        //second channel
        if (randomColors) {
            graphicsContext.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
        } else {
            graphicsContext.setStroke(colors[1]);
        }
        graphicsContext.beginPath();
        graphicsContext.moveTo(0, yHeight);
        for (int i = 0; i < samples.length; i = i + 2) {
            float var = samples[i + 1];
            graphicsContext.lineTo(i, yHeight + (var * amplitude));
        }
        graphicsContext.stroke();

        //duplicate channel
        if (randomColors) {
            graphicsContext.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
        } else {
            graphicsContext.setStroke(colors[2]);
        }
        graphicsContext.beginPath();
        graphicsContext.moveTo(0, yHeight); //center y
        for (int i = 0; i < samples.length; i = i + 2) {
            float var = samples[i];
            graphicsContext.lineTo(i, yHeight + (var * amplitude));
        }
        graphicsContext.stroke();
    }

}
