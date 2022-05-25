package main.java.xy.sourbet;

import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.prefs.Preferences;

public class Controller {
    //colors
    private static Color colorWave1 = Color.RED;
    private static Color colorWave2 = Color.BLUE;
    private static Color colorWave3 = Color.GREEN;
    private static Color colorBackground = Color.WHITE;

    final static Color[] colors = new Color[3];


    double waveStartHeight; // position of wave
    int amplitude = 100;
    double strokeWidth = 3.0;
    boolean randomColors = false;
    int passes = 1;
    //
    GraphicsContext graphicsContext;

    Double t = 0.0;


    @FXML
    private AnchorPane paneMain;
    @FXML
    private Pane paneSettings;
    @FXML
    private Canvas canvasPrimary;
    @FXML
    private ColorPicker colorPickBackground;
    @FXML
    private ColorPicker colorPickWave1;
    @FXML
    private ColorPicker colorPickWave2;
    @FXML
    private ColorPicker colorPickWave3;
    @FXML
    private Spinner<Double> spinnerWaveWidth;
    @FXML
    private Slider sliderAmplitude;
    @FXML
    private Slider sliderPasses;
    @FXML
    private ToggleButton toggleRandom;

    //preferences
    final private String prefWaveColor1 = "wave1";
    final private String prefWaveColor2 = "wave2";
    final private String prefWaveColor3 = "wave3";
    final private String prefBackgroundColor = "back";
    final private String prefStrokeWidth = "1.0";
    final private String prefAmplitude = "100";
    final private String prefsToggleRandom = String.valueOf(false);
    final private String prefsPasses = "1";
    private Preferences prefs;

    Audio audio = Audio.getInstance();
    Drawing drawing;

    Task<Void> micTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
            audio.openMic();
            return null;
        }
    };

    @FXML
    void initialize() {
        prefs();
        actions();
        drawing = Drawing.getInstance(canvasPrimary);
        graphicsContext = canvasPrimary.getGraphicsContext2D();
        waveStartHeight = canvasPrimary.getHeight() * .5;
        Thread thread = new Thread(micTask);
        thread.start();
        animate();
    }

    void prefs() {
        defaultPrefs();
        getPrefs();
        setPrefs();
        setBackgroundColor();
    }

    void getPrefs() {
        colorWave1 = Color.web(prefs.get(prefWaveColor1, colorWave1.toString()));
        colorWave2 = Color.web(prefs.get(prefWaveColor2, colorWave2.toString()));
        colorWave3 = Color.web(prefs.get(prefWaveColor3, colorWave3.toString()));
        colors[0] = colorWave1;
        colors[1] = colorWave2;
        colors[2] = colorWave3;
        colorBackground = Color.web(prefs.get(prefBackgroundColor, colorBackground.toString()));
        strokeWidth = Double.parseDouble(prefs.get(prefStrokeWidth, "1.0"));
        amplitude = Integer.parseInt(prefs.get(prefAmplitude, "100"));
        passes = Integer.parseInt(prefs.get(prefsPasses, "1"));
        randomColors = Boolean.parseBoolean(prefs.get(prefsToggleRandom, "false"));
    }

    void setPrefs() {
        colorPickWave1.setValue(colorWave1);
        colorPickWave2.setValue(colorWave2);
        colorPickWave3.setValue(colorWave3);
        colorPickBackground.setValue(colorBackground);
        spinnerWaveWidth.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 15, strokeWidth));
        sliderAmplitude.setValue(amplitude);
        sliderPasses.setValue(passes);
        toggleRandom.setSelected(randomColors);
    }

    void defaultPrefs() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.get(prefWaveColor1, "0xff0000ff");
        prefs.get(prefWaveColor2, "0xff0000ff");
        prefs.get(prefWaveColor3, "0xff0000ff");
        prefs.get(prefBackgroundColor, "0xff0000ff");
        prefs.get(prefStrokeWidth, "1.0");
        prefs.get(prefAmplitude, "100");
        prefs.get(prefsToggleRandom, "false");
        prefs.get(prefsPasses, "1");
    }

    private void actions() {
        colorPickWave1.setOnAction(actionEvent -> {
            colorWave1 = colorPickWave1.getValue();
            colors[0] = colorWave1;
            prefs.put(prefWaveColor1, String.valueOf(colorWave1));
        });
        colorPickWave2.setOnAction(actionEvent -> {
            colorWave2 = colorPickWave2.getValue();
            colors[1] = colorWave2;
            prefs.put(prefWaveColor2, String.valueOf(colorWave2));
        });
        colorPickWave3.setOnAction(actionEvent -> {
            colorWave3 = colorPickWave3.getValue();
            colors[2] = colorWave3;
            prefs.put(prefWaveColor3, String.valueOf(colorWave3));
        });

        colorPickBackground.setOnAction(actionEvent -> {
            colorBackground = colorPickBackground.getValue();
            setBackgroundColor();
            prefs.put(prefBackgroundColor, String.valueOf(colorBackground));
        });

        canvasPrimary.setOnMouseDragged((event) -> {
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(event.getX(), event.getY(), 2, 2);
        });

        spinnerWaveWidth.valueProperty().addListener((obs, oldValue, newValue) -> {
            strokeWidth = newValue;
            prefs.put(prefStrokeWidth, String.valueOf(strokeWidth));
        });

        sliderAmplitude.valueProperty().addListener((obs, oldValue, newVal) -> {
            amplitude = newVal.intValue();
            prefs.put(prefAmplitude, String.valueOf(amplitude));
        });

        sliderPasses.valueProperty().addListener((obs, oldValue, newVal) -> {
            passes = newVal.intValue();
            prefs.put(prefsPasses, String.valueOf(passes));
        });

        toggleRandom.setOnAction(actionEvent -> {
            randomColors = toggleRandom.selectedProperty().getValue();
            prefs.put(prefsToggleRandom, String.valueOf(randomColors));

        });


    }

    public void setPaneVisible() {
        paneSettings.setVisible(true);
    }

    public void setPaneNotVisible() {
        paneSettings.setVisible(false);
    }

    public void setBackgroundColor() {
        paneMain.setBackground(new Background(new BackgroundFill(colorBackground, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    void animate() {
        AnimationTimer timer = new AnimationTimer() {
            long lastTimerCall = System.nanoTime();
            @Override
            public void handle(long NOW) {
                if (NOW > lastTimerCall + 60000000) { //nanoseconds
                    t += 0.017;
                    //clear canvas
                    audio.movingAveragePass = passes;
                    graphicsContext.clearRect(0, 0, canvasPrimary.getWidth(), canvasPrimary.getHeight());
                    drawing.draw(audio.samples, amplitude, strokeWidth, colors, randomColors);
                    lastTimerCall = NOW;
                }
            }
        };
        timer.start();
    }


}
