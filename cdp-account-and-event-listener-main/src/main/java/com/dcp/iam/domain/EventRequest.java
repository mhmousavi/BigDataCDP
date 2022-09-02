package com.dcp.iam.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventRequest {
    private String id;
    private String timestamp;
    private String type;
    private String data;
    private String companyName;

    public EventRequest withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }
}
