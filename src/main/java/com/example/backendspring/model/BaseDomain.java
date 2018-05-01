
package com.example.backendspring.model;

import java.io.Serializable;

public abstract class BaseDomain implements Serializable, DeepClone, Cloneable {

  public abstract String getId();

  public abstract void setId(String id);
}
