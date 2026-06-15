package model;

import java.util.ArrayList;
import java.util.List;

public class Autor extends Pessoa{
    protected String pseudonimo;
    protected String nacionalidade;

    List<Livro> livros = new ArrayList<>();
}
