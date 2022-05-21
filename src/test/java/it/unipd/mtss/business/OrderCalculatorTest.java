package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.ItemType;
import it.unipd.mtss.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    assertEquals(2, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnTwoElements()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Intel Qualcosa", 69),
            new EItem(ItemType.Motherboard, "MadreTavola", 31)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(100.0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnOneElementWithPriceGreaterThanZero()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Mouse, "MegaMickey", 22)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(22.0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMoreThanFiveProcessors()
      throws OrderBillException {
    var products = List.of(
        new EItem(ItemType.Processor, "Processore1", 50),
        new EItem(ItemType.Processor, "Processore2", 50),
        new EItem(ItemType.Processor, "Processore3", 50),
        new EItem(ItemType.Keyboard, "Tastierona", 50),
        new EItem(ItemType.Processor, "Processore4", 50),
        new EItem(ItemType.Processor, "Processore5", 50),
        new EItem(ItemType.Processor, "ProcessoreCostoMinimo", 30)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(315.0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMoreThanTenMouse()
      throws OrderBillException {
    var products = List.of(
        new EItem(ItemType.Mouse, "Mouse1", 10),
        new EItem(ItemType.Mouse, "Mouse2", 10),
        new EItem(ItemType.Mouse, "Mouse3", 10),
        new EItem(ItemType.Mouse, "Mouse4", 10),
        new EItem(ItemType.Mouse, "CheapestMouse", 5),
        new EItem(ItemType.Mouse, "Mouse5", 10),
        new EItem(ItemType.Mouse, "Mouse6", 10),
        new EItem(ItemType.Mouse, "Mouse7", 10),
        new EItem(ItemType.Mouse, "Mouse8", 10),
        new EItem(ItemType.Mouse, "Mouse9", 10),
        new EItem(ItemType.Mouse, "Mouse10", 10)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(100.0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnSameKeyboardAndMouse()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Keyboard, "Keyboard", 50),
            new EItem(ItemType.Mouse, "Mouse", 50),
            new EItem(ItemType.Motherboard, "ProdottoMenoCaro", 20)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(100.0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnSameTwoDiscountRules()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Keyboard, "Keyboard", 50),
            new EItem(ItemType.Mouse, "Mouse", 50),
            new EItem(ItemType.Motherboard, "ProdottoMenoCaro", 20),
            new EItem(ItemType.Processor, "Processore1", 50),
            new EItem(ItemType.Processor, "Processore2", 50),
            new EItem(ItemType.Processor, "Processore3", 50),
            new EItem(ItemType.Processor, "Processore4", 50),
            new EItem(ItemType.Processor, "Processore5", 50),
            new EItem(ItemType.Processor, "ProcessoreCostoMinimo", 30)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(365, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnBigOrderPrice()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Motherboard, "Board costosa", 550),
            new EItem(ItemType.Processor, "Processore costoso", 550)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(990, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnBigOrderAndMultipleDiscount()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Keyboard, "Keyboard", 50),
            new EItem(ItemType.Mouse, "Mouse", 50),
            new EItem(ItemType.Motherboard, "ProdottoMenoCaro", 20),
            new EItem(ItemType.Processor, "Processore1", 50),
            new EItem(ItemType.Processor, "Processore2", 50),
            new EItem(ItemType.Processor, "Processore3", 50),
            new EItem(ItemType.Processor, "Processore4", 50),
            new EItem(ItemType.Processor, "Processore5", 50),
            new EItem(ItemType.Processor, "ProcessoreCostoMinimo", 30),
            new EItem(ItemType.Motherboard, "MOBOCostosa", 800)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(1048.5, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnOverLimitProductList() {
    var products =
        Stream.generate(() -> new EItem(ItemType.Mouse, "Mouse", 10))
            .limit(31)
            .collect(Collectors.toList());

    OrderBillException exc = assertThrows(
        OrderBillException.class,
        () -> order.getOrderPrice(products, user)
    );

    assertEquals(
        "You can't place an order with more than 30 items",
        exc.getMessage()
    );
  }
  @Test
  public void testGetOrderPrice_OnUnderPriceLimit()
      throws OrderBillException {
    var products = List.of(
        new EItem(ItemType.Keyboard, "Tastierina", 9)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(11, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnCommissionWithProcessorDiscount()
      throws OrderBillException {
    System.out.println("testComissione");
    var products = List.of(
        new EItem(ItemType.Processor, "ProcessoreNonCaro", 0.6),
        new EItem(ItemType.Processor, "Processore1", 1.5),
        new EItem(ItemType.Processor, "Processore2", 1.5),
        new EItem(ItemType.Processor, "Processore3", 1.5),
        new EItem(ItemType.Processor, "Processore4", 1.5),
        new EItem(ItemType.Processor, "Processore5", 3.5)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(11.8, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnCommissionWithMouseDiscount()
      throws OrderBillException {
    var products = List.of(
        new EItem(ItemType.Mouse, "Mouse1", 0.99),
        new EItem(ItemType.Mouse, "Mouse2", 0.99),
        new EItem(ItemType.Mouse, "Mouse3", 0.99),
        new EItem(ItemType.Mouse, "Mouse4", 0.99),
        new EItem(ItemType.Mouse, "Mouse5", 0.99),
        new EItem(ItemType.Mouse, "CheapestMouse", 0.90),
        new EItem(ItemType.Mouse, "Mouse6", 0.99),
        new EItem(ItemType.Mouse, "Mouse7", 0.99),
        new EItem(ItemType.Mouse, "Mouse8", 0.99),
        new EItem(ItemType.Mouse, "Mouse9", 0.99),
        new EItem(ItemType.Mouse, "Mouse10", 0.99)
    );

    var computedPrice = order.getOrderPrice(products, user);

    assertEquals(11.9, computedPrice, DELTA);
  }
}