package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TmpData {
    private String name;
    private Set<Adjacent> adjacents;

    @Override
    public String toString() {
        return "TmpData{" +
                "name='" + name + '\'' +
                ", adjacents=" + adjacents +
                '}';
    }
}
