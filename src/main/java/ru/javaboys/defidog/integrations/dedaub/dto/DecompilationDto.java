package ru.javaboys.defidog.integrations.dedaub.dto;

import lombok.Data;

@Data
public class DecompilationDto {
    private String md5;
    private String bytecode;
    private String disassembled;
    private String tac;
    private String yul;
    private String source;
}
