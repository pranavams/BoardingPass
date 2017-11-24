package com.ddairways.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Airport {
    private String code;
    private String city;

    @Override
    public String toString() {
        return String.format("%s (%s)", city, code);
    }
}
