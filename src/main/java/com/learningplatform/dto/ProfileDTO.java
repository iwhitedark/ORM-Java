package com.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Long id;
    private Long userId;
    private String bio;
    private String avatarUrl;
    private String phone;
    private String address;
    private String website;
    private String linkedinUrl;
    private String githubUrl;
}
