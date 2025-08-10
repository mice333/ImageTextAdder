package ru.nokisev.ImageTextAdder.model;

import lombok.Data;

@Data
public class ImageDetails {

    private Long id;
    private String title;
    private String description;
    private String priority;
    private String createdAt;
}
