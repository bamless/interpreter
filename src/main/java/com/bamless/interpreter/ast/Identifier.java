package com.bamless.interpreter.ast;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Identifier implements Serializable {
    private transient Position position;
    private String id;

    public Identifier(Position pos, String id) {
        this.position = pos;
        this.id = id;
    }

    public Identifier(String id) {
        this(new Position(0, 0), id);
    }

    public String getVal() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Identifier other = (Identifier) obj;
        if(id == null) {
            if(other.id != null)
                return false;
        } else if(!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

}
