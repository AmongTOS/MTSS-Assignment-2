package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.ItemType;
import it.unipd.mtss.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderCalculatorTest {

  private static final double DELTA = 0.0009;
  private static final LocalTime IN_TIME = LocalTime.of(18, 30);
  private static final LocalTime BEFORE_TIME = LocalTime.of(2, 0);
  private static final LocalTime AFTER_TIME = LocalTime.of(20, 0);

  private OrderCalculator alwaysFalseInTime;
  private OrderCalculator alwaysTrueInTime;
  private OrderCalculator alwaysFalseBeforeTime;
  private OrderCalculator alwaysFalseAfterTime;
  private OrderCalculator alwaysTrueBeforeTime;
  private OrderCalculator alwaysTrueAfterTime;

  private static User adult;
  private static User minor;

  private static Random alwaysFalse;
  private static Random alwaysTrue;

  @BeforeClass
  public static void classSetUp() {
    alwaysTrue = mock(Random.class);
    when(alwaysTrue.nextBoolean()).thenReturn(true);

    alwaysFalse = mock(Random.class);
    when(alwaysFalse.nextBoolean()).thenReturn(false);

    adult = new User("Adulto", 19);
    minor = new User("Bocia", 1);
  }

  @Before
  public void setUp() {
    alwaysFalseInTime = new OrderCalculator(alwaysFalse, IN_TIME);
    alwaysTrueInTime = new OrderCalculator(alwaysTrue, IN_TIME);

    alwaysFalseAfterTime = new OrderCalculator(alwaysFalse, AFTER_TIME);
    alwaysFalseBeforeTime = new OrderCalculator(alwaysFalse, BEFORE_TIME);

    alwaysTrueAfterTime = new OrderCalculator(alwaysTrue, AFTER_TIME);
    alwaysTrueBeforeTime = new OrderCalculator(alwaysTrue, BEFORE_TIME);
  }

  @Test
  public void testGetOrderPrice_OnNullList() {

    NullPointerException exc = assertThrows(
        NullPointerException.class,
        () -> alwaysFalseInTime.getOrderPrice(null, adult)
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
        () -> alwaysFalseInTime.getOrderPrice(products, null)
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
        () -> alwaysFalseInTime.getOrderPrice(products, adult)
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
        () -> alwaysFalseInTime.getOrderPrice(products, adult)
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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

    assertEquals(2, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnTwoElements()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Intel Qualcosa", 69),
            new EItem(ItemType.Motherboard, "MadreTavola", 31)
    );

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

    assertEquals(100.0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnOneElementWithPriceGreaterThanZero()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Mouse, "MegaMickey", 22)
    );

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

    assertEquals(365, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnBigOrderPrice()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Motherboard, "Board costosa", 550),
            new EItem(ItemType.Processor, "Processore costoso", 550)
    );

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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
        () -> alwaysFalseInTime.getOrderPrice(products, adult)
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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

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

    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

    assertEquals(11.9, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMinorUserAndWinningTimeAndLucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysTrueInTime.getOrderPrice(products, minor);

    assertEquals(0, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMinorUserAndWinningTimeAndUnlucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysFalseInTime.getOrderPrice(products, minor);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMinorUserAndAfterWinningTimeAndLucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysTrueAfterTime.getOrderPrice(products, minor);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMinorUserAndBeforeWinningTimeAndLucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysTrueBeforeTime.getOrderPrice(products, minor);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMinorUserAndAfterWinningTimeAndUnlucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysFalseAfterTime.getOrderPrice(products, minor);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnMinorUserAndBeforeWinningTimeAndUnlucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysFalseBeforeTime.getOrderPrice(products, minor);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnAdultUserAndWinningTimeAndLucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysTrueInTime.getOrderPrice(products, adult);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnAdultUserAndWinningTimeAndUnlucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysFalseInTime.getOrderPrice(products, adult);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnAdultUserAndAfterWinningTimeAndLucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysTrueAfterTime.getOrderPrice(products, adult);

    assertEquals(13, computedPrice, DELTA);
  }


  @Test
  public void testGetOrderPrice_OnAdultUserAndBeforeWinningTimeAndLucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysTrueBeforeTime.getOrderPrice(products, adult);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnAdultUserAndAfterWinningTimeAndUnlucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysFalseAfterTime.getOrderPrice(products, adult);

    assertEquals(13, computedPrice, DELTA);
  }

  @Test
  public void testGetOrderPrice_OnAdultUserAndBeforeWinningTimeAndUnlucky()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );
    var computedPrice = alwaysFalseBeforeTime.getOrderPrice(products, adult);

    assertEquals(13, computedPrice, DELTA);
  }
  @Test
  public void
  testGetOrderPrice_OnMinorUserAndWinningTimeAndLuckyButEleventhGift()
          throws OrderBillException {
    var products = List.of(
            new EItem(ItemType.Processor, "Provola", 13)
    );

    List<Double> prices = new ArrayList<>();

    for(int i = 0; i < 11; ++i) {
      prices.add(alwaysTrueInTime.getOrderPrice(products, minor));
    }

    var expectedPrice = List.of(
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 13.0
    );
    assertEquals(expectedPrice, prices);
  }

}