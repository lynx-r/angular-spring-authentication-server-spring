package com.example.backendspring.model;

import com.rits.cloning.Cloner;

/**
 * Created by Aleksey Popryaduhin on 10:38 20/09/2017.
 */
@SuppressWarnings("unchecked")
public interface DeepClone {
  default <T> T deepClone() {
    Cloner cloner = new Cloner();
    return cloner.deepClone((T) this);
  }
}
