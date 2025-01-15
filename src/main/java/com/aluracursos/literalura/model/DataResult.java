package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataResult(
        @JsonAlias("title") String title,
        @JsonAlias("authors") List<DataPerson> authorList,
        @JsonAlias("languages") List<String> languages,
        @JsonAlias("download_count") Integer download_count
) {

}
