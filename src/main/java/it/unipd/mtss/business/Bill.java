////////////////////////////////////////////////////////////////////
// Elia Pasquali 1225412
// Alessio Ferrarini 1223860
////////////////////////////////////////////////////////////////////

package it.unipd.mtss.business;

import it.unipd.mtss.business.exceptions.OrderBillException;
import it.unipd.mtss.model.EItem;
import it.unipd.mtss.model.User;

import java.util.List;

public interface Bill {
  double getOrderPrice(List<EItem> itemsOrdered, User user)
      throws OrderBillException;
}