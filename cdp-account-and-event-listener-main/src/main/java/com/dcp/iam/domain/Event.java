package com.dcp.iam.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

public record Event(String id, String timestamp, String type, String data) implements Serializable {
}
