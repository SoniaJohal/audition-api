package com.audition.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;

}
