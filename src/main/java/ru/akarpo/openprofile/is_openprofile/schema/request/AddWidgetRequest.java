package ru.akarpo.openprofile.is_openprofile.schema.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddWidgetRequest {
    private String widgetCode;
    private String title;
    private String settings;
    private String layout;
}