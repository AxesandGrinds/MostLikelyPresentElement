package com.app.ej.ms.Model;

import com.opencsv.bean.CsvBindByName;

public class CSVLibLines {

    @CsvBindByName
    private String element;

    @CsvBindByName(column = "wavelength", required = true)
    private String wavelength;

    @CsvBindByName(column = "intensity", required = true)
    private String relative_intensity;

    public CSVLibLines(String element, String wavelength, String relative_intensity) {
        this.element = element;
        this.wavelength = wavelength;
        this.relative_intensity = relative_intensity;
    }

    public String getElement() {
        return element;
    }

    public String getWavelength() {
        return wavelength;
    }

    public String getRelativeIntensity() {
        return relative_intensity;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public void setWavelength(String wavelength) {
        this.wavelength = wavelength;
    }

    public void setRelativeIntensity(String relative_intensity) {
        this.relative_intensity = relative_intensity;
    }
}

