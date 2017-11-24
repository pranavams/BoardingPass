package com.ddairways.model;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Passenger {
    private String firstName;
    private String lastName;
    private String email;
    private final String travelClass;

    public String fullName() {
        return firstName + " " + lastName;
    }

    public URI getEmailUri() throws URISyntaxException {
        return new URI("mailto:" + email);
    }

}
