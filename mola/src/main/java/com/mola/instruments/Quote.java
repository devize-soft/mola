package com.mola.instruments;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;

@Entity
public class Quote<K, V> extends HashMap<K, V> implements Serializable {

    Long id;
    /**
     *
     */
    private static final long serialVersionUID = 6588065769724689856L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
