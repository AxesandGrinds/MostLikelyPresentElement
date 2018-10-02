package com.app.ej.ms.Controller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import com.app.ej.ms.R;
import static com.app.ej.ms.Controller.MainActivity.getLibLinesRelativeIntensityStringArray;
import static com.app.ej.ms.Controller.MainActivity.getLibLinesWaveLengthStringArray;
import static com.app.ej.ms.Controller.MainActivity.getSpectrumRelativeIntensityStringArray;
import static com.app.ej.ms.Controller.MainActivity.getSpectrumWaveLengthStringArray;

public class GraphActivity extends AppCompatActivity {

    static private String TAG = GraphActivity.class.getSimpleName();

    static private String[] libLinesWaveLengthStringArray, libLinesRelativeIntensityStringArray;
    static private String[] orderedLibLinesWaveLengthStringArray, orderedLibLinesRelativeIntensityStringArray;
    static private String[] spectrumWaveLengthStringArray, spectrumRelativeIntensityStringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        libLinesWaveLengthStringArray        = getLibLinesWaveLengthStringArray();
        libLinesRelativeIntensityStringArray = getLibLinesRelativeIntensityStringArray();

        orderedLibLinesWaveLengthStringArray        = getLibLinesWaveLengthStringArray();
        orderedLibLinesRelativeIntensityStringArray = getLibLinesRelativeIntensityStringArray();

        spectrumWaveLengthStringArray        = getSpectrumWaveLengthStringArray();
        spectrumRelativeIntensityStringArray = getSpectrumRelativeIntensityStringArray();

        GraphView libLinesGraphView = (GraphView) findViewById(R.id.graphLibLinesOnly);
        GraphView spectrumGraphView = (GraphView) findViewById(R.id.graphSpectrumOnly);
        GraphView allGraphView      = (GraphView) findViewById(R.id.graphLibLinesandSpectrum);

