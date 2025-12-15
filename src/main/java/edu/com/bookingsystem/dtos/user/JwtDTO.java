package edu.com.bookingsystem.dtos.user;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTO {
    private String accessToken;
    private String refreshToken;


}
