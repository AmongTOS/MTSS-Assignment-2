////////////////////////////////////////////////////////////////////
// Elia Pasquali 1225412
// Alessio Ferrarini 1223860
////////////////////////////////////////////////////////////////////

package it.unipd.mtss.model;

public class EItem {

  public final ItemType itemType;
  public final String name;
  public double price;
  public boolean isDiscounted;

  public EItem(ItemType itemType, String name, double price) {
    this.itemType = itemType;
    this.name = name;
    this.price = price;
    this.isDiscounted = false;
  }
}
