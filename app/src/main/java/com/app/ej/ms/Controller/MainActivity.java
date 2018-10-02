package com.app.ej.ms.Controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Parcelable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

import com.app.ej.ms.R;
import com.app.ej.ms.CSVRawFileReader.CSVFile;
import com.app.ej.ms.CSVRawFileReader.ItemArrayAdapterLibLines;
import com.app.ej.ms.CSVRawFileReader.ItemArrayAdapterSpectrum;
import com.app.ej.ms.Utils.CSVGetFilePath;
import com.app.ej.ms.Utils.FloatUtils;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE= 1;
    private static final int EXPLICIT_READ_STORAGE_PERMISSION_REQUEST_CODE= 2;
    private static final int ACTIVITY_CHOOSE_CSV_FILE = 22;

    private static final String TAG = MainActivity.class.getName();

    private FloatingActionMenu   fabMenuRed;
    private FloatingActionButton fabGraph, fabUseEmbedded, fabChooseFile;

    private ListView libLinesListView, spectrumListView;
    private ItemArrayAdapterLibLines libLinesItemArrayAdapter;
    private ItemArrayAdapterSpectrum spectrumItemArrayAdapter;

    static private List<String[]> records, scoreListLibLines, scoreListSpectrum;
    static private String[] libLinesElementStringArray, libLinesWaveLengthStringArray, libLinesRelativeIntensityStringArray;
    static private String[] spectrumWaveLengthStringArray, spectrumIntensityStringArray, spectrumRelativeIntensityStringArray;
    static private float[]  spectrumWaveLengthFloatArray, spectrumIntensityFloatArray, spectrumRelativeIntensityFloatArray;
    static private float[]  libLinesWaveLengthFloatArray, libLinesRelativeIntensityFloatArray;
    static private float    spectrumIntensityMaxFloat, spectrumIntensityMinFloat;
    static private float    spectrumRelativeIntensityMaxFloat, spectrumRelativeIntensityMinFloat;
    static private float    spectrumWavelengthMaxFloat, spectrumWavelengthMinFloat;

    public static String[] getLibLinesWaveLengthStringArray() {
        return libLinesWaveLengthStringArray;
    }

    public static String[] getLibLinesRelativeIntensityStringArray() {
        return libLinesRelativeIntensityStringArray;
    }

    public static String[] getSpectrumWaveLengthStringArray() {
        return spectrumWaveLengthStringArray;
    }

    public static String[] getSpectrumRelativeIntensityStringArray() {
        return spectrumRelativeIntensityStringArray;
    }

    public static void setLibLinesWaveLengthStringArray(String[] libLinesWaveLengthStringArray) {
        MainActivity.libLinesWaveLengthStringArray = libLinesWaveLengthStringArray;
    }
    public static void setLibLinesRelativeIntensityStringArray(String[] libLinesRelativeIntensityStringArray) {
        MainActivity.libLinesRelativeIntensityStringArray = libLinesRelativeIntensityStringArray;
    }

    public static void setSpectrumWaveLengthStringArray(String[] spectrumWaveLengthStringArray) {
        MainActivity.spectrumWaveLengthStringArray = spectrumWaveLengthStringArray;
    }

    public static void setSpectrumRelativeIntensityStringArray(String[] spectrumRelativeIntensityStringArray) {
        MainActivity.spectrumRelativeIntensityStringArray = spectrumRelativeIntensityStringArray;
    }

    // From importCSV(), we know:
    // spectrumWaveLengthFloatArray and spectrumRelativeIntensityFloatArray are arrays
    // of values in Spectrum Chosen File, Wavelength and Relative Intensity, respectively
    // From onCreate(), we know:
    // libLinesWaveLengthFloatArray and libLinesRelativeIntensityFloatArray are arrays
    // of values in LibLines Raw File, Wavelength and Relative Intensity, respectively
    // libLinesElementStringArray is an array of strings that are elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissionsWrite();
        askPermissionsRead();

        fabMenuRed     = (FloatingActionMenu)   findViewById(R.id.menu_red);
        fabGraph       = (FloatingActionButton) findViewById(R.id.fab_graph);
        fabUseEmbedded = (FloatingActionButton) findViewById(R.id.fab_use_embedded);
        fabChooseFile  = (FloatingActionButton) findViewById(R.id.fab_spectrum);

        fabGraph.setEnabled(false);
        fabMenuRed.setClosedOnTouchOutside(true);

        fabGraph.setShowAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.scale_up));
        fabGraph.setHideAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.scale_down));
        fabUseEmbedded.setShowAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.scale_up));
        fabUseEmbedded.setHideAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.scale_down));
        fabChooseFile.setShowAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.scale_up));
        fabChooseFile.setHideAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.scale_down));

        fabGraph.setOnClickListener(clickListener);
        fabUseEmbedded.setOnClickListener(clickListener);
        fabChooseFile.setOnClickListener(clickListener);

        fabMenuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenuRed.isOpened()) {
                    Toast.makeText(getApplication(), fabMenuRed.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }

                fabMenuRed.toggle(true);
            }
        });

        fabGraph.setImageDrawable(getDrawable(R.drawable.ic_line_graph));
        fabUseEmbedded.setImageDrawable(getDrawable(R.drawable.ic_use_embedded_black_30dp));
        fabChooseFile.setImageDrawable(getDrawable(R.drawable.ic_add_circle_black_30dp));

        libLinesListView = (ListView) findViewById(R.id.listViewLibLines);
        spectrumListView = (ListView) findViewById(R.id.listViewSpectrum);

        // Setup spectrumItemArrayAdapter to be initialized inside importCSV() or addEmbeddedSpectrum() and then displayed
        // Display contents of  libLinesItemArrayAdapter immediately
        spectrumItemArrayAdapter = new ItemArrayAdapterSpectrum(getApplicationContext(), R.layout.item_layout_spectrum);
        libLinesItemArrayAdapter = new ItemArrayAdapterLibLines(getApplicationContext(), R.layout.item_layout);

        Parcelable stateLibLines = libLinesListView.onSaveInstanceState();
        libLinesListView.setAdapter(libLinesItemArrayAdapter);
        libLinesListView.onRestoreInstanceState(stateLibLines);

        InputStream    inputStreamLibLines  = getResources().openRawResource(R.raw.libs_lines);
        CSVFile        csvFileLibLines      = new CSVFile(inputStreamLibLines);
                       scoreListLibLines    = csvFileLibLines.read();

        libLinesElementStringArray = new String[scoreListLibLines.size()];
        libLinesWaveLengthStringArray = new String[scoreListLibLines.size()];
        libLinesRelativeIntensityStringArray = new String[scoreListLibLines.size()];

        libLinesWaveLengthFloatArray = new float[scoreListLibLines.size()];
        libLinesRelativeIntensityFloatArray = new float[scoreListLibLines.size()];

        int i = 0;
        for(String[] scoreDataLibLines:scoreListLibLines ) {
            libLinesItemArrayAdapter.add(scoreDataLibLines);

            libLinesElementStringArray[i]            = scoreDataLibLines[0];
            libLinesWaveLengthStringArray[i]         = scoreDataLibLines[1];
            libLinesRelativeIntensityStringArray[i]  = scoreDataLibLines[2];
            libLinesWaveLengthFloatArray[i]          = Float.parseFloat(scoreDataLibLines[1]);
            libLinesRelativeIntensityFloatArray[i]   = Float.parseFloat(scoreDataLibLines[2]);

            i++;
        }
    }


    private void selectCSVFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), ACTIVITY_CHOOSE_CSV_FILE);
    }


    private void addEmbeddedSpectrum() {

        fabGraph.setEnabled(true);
        spectrumItemArrayAdapter.clear();
        spectrumItemArrayAdapter.notifyDataSetChanged();

        Parcelable stateSpectrum = spectrumListView.onSaveInstanceState();
        spectrumListView.setAdapter(spectrumItemArrayAdapter);
        spectrumListView.onRestoreInstanceState(stateSpectrum);

        InputStream    inputStreamSpectrum   = getResources().openRawResource(R.raw.al_2024);
        CSVFile        csvFileSpectrum       = new CSVFile(inputStreamSpectrum);
        scoreListSpectrum    = csvFileSpectrum.read();

        spectrumWaveLengthStringArray        = new String[scoreListSpectrum.size()];
        spectrumIntensityStringArray         = new String[scoreListSpectrum.size()];
        spectrumRelativeIntensityStringArray = new String[scoreListSpectrum.size()];

        spectrumWaveLengthFloatArray         = new float[scoreListSpectrum.size()];
        spectrumIntensityFloatArray          = new float[scoreListSpectrum.size()];
        spectrumRelativeIntensityFloatArray  = new float[scoreListSpectrum.size()];

        int i = 0;
        for(String[] scoreDataSpectrum:scoreListSpectrum ) {
            spectrumItemArrayAdapter.add(scoreDataSpectrum);

            spectrumWaveLengthStringArray[i] = scoreDataSpectrum[0];
            spectrumIntensityStringArray[i]  = scoreDataSpectrum[1];
            spectrumWaveLengthFloatArray[i]  = Float.parseFloat(scoreDataSpectrum[0]);
            spectrumIntensityFloatArray[i]   = Float.parseFloat(scoreDataSpectrum[1]);

            i++;
        }

        spectrumWavelengthMaxFloat = FloatUtils.getMaxValue(spectrumWaveLengthFloatArray);
        spectrumWavelengthMinFloat = FloatUtils.getMinValue(spectrumWaveLengthFloatArray);

        spectrumIntensityMaxFloat  = FloatUtils.getMaxValue(spectrumIntensityFloatArray);
        spectrumIntensityMinFloat  = FloatUtils.getMinValue(spectrumIntensityFloatArray);

        Log.e(TAG, "Embedded Spectrum Wavelength Max value (spectrumWavelengthMaxFloat): " + spectrumWavelengthMaxFloat);
        Log.e(TAG, "Embedded Spectrum Wavelength Min value (spectrumWavelengthMinFloat): " + spectrumWavelengthMinFloat);

        Log.e(TAG, "Embedded Spectrum Intensity Max value (spectrumIntensityMaxFloat): " + spectrumIntensityMaxFloat);
        Log.e(TAG, "Embedded Spectrum Intensity Min value (spectrumIntensityMinFloat): " + spectrumIntensityMinFloat);

        //https://en.wikipedia.org/wiki/Normalization_(statistics)
        // Normalization entails a start and finish value, a & b
        int a = 0;
        int b = 1;
        for(int j = 0; j < spectrumIntensityFloatArray.length; j++) {

            spectrumRelativeIntensityFloatArray[j] =
                    a + (
                            ( (spectrumIntensityFloatArray[j] - spectrumIntensityMinFloat) * (b - a) ) /
                                    (spectrumIntensityMaxFloat - spectrumIntensityMinFloat)
                    );

            spectrumRelativeIntensityStringArray[j] = Float.toString(spectrumRelativeIntensityFloatArray[j]);
        }

        spectrumRelativeIntensityMaxFloat = FloatUtils.getMaxValue(spectrumRelativeIntensityFloatArray);
        spectrumRelativeIntensityMinFloat = FloatUtils.getMinValue(spectrumRelativeIntensityFloatArray);

        Log.e(TAG, "Embedded Spectrum Relative Intensity Max value (spectrumRelativeIntensityMaxFloat): " + spectrumRelativeIntensityMaxFloat);
        Log.e(TAG, "Embedded Spectrum Relative Intensity Min value (spectrumRelativeIntensityMinFloat): " + spectrumRelativeIntensityMinFloat);
    }


    private void importCSV(Uri uri) {

        spectrumItemArrayAdapter.clear();
        spectrumItemArrayAdapter.notifyDataSetChanged();

        InputStream spectrumInputStream = null;
        try {
            spectrumInputStream = getContentResolver().openInputStream(uri);
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(spectrumInputStream)).withSkipLines(1).build();

            //Reading All Records at once into a List<String[]>
            records = csvReader.readAll();
            spectrumInputStream.close();
            Log.e(TAG, "CSV File Size : " + records.size());

            int recordSizeInt = records.size();
            spectrumWaveLengthStringArray = new String[recordSizeInt];
            spectrumIntensityStringArray  = new String[recordSizeInt];
            spectrumRelativeIntensityStringArray  = new String[recordSizeInt];

            spectrumWaveLengthFloatArray        = new float[recordSizeInt];
            spectrumIntensityFloatArray         = new float[recordSizeInt];
            spectrumRelativeIntensityFloatArray = new float[recordSizeInt];

            if (records != null) {
                int i = 0;
                for (String[] record : records) {

                    spectrumItemArrayAdapter.add(record);
                    spectrumWaveLengthStringArray[i]  = record[0];
                    spectrumIntensityStringArray[i]   = record[1];

                    spectrumWaveLengthFloatArray[i] = Float.parseFloat(record[0]);
                    spectrumIntensityFloatArray[i]  = Float.parseFloat(record[1]);

                    i++;
                }
            }

            spectrumWavelengthMaxFloat = FloatUtils.getMaxValue(spectrumWaveLengthFloatArray);
            spectrumWavelengthMinFloat = FloatUtils.getMinValue(spectrumWaveLengthFloatArray);

            spectrumIntensityMaxFloat  = FloatUtils.getMaxValue(spectrumIntensityFloatArray);
            spectrumIntensityMinFloat  = FloatUtils.getMinValue(spectrumIntensityFloatArray);

            Log.e(TAG, "Spectrum Wavelength Max value (spectrumWavelengthMaxFloat): " + spectrumWavelengthMaxFloat);
            Log.e(TAG, "Spectrum Wavelength Min value (spectrumWavelengthMinFloat): " + spectrumWavelengthMinFloat);

            Log.e(TAG, "Spectrum Intensity Max value (spectrumIntensityMaxFloat): " + spectrumIntensityMaxFloat);
            Log.e(TAG, "Spectrum Intensity Min value (spectrumIntensityMinFloat): " + spectrumIntensityMinFloat);

            //https://en.wikipedia.org/wiki/Normalization_(statistics)
            // Normalization entails a start and finish value, a & b
            int a = 0;
            int b = 1;
            for(int j = 0; j < spectrumIntensityFloatArray.length; j++) {

                spectrumRelativeIntensityFloatArray[j] =
                        a + (
                                ( (spectrumIntensityFloatArray[j] - spectrumIntensityMinFloat) * (b - a) ) /
                                        (spectrumIntensityMaxFloat - spectrumIntensityMinFloat)
                        );

                spectrumRelativeIntensityStringArray[j] = Float.toString(spectrumRelativeIntensityFloatArray[j]);
            }

            spectrumRelativeIntensityMaxFloat = FloatUtils.getMaxValue(spectrumRelativeIntensityFloatArray);
            spectrumRelativeIntensityMinFloat = FloatUtils.getMinValue(spectrumRelativeIntensityFloatArray);

            Log.e(TAG, "Spectrum Relative Intensity Max value (spectrumRelativeIntensityMaxFloat): " + spectrumRelativeIntensityMaxFloat);
            Log.e(TAG, "Spectrum Relative Intensity Min value (spectrumRelativeIntensityMinFloat): " + spectrumRelativeIntensityMinFloat);

            Parcelable stateSpectrum = spectrumListView.onSaveInstanceState();
            spectrumListView.setAdapter(spectrumItemArrayAdapter);
            spectrumListView.onRestoreInstanceState(stateSpectrum);
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch(IOException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_CHOOSE_CSV_FILE) {
            if (resultCode == RESULT_OK) {

                fabGraph.setEnabled(true);

                Uri selectedCSVFileUri = data.getData();
                final String resultUriPathString = selectedCSVFileUri.getPath();

                String selectedCSVFilePath = CSVGetFilePath.getPath(
                        getApplicationContext(), selectedCSVFileUri);

                Log.e(TAG, "Result Uri: selectedCSVFileUri(or data.getData()).toString(): " + selectedCSVFileUri.toString());
                Log.e(TAG, "Result Uri Path: resultUriPathString = data.getData().getPath(): " + resultUriPathString);
                Log.e(TAG, "Result Get Path: selectedCSVFilePath = CSVGetFilePath.getPath(...): " + selectedCSVFilePath);

                importCSV(selectedCSVFileUri);
                fabMenuRed.close(true);

                //For debugging purposes
                //Toast.makeText(this, "Result Uri (selectedCSVFileUri.toString()): " + selectedCSVFileUri.toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "Result Uri Path (resultUriPathString): " + resultUriPathString, Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "Result Get Path (selectedCSVFilePath): " + selectedCSVFilePath, Toast.LENGTH_LONG).show();
            }
        }
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_graph:
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    startActivity(intent);
                    break;
                case R.id.fab_use_embedded:
                    addEmbeddedSpectrum();  // enabled
                    fabMenuRed.close(true);
                    break;
                case R.id.fab_spectrum:
                    askPermissionsWrite();
                    askPermissionsRead();
                    Snackbar.make(v, "Choose your Spectrum CSV file", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    selectCSVFile();
                    break;
            }
        }
    };


    private void askPermissionsWrite() {

        int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        // if storage request is denied or has not been granted.
        if (!(permissionCheckStorage == PackageManager.PERMISSION_GRANTED)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))                {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to give permission to access storage in order to graph points.");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Show permission request popup
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                STORAGE_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.show();

            } //asking permission for first time
            else {
                // Show permission request popup for the first time
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);

            }

        }
    }


    private void askPermissionsRead() {

        int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        // if read storage request is denied or has not been granted.
        if (!(permissionCheckStorage == PackageManager.PERMISSION_GRANTED)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))                {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Sometimes it is necessary to have read permission to access storage..");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Show permission request popup
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                EXPLICIT_READ_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.show();

            } //asking permission for first time
            else {
                // Show permission request popup for the first time
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXPLICIT_READ_STORAGE_PERMISSION_REQUEST_CODE);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    }
                }
                break;
            case EXPLICIT_READ_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE )) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    }
                }
                break;
            default:
                break;
        }
    }
}


