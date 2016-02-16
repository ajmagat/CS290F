package com.rao.tba;

/**
 * Created by romankazarin on 2/15/16.
 */
public class Recipe {

    private String m_if;
    private String m_then1;
    private String m_then2;

    public Recipe(String m_if, String m_then1, String m_then2) {
        this.m_if = m_if;
        this.m_then1 = m_then1;
        this.m_then2 = m_then2;
    }

    public Recipe(String serializedRecipe){
        String[] parts = serializedRecipe.split("#");
        this.m_if = parts[0];
        this.m_then1 = parts[1];
        this.m_then2 = parts[2];
    }

    public String toString(){
        return m_if + "#" + m_then1 + "#" + m_then2;
    }


}
