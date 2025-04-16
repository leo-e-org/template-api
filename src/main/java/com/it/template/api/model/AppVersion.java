package com.it.template.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AppVersion {

    private String version;

    @Override
    public String toString() {
        return "{ \"version\": \"%s\" }".formatted(version);
    }
}
