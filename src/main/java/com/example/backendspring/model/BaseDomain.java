
package com.example.backendspring.model;

import java.io.Serializable;

public abstract class BaseDomain implements Serializable, Cloneable {

  public abstract String getId();

  public abstract void setId(String id);
}
