package com.codecorecix.ecommerce.utils.address;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.event.entities.Address;
import org.apache.commons.lang3.StringUtils;

public class AddressUtils {

  /**
   * Return the full name used to send as input parameter to service.
   *
   * @param address Address
   * @return Full name
   */
  public static String returnFullName(final Address address, final boolean surnameFirst) {
    final ArrayList<String> namePartsList = new ArrayList<>();
    namePartsList.add(address.getName());
    namePartsList.add(address.getName2());
    namePartsList.add(surnameFirst ? 0 : namePartsList.size(), address.getSurname());
    return StringUtils.trim(
        namePartsList.stream().filter(StringUtils::isNotBlank).map(Object::toString).collect(Collectors.joining(StringUtils.SPACE)));
  }
}
