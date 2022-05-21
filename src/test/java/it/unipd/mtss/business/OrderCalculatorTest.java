package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.ItemType;
import it.unipd.mtss.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class OrderCalculatorTest {

  private static final double DELTA = 0.09;
  private static OrderCalculator order;

  private static User user;

  @BeforeClass
  public static void classSetUp() {
    order = new OrderCalculator();
    user = new User("User", 19);
  }

  @Test
  public void testGetOrderPrice_OnNullList() {

    NullPointerException exc = assertThrows(
        NullPointerException.class,
        () -> order.getOrderPrice(null, user)
    );

    assertEquals("The ordered items must not be null", exc.getMessage());
  }


  @Test
  public void testGetOrderPrice_OnNullUser() {

    var products = List.of(
        new EItem(ItemType.Processor, "Intel Qualcosa", 69),
        new EItem(ItemType.Motherboard, "MadreTavola", 31)
    );

    NullPointerException exc = assertThrows(
        NullPointerException.class,
        () -> order.getOrderPrice(products, null)
    );

    assertEquals("The user must not be null", exc.getMessage());

  }

  @Test
  public void testGetOrderPrice_OnNegativePrice() {
    var products = List.of(
        new EItem(ItemType.Motherboard, "Prodotto positivo", 50),
        new EItem(ItemType.Processor, "Prodotto negativo", -30)
    );

    OrderBillException exc = assertThrows(
        OrderBillException.class,
        () -> order.getOrderPrice(products, user)
    );

    assertEquals(
        "All the items must have a positive price",
        exc.getMessage()
    );

  }

  @Test
  public void testGetOrderPrice_OnEmptyList() {
    List<EItem> products = List.of();

    OrderBillException exc = assertThrows(
        OrderBillException.class,
        () -> order.getOrderPrice(products, user)
    );

    assertEquals(
        "You can't place an order with 0 items",
        exc.getMessage()
    );
  }

  @Test
  public void testGetOrderPrice_OnOneElement()
      throws OrderBillException {
    var products = List.of(
        new EItem(ItemType.Motherboard, "Prodotto positivo", 0)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(0, computedPrice, DELTA);
  }

}