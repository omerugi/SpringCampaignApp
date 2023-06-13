package com.example.mabaya.dto;

import com.example.mabaya.consts.ValidationMsg;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {

    private Long id;

    @Size(min = 2, max = 25, message =ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)
    @NotNull(message =  ValidationMsg.NULL_NAME)
    private String name;

    public CategoryDTO(String name) {
        this.name = name;
    }

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
