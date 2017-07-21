/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.cce.service;

import static org.openlmis.cce.i18n.MessageKeys.ERROR_NO_FOLLOWING_PERMISSION;

import org.openlmis.cce.dto.ResultDto;
import org.openlmis.cce.dto.RightDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.exception.PermissionMessageException;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

  public static final String CCE_MANAGE = "CCE_MANAGE";
  public static final String CCE_INVENTORY_VIEW = "CCE_INVENTORY_VIEW";
  public static final String CCE_INVENTORY_EDIT = "CCE_INVENTORY_EDIT";

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  /**
   * Checks if current user has permission to manage CCE.
   * @throws PermissionMessageException if the current user has not a permission.
   */
  public void canManageCce() {
    checkPermission(CCE_MANAGE);
  }

  /**
   * Checks if current user has permission to view CCE inventory.
   * @throws PermissionMessageException if the current user has not a permission.
   */
  public void canViewInventory() {
    checkPermission(CCE_INVENTORY_VIEW);
  }

  /**
   * Checks if current user has permission to edit CCE inventory.
   * @throws PermissionMessageException if the current user has not a permission.
   */
  public void canEditInventory() {
    checkPermission(CCE_INVENTORY_EDIT);
  }

  private void checkPermission(String rightName) {
    if (!hasPermission(rightName)) {
      throw new PermissionMessageException(new Message(ERROR_NO_FOLLOWING_PERMISSION, rightName));
    }
  }

  private Boolean hasPermission(String rightName) {
    if (isClientOnly()) {
      return true;
    }
    UserDto user = authenticationHelper.getCurrentUser();
    RightDto right = authenticationHelper.getRight(rightName);
    ResultDto<Boolean> result = userReferenceDataService.hasRight(user.getId(), right.getId());
    return null != result && result.getResult();
  }

  private boolean isClientOnly() {
    return getAuthentication().isClientOnly();
  }

  private OAuth2Authentication getAuthentication() {
    return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
  }

}
