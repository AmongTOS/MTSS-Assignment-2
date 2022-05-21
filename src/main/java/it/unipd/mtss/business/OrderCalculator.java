////////////////////////////////////////////////////////////////////
// Elia Pasquali 1225412
// Alessio Ferrarini 1223860
////////////////////////////////////////////////////////////////////

package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.ItemType;
import it.unipd.mtss.model.User;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class OrderCalculator implements Bill {

  @Override
  public double getOrderPrice(List<EItem> itemsOrdered, User user)
      throws OrderBillException {
    validateArguments(itemsOrdered, user);
    applyProcessorDiscount(itemsOrdered);

    var actualPrice = itemsOrdered.stream()
            .mapToDouble(e -> e.price)
            .sum();

    return actualPrice;
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

  private void applyProcessorDiscount(List<EItem> itemsOrdered) {
    applyQuantityDiscount(
        itemsOrdered,
        5,
        0.5f,
        e -> e.itemType == ItemType.Processor
    );
  }
  private void applyQuantityDiscount(
      List<EItem> itemsOrdered, int minimumOrder,
      float discount, Predicate<EItem> match) {
    var count = itemsOrdered.stream()
        .filter(match)
        .count();
    if (count >= minimumOrder) {
      var cheapestProcessor = itemsOrdered.stream()
          .filter(match)
          .min((e1, e2) -> (int) (e1.price - e2.price))
          .get(); // Safe since we know we have at least 5 processors
      cheapestProcessor.price *= discount;
      cheapestProcessor.isDiscounted = true;
    }
  }

}