        initGraphLibLines(libLinesGraphView);
        initGraphSpectrum(spectrumGraphView);
        initGraphAll(allGraphView);
    }


    public static void order(String[]... arrays)
    {
        final String[] first = arrays[0];

        // Create an array of indices, initially in order.
        Integer[] indices = ascendingIntegerArray(first.length);

        // Sort the indices in order of the first array's items.
        Arrays.sort(indices, new Comparator<Integer>()
        {
            public int compare(Integer i1, Integer i2)
            {
                return
                        first[i1].compareToIgnoreCase(
                                first[i2]);
            }
        });

        // Sort the input arrays in the order
        // specified by the indices array.
        for (int i = 0; i < indices.length; i++)
        {
            int thisIndex = indices[i];

            for (String[] arr : arrays)
            {
                swap(arr, i, thisIndex);
            }

            // Find the index which references the switched
            // position and update it with the new index.
            for (int j = i+1; j < indices.length; j++)
            {
                if (indices[j] == i)
                {
                    indices[j] = thisIndex;
                    break;
                }
            }
        }
        // Note: The indices array is now trashed.
        // The first array is now in order and all other
        // arrays match that order.
    }

    public static Integer[] ascendingIntegerArray(int length)
    {
        Integer[] array = new Integer[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = i;
        }
        return array;
    }

    public static <T> void swap(T[] array, int i1, int i2)
    {
        T temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }


    public void initGraphLibLines(GraphView graph) {

        DataPoint[] pointsLibLinesAl = new DataPoint[7];
        DataPoint[] pointsLibLinesCu = new DataPoint[9];
        DataPoint[] pointsLibLinesFe = new DataPoint[13];
        DataPoint[] pointsLibLinesMg = new DataPoint[11];

        int j = 0;
        for (int i = 0; i < pointsLibLinesAl.length; i++) {
            pointsLibLinesAl[j] = new DataPoint(Float.parseFloat(libLinesWaveLengthStringArray[i]),
                    Float.parseFloat(libLinesRelativeIntensityStringArray[i]));
            j++;
        }
        Log.e(TAG, "pointsLibLinesAl.length: " + pointsLibLinesAl.length); // for debugging purposes


        j = 0;
        int AlToCuRange = pointsLibLinesAl.length+pointsLibLinesCu.length;
        for (int i = pointsLibLinesAl.length; i < AlToCuRange; i++) { //for (int i = 7; i < 15; i++) {
            pointsLibLinesCu[j] = new DataPoint(Float.parseFloat(libLinesWaveLengthStringArray[i]),
                    Float.parseFloat(libLinesRelativeIntensityStringArray[i]));
            j++;
        }
        Log.e(TAG, "pointsLibLinesCu.length: " + pointsLibLinesCu.length); // for debugging purposes


        j = 0;
        int CuToFeRange = AlToCuRange + pointsLibLinesFe.length;
        for (int i = AlToCuRange; i < CuToFeRange; i++) {
            pointsLibLinesFe[j] = new DataPoint(Float.parseFloat(libLinesWaveLengthStringArray[i]),
                    Float.parseFloat(libLinesRelativeIntensityStringArray[i]));
            j++;
        }

        j = 0;
        int FeToMgRange = CuToFeRange + pointsLibLinesMg.length;
        for (int i = CuToFeRange; i < FeToMgRange; i++) {
            pointsLibLinesMg[j] = new DataPoint(Float.parseFloat(libLinesWaveLengthStringArray[i]),
                    Float.parseFloat(libLinesRelativeIntensityStringArray[i]));
            j++;
        }

        PointsGraphSeries<DataPoint> seriesLibLinesAl = new PointsGraphSeries<>(pointsLibLinesAl);
        PointsGraphSeries<DataPoint> seriesLibLinesCu = new PointsGraphSeries<>(pointsLibLinesCu);
        PointsGraphSeries<DataPoint> seriesLibLinesFe = new PointsGraphSeries<>(pointsLibLinesFe);
        PointsGraphSeries<DataPoint> seriesLibLinesMg = new PointsGraphSeries<>(pointsLibLinesMg);

        graph.setTitle("Lib Lines");
        graph.setTitleTextSize(60);

        // Y axis is the relative intensity
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-1); // instead of 0 (a=0) (removed some points for graphic reasons)
        graph.getViewport().setMaxY(2);  // instead of 1 (b=0) (added more points for graphic reasons)

        //X axis is the wavelength
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(160); // instead of 180 (removed some points for graphic reasons)
        graph.getViewport().setMaxX(650); // instead of 633 (added more points for graphic reasons)

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        seriesLibLinesAl.setTitle("Al");
        seriesLibLinesCu.setTitle("Cu");
        seriesLibLinesFe.setTitle("Fe");
        seriesLibLinesMg.setTitle("Mg");

        seriesLibLinesAl.setColor(Color.MAGENTA);
        seriesLibLinesCu.setColor(Color.RED);
        seriesLibLinesFe.setColor(Color.BLACK);
        seriesLibLinesMg.setColor(Color.GREEN);

        graph.addSeries(seriesLibLinesAl);
        graph.addSeries(seriesLibLinesCu);
        graph.addSeries(seriesLibLinesFe);
        graph.addSeries(seriesLibLinesMg);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }


    public void initGraphSpectrum(GraphView graph) {

        // Order necessary to make line graph
        order(spectrumWaveLengthStringArray, spectrumRelativeIntensityStringArray);

        int spectrumLength = spectrumWaveLengthStringArray.length;
        DataPoint[] pointsSpectrum = new DataPoint[spectrumLength];
        for (int i = 0; i < pointsSpectrum.length; i++) {
            pointsSpectrum[i] = new DataPoint(Float.parseFloat(spectrumWaveLengthStringArray[i]),
                    Float.parseFloat(spectrumRelativeIntensityStringArray[i]));
        }

        LineGraphSeries<DataPoint> seriesSpectrum = new LineGraphSeries<>(pointsSpectrum);

        //Y axis is the relative intensity
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-1); // instead of 0 (a=0) (removed some points for graphic reasons)
        graph.getViewport().setMaxY(2);  // instead of 1 (b=0) (added more points for graphic reasons)

        //X axis is the wavelength
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(160); // instead of 180 (removed some points for graphic reasons)
        graph.getViewport().setMaxX(650); // instead of 633 (added more points for graphic reasons)

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        seriesSpectrum.setTitle("Spectrum Plot");

        seriesSpectrum.setColor(Color.BLUE);

        graph.addSeries(seriesSpectrum);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }


    public void initGraphAll(GraphView graph) {

        // Order Lib Lines in order to see line graph
        order(orderedLibLinesWaveLengthStringArray, orderedLibLinesRelativeIntensityStringArray);
        order(spectrumWaveLengthStringArray, spectrumRelativeIntensityStringArray);

        int libLinesLength = orderedLibLinesWaveLengthStringArray.length;
        DataPoint[] pointsLibLines = new DataPoint[libLinesLength];
        for (int i = 0; i < pointsLibLines.length; i++) {
            pointsLibLines[i] = new DataPoint(Float.parseFloat(orderedLibLinesWaveLengthStringArray[i]),
                    Float.parseFloat(orderedLibLinesRelativeIntensityStringArray[i]));
        }
        LineGraphSeries<DataPoint> seriesLibLines = new LineGraphSeries<>(pointsLibLines);

        int spectrumLength = spectrumWaveLengthStringArray.length;
        DataPoint[] pointsSpectrum = new DataPoint[spectrumLength];
        for (int i = 0; i < pointsSpectrum.length; i++) {
            pointsSpectrum[i] = new DataPoint(Float.parseFloat(spectrumWaveLengthStringArray[i]),
                    Float.parseFloat(spectrumRelativeIntensityStringArray[i]));
        }
        PointsGraphSeries<DataPoint> seriesSpectrum = new PointsGraphSeries<>(pointsSpectrum);

        //Y axis is the relative intensity
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-1); // instead of 0 (a=0) (removed some points for graphic reasons)
        graph.getViewport().setMaxY(2);  // instead of 1 (b=0) (added more points for graphic reasons)

        //X axis is the wavelength
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(160); // instead of 180 (removed some points for graphic reasons)
        graph.getViewport().setMaxX(650); // instead of 633 (added more points for graphic reasons)

        // enable scaling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        seriesLibLines.setTitle("Lib Lines Plot");
        seriesSpectrum.setTitle("Spectrum Plot");

        seriesLibLines.setColor(Color.GREEN);
        seriesSpectrum.setColor(Color.BLUE);

        graph.addSeries(seriesLibLines);
        graph.addSeries(seriesSpectrum);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

}
