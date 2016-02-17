package com.rao.tba;

/**
 * Created by romankazarin on 2/15/16.
 */
public class Recipe {

    private String m_if;
    private String m_then1;
    private String m_then2;

    private String m_name;
    public Recipe(String m_if, String m_then1, String m_then2, String recipeName) {
        this.m_if = m_if;
        this.m_then1 = m_then1;
        this.m_then2 = m_then2;
        this.m_name = recipeName;
    }

    public Recipe(String serializedRecipe){
        String[] parts = serializedRecipe.split("#");
        this.m_if = parts[0];
        this.m_then1 = parts[1];
        this.m_then2 = parts[2];
    }

    public Recipe(String serializedRecipe, String name) {
        String[] parts = serializedRecipe.split("#");
        this.m_if = parts[0];
        this.m_then1 = parts[1];
        this.m_then2 = parts[2];
        this.m_name = name;
    }

    public String getName() {
        return this.m_name;
    }

    public String toString(){
        return m_if + "#" + m_then1 + "#" + m_then2;
    }


}
