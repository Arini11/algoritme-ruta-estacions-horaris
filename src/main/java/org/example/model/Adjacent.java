package org.example.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Adjacent {
    private String id;
    @EqualsAndHashCode.Exclude
    private int duration;

}
