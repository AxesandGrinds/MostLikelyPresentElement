package com.app.ej.ms.Model;

import com.opencsv.bean.CsvBindByName;

public class CSVSpectrum {

    @CsvBindByName(column = "wavelength", required = true)
    private String wavelength;

    @CsvBindByName(column = "intensity", required = true)
    private String intensity;

    public CSVSpectrum(String wavelength, String intensity) {
        this.wavelength = wavelength;
        this.intensity = intensity;
    }

    public String getWavelength() {
        return wavelength;
    }

    public void setWavelength(String wavelength) {
        this.wavelength = wavelength;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }
}

