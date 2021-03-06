////////////////////////////////////////////////////////////////////
// Elia Pasquali 1225412
// Alessio Ferrarini 1223860
////////////////////////////////////////////////////////////////////

package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.ItemType;
import it.unipd.mtss.model.User;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;

public class OrderCalculator implements Bill {

  private static final LocalTime AFTER = LocalTime.of(18, 0);
  private static final LocalTime BEFORE = LocalTime.of(19, 0);

  private final Random random;
  private int giftGiven;
  private final LocalTime orderTime;

  public OrderCalculator(Random random, LocalTime orderTime) {
    this.random = random;
    this.orderTime = orderTime;
    giftGiven = 0;
  }

  @Override
  public double getOrderPrice(List<EItem> itemsOrdered, User user)
      throws OrderBillException {
    validateArguments(itemsOrdered, user);
    applyProcessorDiscount(itemsOrdered);
    applyMouseGift(itemsOrdered);
    applySameQuantityGift(itemsOrdered);

    var actualPrice = itemsOrdered.stream()
            .mapToDouble(e -> e.price)
            .sum();

    // Apply 10% discount on big orders
    if (actualPrice > 1000) {
      actualPrice *= 0.9;
    }

    // Apply 2€ commission on small orders
    if (actualPrice < 10) {
      actualPrice += 2;
    }

    if (isEligibleForChildrenGift(user)) {
      return 0;
    } else {
      return actualPrice;
    }
  }

  private boolean isEligibleForChildrenGift(User user) {
    var isLucky = random.nextBoolean();
    var gift = user.age < 18
            && orderTime.isAfter(AFTER)
            && orderTime.isBefore(BEFORE)
            && isLucky;
    if (gift && giftGiven < 10) {
      giftGiven++;
      return true;
    } else {
      return false;
    }
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

    if (itemsOrdered.size() > 30) {
      throw new OrderBillException(
          "You can't place an order with more than 30 items"
      );
    }
  }

  private void applySameQuantityGift(List<EItem> itemsOrdered) {
    var mouseCount = itemsOrdered.stream()
            .filter(e -> e.itemType == ItemType.Mouse)
            .count();
    var keyboardCount = itemsOrdered.stream()
            .filter(e -> e.itemType == ItemType.Keyboard)
            .count();
    if (mouseCount == keyboardCount && mouseCount > 0) {
      var cheapestItem = itemsOrdered.stream()
              .filter(e -> !e.isDiscounted)
              .min((e1, e2) -> Double.compare(e1.price, e2.price));
      cheapestItem.ifPresent(e -> {
        e.isDiscounted = true;
        e.price = 0;
      });
    }
  }

  private void applyMouseGift(List<EItem> itemsOrdered) {
    applyQuantityDiscount(
        itemsOrdered,
        10,
        0.0f,
        e -> e.itemType == ItemType.Mouse
    );
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
          .min((e1, e2) -> Double.compare(e1.price, e2.price))
          .get(); // Safe since we know we have at least 5 processors
      cheapestProcessor.price *= discount;
      cheapestProcessor.isDiscounted = true;
    }
  }

}
