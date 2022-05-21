////////////////////////////////////////////////////////////////////
// Elia Pasquali 1225412
// Alessio Ferrarini 1223860
////////////////////////////////////////////////////////////////////

package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.User;

import java.util.List;
import java.util.Objects;

public class OrderCalculator implements Bill {

  @Override
  public double getOrderPrice(List<EItem> itemsOrdered, User user)
      throws OrderBillException {
    validateArguments(itemsOrdered, user);
    return 0;
  }

  private void validateArguments(List<EItem> itemsOrdered, User user)
      throws OrderBillException {
    Objects.requireNonNull(itemsOrdered, "The ordered items must not be null");
    Objects.requireNonNull(user, "The user must not be null");
    if (itemsOrdered.isEmpty()) {
      throw new OrderBillException("You can't place an order with 0 items");
    }

    var isAnyNegative = itemsOrdered.stream().anyMatch(e -> e.price < 0);
    if (isAnyNegative) {
      throw new OrderBillException("All the items must have a positive price");
    }

  }

}
